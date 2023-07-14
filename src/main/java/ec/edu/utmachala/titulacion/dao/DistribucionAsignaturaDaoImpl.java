package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.DistribucionAsignatura;

@Repository
public class DistribucionAsignaturaDaoImpl extends GenericDaoImpl<DistribucionAsignatura, Integer>
		implements DistribucionAsignaturaDao, Serializable {
	private static final long serialVersionUID = 1L;
}