package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DistribucionAsignatura {

	@Id
	private Integer id;
	private String unidadAcademica;
	private String carrera;
	private String unidadCurricular;
	private String asignatura;
	private String docente;
	private String telefono;
	private String email;

	public DistribucionAsignatura() {

	}

	public DistribucionAsignatura(Integer id, String unidadAcademica, String carrera, String unidadCurricular,
			String asignatura, String docente, String telefono, String email) {
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.unidadCurricular = unidadCurricular;
		this.asignatura = asignatura;
		this.docente = docente;
		this.telefono = telefono;
		this.email = email;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getDocente() {
		return docente;
	}

	public String getEmail() {
		return email;
	}

	public Integer getId() {
		return id;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public String getUnidadCurricular() {
		return unidadCurricular;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

	public void setUnidadCurricular(String unidadCurricular) {
		this.unidadCurricular = unidadCurricular;
	}

}
