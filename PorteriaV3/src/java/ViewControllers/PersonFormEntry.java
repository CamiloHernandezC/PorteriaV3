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
@Named(value = "personFormEntry")
@ViewScoped
public class PersonFormEntry implements Serializable{

    //<editor-fold desc="VARIBLES DECLARATION" defaultstate="collapsed">
    boolean disableNoEditableField;
    //</editor-fold>
    
   

    //<editor-fold desc="GETTER AND SETTER" defaultstate="collapsed">
    public boolean isDisableNoEditableField() {
        return disableNoEditableField;
    }
    public void setDisableNoEditableField(boolean disableNoEditableField) {
        this.disableNoEditableField = disableNoEditableField;
    }
    //</editor-fold>
    /**
     * Creates a new instance of CompleteEntry
     */
    public PersonFormEntry() {
    }
    
}
