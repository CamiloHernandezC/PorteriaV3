package Controllers;

import Entities.ConfigFormCli;
import Facade.ConfigFormCliFacade;

import java.io.Serializable;
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

@Named("configFormCliController")
@SessionScoped
public class ConfigFormCliController implements Serializable {

    @EJB
    private Facade.ConfigFormCliFacade ejbFacade;
    private List<ConfigFormCli> items = null;
    private ConfigFormCli selected;

    public ConfigFormCliController() {
    }

    public ConfigFormCli getSelected() {
        return selected;
    }

    public void setSelected(ConfigFormCli selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConfigFormCliFacade getFacade() {
        return ejbFacade;
    }

    public List<ConfigFormCli> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public ConfigFormCli getConfigFormCli(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ConfigFormCli> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ConfigFormCli> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ConfigFormCli.class)
    public static class ConfigFormCliControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConfigFormCliController controller = (ConfigFormCliController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configFormCliController");
            return controller.getConfigFormCli(getKey(value));
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
            if (object instanceof ConfigFormCli) {
                ConfigFormCli o = (ConfigFormCli) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ConfigFormCli.class.getName()});
                return null;
            }
        }

    }

}
