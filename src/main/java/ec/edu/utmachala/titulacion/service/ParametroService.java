package ec.edu.utmachala.titulacion.service;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Parametro;

public interface ParametroService {
	@Transactional
	public void actualizar(Parametro parametro);

	@Transactional
	public Parametro obtener();

	@Transactional
	public Map<String, String> traerMap(Parametro p);

}
