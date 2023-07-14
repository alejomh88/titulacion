package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.NumeroFechaActaIncorporacion;

@Repository
public class NumeroFechaActaIncorporacionDaoImpl extends GenericDaoImpl<NumeroFechaActaIncorporacion, String>
		implements NumeroFechaActaIncorporacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}