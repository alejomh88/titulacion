package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.DetalleEntidadGenerica;

@Repository
public class DetalleEntidadGenericaDaoImpl extends GenericDaoImpl<DetalleEntidadGenerica, String>
		implements DetalleEntidadGenericaDao, Serializable {
	private static final long serialVersionUID = 1L;
}