package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.PreguntaExamenDao;
import ec.edu.utmachala.titulacion.entity.PreguntaExamen;

@Service
public class PreguntaExamenServiceImpl extends GenericServiceImpl<PreguntaExamen, Integer>
		implements PreguntaExamenService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PreguntaExamenDao preguntaExamenDao;

	public void actualizar(PreguntaExamen preguntaExamen) {
		preguntaExamenDao.actualizar(preguntaExamen);
	}

	public void insertar(PreguntaExamen preguntaExamen) {
		preguntaExamenDao.insertar(preguntaExamen);
	}
}