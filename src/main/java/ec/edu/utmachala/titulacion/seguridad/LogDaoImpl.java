package ec.edu.utmachala.titulacion.seguridad;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.dao.GenericDaoImpl;

@Repository
public class LogDaoImpl extends GenericDaoImpl<Log, Integer> implements LogDao, Serializable {
	private static final long serialVersionUID = 1L;
}