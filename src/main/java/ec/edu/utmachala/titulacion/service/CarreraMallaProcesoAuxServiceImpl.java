package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CarreraMallaProcesoAuxDao;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAux;

@Service
public class CarreraMallaProcesoAuxServiceImpl extends GenericServiceImpl<CarreraMallaProcesoAux, Integer>
		implements CarreraMallaProcesoAuxService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraMallaProcesoAuxDao carreraMallaProcesoAuxDao;

}