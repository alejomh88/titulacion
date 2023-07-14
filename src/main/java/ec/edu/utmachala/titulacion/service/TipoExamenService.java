package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.TipoExamen;

public interface TipoExamenService extends GenericService<TipoExamen, Integer> {

	@Transactional
	public void actualizar(TipoExamen tipoExamen);

	@Transactional
	public void insertar(TipoExamen tipoExamen);

}
