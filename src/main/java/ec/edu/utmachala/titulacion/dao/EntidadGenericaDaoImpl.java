package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.EntidadGenerica;

@Repository
public class EntidadGenericaDaoImpl extends GenericDaoImpl<EntidadGenerica, String>
		implements EntidadGenericaDao, Serializable {
	private static final long serialVersionUID = 1L;
}