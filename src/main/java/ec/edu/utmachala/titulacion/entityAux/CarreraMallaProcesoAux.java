package ec.edu.utmachala.titulacion.entityAux;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CarreraMallaProcesoAux implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String proceso;

	private String carrera;

	private Integer mallaCurricular;

	private String asignatura;

	public String getAsignatura() {
		return asignatura;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getId() {
		return id;
	}

	public Integer getMallaCurricular() {
		return mallaCurricular;
	}

	public String getProceso() {
		return proceso;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setMallaCurricular(Integer mallaCurricular) {
		this.mallaCurricular = mallaCurricular;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

}
