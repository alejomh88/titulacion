package ec.edu.utmachala.titulacion.entityAux;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CantidadReactivoPractico {

	@Id
	private Integer id;
	private String unidadAcademica;
	private String carrera;
	private Integer aprobadosParteTeorica;
	private Integer casosSeleccionados;
	private Integer casosDisponibles;

	public CantidadReactivoPractico() {

	}

	public CantidadReactivoPractico(Integer id, String unidadAcademica, String carrera, Integer aprobadosParteTeorica,
			Integer casosSeleccionados, Integer casosDisponibles) {
		super();
		this.id = id;
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.aprobadosParteTeorica = aprobadosParteTeorica;
		this.casosSeleccionados = casosSeleccionados;
		this.casosDisponibles = casosDisponibles;
	}

	public Integer getAprobadosParteTeorica() {
		return aprobadosParteTeorica;
	}

	public String getCarrera() {
		return carrera;
	}

	public Integer getCasosDisponibles() {
		return casosDisponibles;
	}

	public Integer getCasosSeleccionados() {
		return casosSeleccionados;
	}

	public Integer getId() {
		return id;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setAprobadosParteTeorica(Integer aprobadosParteTeorica) {
		this.aprobadosParteTeorica = aprobadosParteTeorica;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setCasosDisponibles(Integer casosDisponibles) {
		this.casosDisponibles = casosDisponibles;
	}

	public void setCasosSeleccionados(Integer casosSeleccionados) {
		this.casosSeleccionados = casosSeleccionados;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
