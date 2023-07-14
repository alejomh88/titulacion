package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.SeminarioTrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;

@Service
public class SeminarioTrabajoTitulacionServiceImpl extends GenericServiceImpl<SeminarioTrabajoTitulacion, Integer>
		implements SeminarioTrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SeminarioTrabajoTitulacionDao seminarioTrabajoTitulacionDao;

	public void actualizar(SeminarioTrabajoTitulacion seminarioTrabajoTitulacion) {
		seminarioTrabajoTitulacionDao.actualizar(seminarioTrabajoTitulacion);
	}

	public void insertar(SeminarioTrabajoTitulacion seminarioTrabajoTitulacion) {
		seminarioTrabajoTitulacionDao.insertar(seminarioTrabajoTitulacion);
	}

}