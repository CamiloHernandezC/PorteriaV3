package Controllers;

import Entities.PersonasCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.EntidadesCli;
import Entities.EstadosCli;
import Entities.TiposDocumentoCli;
import Facade.PersonasCliFacade;
import Querys.Querys;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.Result;
import ViewControllers.PersonFormEntry;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import javax.faces.event.AjaxBehaviorEvent;

@Named("personasCliController")
@SessionScoped
public class PersonasCliController extends AbstractPersistenceController<PersonasCli>{

    @EJB
    private Facade.PersonasCliFacade ejbFacade;
    private List<PersonasCli> items = null;
    private PersonasCli selected;
    private String code;

    public PersonasCliController() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
        Result result = ejbFacade.findByQuery(Querys.PERSONA_CLI_PRIMARY_KEY, true);
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){//First record will be created
            return "1";
        }
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

    /**
     * Set some attributes needed to save
     * @param cleanModel 
     */
    public void prepareCreate(boolean cleanModel) {
        if(cleanModel){
            selected = new PersonasCli();
        }
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
        if(code==null){
            return;
        }
        String  pageToRedirect = null;
        if(code.startsWith("C,")){//ID CARD (CEDULA)
            pageToRedirect = redirectToRegisterForm(findByCodeReader(), false);//If person is not find with id card (cedula), the field are not clean because it already has information
        }
        if(code.startsWith("B,")){//BAR CODE
            pageToRedirect = redirectToRegisterForm(findByCodeReader(), true);
        }
        code = null;
        JsfUtil.redirectTo(Navigation.PAGE_INDEX+pageToRedirect);
        
    }
    
    /**
     * Method used to search person and redirect to register form, verifying if
     * person is blocked
     * @return page to redirect
     */
    public String manualEntry(){
        return redirectToRegisterForm(findPersonByDocument(), true);
    }
    
    /**
     * 
     * @param result
     * @return 
     */
     private String redirectToRegisterForm(Result result, boolean cleanToCreate) {
         if(result.errorCode != 0 && result.errorCode != Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Tecnical_Failure"));
            return null;
        }
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Please_Register"));
            prepareCreate(cleanToCreate);
        }else{
            if(verifyBlockedPerson()){//Onlye when person is registered, we can verify if is a blocked person
                return null;
            }
            selected = (PersonasCli) result.result;
            disableNoEditableFields();
        }
        ConfigFormCliController configFormCliController =  JsfUtil.findBean("configFormCliController");
        configFormCliController.showFieldsPerson();
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
    
    /**
     * Take the code readed and find a person to return
     * @return 
     */
    private Result findByCodeReader() {
        
        if(code.startsWith("C,")){//ID CARD (CEDULA)
            String[] separatedWords = separateWords();
            if (separatedWords != null) {
                //Se debe asignar el tipo de documento y número de documento para poder buscar
                selected.setTipoDocumento(new TiposDocumentoCli("13"));//Se asigna el tipo de documento como cedula
                selected.setNumDocumento(separatedWords[0]);//Se le asigna el numero de cedula que fue leido por el lector de cedulas
                //<editor-fold desc="Assign selected to info in id card" defaultstate="collapsed">
                selected.setApellido1(separatedWords[1]);
                selected.setApellido2(separatedWords[2]);
                selected.setNombre1(separatedWords[3]);
                selected.setNombre2(separatedWords[4]);
                selected.setSexo(separatedWords[5].equals("M"));
                DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                try {                
                    Date birthDate = formatter.parse(separatedWords[6]);
                    selected.setFechaNacimiento(birthDate);
                } catch (ParseException ex) {
                    System.out.println(Constants.MESSAGE_DATE_FORMAT_EXCEPTION);
                }
                String RH = "¡".equals(separatedWords[7].substring(1)) ? "+":"-";
                selected.setRh(separatedWords[7].substring(0, 1)+RH);
                //</editor-fold>
                return findPersonByDocument();
                //TODO FINISH THIS METHOD
                //SI ES UN VISITANTE TIENE QUE LLENAR LA SUCURSAL Y EL AREA A LA CUAL VA
            }
        }
        if(code.startsWith("B,")){//BAR CODE
            selected.setIdExterno("");
            return findPersonByIdExterno();
        }
        return new Result(null, Constants.UNKNOWN_EXCEPTION);//This should never happen
    }
    
    private String[] separateWords() {
        int commaCounter = 0;
        String[] separatedWords = new String[10];
        int oldi = 1;
        for (int i = 2; i <code.length(); i++) {//Start in 2 to avoid "C,"
            char c = code.charAt(i);
            if (c == ',') {
                if (oldi + 1 != i) {
                    separatedWords[commaCounter] = code.substring(oldi + 1, i);    
                } else {
                    separatedWords[commaCounter] = "";
                }
                commaCounter++;
                oldi = i;
            }
        }
        if (commaCounter == 9) {
            separatedWords[1]= String.valueOf(Integer.parseInt(separatedWords[0]));//Las cedulas las completa con 0 a la izquierda, esta linea de codigo quita los 0
            return separatedWords;
        }
        return null;
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
