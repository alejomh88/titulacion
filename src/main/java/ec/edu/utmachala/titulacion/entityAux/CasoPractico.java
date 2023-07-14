package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CasoPractico {

	@Id
	private Integer id;
	private String unidadAcademica;
	private String carrera;
	private String docente;
	private String asignatura;
	private String estudiante;
	private String casoInvestigacion;
	private String archivoAdjunto;

	public CasoPractico() {

	}

	public CasoPractico(Integer id, String unidadAcademica, String carrera, String docente, String asignatura,
			String estudiante, String casoInvestigacion, String archivoAdjunto) {
		super();
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.docente = docente;
		this.asignatura = asignatura;
		this.estudiante = estudiante;
		this.casoInvestigacion = casoInvestigacion;
		this.archivoAdjunto = archivoAdjunto;
	}

	public String getArchivoAdjunto() {
		return archivoAdjunto;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getCasoInvestigacion() {
		return casoInvestigacion;
	}

	public String getDocente() {
		return docente;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public Integer getId() {
		return id;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setArchivoAdjunto(String archivoAdjunto) {
		this.archivoAdjunto = archivoAdjunto;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCasoInvestigacion(String casoInvestigacion) {
		this.casoInvestigacion = casoInvestigacion;
	}

	public void setDocente(String docente) {
		this.docente = docente;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
