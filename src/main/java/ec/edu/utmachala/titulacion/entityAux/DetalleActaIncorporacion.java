package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DetalleActaIncorporacion {

	@Id
	private String cedula;
	private String estudiante;
	private String titulo;

	public DetalleActaIncorporacion() {
	}

	public DetalleActaIncorporacion(String cedula, String estudiante, String titulo) {
		this.cedula = cedula;
		this.estudiante = estudiante;
		this.titulo = titulo;
	}

	public String getCedula() {
		return cedula;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
