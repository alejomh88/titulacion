package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.UnidadAcademica;

public interface UnidadAcademicaService extends GenericService<UnidadAcademica, String> {
	@Transactional
	public void actualizar(UnidadAcademica unidadAcademica);

	@Transactional(readOnly = true)
	public List<UnidadAcademica> obtenerTodas();

	@Transactional
	public List<UnidadAcademica> obtenerUnidades();
}