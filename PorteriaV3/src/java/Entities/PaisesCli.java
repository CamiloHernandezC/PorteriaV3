/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author amorales
 */
@Entity
@Table(name = "Paises_Cli")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PaisesCli.findAll", query = "SELECT p FROM PaisesCli p"),
    @NamedQuery(name = "PaisesCli.findByIdPais", query = "SELECT p FROM PaisesCli p WHERE p.idPais = :idPais"),
    @NamedQuery(name = "PaisesCli.findByDescripcion", query = "SELECT p FROM PaisesCli p WHERE p.descripcion = :descripcion")})
public class PaisesCli implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "Id_Pais")
    private String idPais;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "Descripcion")
    private String descripcion;
    @OneToMany(mappedBy = "idPais")
    private List<VehiculosCli> vehiculosCliList;
    @OneToMany(mappedBy = "idPais")
    private List<EmpresaOrigenCli> empresaOrigenCliList;
    @OneToMany(mappedBy = "pais")
    private List<SucursalesCli> sucursalesCliList;
    @OneToMany(mappedBy = "idPais")
    private List<PersonasCli> personasCliList;
    @OneToMany(mappedBy = "idPais")
    private List<ObjetosCli> objetosCliList;

    public PaisesCli() {
    }

    public PaisesCli(String idPais) {
        this.idPais = idPais;
    }

    public PaisesCli(String idPais, String descripcion) {
        this.idPais = idPais;
        this.descripcion = descripcion;
    }

    public String getIdPais() {
        return idPais;
    }

    public void setIdPais(String idPais) {
        this.idPais = idPais;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlTransient
    public List<VehiculosCli> getVehiculosCliList() {
        return vehiculosCliList;
    }

    public void setVehiculosCliList(List<VehiculosCli> vehiculosCliList) {
        this.vehiculosCliList = vehiculosCliList;
    }

    @XmlTransient
    public List<EmpresaOrigenCli> getEmpresaOrigenCliList() {
        return empresaOrigenCliList;
    }

    public void setEmpresaOrigenCliList(List<EmpresaOrigenCli> empresaOrigenCliList) {
        this.empresaOrigenCliList = empresaOrigenCliList;
    }

    @XmlTransient
    public List<SucursalesCli> getSucursalesCliList() {
        return sucursalesCliList;
    }

    public void setSucursalesCliList(List<SucursalesCli> sucursalesCliList) {
        this.sucursalesCliList = sucursalesCliList;
    }

    @XmlTransient
    public List<PersonasCli> getPersonasCliList() {
        return personasCliList;
    }

    public void setPersonasCliList(List<PersonasCli> personasCliList) {
        this.personasCliList = personasCliList;
    }

    @XmlTransient
    public List<ObjetosCli> getObjetosCliList() {
        return objetosCliList;
    }

    public void setObjetosCliList(List<ObjetosCli> objetosCliList) {
        this.objetosCliList = objetosCliList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPais != null ? idPais.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PaisesCli)) {
            return false;
        }
        PaisesCli other = (PaisesCli) object;
        if ((this.idPais == null && other.idPais != null) || (this.idPais != null && !this.idPais.equals(other.idPais))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.PaisesCli[ idPais=" + idPais + " ]";
    }
    
}
