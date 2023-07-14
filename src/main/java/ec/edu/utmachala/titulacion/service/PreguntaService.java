package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Pregunta;

public interface PreguntaService extends GenericService<Pregunta, Integer> {

	@Transactional
	public void actualizar(Pregunta pregunta);

	@Transactional
	public void insertar(Pregunta pregunta);

	@Transactional(readOnly = true)
	public List<Pregunta> obtener(Integer carrera, String ejeProfesional, Integer numeroPreguntas);

	@Transactional(readOnly = true)
	public List<Pregunta> obtener(Integer carrera, String ejeProfesional, String dificultad, Integer numeroPreguntas);

	@Transactional(readOnly = true)
	public Pregunta obtenerPorCarrera(Integer carrera);

	@Transactional(readOnly = true)
	public Pregunta obtenerPorId(Integer id);

	@Transactional(readOnly = true)
	public List<Pregunta> obtenerSecretariado(Integer carrera, Integer numeroPreguntas);
}