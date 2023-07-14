package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Grupo;

public interface GrupoService extends GenericService<Grupo, Integer> {

	@Transactional
	public void actualizar(Grupo grupo);

	@Transactional
	public void insertar(Grupo grupo);
}