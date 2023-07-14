package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CarreraMallaProcesoAgrupadoDao;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAgrupado;

@Service
public class CarreraMallaProcesoAgrupadoServiceImpl extends GenericServiceImpl<CarreraMallaProcesoAgrupado, Integer>
		implements CarreraMallaProcesoAgrupadoService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraMallaProcesoAgrupadoDao carreraMallaProcesoAgrupadoDao;

}