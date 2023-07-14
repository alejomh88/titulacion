package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Pregunta;

@Repository
public class PreguntaDaoImpl extends GenericDaoImpl<Pregunta, Integer> implements PreguntaDao, Serializable {
	private static final long serialVersionUID = 1L;
}