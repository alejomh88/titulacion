package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Tutoria;

public interface TutoriaService extends GenericService<Tutoria, Integer> {

	@Transactional
	public void actualizar(Tutoria tutoria);

	@Transactional
	public void eliminar(Tutoria tutoria);

	@Transactional
	public void insertar(Tutoria tutoria);
}
