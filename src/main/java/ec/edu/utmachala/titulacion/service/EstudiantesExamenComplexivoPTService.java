package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;

public interface EstudiantesExamenComplexivoPTService extends GenericService<EstudianteExamenComplexivoPT, Integer> {

	@Transactional
	public void actualizar(EstudianteExamenComplexivoPT estudiantePeriodo);

	@Transactional(readOnly = true)
	public EstudianteExamenComplexivoPT obtenerPeriodoActivoPorCarrera(String email, Integer carrera);

	@Transactional(readOnly = true)
	public EstudianteExamenComplexivoPT obtenerPeriodoActivoPorCarrera(String cedula, String carrera);

	@Transactional(readOnly = true)
	public EstudianteExamenComplexivoPT obtenerPeriodoPorCarrera(String email, Integer carrera);
}