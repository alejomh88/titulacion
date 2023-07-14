package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.UnidadAcademica;

@Repository
public class UnidadAcademicaDaoImpl extends GenericDaoImpl<UnidadAcademica, String>
		implements UnidadAcademicaDao, Serializable {
	private static final long serialVersionUID = 1L;
}