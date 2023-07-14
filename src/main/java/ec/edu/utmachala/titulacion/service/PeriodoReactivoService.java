package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.PeriodoReactivo;

public interface PeriodoReactivoService extends GenericService<PeriodoReactivo, Integer> {
	@Transactional
	public PeriodoReactivo obtenerPorPeriodoYCarrera(String carrera);
}