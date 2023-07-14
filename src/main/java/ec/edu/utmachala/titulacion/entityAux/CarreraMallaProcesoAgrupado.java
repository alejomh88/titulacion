package ec.edu.utmachala.titulacion.entityAux;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CarreraMallaProcesoAgrupado implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String carrera;

	private Integer malla;

	private String proceso;

	public String getCarrera() {
		return carrera;
	}

	public Integer getId() {
		return id;
	}

	public Integer getMalla() {
		return malla;
	}

	public String getProceso() {
		return proceso;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMalla(Integer malla) {
		this.malla = malla;
	}

	public void setProcesos(String proceso) {
		this.proceso = proceso;
	}

}
