package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.TutoriaECDao;
import ec.edu.utmachala.titulacion.entity.TutoriaEC;

@Service
public class TutoriaECServiceImpl extends GenericServiceImpl<TutoriaEC, Integer>
		implements TutoriaECService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private TutoriaECDao tutoriaECDao;

	public void actualizar(TutoriaEC tutoriaEC) {
		tutoriaECDao.actualizar(tutoriaEC);
	}

	public void eliminar(TutoriaEC tutoriaEC) {
		tutoriaECDao.eliminar(tutoriaEC);
	}

	public void insertar(TutoriaEC tutoriaEC) {
		tutoriaECDao.insertar(tutoriaEC);
	}

}