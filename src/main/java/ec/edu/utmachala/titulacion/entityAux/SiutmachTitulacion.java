package ec.edu.utmachala.titulacion.entityAux;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SiutmachTitulacion {

	@Id
	private String id;
	private String titulo;
	private String tipoTitulo;
	private String detalleTipo;
	private String tituloInvestigacion;
	private BigDecimal notaFinal;
	private BigDecimal notaEscrita;
	private BigDecimal notaOral;
	private BigDecimal notaTeorica;
	private Date fechaInicioClase;
	private Date fechaFinClase;
	private String numeroActaIncorporacion;

	public SiutmachTitulacion() {

	}

	public SiutmachTitulacion(String id, String titulo, String tipoTitulo, String detalleTipo,
			String tituloInvestigacion, BigDecimal notaFinal, BigDecimal notaEscrita, BigDecimal notaOral,
			BigDecimal notaTeorica, Date fechaInicioClase, Date fechaFinClase, String numeroActaIncorporacion) {
		this.id = id;
		this.titulo = titulo;
		this.tipoTitulo = tipoTitulo;
		this.detalleTipo = detalleTipo;
		this.tituloInvestigacion = tituloInvestigacion;
		this.notaFinal = notaFinal;
		this.notaEscrita = notaEscrita;
		this.notaOral = notaOral;
		this.notaTeorica = notaTeorica;
		this.fechaInicioClase = fechaInicioClase;
		this.fechaFinClase = fechaFinClase;
		this.numeroActaIncorporacion = numeroActaIncorporacion;
	}

	public String getDetalleTipo() {
		return detalleTipo == null ? "" : detalleTipo;
	}

	public Date getFechaFinClase() {
		return fechaFinClase;
	}

	public Date getFechaInicioClase() {
		return fechaInicioClase;
	}

	public String getId() {
		return id;
	}

	public BigDecimal getNotaEscrita() {
		return notaEscrita;
	}

	public BigDecimal getNotaFinal() {
		return notaFinal;
	}

	public BigDecimal getNotaOral() {
		return notaOral;
	}

	public BigDecimal getNotaTeorica() {
		return notaTeorica;
	}

	public String getNumeroActaIncorporacion() {
		return numeroActaIncorporacion;
	}

	public String getTipoTitulo() {
		return tipoTitulo == null ? "" : tipoTitulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getTituloInvestigacion() {
		return tituloInvestigacion;
	}

	public void setDetalleTipo(String detalleTipo) {
		this.detalleTipo = detalleTipo;
	}

	public void setFechaFinClase(Date fechaFinClase) {
		this.fechaFinClase = fechaFinClase;
	}

	public void setFechaInicioClase(Date fechaInicioClase) {
		this.fechaInicioClase = fechaInicioClase;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNotaEscrita(BigDecimal notaEscrita) {
		this.notaEscrita = notaEscrita;
	}

	public void setNotaFinal(BigDecimal notaFinal) {
		this.notaFinal = notaFinal;
	}

	public void setNotaOral(BigDecimal notaOral) {
		this.notaOral = notaOral;
	}

	public void setNotaTeorica(BigDecimal notaTeorica) {
		this.notaTeorica = notaTeorica;
	}

	public void setNumeroActaIncorporacion(String numeroActaIncorporacion) {
		this.numeroActaIncorporacion = numeroActaIncorporacion;
	}

	public void setTipoTitulo(String tipoTitulo) {
		this.tipoTitulo = tipoTitulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setTituloInvestigacion(String tituloInvestigacion) {
		this.tituloInvestigacion = tituloInvestigacion;
	}

}
