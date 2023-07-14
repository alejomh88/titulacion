package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Rol;

public interface RolService extends GenericService<Rol, String> {
	@Transactional(readOnly = true)
	public Rol obtenerPorNombre(String nombre);
}