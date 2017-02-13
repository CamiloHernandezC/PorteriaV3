package Controllers;

import Entities.MovPersonasCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.PersonasCli;
import Facade.MovPersonasCliFacade;
import Querys.Querys;
import Utils.Constants;
import Utils.Result;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("movPersonasCliController")
@SessionScoped
public class MovPersonasCliController extends AbstractPersistenceController<MovPersonasCli>{

    @EJB
    private Facade.MovPersonasCliFacade ejbFacade;
    private List<MovPersonasCli> items = null;
    private MovPersonasCli selected;
    private List<MovPersonasCli> movsToForceOut = null;

    public MovPersonasCliController() {
    }
    
    //<editor-fold desc="INHERITED METHODS" defaultstate="collapsed">
    @Override
    protected void setEmbeddableKeys() {
        //Nothing to do here
    }

    @Override
    protected void initializeEmbeddableKey() {
        //Nothing to do here
    }

    @Override
    public MovPersonasCli getSelected() {
        return selected;
    }

    @Override
    public void setSelected(MovPersonasCli selected) {
        this.selected = selected;
    }

    @Override
    protected MovPersonasCliFacade getFacade() {
        return ejbFacade;
    }
    
    @Override
    protected void setItems(List<MovPersonasCli> items) {
        this.items = items;
    }

    @Override
    protected String calculatePrimaryKey() {
        Result result = ejbFacade.findByQuery(Querys.MOV_PERSONA_CLI_PRIMARY_KEY, true);
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){//First record will be created
            return "1";
        }
        MovPersonasCli lastMovement = (MovPersonasCli) result.result;
        Long lastPrimaryKey = lastMovement.getIdMovimiento();
        return String.valueOf(lastPrimaryKey+1L);
    }
    //</editor-fold>

    public MovPersonasCli prepareCreate() {
        selected = new MovPersonasCli();
        initializeEmbeddableKey();
        selected.setIdMovimiento(Long.valueOf(calculatePrimaryKey()));
        return selected;
    }

    public List<MovPersonasCli> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public MovPersonasCli getMovPersonasCli(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MovPersonasCli> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MovPersonasCli> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MovPersonasCli.class)
    public static class MovPersonasCliControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MovPersonasCliController controller = (MovPersonasCliController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "movPersonasCliController");
            return controller.getMovPersonasCli(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MovPersonasCli) {
                MovPersonasCli o = (MovPersonasCli) object;
                return getStringKey(o.getIdMovimiento());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MovPersonasCli.class.getName()});
                return null;
            }
        }

    }
    
    public void recordEntryMovement(PersonasCli person, int persistAction) {
        if(persistAction == Constants.UPDATE){
            if(verifyEntry(person)){
                recordForcedOut();
            }
        }
        recordEntry(person);
    }
    public void recordEntry(PersonasCli person){
        prepareEntityToCreate(person);
        create();
    }

    public boolean verifyEntry(PersonasCli person){
        String squery = Querys.MOV_PERSONA_CLI_ALL+"WHERE"+Querys.MOV_PERSONA_CLI_PERSONA+person.getIdPersona()+"'"+ 
                "and"+Querys.MOV_PERSONA_CLI_FECHA_SALIDA_NULL;
        Result result = ejbFacade.findByQueryArray(squery);
        if(result.errorCode == Constants.NO_RESULT_EXCEPTION){
            return false;
        }
        movsToForceOut = (List<MovPersonasCli>) result.result;
        return true;
    }
    
    public void recordForcedOut(){
        for(MovPersonasCli mov: movsToForceOut){
            mov.setFechaSalida(mov.getFechaEntrada());
            mov.setHoraSalida(mov.getHoraEntrada());
            mov.setSalidaForzosa(true);
            selected = mov;
            update();
        }
        movsToForceOut =  null;
    }
    
    private void prepareEntityToCreate(PersonasCli person) {
        prepareCreate();
        Date actualDate = new Date();
        selected.setIdPersona(person);
        selected.setIdSucursal(person.getIdSucursal());
        selected.setIdArea(person.getArea());
        selected.setFechaEntrada(actualDate);
        selected.setHoraEntrada(actualDate);
        //TODO PERSONA QUE AUTORIZA
        selected.setUsuario(person);//TODO ASSIGN REAL USER
        selected.setFecha(actualDate);
        selected.setSalidaForzosa(false);
    }
}
