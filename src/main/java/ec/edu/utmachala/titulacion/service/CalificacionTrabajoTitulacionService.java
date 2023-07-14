package ec.edu.utmachala.titulacion.service;

import ec.edu.utmachala.titulacion.entity.CalificacionTrabajoTitulacion;

public interface CalificacionTrabajoTitulacionService extends GenericService<CalificacionTrabajoTitulacion, Integer> {

	// @Transactional
	public void eliminar(CalificacionTrabajoTitulacion calificacionTrabajoTitulacion);

}
