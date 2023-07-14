package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Criterio;

public interface CriterioService extends GenericService<Criterio, Integer> {

	@Transactional
	public void actualizar(Criterio criterio);

	@Transactional
	public void insertar(Criterio criterio);

}
