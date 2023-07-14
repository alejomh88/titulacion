package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ActaGraduacion {

	@Id
	private String id;

	private String tipo;
	private String unidadAcademica;
	private String carrera;
	private String numeroActaGraduacion;
	private Date fechaSustentacion;
	private String fechaSustentacion1;
	private String especialista1;
	private String especialista2;
	private String especialista3;
	private String tituloInvestigacion;
	private String titulo;
	private String estudiante;
	private BigDecimal calExamen;
	private BigDecimal calEscrita;
	private BigDecimal calOral;
	private BigDecimal calTotal;
	private String secretarioAbogado;

	public ActaGraduacion() {
	}

	public ActaGraduacion(String id, String tipo, String unidadAcademica, String carrera, String numeroActaGraduacion,
			Date fechaSustentacion, String fechaSustentacion1, String especialista1, String especialista2,
			String especialista3, String tituloInvestigacion, String titulo, String estudiante, BigDecimal calExamen,
			BigDecimal calEscrita, BigDecimal calOral, BigDecimal calTotal, String secretarioAbogado) {
		this.id = id;
		this.tipo = tipo;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.numeroActaGraduacion = numeroActaGraduacion;
		this.fechaSustentacion = fechaSustentacion;
		this.fechaSustentacion1 = fechaSustentacion1;
		this.especialista1 = especialista1;
		this.especialista2 = especialista2;
		this.especialista3 = especialista3;
		this.tituloInvestigacion = tituloInvestigacion;
		this.titulo = titulo;
		this.estudiante = estudiante;
		this.calExamen = calExamen;
		this.calEscrita = calEscrita;
		this.calOral = calOral;
		this.calTotal = calTotal;
		this.secretarioAbogado = secretarioAbogado;
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

	public String getEspecialista1() {
		return especialista1;
	}

	public String getEspecialista2() {
		return especialista2;
	}

	public String getEspecialista3() {
		return especialista3;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public Date getFechaSustentacion() {
		return fechaSustentacion;
	}

	public String getFechaSustentacion1() {
		return fechaSustentacion1;
	}

	public String getId() {
		return id;
	}

	public String getNumeroActaGraduacion() {
		return numeroActaGraduacion;
	}

	public String getSecretarioAbogado() {
		return secretarioAbogado;
	}

	public String getTipo() {
		return tipo;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTituloInvestigacion() {
		return tituloInvestigacion;
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

	public void setEspecialista1(String especialista1) {
		this.especialista1 = especialista1;
	}

	public void setEspecialista2(String especialista2) {
		this.especialista2 = especialista2;
	}

	public void setEspecialista3(String especialista3) {
		this.especialista3 = especialista3;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setFechaSustentacion(Date fechaSustentacion) {
		this.fechaSustentacion = fechaSustentacion;
	}

	public void setFechaSustentacion1(String fechaSustentacion1) {
		this.fechaSustentacion1 = fechaSustentacion1;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNumeroActaGraduacion(String numeroActaGraduacion) {
		this.numeroActaGraduacion = numeroActaGraduacion;
	}

	public void setSecretarioAbogado(String secretarioAbogado) {
		this.secretarioAbogado = secretarioAbogado;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setTituloInvestigacion(String tituloInvestigacion) {
		this.tituloInvestigacion = tituloInvestigacion;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
