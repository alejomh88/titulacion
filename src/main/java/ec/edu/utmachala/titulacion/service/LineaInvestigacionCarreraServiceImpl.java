package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.LineaInvestigacionCarreraDao;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;

@Service
public class LineaInvestigacionCarreraServiceImpl extends GenericServiceImpl<LineaInvestigacionCarrera, Integer>
		implements LineaInvestigacionCarreraService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private LineaInvestigacionCarreraDao lineaInvestigacionCarreraDao;

	public void actualizar(LineaInvestigacionCarrera lineaInvestigacionCarrera) {
		lineaInvestigacionCarreraDao.actualizar(lineaInvestigacionCarrera);
	}

	public void eliminar(LineaInvestigacionCarrera lineaInvestigacionCarrera) {
		lineaInvestigacionCarreraDao.eliminar(lineaInvestigacionCarrera);
	}

	public void insertar(LineaInvestigacionCarrera lineaInvestigacionCarrera) {
		lineaInvestigacionCarreraDao.insertar(lineaInvestigacionCarrera);
	}

}