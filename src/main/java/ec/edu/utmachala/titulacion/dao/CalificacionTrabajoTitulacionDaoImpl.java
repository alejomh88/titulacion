package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.CalificacionTrabajoTitulacion;

@Repository
public class CalificacionTrabajoTitulacionDaoImpl extends GenericDaoImpl<CalificacionTrabajoTitulacion, Integer>
		implements CalificacionTrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}