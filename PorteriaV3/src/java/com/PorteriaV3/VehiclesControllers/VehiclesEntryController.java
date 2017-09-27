package com.PorteriaV3.VehiclesControllers;

import Entities.Vehiculos;
import Controllers.util.JsfUtil;
import Entities.ConfigForm;
import Entities.VehiculosSucursal;
import PersonControllers.PersonasSucursalController;
import Utils.Constants;
import Utils.Navigation;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;


@Named("vehiclesEntryController")
@ViewScoped
public class VehiclesEntryController implements Serializable {

    @EJB
    private com.PorteriaV3.Facade.VehiculosFacade ejbFacade;
    
    @EJB
    private Facade.ConfigFormFacade ejbFacadeConfig;
    
    @EJB
    private BusinessVehicles ejbBusinessVehicles;
    
    @Inject
    private PersonasSucursalController personaSucursalController;
    
    private List<Vehiculos> items = null;
    private VehiculosSucursal selected;
    
    // <editor-fold desc="Array de config y Booleanos Vehiculos" defaultstate="collapsed">
    private List<ConfigForm> array;
    
    private boolean mostrarColor;
    private boolean mostrarObservacion;
    private boolean mostrarTipoVehiculo;
    private boolean mostrarFoto;
    
    private boolean bloquearCampos =true;
    private boolean bloquearCampoObservacion =true;
    private boolean bloquearPlaca =false;

    public boolean isBloquearPlaca() {
        return bloquearPlaca;
    }

    public void setBloquearPlaca(boolean bloquearPlaca) {
        this.bloquearPlaca = bloquearPlaca;
    }

    public boolean isBloquearCampoObservacion() {
        return bloquearCampoObservacion;
    }

    public void setBloquearCampoObservacion(boolean bloquearCampoObservacion) {
        this.bloquearCampoObservacion = bloquearCampoObservacion;
    }
    
    public boolean isBloquearCampos() {
        return bloquearCampos;
    }

    public void setBloquearCampos(boolean bloquearCampos) {
        this.bloquearCampos = bloquearCampos;
    }
    
    public boolean isMostrarColor() {
        return mostrarColor;
    }

    public void setMostrarColor(boolean mostrarColor) {
        this.mostrarColor = mostrarColor;
    }

    public boolean isMostrarObservacion() {
        return mostrarObservacion;
    }

    public void setMostrarObservacion(boolean mostrarObservacion) {
        this.mostrarObservacion = mostrarObservacion;
    }

    public boolean isMostrarTipoVehiculo() {
        return mostrarTipoVehiculo;
    }

    public void setMostrarTipoVehiculo(boolean mostrarTipoVehiculo) {
        this.mostrarTipoVehiculo = mostrarTipoVehiculo;
    }

    public boolean isMostrarFoto() {
        return mostrarFoto;
    }

    public void setMostrarFoto(boolean mostrarFoto) {
        this.mostrarFoto = mostrarFoto;
    }
    //</editor-fold>

    // <editor-fold desc="Constructor - Getters/Setters" defaultstate="collapsed">
    public VehiclesEntryController() {
    }

    public VehiculosSucursal getSelected() {
        if(selected==null){
            selected=new VehiculosSucursal();
            selected.setVehiculos(new Vehiculos());
        }
        return selected;
    }

    public void setSelected(VehiculosSucursal selected) {
        this.selected = selected;
    }

    public List<Vehiculos> getItems() {
        if (items == null) {
            items = ejbFacade.findAll();
        }
        return items;
    }

    public Vehiculos getVehiculos(java.lang.String id) {
        return ejbFacade.find(id);
    }

    public List<Vehiculos> getItemsAvailableSelectMany() {
        return ejbFacade.findAll();
    }
    //</editor-fold>
    
    public void cancel() {
        clean();
        JsfUtil.cancelToSelectEntry();
    }
    
     public void clean(){
        selected = null;
        items = null;
    }
     
    /**
     * Find by plate the vehicle into the table vehiculo, later show messages de-
     * pend of the value of selected.
     */ 
    public void buscarPorPlaca(){
        Vehiculos vehicle;
        vehicle = ejbBusinessVehicles.searchVehicle(selected.getVehiculos().getPlaca());
        bloquearCampoObservacion=false;
        bloquearPlaca=true;
        if(vehicle == null){
            JsfUtil.addErrorMessage("Error: No se enconotro el vehiculo,por favor pegistrelo");
            bloquearCampos=false;
            return;
        }
        //Assing vehicles to vehicle sucursal.
        selected.setVehiculos(vehicle);
        bloquearCampos=true;
        JsfUtil.addSuccessMessage("Info: Vehiculo registrado, tome los datos necesarios y de click en terminar.");
    }
    
    public String entryVehicle(){
        
        int resutlMethot = ejbBusinessVehicles.entryVehicle(selected,personaSucursalController.getSelected());
        switch(resutlMethot){
            case Constants.OK:
                JsfUtil.addSuccessMessage("Registro exitoso");
                return Navigation.PAGE_SELECT_ENTRY;
            case Constants.NO_SEARCH:
                JsfUtil.addErrorMessage("Recuerde, el primer paso es BUSCAR el vehiculo");
                return Navigation.PAGE_VEHICLE_ENTRY;
            case Constants.UNKNOWN_EXCEPTION:
                JsfUtil.addErrorMessage("No se pudo realizar el registro. Contacte al servicio tecnico");
                return Navigation.PAGE_VEHICLE_ENTRY;
             case Constants.PERSISTANCE_EXCEPTION:
                JsfUtil.addErrorMessage("No se pudo crear el registro de vehiculos. Contacte al servicio tecnico");
                return Navigation.PAGE_VEHICLE_ENTRY;
            case Constants.OBJECT_BLOCK:
                JsfUtil.addErrorMessage("El vehiculo que intenta ingresar esta bloqueado.");
                return Navigation.PAGE_VEHICLE_ENTRY;
        }
        JsfUtil.addErrorMessage("Algo salio mal. Contacte al servicio tecnico");
        return Navigation.PAGE_VEHICLE_ENTRY;
    }
    
    // <editor-fold desc="Load form vehicles" defaultstate="collapsed">
    /**
     * Load the fields to use into the form of the vehiclesEntry.
     */
    @PostConstruct
    public void loadFormVehicles(){
        array = ejbFacadeConfig.showFieldsVehicles();
        if(array.isEmpty() || array == null){
            return;
        }
        for (int i = 0; i < array.size(); i++) {
            ConfigForm modelo = array.get(i);
            String tmp = modelo.getCampo();
            tmp = JsfUtil.quitaEspacios(tmp);
            switch (tmp) {
                case "Color":
                    mostrarColor = modelo.getMostrar();
                    break;
                case "Observacion":
                    mostrarObservacion = modelo.getMostrar();
                    break;
                case "TipoVehiculo":
                    mostrarTipoVehiculo = modelo.getMostrar();
                    break;
                case "Foto":
                    mostrarFoto = modelo.getMostrar();
                    break;
            }
        }
    }
    //</editor-fold>
    
    //<editor-fold desc="Convertidor" defaultstate="collapsed">
    @FacesConverter(forClass = Vehiculos.class)
    public static class VehiculosControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VehiclesEntryController controller = (VehiclesEntryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vehiculosController");
            return controller.getVehiculos(getKey(value));
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
            if (object instanceof Vehiculos) {
                Vehiculos o = (Vehiculos) object;
                return getStringKey(o.getPlaca());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Vehiculos.class.getName()});
                return null;
            }
        }

    }
    //</editor-fold>
    
    public String resetView(){
        return Navigation.PAGE_VEHICLE_ENTRY;
    }
    
    @PreDestroy
    public void destroy(){
        System.out.println("Destruido");
    }
}
