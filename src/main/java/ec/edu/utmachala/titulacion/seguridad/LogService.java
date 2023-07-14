package ec.edu.utmachala.titulacion.seguridad;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.service.GenericService;

public interface LogService extends GenericService<Log, Integer> {

	@Transactional
	public void insertar(Log log);

}