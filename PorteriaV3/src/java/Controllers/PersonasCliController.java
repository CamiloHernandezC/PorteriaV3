package Controllers;

import Entities.PersonasCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.EntidadesCli;
import Entities.EstadosCli;
import Facade.PersonasCliFacade;
import Querys.Querys;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.Result;
import ViewControllers.PersonFormEntry;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("personasCliController")
@SessionScoped
public class PersonasCliController extends AbstractPersistenceController<PersonasCli>{

    @EJB
    private Facade.PersonasCliFacade ejbFacade;
    private List<PersonasCli> items = null;
    private PersonasCli selected;

    public PersonasCliController() {
    }

    //<editor-fold desc="INHERITED METHODS" defaultstate="collapsed">
    @Override
    public PersonasCli getSelected() {
        if(selected==null){
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
    protected String calculatePrimaryKey(){
        PersonasCli lastPerson = (PersonasCli) ejbFacade.findByQuery(Querys.PERSONA_CLI_PRIMARY_KEY, true).result;
        Long lastPrimaryKey = Long.valueOf(lastPerson.getIdPersona());
        return String.valueOf(lastPrimaryKey+1L);
    }
    //</editor-fold>
    
    @Override
    protected void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            selected.setUsuario("1");//TODO ASSIGN REAL USER
            selected.setFecha(new Date());
        }
        super.persist(persistAction, successMessage);
    }

    public void prepareCreate() {
        selected.setIdPersona(calculatePrimaryKey());
        selected.setIdEntidad(new EntidadesCli("1"));//TODO ASSIGN VISITAN
        selected.setIdEstado(new EstadosCli(3L));//TODO ASSIGN AT ENTRY
    }

    public List<PersonasCli> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public PersonasCli getPersonasCli(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<PersonasCli> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PersonasCli> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    // <editor-fold desc="CONVERTER" defaultstate="collapsed">
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
    
    /**
     * Method used to add object in an existing entry
     */
    public void addObjects(){
        
    }
    
    /**
     * Method used to read barcode or id card (cedula)
     * The readed value is stored in selected.numDocum
     */
    public void completeEntryByCodeReader(){
        String  pageToRedirect = redirectToRegisterForm(findByCodeReader());
        //TODO Here it is necessary to redirect
    }
    
    /**
     * Method used to search person and redirect to register form, verifying if
     * person is blocked
     * @return page to redirect
     */
    public String manualEntry(){
        return redirectToRegisterForm(findPersonByDocument());
    }
    
    /**
     * 
     * @param result
     * @return 
     */
     private String redirectToRegisterForm(Result result) {
         if(result.errorCode != 0 && result.errorCode != Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Tecnical_Failure"));
            return null;
        }
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Please_Register"));
            prepareCreate();
        }else{
            if(verifyBlockedPerson()){//Onlye when person is registered, we can verify if is a blocked person
                return null;
            }
            selected = (PersonasCli) result.result;
            disableNoEditableFields();
        }
        return Navigation.PAGE_PERSON_REGISTER;
    }
    
    private Result findPersonByDocument() {
        String squery = Querys.PERSONA_CLI_ALL+"WHERE"+Querys.PERSONA_CLI_DOC_TYPE+selected.getTipoDocumento().getTipodocumento()+"' AND "+
                Querys.PERSONA_CLI_DOC_NUMBER+selected.getNumDocumento()+"'";
        return ejbFacade.findByQuery(squery, true);
    }
    
    private Result findPersonByIdExterno() {
        String squery = Querys.PERSONA_CLI_ALL+"WHERE"+Querys.PERSONA_CLI_ID_EXTERNO+selected.getIdExterno()+"'";
        return ejbFacade.findByQuery(squery, false);//False because person must have unique id externo
    }
    
    /**
     * If person is blocked (registered in blocked table) will show a dialog 
     * @return true if the person is blocked, false otherwise
     */
    private boolean verifyBlockedPerson() {
        //TODO FINISH THIS METHOD
        return false;
    }
    
    private void disableNoEditableFields() {
        PersonFormEntry personFormEntry = JsfUtil.findBean("personFormEntry");
        personFormEntry.setDisableNoEditableField(true);
        
    }
    
    private Result findByCodeReader() {
        /*
        if(selected.getNumDocumento().startsWith("C,")){//ID CARD (CEDULA)
            String[] palabrasSeparadas = separarPalabras();
            if (palabrasSeparadas != null && palabrasSeparadas.length == 10) {
                selected.setTipoDocumento(new TiposDocumentoCli("13"));//Se asigna el tipo de documento como cedula
                selected.setNumDocumento(palabrasSeparadas[0]);//Se le asigna el numero de cedula que fue leido por el lector de cedulas
                return findPersonByDocument();
                //TODO FINISH THIS METHOD
                //SI ES UN VISITANTE TIENE QUE LLENAR LA SUCURSAL Y EL AREA A LA CUAL VA
            }
        }
        if(selected.getNumDocumento().startsWith("B,")){//BAR CODE
            selected.setIdExterno("");
            return findPersonByIdExterno();
        }
        */
        return new Result(null, Constants.UNKNOWN_EXCEPTION);//This should never happen
    }
    
    public String save(){
        Result result = findSpecificPerson();
        if(result.errorCode!=Constants.OK && result.errorCode != Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Tecnical_Failure"));
            return null;//This should never happend
        }
        MovPersonasCliController movPersonasCliController = JsfUtil.findBean("movPersonasCliController");
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){
            create();
            movPersonasCliController.recordEntryMovement(selected, Constants.CREATE);
        }
        if(result.errorCode==Constants.OK){
            movPersonasCliController.recordEntryMovement(selected, Constants.UPDATE);
            update();
        }
        return Navigation.PAGE_SELECT_ENTRY;
        
    }
    
    /**
     * Search a person whit selected branch office, and status different to inactive
     * @return 
     */
    public Result findSpecificPerson(){
        String squery = Querys.PERSONA_CLI_ALL+"WHERE"+Querys.PERSONA_CLI_DOC_TYPE+selected.getTipoDocumento().getTipodocumento()+"' AND"+
                Querys.PERSONA_CLI_DOC_NUMBER+selected.getNumDocumento()+"' AND"+Querys.PERSONA_CLI_SUCURSAL+selected.getIdSucursal().getIdSucursal()+
                "' AND"+Querys.PERSONA_CLI_ESTADO_N+"4'";
        return ejbFacade.findByQuery(squery, false);//False because only one person should appear
    }
    
    public void valueChangeHandlerOriginEnterprise(){
        //TODO finish this method
    }

}
