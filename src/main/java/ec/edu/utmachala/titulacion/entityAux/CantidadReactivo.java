package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CantidadReactivo {

	@Id
	private Integer id;
	private String unidadAcademica;
	private String carrera;
	private Integer preguntasEjeHumano;
	private Integer preguntasEjeBasico;
	private Integer preguntasEjeProfesional;

	public CantidadReactivo() {

	}

	public CantidadReactivo(Integer id, String unidadAcademica, String carrera, Integer preguntasEjeHumano,
			Integer preguntasEjeBasico, Integer preguntasEjeProfesional) {
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.preguntasEjeHumano = preguntasEjeHumano;
		this.preguntasEjeBasico = preguntasEjeBasico;
		this.preguntasEjeProfesional = preguntasEjeProfesional;
	}

	public String getCarrera() {
		return carrera;
	}

	public Integer getId() {
		return id;
	}

	public Integer getPreguntasEjeBasico() {
		return preguntasEjeBasico;
	}

	public Integer getPreguntasEjeHumano() {
		return preguntasEjeHumano;
	}

	public Integer getPreguntasEjeProfesional() {
		return preguntasEjeProfesional;
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

	public void setPreguntasEjeBasico(Integer preguntasEjeBasico) {
		this.preguntasEjeBasico = preguntasEjeBasico;
	}

	public void setPreguntasEjeHumano(Integer preguntasEjeHumano) {
		this.preguntasEjeHumano = preguntasEjeHumano;
	}

	public void setPreguntasEjeProfesional(Integer preguntasEjeProfesional) {
		this.preguntasEjeProfesional = preguntasEjeProfesional;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
