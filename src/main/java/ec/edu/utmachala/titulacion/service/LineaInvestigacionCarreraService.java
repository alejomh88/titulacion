package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;

public interface LineaInvestigacionCarreraService extends GenericService<LineaInvestigacionCarrera, Integer> {
	@Transactional
	public void actualizar(LineaInvestigacionCarrera lineaInvestigacionCarrera);

	@Transactional
	public void eliminar(LineaInvestigacionCarrera lineaInvestigacionCarrera);

	@Transactional
	public void insertar(LineaInvestigacionCarrera lineaInvestigacionCarrera);

}