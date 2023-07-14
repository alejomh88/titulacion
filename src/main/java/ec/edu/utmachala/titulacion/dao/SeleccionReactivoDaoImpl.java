package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.SeleccionReactivo;

@Repository
public class SeleccionReactivoDaoImpl extends GenericDaoImpl<SeleccionReactivo, String>
		implements SeleccionReactivoDao, Serializable {
	private static final long serialVersionUID = 1L;
}