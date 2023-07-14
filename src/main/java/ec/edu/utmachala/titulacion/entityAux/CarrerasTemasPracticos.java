package ec.edu.utmachala.titulacion.entityAux;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CarrerasTemasPracticos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String carrera;
	private Integer puntaje;

	public String getCarrera() {
		return carrera;
	}

	public Integer getPuntaje() {
		return puntaje;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setPuntaje(Integer puntaje) {
		this.puntaje = puntaje;
	}

}
