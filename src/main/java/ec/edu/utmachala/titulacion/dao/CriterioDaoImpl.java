package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Criterio;

@Repository
public class CriterioDaoImpl extends GenericDaoImpl<Criterio, Integer> implements CriterioDao, Serializable {
	private static final long serialVersionUID = 1L;
}