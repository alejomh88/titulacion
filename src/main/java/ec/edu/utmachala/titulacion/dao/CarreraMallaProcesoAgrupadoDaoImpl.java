package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAgrupado;

@Repository
public class CarreraMallaProcesoAgrupadoDaoImpl extends GenericDaoImpl<CarreraMallaProcesoAgrupado, Integer>
		implements CarreraMallaProcesoAgrupadoDao, Serializable {
	private static final long serialVersionUID = 1L;
}