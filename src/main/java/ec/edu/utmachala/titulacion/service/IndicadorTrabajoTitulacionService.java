package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.IndicadorTrabajoTitulacion;

public interface IndicadorTrabajoTitulacionService extends GenericService<IndicadorTrabajoTitulacion, Integer> {

	@Transactional
	public void actualizar(IndicadorTrabajoTitulacion indicadorTrabajoTitulacion);

	@Transactional
	public void insertar(IndicadorTrabajoTitulacion indicadorTrabajoTitulacion);

}
