package PersonControllers;

import Controllers.util.JsfUtil;
import Entities.EmpresaOrigenCli;
import Entities.MovPersonasCli;
import Entities.PersonasCli;
import Entities.PersonasSucursalCli;
import PersonControllers.Views.PersonFormEntry;
import Utils.BundleUtils;
import Utils.Constants;
import Utils.Navigation;
import Utils.Result;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;

@Named("manualController")
@SessionScoped
public class manualController extends PersonasCliController {

    private String otherOriginEnterpriseName;

    public String getOtherOriginEnterpriseName() {
        return otherOriginEnterpriseName;
    }

    public void setOtherOriginEnterpriseName(String otherOriginEnterpriseName) {
        this.otherOriginEnterpriseName = otherOriginEnterpriseName;
    }
    
    public void valueChangeHandlerOriginEnterprise(ValueChangeEvent changeEvent) {
        PersonFormEntry personFormEntry = JsfUtil.findBean("personFormEntry");
        EmpresaOrigenCli selectedOriginEnterprise = (EmpresaOrigenCli) changeEvent.getNewValue();
        if (selectedOriginEnterprise.getIdEmorigen() != null && selectedOriginEnterprise.getIdEmorigen().equals(Constants.ORIGIN_ENTERPRISE_OTHER)) {
            personFormEntry.setDisableOtherEnterprise(false);
            return;
        }
        personFormEntry.setDisableOtherEnterprise(true);
    }
    
    /**
     * Method used to search person and redirect to register form, verifying if
     * person is blocked
     *
     * @return page to redirect
     */
    public String manualEntry() {
        //Carga los campos del formulario.
        configFormCliController.showFieldsPerson();
        Result result = findPersonByDocument();
        if (result.errorCode == Constants.NO_RESULT_EXCEPTION) {
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("Please_Register"));
            selected = new PersonasCli();
            disableNoEditableFields(false);
        }else{
            selected = (PersonasCli) result.result;
            if (verifyBlockedPerson()) {
                return null;
            }
            verifyDatesPerson();
            disableNoEditableFields(true);
        }
        return Navigation.PAGE_PERSON_REGISTER;
    }
    
    public String manualExit(){
        
        Result result = findPersonByDocument();
        if(result.errorCode==Constants.NO_RESULT_EXCEPTION){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("No_Entry_Register"));
            return Navigation.PAGE_COMPLETE_EXIT;
        }
        selected = (PersonasCli) result.result;
        if (verifyBlockedPerson()) {
                return null;
        }
        result = movPersonasCliController.loadEntry(selected.getIdPersona());
        if(result.errorCode!=Constants.OK){
            JsfUtil.addErrorMessage(BundleUtils.getBundleProperty("No_Entry_Register"));
            return Navigation.PAGE_COMPLETE_EXIT;
        } 
        MovPersonasCli movPersona = (MovPersonasCli) result.result;
        result = personasSucursalCliController.findSpecificPerson(movPersona.getIdPersona().getIdPersona() ,movPersona.getIdSucursal().getIdSucursal());
        personasSucursalCliController.setSelected((PersonasSucursalCli) result.result);
        if(personasSucursalCliController.verifyBlockSpecificPerson()){
            return null;
        }
        return Navigation.PAGE_PERSON_EXIT;
    }
    
    public String save() {
        boolean existPerson = selected.getIdPersona()!=null;
        if (existPerson) {
            //Verifica existencia de registro en la sucursal determinada por el formulario.
            Result result = personasSucursalCliController.findSpecificPerson(selected.getIdPersona());
            if(result.errorCode == Constants.NO_RESULT_EXCEPTION){
                personasSucursalCliController.create();
                movPersonasCliController.recordEntryMovement(Constants.CREATE);
            }else{
                PersonasSucursalCli specificPerson = (PersonasSucursalCli) result.result;
                specificPerson.setArea(personasSucursalCliController.getSelected().getArea());
                personasSucursalCliController.setSelected(specificPerson);
                if(personasSucursalCliController.verifyBlockSpecificPerson()){
                    return null;
                }
                personasSucursalCliController.update();
                movPersonasCliController.recordEntryMovement(Constants.UPDATE);
            }
            //Actualiza la personaCli
            update();
        }else{
            if(findPersonByDocument().errorCode!=Constants.NO_RESULT_EXCEPTION){//This person exist, so maybe was an error in identification number
                //We doesn't assign encoutered person. This way data is not lost
                JsfUtil.addErrorMessage("ESTA PERSONA YA ESTA REGISTRADA");//TODO CREATE BUNDLE PROPERTY
                return Navigation.PAGE_PERSON_REGISTER;
            }
            create();
            personasSucursalCliController.create();
            movPersonasCliController.recordEntryMovement(Constants.CREATE);
            //TODO METODO COMUN PARA LOS DOS
        }
        return Navigation.PAGE_SELECT_ENTRY;
    }
    
    public String exit(){
        movPersonasCliController.recordOut();
        return Navigation.PAGE_INDEX;
    }
}
