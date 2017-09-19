/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.PorteriaV3.VehiclesControllers;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author amorales
 */
@Stateless
public class BusinessVehicles{
    
    @Inject
    private VehiculosController vehiculosController;
    
    public boolean buscar(){
        return true;
    }
    
}
