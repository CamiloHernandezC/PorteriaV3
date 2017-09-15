package PersonControllers;

import Controllers.AbstractPersistenceController;
import Controllers.util.JsfUtil;
import Entities.MovPersonas;
import Entities.PersonasSucursal;
import Entities.Porterias;
import Facade.MovPersonasFacade;
import Querys.Querys;
import Utils.Constants;
import Utils.Result;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("movPersonasController")
@SessionScoped
public class MovPersonasController extends AbstractPersistenceController<MovPersonas> {

    @EJB
    private Facade.MovPersonasFacade ejbFacade;
    private List<MovPersonas> items = null;
    private List<MovPersonas> ultimosMovimientos = null;
    private List<MovPersonas> movimientosDiarios = null;
    private MovPersonas selected;
    private Date fechaMov = null;

    public MovPersonasController() {
    }

    @Override
    public MovPersonas getSelected() {
        return selected;
    }

    @Override
    public void setSelected(MovPersonas selected) {
        this.selected = selected;
    }

    @Override
    protected void setEmbeddableKeys() {
    }

    @Override
    protected void initializeEmbeddableKey() {
    }

    @Override
    protected MovPersonasFacade getFacade() {
        return ejbFacade;
    }

    @Override
    public void prepareCreate() {
        calculatePrimaryKey(Querys.MOV_PERSONA_CLI_PRIMARY_KEY);
        prepareUpdate();
    }

    public List<MovPersonas> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public MovPersonas getMovPersonas(java.lang.Integer id) {
        return getFacade().find(id);
    }


    public List<MovPersonas> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MovPersonas> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public List<MovPersonas> getUltimosMovimientos() {
        return ultimosMovimientos;
    }

    public void setUltimosMovimientos(List<MovPersonas> ultimosMovimientos) {
        this.ultimosMovimientos = ultimosMovimientos;
    }

    public Date getFechaMov() {
        return fechaMov;
    }

    public void setFechaMov(Date fechaMov) {
        this.fechaMov = fechaMov;
    }
    
    public List<MovPersonas> getMovimientosDiarios() {
        return movimientosDiarios;
    }
    
    public void buscarMovFecha() {
        if(fechaMov==null){
            java.util.Date fechaActual = new java.util.Date();
            java.sql.Date hoy = new java.sql.Date(fechaActual.getTime()); 
            String squery = Querys.MOV_PERSONA_CLI_ALL+"Where a.momentoEntrada LIKE '"+hoy+"%' ORDER BY a.idMovPersona DESC";
            movimientosDiarios = (List<MovPersonas>) ejbFacade.findByQueryArray(squery).result;
        }else{
            java.sql.Date fecha = new java.sql.Date(fechaMov.getTime()); 
            String squery = Querys.MOV_PERSONA_CLI_ALL+"Where a.momentoEntrada LIKE '"+fecha+"%' ORDER BY a.idMovPersona DESC";
            movimientosDiarios = (List<MovPersonas>) ejbFacade.findByQueryArray(squery).result;
        }
        if(movimientosDiarios==null){
            JsfUtil.addErrorMessage("No hay movimientos para mostrar");
        }
        
    }

    public void setMovimientosDiarios(List<MovPersonas> movimientosDiarios) {
        this.movimientosDiarios = movimientosDiarios;
    }
    
    public void recordEntryMovement(int persistAction) {
        PersonasSucursalController personasSucursalCli = JsfUtil.findBean("personasSucursalController");
        PersonasSucursal specificPerson = personasSucursalCli.getSelected();
        if(persistAction == Constants.UPDATE){
            if(verifyEntry(specificPerson)){
                recordForcedOut();
            }
        }
        recordEntry(specificPerson);
    }
    
    public void recordEntry(PersonasSucursal specificPerson){
        prepareEntityToCreate(specificPerson);
        create();
    }
    
    public void recordExitMovement() {
        PersonasSucursalController personasSucursal = JsfUtil.findBean("personasSucursalController");
        PersonasSucursal specificPerson = personasSucursal.getSelected();
        if(!verifyEntry(specificPerson)){
            recordForcedEntry(specificPerson);
            return;
        }
        selected = items.get(0);
        recordOut();
    }
    
    public void recordOut(){
        Date actualDate = new Date();
        selected.setFechaSalida(actualDate);
        selected.setHoraSalida(actualDate);
        selected.setMomentoSalida(actualDate);
        update();
    }
    
    private void prepareEntityToCreate(PersonasSucursal specificPerson) {
        
        Date actualDate = new Date();
        selected = new MovPersonas();
        selected.setPersonasSucursal(specificPerson);
        selected.setArea(specificPerson.getArea());
        selected.setMomentoEntrada(actualDate);
        selected.setFechaEntrada(actualDate);
        selected.setHoraEntrada(actualDate);
        //TODO PERSONA QUE AUTORIZA
        selected.setSalidaForzosa(false);
    }

    public boolean verifyEntry(int idPersona){
        String squery = Querys.MOV_PERSONA_CLI_ALL+"WHERE"+Querys.MOV_PERSONA_CLI_PERSONA+idPersona
                +"' AND"+Querys.MOV_PERSONA_CLI_MOMENTO_SALIDA_NULL;
        Result result = ejbFacade.findByQueryArray(squery);
        if(result.errorCode == Constants.NO_RESULT_EXCEPTION){
            return false;
        }
        items = (List<MovPersonas>) result.result;
        return true;
    }
    
    public boolean verifyEntry(PersonasSucursal specificPerson){
        return verifyEntry(specificPerson.getPersonas().getIdPersona());
    }
    
    public void recordForcedEntry(PersonasSucursal specificPerson){
        prepareEntityToCreate(specificPerson);
        selected.setMomentoSalida(selected.getMomentoEntrada());
        selected.setFechaSalida(selected.getFechaEntrada());
        selected.setHoraSalida(selected.getHoraEntrada());
        selected.setIngresoForzado(true);
        create();
        selected =  null;
    }
    
    public void recordForcedOut(){
        for(MovPersonas mov: items){
            mov.setFechaSalida(mov.getFechaEntrada());
            mov.setHoraSalida(mov.getHoraEntrada());
            mov.setMomentoSalida(mov.getMomentoEntrada());
            mov.setSalidaForzosa(true);
            selected = mov;
            update();
        }
        selected =  null;
    }

    @Override
    protected void setItems(List<MovPersonas> items) {
        this.items = items;
    }

    @Override
    protected void prepareUpdate() {
        selected.setUsuario(JsfUtil.getSessionUser().getPersona());//TODO ASSIGN REAL USER
        selected.setFecha(new Date());
        //TODO Asign real Porteria.
        selected.setPorteria(new Porterias(1));
    }
    
    public void lastEntry(int idPersona) {
        String squery = Querys.MOV_PERSONA_CLI_ALL+"WHERE"+Querys.MOV_PERSONA_CLI_PERSONA+idPersona+"'"+Querys.MOV_PERSONA_CLI_ORDER_BY_ID;
        selected = (MovPersonas) ejbFacade.findByQuery(squery, true).result;
    }

    public Result loadEntry(String idPersona) {
        String squery = Querys.MOV_PERSONA_CLI_ALL+"WHERE"+Querys.MOV_PERSONA_CLI_PERSONA+idPersona+
                "' AND"+Querys.MOV_PERSONA_CLI_MOMENTO_SALIDA_NULL+Querys.MOV_PERSONA_CLI_ORDER_BY_ID;
        Result result = ejbFacade.findByQuery(squery,true);
        if(result.errorCode == Constants.OK){
            selected = (MovPersonas) result.result;
        }
        return result;
    }

    @Override
    public void clean() {
        selected = null;
        items = null;
    }

    public void findLastMovements() {
         String squery = Querys.MOV_PERSONA_CLI_ALL+" ORDER BY a.idMovPersona DESC";
         ultimosMovimientos = (List<MovPersonas>) ejbFacade.findByQueryArray(squery,15).result;
    }

    @FacesConverter(forClass = MovPersonas.class)
    public static class MovPersonasControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MovPersonasController controller = (MovPersonasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "movPersonasController");
            return controller.getMovPersonas(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof MovPersonas) {
                MovPersonas o = (MovPersonas) object;
                return getStringKey(o.getIdMovPersona());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MovPersonas.class.getName()});
                return null;
            }
        }

    }

}
