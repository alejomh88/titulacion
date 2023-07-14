package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.SecuenciaActaGraduacion;

@Repository
public class SecuenciaActaGraduacionDaoImpl extends GenericDaoImpl<SecuenciaActaGraduacion, Integer>
		implements SecuenciaActaGraduacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}