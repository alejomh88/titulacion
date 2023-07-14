package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Examen;

@Repository
public class ExamenDaoImpl extends GenericDaoImpl<Examen, Integer> implements ExamenDao, Serializable {
	private static final long serialVersionUID = 1L;
}