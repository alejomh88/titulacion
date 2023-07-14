package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;

public interface DocenteAsignaturaService extends GenericService<DocenteAsignatura, Integer> {

	@Transactional
	public void actualizar(DocenteAsignatura docenteAsignatura);

	@Transactional
	public void eliminar(DocenteAsignatura docenteAsignatura);

	@Transactional
	public void insertar(DocenteAsignatura docenteAsignatura);

	@Transactional(readOnly = true)
	public DocenteAsignatura obtenerPorDocenteAsignaturaCarrera(String docente, String carrera, String asignatura);

}