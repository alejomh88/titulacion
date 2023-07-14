package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Seminario;

@Repository
public class SeminarioDaoImpl extends GenericDaoImpl<Seminario, Integer> implements SeminarioDao, Serializable {
	private static final long serialVersionUID = 1L;
}