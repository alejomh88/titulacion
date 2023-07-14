package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.DocenteSeminario;

public interface DocenteSeminarioService extends GenericService<DocenteSeminario, Integer> {

	@Transactional
	public void actualizar(DocenteSeminario docenteSeminario);

	@Transactional
	public void eliminar(DocenteSeminario docenteSeminario);

	@Transactional
	public void insertar(DocenteSeminario docenteSeminario);

}
