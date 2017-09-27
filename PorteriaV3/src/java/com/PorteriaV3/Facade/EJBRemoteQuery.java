/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.PorteriaV3.Facade;

import Utils.Result;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author chernandez
 * @param <T> entity type
 */
public interface EJBRemoteQuery<T> {
    
   public abstract EntityManager getEntityManager();
   public T find(Object id);
   public List<T> findAll();
   public Result findByQuery(String squery, boolean maxResult);
   public Result findByQueryArray(String squery);
   public void executeQuery(String squery);
}
