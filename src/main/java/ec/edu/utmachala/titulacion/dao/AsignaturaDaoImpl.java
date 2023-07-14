package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Asignatura;

@Repository
public class AsignaturaDaoImpl extends GenericDaoImpl<Asignatura, Integer> implements AsignaturaDao, Serializable {
	private static final long serialVersionUID = 1L;
}