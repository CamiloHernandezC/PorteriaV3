/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Entities.MovPersonas;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author a.morales
 */
@Stateless
public class MovPersonasFacade extends AbstractFacade<MovPersonas> {

    @PersistenceContext(unitName = "PorteriaV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovPersonasFacade() {
        super(MovPersonas.class);
    }
    
}
