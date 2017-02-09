package Controllers;

import Entities.VehiculosCli;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Facade.VehiculosCliFacade;

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

@Named("vehiculosCliController")
@SessionScoped
public class VehiculosCliController implements Serializable {

    @EJB
    private Facade.VehiculosCliFacade ejbFacade;
    private List<VehiculosCli> items = null;
    private VehiculosCli selected;

    public VehiculosCliController() {
    }

    public VehiculosCli getSelected() {
        return selected;
    }

    public void setSelected(VehiculosCli selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VehiculosCliFacade getFacade() {
        return ejbFacade;
    }

    public VehiculosCli prepareCreate() {
        selected = new VehiculosCli();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("Utils/Bundle").getString("VehiculosCliCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("Utils/Bundle").getString("VehiculosCliUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("Utils/Bundle").getString("VehiculosCliDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<VehiculosCli> getItems() {
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

    public VehiculosCli getVehiculosCli(java.lang.String id) {
        return getFacade().find(id);
    }

    public List<VehiculosCli> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<VehiculosCli> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = VehiculosCli.class)
    public static class VehiculosCliControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VehiculosCliController controller = (VehiculosCliController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vehiculosCliController");
            return controller.getVehiculosCli(getKey(value));
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
            if (object instanceof VehiculosCli) {
                VehiculosCli o = (VehiculosCli) object;
                return getStringKey(o.getIdVehiculo());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), VehiculosCli.class.getName()});
                return null;
            }
        }

    }

}
