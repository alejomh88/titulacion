package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CumplimientoReactivoPractico {

	@Id
	private String id;
	private String unidadAcademica;
	private String carrera;
	private String docente;
	private String contacto;
	private String asignatura;
	private Integer reactivosPracticos;

	public CumplimientoReactivoPractico() {

	}

	public CumplimientoReactivoPractico(String id, String unidadAcademica, String carrera, String docente,
			String contacto, String asignatura, Integer reactivosPracticos) {
		super();
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.docente = docente;
		this.contacto = contacto;
		this.asignatura = asignatura;
		this.reactivosPracticos = reactivosPracticos;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getContacto() {
		return contacto;
	}

	public String getDocente() {
		return docente;
	}

	public String getId() {
		return id;
	}

	public Integer getReactivosPracticos() {
		return reactivosPracticos;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setReactivosPracticos(Integer reactivosPracticos) {
		this.reactivosPracticos = reactivosPracticos;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
