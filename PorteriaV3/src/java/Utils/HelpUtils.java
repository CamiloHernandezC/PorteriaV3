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
    
    private String Nombre;
    private String correoContacto;
    private String Descripcion;

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getCorreoContacto() {
        return correoContacto;
    }

    public void setCorreoContacto(String correoContacto) {
        this.correoContacto = correoContacto;
    }
    
    public void showModalHelpConfiguration(){
        JsfUtil.showModal("dlgHelpTheme");
    }
    
    public void showModalHelpContact(){
        JsfUtil.showModal("dlgHelpContact");
    }
    
    public void enviarCorreoContacto(){
        Email.crearEmail(Nombre, correoContacto, Descripcion);
        JsfUtil.addSuccessMessage("Correo electornico enviado al servicio tecnico");
        JsfUtil.exitModal("diagContact");
    }
    
}
