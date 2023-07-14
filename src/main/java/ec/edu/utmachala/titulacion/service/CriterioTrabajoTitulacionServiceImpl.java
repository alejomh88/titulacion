package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CriterioTrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.entity.CriterioTrabajoTitulacion;

@Service
public class CriterioTrabajoTitulacionServiceImpl extends GenericServiceImpl<CriterioTrabajoTitulacion, Integer>
		implements CriterioTrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CriterioTrabajoTitulacionDao criterioTrabajoTitulacionDao;

	public void actualizar(CriterioTrabajoTitulacion criterioTrabajoTitulacion) {
		criterioTrabajoTitulacionDao.actualizar(criterioTrabajoTitulacion);
	}

	public void insertar(CriterioTrabajoTitulacion criterioTrabajoTitulacion) {
		criterioTrabajoTitulacionDao.insertar(criterioTrabajoTitulacion);
	}

}