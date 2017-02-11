/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Facade;

import Entities.PersonasCli;
import Querys.Querys;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author MAURICIO
 */
@Stateless
public class PersonasCliFacade extends AbstractFacade<PersonasCli> {

    @PersistenceContext(unitName = "PorteriaV3PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PersonasCliFacade() {
        super(PersonasCli.class);
    }

    @Override
    public void create(PersonasCli entity) {
        entity.setIdPersona(calculatePimaryKey());
        super.create(entity); //To change body of generated methods, choose Tools | Templates.
    }

    private String calculatePimaryKey() {
        PersonasCli lastPerson = (PersonasCli) findByQuery(Querys.PERSONA_CLI_PRIMARY_KEY, true).result;
        Long lastPrimaryKey = Long.valueOf(lastPerson.getIdPersona());
        return String.valueOf(lastPrimaryKey+1L);
    }
    
    
}
