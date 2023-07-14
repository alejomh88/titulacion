package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cuestionario {

	@Id
	private Integer id;
	private Integer pregunta;
	private String carrera;
	private String asignatura;
	private String ejeFormacion;
	private String unidadDidactica;
	private String ejeTematico;
	private String bibliografia;
	private String planteamiento;
	private String imagenPlanteamiento;
	private String literal;
	private String opcion;
	private String imagenOpcion;

	public Cuestionario() {

	}

	public Cuestionario(Integer id, Integer pregunta, String carrera, String asignatura, String ejeFormacion,
			String unidadDidactica, String ejeTematico, String bibliografia, String planteamiento,
			String imagenPlanteamiento, String literal, String opcion, String imagenOpcion) {
		super();
		this.id = id;
		this.pregunta = pregunta;
		this.carrera = carrera;
		this.asignatura = asignatura;
		this.ejeFormacion = ejeFormacion;
		this.unidadDidactica = unidadDidactica;
		this.ejeTematico = ejeTematico;
		this.bibliografia = bibliografia;
		this.planteamiento = planteamiento;
		this.imagenPlanteamiento = imagenPlanteamiento;
		this.literal = literal;
		this.opcion = opcion;
		this.imagenOpcion = imagenOpcion;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public String getBibliografia() {
		return bibliografia;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getEjeFormacion() {
		return ejeFormacion;
	}

	public String getEjeTematico() {
		return ejeTematico;
	}

	public Integer getId() {
		return id;
	}

	public String getImagenOpcion() {
		return imagenOpcion;
	}

	public String getImagenPlanteamiento() {
		return imagenPlanteamiento;
	}

	public String getLiteral() {
		return literal;
	}

	public String getOpcion() {
		return opcion;
	}

	public String getPlanteamiento() {
		return planteamiento;
	}

	public Integer getPregunta() {
		return pregunta;
	}

	public String getUnidadDidactica() {
		return unidadDidactica;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setBibliografia(String bibliografia) {
		this.bibliografia = bibliografia;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setEjeFormacion(String ejeFormacion) {
		this.ejeFormacion = ejeFormacion;
	}

	public void setEjeTematico(String ejeTematico) {
		this.ejeTematico = ejeTematico;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImagenOpcion(String imagenOpcion) {
		this.imagenOpcion = imagenOpcion;
	}

	public void setImagenPlanteamiento(String imagenPlanteamiento) {
		this.imagenPlanteamiento = imagenPlanteamiento;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	public void setPlanteamiento(String planteamiento) {
		this.planteamiento = planteamiento;
	}

	public void setPregunta(Integer pregunta) {
		this.pregunta = pregunta;
	}

	public void setUnidadDidactica(String unidadDidactica) {
		this.unidadDidactica = unidadDidactica;
	}

}
