package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.UnidadCurricular;

public interface UnidadCurricularService extends GenericService<UnidadCurricular, String> {
	@Transactional(readOnly = true)
	public UnidadCurricular obtenerPorNombre(String nombre);
}