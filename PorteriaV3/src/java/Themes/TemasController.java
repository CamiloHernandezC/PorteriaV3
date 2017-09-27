package Themes;

import Controllers.UsuariosController;
import Entities.Theme;
import Facade.ThemeFacade;


import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

@Named("temasController")
@SessionScoped
public class TemasController implements Serializable {

    @EJB
    private Facade.ThemeFacade ejbFacade;
    private List<Theme> items = null;
    private Theme selected;
    
    @Inject
    UsuariosController usuariosController;

    public TemasController() {
    }
    
    public Theme getSelected() {
        if(selected==null){
            selected = ejbFacade.find(14);
        }
        return selected;
    }
    
    public void reset(){
        selected = ejbFacade.find(14);
        //usuariosController = JsfUtil.findBean("usuariosController");
        usuariosController.saveTheme(selected);
    }

    public void setSelected(Theme selected) {
        this.selected = selected;
        //usuariosController = JsfUtil.findBean("usuariosController");
        usuariosController.saveTheme(selected);
    }
    
    public void setSelectedLogin(Theme selected) {
        this.selected = selected;
    }

    private ThemeFacade getFacade() {
        return ejbFacade;
    }

    public List<Theme> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public Theme getTheme(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Theme> cargarTemas() {
        List<Theme> lista = ejbFacade.findAll();
        //List<Themes.Theme> listaFinal = new ArrayList<>();
        return lista;
    }

    @FacesConverter(forClass = Theme.class)
    public static class ThemeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TemasController controller = (TemasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "temasController");
            return controller.getTheme(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Theme) {
                Theme o = (Theme) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Theme.class.getName()});
                return null;
            }
        }

    }

}
