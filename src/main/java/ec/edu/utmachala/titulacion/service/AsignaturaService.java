package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Asignatura;

public interface AsignaturaService extends GenericService<Asignatura, Integer> {
	@Transactional
	public void actualizar(Asignatura asignatura);

	@Transactional
	public void insertar(Asignatura asignatura);

	@Transactional
	public void insertar(List<Asignatura> asignaturas);

	@Transactional
	public Asignatura obtenerPorNombreYCarrera(String nombre, String carrera);
}