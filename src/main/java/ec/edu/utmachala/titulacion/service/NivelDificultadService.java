package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.NivelDificultad;

public interface NivelDificultadService extends GenericService<NivelDificultad, String> {
	@Transactional
	public NivelDificultad obtenerPorNombre(String nombre);

	@Transactional
	public List<NivelDificultad> obtenerTodos();
}