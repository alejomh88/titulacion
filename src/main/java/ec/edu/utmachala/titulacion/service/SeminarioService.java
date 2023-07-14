package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Seminario;

public interface SeminarioService extends GenericService<Seminario, Integer> {

	@Transactional
	public void actualizar(Seminario seminario);

	@Transactional
	public void eliminar(Seminario seminario);

	@Transactional
	public void insertar(Seminario seminario);
}