package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.PreguntaExamen;

public interface PreguntaExamenService extends GenericService<PreguntaExamen, Integer> {

	@Transactional
	public void actualizar(PreguntaExamen preguntaExamen);

	@Transactional
	public void insertar(PreguntaExamen preguntaExamen);

}