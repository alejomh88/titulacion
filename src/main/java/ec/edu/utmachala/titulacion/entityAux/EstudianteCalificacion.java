package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EstudianteCalificacion {

	@Id
	private String id;
	private String unidadAcademica;
	private String carrera;
	private String proceso;
	private String estudiante;
	private BigDecimal calExamen;
	private BigDecimal calEscrita;
	private BigDecimal calOral;
	private BigDecimal calTotal;

	public EstudianteCalificacion() {

	}

	public EstudianteCalificacion(String id, String unidadAcademica, String carrera, String proceso, String estudiante,
			BigDecimal calExamen, BigDecimal calEscrita, BigDecimal calOral, BigDecimal calTotal) {
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.proceso = proceso;
		this.estudiante = estudiante;
		this.calExamen = calExamen;
		this.calEscrita = calEscrita;
		this.calOral = calOral;
		this.calTotal = calTotal;
	}

	public BigDecimal getCalEscrita() {
		return calEscrita;
	}

	public BigDecimal getCalExamen() {
		return calExamen;
	}

	public BigDecimal getCalOral() {
		return calOral;
	}

	public BigDecimal getCalTotal() {
		return calTotal;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public String getId() {
		return id;
	}

	public String getProceso() {
		return proceso;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCalEscrita(BigDecimal calEscrita) {
		this.calEscrita = calEscrita;
	}

	public void setCalExamen(BigDecimal calExamen) {
		this.calExamen = calExamen;
	}

	public void setCalOral(BigDecimal calOral) {
		this.calOral = calOral;
	}

	public void setCalTotal(BigDecimal calTotal) {
		this.calTotal = calTotal;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
