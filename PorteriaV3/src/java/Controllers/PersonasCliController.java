package Controllers;

import Entities.PersonasCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Facade.PersonasCliFacade;
import Querys.Querys;
import Utils.BundleUtils;
import Utils.Navigation;
import Utils.Result;

import java.io.Serializable;
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

@Named("personasCliController")
@SessionScoped
public class PersonasCliController implements Serializable {

    @EJB
    private Facade.PersonasCliFacade ejbFacade;
    private List<PersonasCli> items = null;
    private PersonasCli selected;

    public PersonasCliController() {
    }

    public PersonasCli getSelected() {
        if(selected==null){
            selected = new PersonasCli();
        }
        return selected;
    }

    public void setSelected(PersonasCli selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonasCliFacade getFacade() {
        return ejbFacade;
    }

    public PersonasCli prepareCreate() {
        selected = new PersonasCli();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("Utils/Bundle").getString("PersonasCliCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("Utils/Bundle").getString("PersonasCliUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("Utils/Bundle").getString("PersonasCliDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<PersonasCli> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("Utils/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("Utils/Bundle").getString("PersistenceErrorOccured"));
            }
        }
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
    
    /**
     * Method used to read barcode or id card (cedula)
     */
    public void automaticSearch(){
        
    }
    
    /**
     * Method used to add object in an existing entry
     */
    public void addObjects(){
        
    }
    
    /**
     * Method used to search person and redirect to register form, verifying if
     * person is blocked
     * @return page to redirect
     */
    public String manualEntry(){
        if(vefityBlockedPerson()){
            return null;
        }
        Result result = findPersonByDocument();
        if(result.errorCode!=0){
            JsfUtil.addErrorMessage(BundleUtils.getBundle("Please_Register"));
            prepareCreate();
        }else{
            selected = (PersonasCli) result.result;
            disableNoEditableFields();
        }
        return Navigation.PAGE_PERSON_REGISTER;
    }
    
    private Result findPersonByDocument() {
        String squery = Querys.PERSONA_CLI_ALL+"WHERE"+Querys.PERSONA_CLI_DOC_TYPE+selected.getTipoDocumento().getTipodocumento()+"'"+
                Querys.PERSONA_CLI_DOC_NUMBER+selected.getNumDocumento()+"'";
        return ejbFacade.findByQuery(squery, true);
    }
    
    /**
     * If person is blocked (registered in blocked table) will show a dialog 
     * @return true if the person is blocked, false otherwise
     */
    private boolean vefityBlockedPerson() {
        //TODO FINISH THIS METHOD
        return false;
    }
    
    private void disableNoEditableFields() {
        //TODO FINISH THIS METHOD
    }

}
