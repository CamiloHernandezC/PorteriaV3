/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewControllers;

import Controllers.util.JsfUtil;
import PersonControllers.ExpressController;
import PersonControllers.MovPersonasController;
import PersonControllers.PersonasController;
import PersonControllers.PersonasSucursalController;
import PersonControllers.ManualController;
import Utils.HelpUtils;
import Utils.Navigation;
import java.io.Serializable;
import java.util.Date;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author MAURICIO
 */
@Named(value = "generalViewControl")
@ViewScoped
public class GeneralViewControl implements Serializable{

    //<editor-fold desc="VARIBLES DECLARATION" defaultstate="collapsed">
    private Date actualDate;
    //</editor-fold>
       
    //<editor-fold desc="GETTER AND SETTER" defaultstate="collapsed">
    public Date getActualDate() {
        actualDate = new Date();
        return actualDate;
    }
    //</editor-fold>
    
    /**
     * Creates a new instance of CompleteEntry
     */
    public GeneralViewControl() {
    }
    
    //<editor-fold desc="NAVEGACION INDEX" defaultstate="collapsed">
    public String irConfiguracion(){
        return Navigation.PAGE_CONFIGURATION;
    }
    
    public void irContactanos(){
        HelpUtils helpUtils = JsfUtil.findBean("helpUtils");
        helpUtils.limpiar();
        JsfUtil.showModal("diagContact");
    }
    
    public void irVerTodosMovimientos(){
        MovPersonasController movPersonasController = JsfUtil.findBean("movPersonasController");
        movPersonasController.buscarMovFecha();
        JsfUtil.showModal("diagMovCompletos");
    }
    
    public String irIndex(){
        return Navigation.PAGE_INDEX;
    }
    
    public String irIngresoManual(){
        cleanPersonControllers();
        return Navigation.PAGE_COMPLETE_ENTRY;
    }
    
    public String irIngresoExpress(){
        cleanPersonControllers();
        return Navigation.PAGE_EXPRESS_ENTRY;
    }
    
      public String irSalidaManual(){
          cleanPersonControllers();
        return Navigation.PAGE_COMPLETE_EXIT;
    }
    
    public String irSalidaExpress(){
        cleanPersonControllers();
        return Navigation.PAGE_EXPRESS_EXIT;
    }
    
    public String irCrearVehiculo(){
        return Navigation.PAGE_VEHICLE_ENTRY;
    }
    //</editor-fold>
    public void cleanPersonControllers(){
        PersonasController personasController = JsfUtil.findBean("personasController");
        PersonasSucursalController personasSucursalController =  JsfUtil.findBean("personasSucursalController");
        MovPersonasController movPersonasController = JsfUtil.findBean("movPersonasController");
        ManualController manualController = JsfUtil.findBean("manualController");
        ExpressController expressController = JsfUtil.findBean("expressController");
        
        if(expressController!=null){
            expressController.clean();
        }
        if(manualController!=null){
            manualController.clean();
        }
        if(personasController!=null){
            personasController.clean();
        }
        if(personasSucursalController!=null){
            personasSucursalController.clean();
        }
        if(movPersonasController!=null){
            movPersonasController.clean();
        }
    }
}
