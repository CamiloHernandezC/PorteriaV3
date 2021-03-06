/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Entities.ConfigForm;
import Utils.Constants;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kmilo
 */
@Stateless
public class ConfigFormFacade extends AbstractFacade<ConfigForm> {

    @PersistenceContext(unitName = "PorteriaV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigFormFacade() {
        super(ConfigForm.class);
    }

    public List<ConfigForm> showFieldsVehicles() {
        List<ConfigForm> array;
        //String squery = "SELECT c FROM ConfigForm c WHERE c.formulario ='" + Constants.CONFIGVEHICLESFORM + "'";
        StringBuilder sQuery = new StringBuilder("SELECT c FROM ConfigForm c WHERE c.formulario ='");
        sQuery.append(Constants.CONFIGVEHICLESFORM);
        sQuery.append("'");

        return array = (List<ConfigForm>) findByQueryArray(sQuery.toString()).result;
    }
    
}
