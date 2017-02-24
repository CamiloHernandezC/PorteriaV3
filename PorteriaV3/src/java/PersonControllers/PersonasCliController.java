package PersonControllers;

import Controllers.AbstractPersistenceController;
import Controllers.ConfigFormCliController;
import Entities.PersonasCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.EstadosCli;
import Facade.PersonasCliFacade;
import PersonControllers.Views.PersonFormEntry;
import Querys.Querys;
import Utils.Constants;
import Utils.Result;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.context.RequestContext;

public class PersonasCliController extends AbstractPersistenceController<PersonasCli> {

    @EJB
    protected Facade.PersonasCliFacade ejbFacade;
    protected List<PersonasCli> items = null;
    protected PersonasCli selected;
    protected ConfigFormCliController configFormCliController = JsfUtil.findBean("configFormCliController");
    protected PersonasSucursalCliController personasSucursalCliController = JsfUtil.findBean("personasSucursalCliController");
    protected MovPersonasCliController movPersonasCliController = JsfUtil.findBean("movPersonasCliController");
    public PersonasCliController() {
    }

    //<editor-fold desc="INHERITED METHODS" defaultstate="collapsed">
    @Override
    public PersonasCli getSelected() {
        if (selected == null) {
            selected = new PersonasCli();
        }
        return selected;
    }

    @Override
    public void setSelected(PersonasCli selected) {
        this.selected = selected;
    }

    @Override
    protected PersonasCliFacade getFacade() {
        return ejbFacade;
    }

    @Override
    protected void setItems(List<PersonasCli> items) {
        this.items = items;
    }

    @Override
    protected void setEmbeddableKeys() {
        //Nothing to do here
    }

    @Override
    protected void initializeEmbeddableKey() {
        //Nothing to do here
    }

    @Override
    protected String calculatePrimaryKey() {
        Result result = ejbFacade.findByQuery(Querys.PERSONA_CLI_PRIMARY_KEY, true);
        if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {//First record will be created
            return "1";
        }
        PersonasCli lastPerson = (PersonasCli) ejbFacade.findByQuery(Querys.PERSONA_CLI_PRIMARY_KEY, true).result;
        Long lastPrimaryKey = Long.valueOf(lastPerson.getIdPersona());
        return String.valueOf(lastPrimaryKey + 1L);
    }

    @Override
    protected void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            selected.setUsuario("1");//TODO ASSIGN REAL USER
            selected.setFecha(new Date());
        }
        super.persist(persistAction, successMessage);
    }
    
    /**
     * 
     */
    @Override
    public void prepareCreate() {
        selected.setIdPersona(calculatePrimaryKey());
        selected.setEstado(new EstadosCli(Constants.STATUS_ENTRY));
        prepareUpdate();
    }
    
    @Override
    protected void prepareUpdate() {
        selected.setUsuario(selected.getIdPersona());//TODO ASSIGN REAL USER HERE
        selected.setFecha(new Date());
    }
    //</editor-fold>

    /**
     * Verifica las fechas de seguridad social ingresadas en el sistema.
     * @return True: Fechas son validas, False: Fechas vencidas
     */
    protected boolean verifyDatesPerson() {
        //return true;
        boolean fechaEPS = true;
        boolean fechaARL = true;
        //Determina si se esta mostrando el campo seleccionado.
        if (configFormCliController.isMostrarFecha_vigencia_ARL() == false || configFormCliController.isMostrarFecha_vigencia_EPS() == false) {
            return true;
        }
        //Determina si viene nulo dicho parametro.
        if (selected.getFechavigenciaARL() == null) {
            JsfUtil.addErrorMessage("La persona no tiene asignada fecha de vigencia de  ARL");
            return false;
        }
        if (selected.getFechavigenciaEPS() == null) {
            JsfUtil.addErrorMessage("La persona no tiene asignada fecha de vigencia de  EPS");
            return false;
        }

        if (selected.getFechavigenciaARL().before(new Date())) {
            fechaARL = false;
            selected.setFechavigenciaARL(null);
            JsfUtil.addErrorMessage("Fecha de vigencia de ARL esta vencida");
        }
        if (selected.getFechavigenciaEPS().before(new Date())) {
            fechaEPS = false;
            selected.setFechavigenciaEPS(null);
            JsfUtil.addErrorMessage("Fecha de vigencia de EPS esta vencida");
        }
        return fechaARL && fechaEPS;
    }

    public Result findPersonByDocument() {
        String squery = Querys.PERSONA_CLI_ALL + "WHERE" + Querys.PERSONA_CLI_DOC_TYPE + selected.getTipoDocumento().getTipodocumento() + "' AND"
                + Querys.PERSONA_CLI_DOC_NUMBER + selected.getNumDocumento() + "'";
        return ejbFacade.findByQuery(squery, false);//Only one person should have document type, an document number (It is unique in database)
    }

    protected void disableNoEditableFields(boolean enable) {
        PersonFormEntry personFormEntry = JsfUtil.findBean("personFormEntry");
        personFormEntry.setDisableNoEditableField(enable);
    }

    /**
     * If person is blocked (registered in blocked table) will show a dialog
     *
     * @return true if the person is blocked, false otherwise
     */
    protected boolean verifyBlockedPerson() {

        if (Objects.equals(selected.getEstado().getIdEstado(), Constants.STATUS_BLOCKED)) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('blockedDialog').show();");
            return true;
        }
        return false;
    }

    public void cancel() {
        selected = new PersonasCli();
        //TODO CLEAN BRANCH AND AREA, MOV, EVERITHING
        JsfUtil.cancel();
    }
    
    // <editor-fold desc="CONVERTER" defaultstate="collapsed">
    
    public PersonasCli getPersonasCli(java.lang.String id) {
        return getFacade().find(id);
    }
    
    @FacesConverter(forClass = PersonasCli.class)
    public static class PersonasCliControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonasCliController controller = (PersonasCliController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personasCliController");
            return controller.getPersonasCli(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PersonasCli) {
                PersonasCli o = (PersonasCli) object;
                return getStringKey(o.getIdPersona());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PersonasCli.class.getName()});
                return null;
            }
        }

    }
    //</editor-fold>
}
