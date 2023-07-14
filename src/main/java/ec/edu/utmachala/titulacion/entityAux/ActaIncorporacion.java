package ec.edu.utmachala.titulacion.entityAux;

import java.util.List;

public class ActaIncorporacion {

	private String unidadAcademica;
	private String carrera;
	private String numeroActa;
	private String fechaActa;
	private String decano;
	private String secretarioAbogado;
	private List<DetalleActaIncorporacion> listDetalleActaIncorporacion;

	public ActaIncorporacion() {
	}

	public ActaIncorporacion(String unidadAcademica, String carrera, String numeroActa, String fechaActa, String decano,
			String secretarioAbogado, List<DetalleActaIncorporacion> listDetalleActaIncorporacion) {
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.numeroActa = numeroActa;
		this.fechaActa = fechaActa;
		this.decano = decano;
		this.secretarioAbogado = secretarioAbogado;
		this.listDetalleActaIncorporacion = listDetalleActaIncorporacion;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getDecano() {
		return decano;
	}

	public String getFechaActa() {
		return fechaActa;
	}

	public List<DetalleActaIncorporacion> getListDetalleActaIncorporacion() {
		return listDetalleActaIncorporacion;
	}

	public String getNumeroActa() {
		return numeroActa;
	}

	public String getSecretarioAbogado() {
		return secretarioAbogado;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setDecano(String decano) {
		this.decano = decano;
	}

	public void setFechaActa(String fechaActa) {
		this.fechaActa = fechaActa;
	}

	public void setListDetalleActaIncorporacion(List<DetalleActaIncorporacion> listDetalleActaIncorporacion) {
		this.listDetalleActaIncorporacion = listDetalleActaIncorporacion;
	}

	public void setNumeroActa(String numeroActa) {
		this.numeroActa = numeroActa;
	}

	public void setSecretarioAbogado(String secretarioAbogado) {
		this.secretarioAbogado = secretarioAbogado;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
