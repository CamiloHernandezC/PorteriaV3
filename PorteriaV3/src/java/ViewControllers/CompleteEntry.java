/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ViewControllers;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author MAURICIO
 */
@Named(value = "completeEntry")
@ViewScoped
public class CompleteEntry implements Serializable{

    //<editor-fold desc="VARIBLES DECLARATION" defaultstate="collapsed">
    boolean selectEntryMethod;
    //</editor-fold>
    
    //<editor-fold desc="GETTER AND SETTER" defaultstate="collapsed">
    public boolean isSelectEntryMethod() {
        return selectEntryMethod;
    }

    public void setSelectEntryMethod(boolean selectEntryMethod) {
        this.selectEntryMethod = selectEntryMethod;
    }
    //</editor-fold>

    /**
     * Creates a new instance of CompleteEntry
     */
    public CompleteEntry() {
    }
    
}
