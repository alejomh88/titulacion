package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Dependencia;

@Repository
public class DependenciaDaoImpl extends GenericDaoImpl<Dependencia, String> implements DependenciaDao, Serializable {
	private static final long serialVersionUID = 1L;
}