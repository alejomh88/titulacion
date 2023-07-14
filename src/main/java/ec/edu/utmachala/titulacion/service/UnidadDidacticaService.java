package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.UnidadDidactica;

public interface UnidadDidacticaService extends GenericService<UnidadDidactica, Integer> {
	@Transactional
	public void actualizar(UnidadDidactica unidadDidactica);

	@Transactional
	public void eliminar(UnidadDidactica unidadDidactica);

	@Transactional
	public void insertar(UnidadDidactica unidadDidactica);

	@Transactional(readOnly = true)
	public UnidadDidactica obtener(String carrera, String asignatura, String nombre);

}