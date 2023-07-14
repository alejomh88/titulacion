package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Texto;

@Repository
public class FechaDaoImpl extends GenericDaoImpl<Texto, Date> implements FechaDao, Serializable {
	private static final long serialVersionUID = 1L;
}