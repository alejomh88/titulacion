package ec.edu.utmachala.titulacion.entityAux;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Asistencia {

	@Id
	private String cedula;
	private String proceso;
	private Integer grupo;
	private String tipo;
	private Timestamp fechaInicio;
	private Timestamp fechaCierre;
	private String unidadAcademica;
	private String nombreUnidadAcademica;
	private String bloque;
	private String nombreLaboratorio;
	private String apellidoNombre;
	private String carrera;
	private String supervisor;
	private String encargadoLaboratorio;
	private String responsable1;
	private String responsable2;

	public Asistencia() {

	}

	public Asistencia(String cedula, String proceso, Integer grupo, String tipo, Timestamp fechaInicio,
			Timestamp fechaCierre, String unidadAcademica, String nombreUnidadAcademica, String bloque,
			String nombreLaboratorio, String apellidoNombre, String carrera, String supervisor,
			String encargadoLaboratorio, String responsable1, String responsable2) {
		super();
		this.cedula = cedula;
		this.proceso = proceso;
		this.grupo = grupo;
		this.tipo = tipo;
		this.fechaInicio = fechaInicio;
		this.fechaCierre = fechaCierre;
		this.unidadAcademica = unidadAcademica;
		this.nombreUnidadAcademica = nombreUnidadAcademica;
		this.bloque = bloque;
		this.nombreLaboratorio = nombreLaboratorio;
		this.apellidoNombre = apellidoNombre;
		this.carrera = carrera;
		this.supervisor = supervisor;
		this.encargadoLaboratorio = encargadoLaboratorio;
		this.responsable1 = responsable1;
		this.responsable2 = responsable2;
	}

	public String getApellidoNombre() {
		return apellidoNombre;
	}

	public String getBloque() {
		return bloque;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getCedula() {
		return cedula;
	}

	public String getEncargadoLaboratorio() {
		return encargadoLaboratorio;
	}

	public Timestamp getFechaCierre() {
		return fechaCierre;
	}

	public Timestamp getFechaInicio() {
		return fechaInicio;
	}

	public Integer getGrupo() {
		return grupo;
	}

	public String getNombreLaboratorio() {
		return nombreLaboratorio;
	}

	public String getNombreUnidadAcademica() {
		return nombreUnidadAcademica;
	}

	public String getProceso() {
		return proceso;
	}

	public String getResponsable1() {
		return responsable1;
	}

	public String getResponsable2() {
		return responsable2;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public String getTipo() {
		return tipo;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setApellidoNombre(String apellidoNombre) {
		this.apellidoNombre = apellidoNombre;
	}

	public void setBloque(String bloque) {
		this.bloque = bloque;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public void setEncargadoLaboratorio(String encargadoLaboratorio) {
		this.encargadoLaboratorio = encargadoLaboratorio;
	}

	public void setFechaCierre(Timestamp fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public void setFechaInicio(Timestamp fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}

	public void setNombreLaboratorio(String nombreLaboratorio) {
		this.nombreLaboratorio = nombreLaboratorio;
	}

	public void setNombreUnidadAcademica(String nombreUnidadAcademica) {
		this.nombreUnidadAcademica = nombreUnidadAcademica;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setResponsable1(String responsable1) {
		this.responsable1 = responsable1;
	}

	public void setResponsable2(String responsable2) {
		this.responsable2 = responsable2;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
