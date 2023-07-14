package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SeleccionReactivo {

	@Id
	private String id;
	private String proceso;
	private String unidadAcademica;
	private String carrera;
	private String estudiante;
	private String contacto;

	public SeleccionReactivo() {
	}

	public SeleccionReactivo(String id, String proceso, String unidadAcademica, String carrera, String estudiante,
			String contacto) {
		super();
		this.id = id;
		this.proceso = proceso;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.estudiante = estudiante;
		this.contacto = contacto;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getContacto() {
		return contacto;
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

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
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
