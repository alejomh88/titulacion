package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CalificacionTrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.entity.CalificacionTrabajoTitulacion;

@Service
public class CalificacionTrabajoTitulacionServiceImpl extends GenericServiceImpl<CalificacionTrabajoTitulacion, Integer>
		implements CalificacionTrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CalificacionTrabajoTitulacionDao calificacionTrabajoTitulacionDao;

	public void eliminar(CalificacionTrabajoTitulacion calificacionTrabajoTitulacion) {
		calificacionTrabajoTitulacionDao.eliminar(calificacionTrabajoTitulacion);
	}

}