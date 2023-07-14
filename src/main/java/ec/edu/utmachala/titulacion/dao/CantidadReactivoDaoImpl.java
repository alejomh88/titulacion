package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CantidadReactivo;

@Repository
public class CantidadReactivoDaoImpl extends GenericDaoImpl<CantidadReactivo, Integer>
		implements CantidadReactivoDao, Serializable {
	private static final long serialVersionUID = 1L;
}