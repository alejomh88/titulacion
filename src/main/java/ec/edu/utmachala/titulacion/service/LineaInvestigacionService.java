package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.LineaInvestigacion;

public interface LineaInvestigacionService extends GenericService<LineaInvestigacion, Integer> {
	@Transactional
	public void actualizar(LineaInvestigacion lineaInvestigacion);

	@Transactional
	public void eliminar(LineaInvestigacion lineaInvestigacion);

	@Transactional
	public void insertar(LineaInvestigacion lineaInvestigacion);

}