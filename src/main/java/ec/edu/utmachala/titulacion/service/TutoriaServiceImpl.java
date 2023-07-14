package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.TutoriaDao;
import ec.edu.utmachala.titulacion.entity.Tutoria;

@Service
public class TutoriaServiceImpl extends GenericServiceImpl<Tutoria, Integer> implements TutoriaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private TutoriaDao tutoriaDao;

	public void actualizar(Tutoria tutoria) {
		tutoriaDao.actualizar(tutoria);
	}

	public void eliminar(Tutoria tutoria) {
		tutoriaDao.eliminar(tutoria);
	}

	public void insertar(Tutoria tutoria) {
		tutoriaDao.insertar(tutoria);
	}

}