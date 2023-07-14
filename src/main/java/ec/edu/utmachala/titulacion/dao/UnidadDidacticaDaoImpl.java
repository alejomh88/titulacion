package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.UnidadDidactica;

@Repository
public class UnidadDidacticaDaoImpl extends GenericDaoImpl<UnidadDidactica, Integer>
		implements UnidadDidacticaDao, Serializable {
	private static final long serialVersionUID = 1L;
}