package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.TrabajoTitulacion;

public interface TrabajoTitulacionService extends GenericService<TrabajoTitulacion, Integer> {

	@Transactional
	public void actualizar(TrabajoTitulacion trabajoTitulacion);

	@Transactional
	public void eliminar(TrabajoTitulacion trabajoTitulacion);

	@Transactional
	public void insertar(TrabajoTitulacion trabajoTitulacion);

}
