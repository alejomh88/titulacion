package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CalificacionDao;
import ec.edu.utmachala.titulacion.entity.Calificacion;

@Service
public class CalificacionServiceImpl extends GenericServiceImpl<Calificacion, Integer>
		implements CalificacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CalificacionDao calificacionDao;

	public void eliminar(Calificacion calificacion) {
		calificacionDao.eliminar(calificacion);
	}

}