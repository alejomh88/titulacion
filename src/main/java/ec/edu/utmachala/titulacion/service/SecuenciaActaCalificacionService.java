package ec.edu.utmachala.titulacion.service;

import ec.edu.utmachala.titulacion.entity.SecuenciaActaCalificacion;

public interface SecuenciaActaCalificacionService extends GenericService<SecuenciaActaCalificacion, Integer> {

	public void insertar(SecuenciaActaCalificacion secuenciaActaCalificacion);
}
