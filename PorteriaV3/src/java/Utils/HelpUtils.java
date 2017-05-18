/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Controllers.util.JsfUtil;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author a.morales
 */

@Named("helpUtils")
@SessionScoped
public class HelpUtils implements Serializable{
    
    public void showModalHelpConfiguration(){
        JsfUtil.showModal("dlgHelpTheme");
    }
    
}
