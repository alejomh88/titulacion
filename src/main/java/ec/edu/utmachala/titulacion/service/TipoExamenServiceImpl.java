package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.TipoExamenDao;
import ec.edu.utmachala.titulacion.entity.TipoExamen;

@Service
public class TipoExamenServiceImpl extends GenericServiceImpl<TipoExamen, Integer>
		implements TipoExamenService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private TipoExamenDao tipoExamenDao;

	public void actualizar(TipoExamen tipoExamen) {
		tipoExamenDao.actualizar(tipoExamen);
	}

	public void insertar(TipoExamen tipoExamen) {
		tipoExamenDao.insertar(tipoExamen);
	}

}