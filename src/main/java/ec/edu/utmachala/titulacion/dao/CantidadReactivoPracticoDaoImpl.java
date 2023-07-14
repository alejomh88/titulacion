package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CantidadReactivoPractico;

@Repository
public class CantidadReactivoPracticoDaoImpl extends GenericDaoImpl<CantidadReactivoPractico, Integer>
		implements CantidadReactivoPracticoDao, Serializable {
	private static final long serialVersionUID = 1L;
}