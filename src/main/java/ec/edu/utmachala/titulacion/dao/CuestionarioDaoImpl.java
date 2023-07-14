package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Cuestionario;

@Repository
public class CuestionarioDaoImpl extends GenericDaoImpl<Cuestionario, Integer>
		implements CuestionarioDao, Serializable {
	private static final long serialVersionUID = 1L;
}