/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Facade.AbstractFacade;
import Entities.Paises;
import Entities.Theme;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kmilo
 */
@Stateless
public class ThemeFacade extends AbstractFacade<Theme> {

    @PersistenceContext(unitName = "PorteriaV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ThemeFacade() {
        super(Theme.class);
    }
    
}
