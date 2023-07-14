package ec.edu.utmachala.titulacion.service;

import ec.edu.utmachala.titulacion.entity.SecuenciaActaGraduacion;

public interface SecuenciaActaGraduacionService extends GenericService<SecuenciaActaGraduacion, Integer> {

	public void insertar(SecuenciaActaGraduacion secuenciaActaGraduacion);
}
