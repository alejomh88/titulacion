package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.LineaInvestigacionDao;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacion;

@Service
public class LineaInvestigacionServiceImpl extends GenericServiceImpl<LineaInvestigacion, Integer>
		implements LineaInvestigacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private LineaInvestigacionDao lineaInvestigacionDao;

	public void actualizar(LineaInvestigacion lineaInvestigacion) {
		lineaInvestigacionDao.actualizar(lineaInvestigacion);
	}

	public void eliminar(LineaInvestigacion lineaInvestigacion) {
		lineaInvestigacionDao.eliminar(lineaInvestigacion);
	}

	public void insertar(LineaInvestigacion lineaInvestigacion) {
		lineaInvestigacionDao.insertar(lineaInvestigacion);
	}

}