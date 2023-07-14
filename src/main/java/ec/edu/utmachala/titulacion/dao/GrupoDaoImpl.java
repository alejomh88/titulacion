package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Grupo;

@Repository
public class GrupoDaoImpl extends GenericDaoImpl<Grupo, Integer> implements GrupoDao, Serializable {
	private static final long serialVersionUID = 1L;
}