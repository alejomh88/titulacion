package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.PreguntaExamen;

@Repository
public class PreguntaExamenDaoImpl extends GenericDaoImpl<PreguntaExamen, Integer>
		implements PreguntaExamenDao, Serializable {
	private static final long serialVersionUID = 1L;
}