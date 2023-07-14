package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;

public interface SeminarioTrabajoTitulacionService extends GenericService<SeminarioTrabajoTitulacion, Integer> {

	@Transactional
	public void actualizar(SeminarioTrabajoTitulacion seminarioTrabajoTitulacion);

	@Transactional
	public void insertar(SeminarioTrabajoTitulacion seminarioTrabajoTitulacion);

}
