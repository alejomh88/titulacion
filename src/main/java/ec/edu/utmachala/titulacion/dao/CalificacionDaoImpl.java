package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Calificacion;

@Repository
public class CalificacionDaoImpl extends GenericDaoImpl<Calificacion, Integer>
		implements CalificacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}