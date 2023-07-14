package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CasoPractico;

@Repository
public class CasoPracticoDaoImpl extends GenericDaoImpl<CasoPractico, Integer>
		implements CasoPracticoDao, Serializable {
	private static final long serialVersionUID = 1L;
}