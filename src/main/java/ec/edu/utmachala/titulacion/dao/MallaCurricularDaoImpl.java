package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.MallaCurricular;

@Repository
public class MallaCurricularDaoImpl extends GenericDaoImpl<MallaCurricular, Integer>
		implements MallaCurricularDao, Serializable {
	private static final long serialVersionUID = 1L;
}