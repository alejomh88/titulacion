package ec.edu.utmachala.titulacion.service;

import java.util.List;

import ec.edu.utmachala.titulacion.entityAux.EstudianteCalificacion;

public interface EstudianteCalificacionService extends GenericService<EstudianteCalificacion, String> {

	public List<EstudianteCalificacion> quicksortCalificacion(List<EstudianteCalificacion> list, int iz, int de);

	public List<EstudianteCalificacion> quicksortNombre(List<EstudianteCalificacion> list, int iz, int de);

}
