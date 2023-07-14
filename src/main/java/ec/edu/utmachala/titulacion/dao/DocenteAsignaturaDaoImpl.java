package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;

@Repository
public class DocenteAsignaturaDaoImpl extends GenericDaoImpl<DocenteAsignatura, Integer>
		implements DocenteAsignaturaDao, Serializable {
	private static final long serialVersionUID = 1L;
}