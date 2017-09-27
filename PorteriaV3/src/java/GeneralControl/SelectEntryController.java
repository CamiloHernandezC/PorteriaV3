/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GeneralControl;

import Utils.Navigation;
import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author amorales
 */
@Named(value = "selecEntryControl")
@ViewScoped
public class SelectEntryController implements Serializable{
    
    public String irCrearVehiculo(){
        return Navigation.PAGE_VEHICLE_ENTRY;
    }
    
}
