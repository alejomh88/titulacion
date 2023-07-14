package ec.edu.utmachala.titulacion.entityAux;

public class TutoriaAux {

	private String unidadAcademica;
	private String carrera;
	private String proceso;
	private String casoInvestigacion;
	private String opcionTitulacion;
	private String estudiante;
	private String estudiante2;
	private String tutor;
	private String tutoria;
	private String fecha;

	public TutoriaAux() {

	}

	public TutoriaAux(String unidadAcademica, String carrera, String proceso, String casoInvestigacion,
			String opcionTitulacion, String estudiante, String estudiante2, String tutor, String tutoria,
			String fecha) {
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.proceso = proceso;
		this.casoInvestigacion = casoInvestigacion;
		this.opcionTitulacion = opcionTitulacion;
		this.estudiante = estudiante;
		this.estudiante2 = estudiante2;
		this.tutor = tutor;
		this.tutoria = tutoria;
		this.fecha = fecha;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getCasoInvestigacion() {
		return casoInvestigacion;
	}

	public String getEstudiante() {
		return estudiante;
	}

	public String getEstudiante2() {
		return estudiante2;
	}

	public String getFecha() {
		return fecha;
	}

	public String getOpcionTitulacion() {
		return opcionTitulacion;
	}

	public String getProceso() {
		return proceso;
	}

	public String getTutor() {
		return tutor;
	}

	public String getTutoria() {
		return tutoria;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCasoInvestigacion(String casoInvestigacion) {
		this.casoInvestigacion = casoInvestigacion;
	}

	public void setEstudiante(String estudiante) {
		this.estudiante = estudiante;
	}

	public void setEstudiante2(String estudiante2) {
		this.estudiante2 = estudiante2;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public void setOpcionTitulacion(String opcionTitulacion) {
		this.opcionTitulacion = opcionTitulacion;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public void setTutoria(String tutoria) {
		this.tutoria = tutoria;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
