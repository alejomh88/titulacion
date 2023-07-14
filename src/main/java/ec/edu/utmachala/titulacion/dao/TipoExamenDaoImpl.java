package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.TipoExamen;

@Repository
public class TipoExamenDaoImpl extends GenericDaoImpl<TipoExamen, Integer> implements TipoExamenDao, Serializable {
	private static final long serialVersionUID = 1L;
}