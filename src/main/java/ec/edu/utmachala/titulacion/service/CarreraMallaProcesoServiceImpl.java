package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CarreraMallaProcesoDao;
import ec.edu.utmachala.titulacion.entity.CarreraMallaProceso;

@Service
public class CarreraMallaProcesoServiceImpl extends GenericServiceImpl<CarreraMallaProceso, Integer>
		implements CarreraMallaProcesoService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraMallaProcesoDao carreraMallaProcesoDao;

}