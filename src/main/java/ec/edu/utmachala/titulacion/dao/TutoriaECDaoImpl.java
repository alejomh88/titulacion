package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.TutoriaEC;

@Repository
public class TutoriaECDaoImpl extends GenericDaoImpl<TutoriaEC, Integer> implements TutoriaECDao, Serializable {
	private static final long serialVersionUID = 1L;
}