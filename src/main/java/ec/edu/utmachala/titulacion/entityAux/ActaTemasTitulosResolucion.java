package ec.edu.utmachala.titulacion.entityAux;

import java.util.List;

public class ActaTemasTitulosResolucion {

	private String unidadAcademica;
	private String carrera;
	private String titulo;
	private String descripcion;
	private List<DetalleActaTemasTitulosResolucion> listActaTemasTitulosResolucion;

	public ActaTemasTitulosResolucion() {
	}

	public ActaTemasTitulosResolucion(String unidadAcademica, String carrera, String titulo, String descripcion,
			List<DetalleActaTemasTitulosResolucion> listActaTemasTitulosResolucion) {
		this.unidadAcademica = unidadAcademica;
		this.carrera = carrera;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.listActaTemasTitulosResolucion = listActaTemasTitulosResolucion;
	}

	public String getCarrera() {
		return carrera;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public List<DetalleActaTemasTitulosResolucion> getListActaTemasTitulosResolucion() {
		return listActaTemasTitulosResolucion;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getUnidadAcademica() {
		return unidadAcademica;
	}

	public void setCarrera(String carrera) {
		this.carrera = carrera;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setListActaTemasTitulosResolucion(
			List<DetalleActaTemasTitulosResolucion> listActaTemasTitulosResolucion) {
		this.listActaTemasTitulosResolucion = listActaTemasTitulosResolucion;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setUnidadAcademica(String unidadAcademica) {
		this.unidadAcademica = unidadAcademica;
	}

}
