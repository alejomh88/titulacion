package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.CriterioTrabajoTitulacion;

public interface CriterioTrabajoTitulacionService extends GenericService<CriterioTrabajoTitulacion, Integer> {

	@Transactional
	public void actualizar(CriterioTrabajoTitulacion criterioTrabajoTitulacion);

	@Transactional
	public void insertar(CriterioTrabajoTitulacion criterioTrabajoTitulacion);

}
