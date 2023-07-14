package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LineaInvestigacionSeminarioTutor {

	@Id
	private Integer id;
	private String unidadAcademica;
	private String carrera;
	private String proceso;
	private String lineaInvestigacion;
	private String seminario;
	private String tutor;

	public LineaInvestigacionSeminarioTutor() {
	}

	public LineaInvestigacionSeminarioTutor(Integer id, String unidadAcademica, String carrera, String proceso,
			String lineaInvestigacion, String seminario, String tutor) {
		super();
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.proceso = proceso;
		this.lineaInvestigacion = lineaInvestigacion;
		this.seminario = seminario;
		this.tutor = tutor;
	}

	public String getCarrera() {
		return carrera;
	}

	public Integer getId() {
		return id;
	}

	public String getLineaInvestigacion() {
		return lineaInvestigacion;
	}

	public String getProceso() {
		return proceso;
	}

	public String getSeminario() {
		return seminario;
	}

	public String getTutor() {
		return tutor;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLineaInvestigacion(String lineaInvestigacion) {
		this.lineaInvestigacion = lineaInvestigacion;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setSeminario(String seminario) {
		this.seminario = seminario;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
