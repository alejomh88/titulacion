package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.SecuenciaActaCalificacion;

@Repository
public class SecuenciaActaCalificacionDaoImpl extends GenericDaoImpl<SecuenciaActaCalificacion, Integer>
		implements SecuenciaActaCalificacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}