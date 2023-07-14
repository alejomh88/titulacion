package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.PreguntaHistorico;

public interface PreguntaHistoricoService {

	@Transactional
	public PreguntaHistorico insertar(Pregunta pregunta);

	@Transactional
	public PreguntaHistorico obtenerPorEnunciadoAndRespuesta(String enunciado, String respuesta);

	@Transactional
	public PreguntaHistorico obtenerPorId(Integer id);
}