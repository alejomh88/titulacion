package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.UnidadCurricular;

@Repository
public class UnidadCurricularDaoImpl extends GenericDaoImpl<UnidadCurricular, String>
		implements UnidadCurricularDao, Serializable {
	private static final long serialVersionUID = 1L;
}