package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CriterioDao;
import ec.edu.utmachala.titulacion.entity.Criterio;

@Service
public class CriterioServiceImpl extends GenericServiceImpl<Criterio, Integer>
		implements CriterioService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CriterioDao criterioDao;

	public void actualizar(Criterio criterio) {
		criterioDao.actualizar(criterio);
	}

	public void insertar(Criterio criterio) {
		criterioDao.insertar(criterio);
	}

}