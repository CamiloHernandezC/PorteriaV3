/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Querys;

/**
 *
 * @author MAURICIO
 */
public class Querys {
    //<editor-fold desc="PERSONAS CLI QUERY" defaultstate="collapsed">
    public static final String PERSONA_CLI_ALL= "SELECT a FROM PersonasCli a ";
    public static final String PERSONA_CLI_DOC_TYPE= " a.tipoDocumento.tipodocumento = '";
    public static final String PERSONA_CLI_ID_EXTERNO =" a.idExterno = '";
    public static final String PERSONA_CLI_DOC_NUMBER= " a.numDocumento = '";
    public static final String PERSONA_CLI_SUCURSAL= " a.idSucursal.idSucursal = '";
    public static final String PERSONA_CLI_ESTADO= " a.idEstado.idEstado = '";
    public static final String PERSONA_CLI_ESTADO_N= " a.idEstado.idEstado != '";
    public static final String PERSONA_CLI_PRIMARY_KEY= "SELECT a FROM PersonasCli a ORDER BY a.idPersona DESC";
    //</editor-fold>
}
