package ec.edu.utmachala.titulacion.seguridad;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.service.GenericService;

public interface AopService extends GenericService<Aop, Integer> {

	@Transactional
	public void insertar(Aop aop);

}