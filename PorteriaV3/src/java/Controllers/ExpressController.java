/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import Entities.PersonasCli;
import Entities.TiposDocumentoCli;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.Result;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author MAURICIO
 */
@Named(value = "expressController")
@ApplicationScoped
public class ExpressController {

    
    //private persona menu;
    private String code;//Store code reader value
    private PersonasCliController personasCliController; 
    /**
     * Creates a new instance of MenuController
     */
    public ExpressController() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
     
    public void completeEntryByCodeReader(){
        if(code==null){
            return;
        }
        String  pageToRedirect = null;
        Result result = findByCodeReader();
        if(result.errorCode== Constants.UNKNOWN_EXCEPTION){//unaccepted text format
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("UNACCEPTED_FORMAT"));
            pageToRedirect = Navigation.PAGE_EXPRESS_ENTRY;
            return;
        }if(result.result == null){
            code = null;
            JsfUtil.showModal("experssDialog");
            return;
        }
        personasCliController= JsfUtil.findBean("personasCliController");
        personasCliController.setSelected((PersonasCli) result.result);
        personasCliController.save();
        
        code = null;
        if(pageToRedirect==null){//This happend when person is blocked
            return;
        }
        JsfUtil.redirectTo(Navigation.PAGE_REDIRECT_TO+pageToRedirect);
    }
    
     private Result findByCodeReader() {
        personasCliController= JsfUtil.findBean("personasCliController");
        if(code.startsWith("C,")){//ID CARD (CEDULA)
            String[] separatedWords = personasCliController.separateWords(code);
            if (separatedWords != null) {
                //Se debe asignar el tipo de documento y número de documento para poder buscar
                personasCliController.getSelected().setTipoDocumento(new TiposDocumentoCli(Constants.DOCUMENT_TYPE_CEDULA));//Se asigna el tipo de documento como cedula
                personasCliController.getSelected().setNumDocumento(separatedWords[0]);//Se le asigna el numero de cedula que fue leido por el lector de cedulas
                //<editor-fold desc="Assign selected to info in id card" defaultstate="collapsed">
                personasCliController.getSelected().setApellido1(separatedWords[1]);
                personasCliController.getSelected().setApellido2(separatedWords[2]);
                personasCliController.getSelected().setNombre1(separatedWords[3]);
                personasCliController.getSelected().setNombre2(separatedWords[4]);
                personasCliController.getSelected().setSexo(separatedWords[5].equals("M"));
                DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                try {                
                    Date birthDate = formatter.parse(separatedWords[6]);
                    personasCliController.getSelected().setFechaNacimiento(birthDate);
                } catch (ParseException ex) {
                    System.out.println(Constants.MESSAGE_DATE_FORMAT_EXCEPTION);
                }
                String RH = "¡".equals(separatedWords[7].substring(1)) ? "+":"-";
                personasCliController.getSelected().setRh(separatedWords[7].substring(0, 1)+RH);
                //</editor-fold>
                return personasCliController.findPersonByDocument();                
            }
        }
        if(code.startsWith("B,")){//BAR CODE
            return personasCliController.findPersonByIdExterno(code.substring(2));
        }
        return new Result(null, Constants.UNKNOWN_EXCEPTION);//This should never happen
    }
     
    public String redirecToPersonFormEntry(){
        ConfigFormCliController configFormCliController = JsfUtil.findBean("configFormCliController");
        configFormCliController.showFieldsPerson();
        return Navigation.PAGE_PERSON_REGISTER;
    }
     
    
     
}
