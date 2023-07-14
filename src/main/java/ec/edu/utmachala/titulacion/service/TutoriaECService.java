package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.TutoriaEC;

public interface TutoriaECService extends GenericService<TutoriaEC, Integer> {

	@Transactional
	public void actualizar(TutoriaEC tutoriaEC);

	@Transactional
	public void eliminar(TutoriaEC tutoriaEC);

	@Transactional
	public void insertar(TutoriaEC tutoriaEC);
}
