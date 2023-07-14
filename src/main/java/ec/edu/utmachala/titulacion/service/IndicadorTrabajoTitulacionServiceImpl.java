package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.IndicadorTrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.entity.IndicadorTrabajoTitulacion;

@Service
public class IndicadorTrabajoTitulacionServiceImpl extends GenericServiceImpl<IndicadorTrabajoTitulacion, Integer>
		implements IndicadorTrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private IndicadorTrabajoTitulacionDao indicadorTrabajoTitulacionDao;

	public void actualizar(IndicadorTrabajoTitulacion indicadorTrabajoTitulacion) {
		indicadorTrabajoTitulacionDao.actualizar(indicadorTrabajoTitulacion);
	}

	public void insertar(IndicadorTrabajoTitulacion indicadorTrabajoTitulacion) {
		indicadorTrabajoTitulacionDao.insertar(indicadorTrabajoTitulacion);
	}

}