package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Tutoria;

@Repository
public class TutoriaDaoImpl extends GenericDaoImpl<Tutoria, Integer> implements TutoriaDao, Serializable {
	private static final long serialVersionUID = 1L;
}