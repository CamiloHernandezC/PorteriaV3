package Controllers;

import Entities.EmpresaOrigen;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import Entities.Estados;
import Facade.EmpresaOrigenFacade;
import Querys.Querys;
import Utils.Constants;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


public class EmpresaOrigenController extends AbstractPersistenceController<EmpresaOrigen>{

    @EJB
    protected Facade.EmpresaOrigenFacade ejbFacade;
    protected List<EmpresaOrigen> items = null;
    protected EmpresaOrigen selected;

    public EmpresaOrigenController() {
    }

    @Override
    public EmpresaOrigen getSelected() {
        if (selected == null) {
            selected = new EmpresaOrigen();
        }
        return selected;
    }

    @Override
    public void setSelected(EmpresaOrigen selected) {
        this.selected = selected;
    }

    @Override
    protected void setEmbeddableKeys() {
    }

    @Override
    protected void initializeEmbeddableKey() {
    }

    @Override
    protected EmpresaOrigenFacade getFacade() {
        return ejbFacade;
    }

    @Override
    public void prepareCreate() {
        calculatePrimaryKey(Querys.EMPRESA_ORIGEN_PRIMARY_KEY);
        selected.setEstado(new Estados(Constants.STATUS_ENTRY));
        prepareUpdate();
    }

    public List<EmpresaOrigen> getItems() {
        items = getFacade().findAll();
        return items;
    }


    public EmpresaOrigen getEmpresaOrigen(java.lang.Integer id) {
        return getFacade().find(id);
    }

    @Override
    protected void setItems(List<EmpresaOrigen> items) {
        this.items = items;
    }

    @Override
    protected void prepareUpdate() {
        assignParametersToUpdate(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clean() {
        selected = null;
        items = null;
    }

    @FacesConverter(forClass = EmpresaOrigen.class)
    public static class EmpresaOrigenControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmpresaOrigenController controller = (EmpresaOrigenController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "empresaOrigenController");
            return controller.getEmpresaOrigen(getKey(value));
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
            if (object instanceof EmpresaOrigen) {
                EmpresaOrigen o = (EmpresaOrigen) object;
                return getStringKey(o.getIdEmpresaOrigen());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EmpresaOrigen.class.getName()});
                return null;
            }
        }

    }

}
