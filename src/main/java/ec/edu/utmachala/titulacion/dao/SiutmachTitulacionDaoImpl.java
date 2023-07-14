package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.SiutmachTitulacion;

@Repository
public class SiutmachTitulacionDaoImpl extends GenericDaoImpl<SiutmachTitulacion, String>
		implements SiutmachTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}