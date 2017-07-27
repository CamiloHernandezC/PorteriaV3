/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PersonControllers;

import Entities.EmpresaOrigen;
import Querys.Querys;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author a.morales
 */

@Named("empresaOrigenController")
@SessionScoped
public class EmpresaOrigenController extends Controllers.EmpresaOrigenController{

    public EmpresaOrigenController() {
    }
    
    public EmpresaOrigen buscarEmpresaNombre() {
        String sQuery = Querys.EMPRESA_ORIGEN_NAME+ selected.getNombre1()+"'";
        EmpresaOrigen empresa = (EmpresaOrigen) ejbFacade.findByQuery(sQuery, true).result;
        return empresa;
    }

}
