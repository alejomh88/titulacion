package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Examen;

public interface ExamenService extends GenericService<Examen, Integer> {

	@Transactional
	public void actualizar(Examen examen);

	@Transactional
	public void asignarGrupo();

	@Transactional
	public void cincoPreguntas();

	@Transactional(readOnly = true)
	public Examen comprobarExamenAperturado(String email, Integer carrera);

	@Transactional(readOnly = true)
	public Examen comprobarExamenAperturado(String cedula, String carrera);

	@Transactional
	public Examen crearExamen(String email, Integer carrera);

	@Transactional
	public void crearRegistroExamen();

	@Transactional
	public Examen obtenerCalificacionExamen(int examenId);

	@Transactional
	public Examen obtenerExamen(String email, Integer carrera);

	@Transactional(readOnly = true)
	public Examen obtenerMejorExamen(String email, Integer carrera);

	@Transactional(readOnly = true)
	public Examen obtenerPreguntasFaltantes(String email, Integer carrera);

	@Transactional(readOnly = true)
	public boolean validarVerCalificacionExamen(int examenId);
}