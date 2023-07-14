package ec.edu.utmachala.titulacion.service;

import ec.edu.utmachala.titulacion.entity.Calificacion;

public interface CalificacionService extends GenericService<Calificacion, Integer> {

	// @Transactional
	public void eliminar(Calificacion calificacion);

}
