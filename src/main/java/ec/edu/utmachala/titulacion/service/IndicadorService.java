package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Indicador;

public interface IndicadorService extends GenericService<Indicador, Integer> {

	@Transactional
	public void actualizar(Indicador indicador);

	@Transactional
	public void insertar(Indicador indicador);

}
