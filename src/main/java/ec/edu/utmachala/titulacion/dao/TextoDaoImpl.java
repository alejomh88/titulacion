package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Texto;

@Repository
public class TextoDaoImpl extends GenericDaoImpl<Texto, String> implements TextoDao, Serializable {
	private static final long serialVersionUID = 1L;
}