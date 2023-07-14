package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Participante {

	@Id
	private String cedulaEstudiante;
	private String proceso;
	private String tipoExamen;
	private String unidadAcademica;
	private String carrera;
	private String nombreEstudiante;
	private Integer calificacion;

	public Participante() {

	}

	public Participante(String cedulaEstudiante, String proceso, String tipoExamen, String unidadAcademica,
			String carrera, String nombreEstudiante, Integer calificacion) {
		super();
		this.cedulaEstudiante = cedulaEstudiante;
		this.proceso = proceso;
		this.tipoExamen = tipoExamen;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.nombreEstudiante = nombreEstudiante;
		this.calificacion = calificacion;
	}

	public Integer getCalificacion() {
		return calificacion;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getCedulaEstudiante() {
		return cedulaEstudiante;
	}

	public String getNombreEstudiante() {
		return nombreEstudiante;
	}

	public String getProceso() {
		return proceso;
	}

	public String getTipoExamen() {
		return tipoExamen;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCalificacion(Integer calificacion) {
		this.calificacion = calificacion;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCedulaEstudiante(String cedulaEstudiante) {
		this.cedulaEstudiante = cedulaEstudiante;
	}

	public void setNombreEstudiante(String nombreEstudiante) {
		this.nombreEstudiante = nombreEstudiante;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setTipoExamen(String tipoExamen) {
		this.tipoExamen = tipoExamen;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
