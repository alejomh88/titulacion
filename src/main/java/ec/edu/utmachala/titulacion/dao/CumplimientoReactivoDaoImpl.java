package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CumplimientoReactivo;

@Repository
public class CumplimientoReactivoDaoImpl extends GenericDaoImpl<CumplimientoReactivo, String>
		implements CumplimientoReactivoDao, Serializable {
	private static final long serialVersionUID = 1L;
}