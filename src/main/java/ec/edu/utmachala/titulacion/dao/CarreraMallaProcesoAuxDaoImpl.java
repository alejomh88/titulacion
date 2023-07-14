package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAux;

@Repository
public class CarreraMallaProcesoAuxDaoImpl extends GenericDaoImpl<CarreraMallaProcesoAux, Integer>
		implements CarreraMallaProcesoAuxDao, Serializable {
	private static final long serialVersionUID = 1L;
}