package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.CriterioTrabajoTitulacion;

@Repository
public class CriterioTrabajoTitulacionDaoImpl extends GenericDaoImpl<CriterioTrabajoTitulacion, Integer>
		implements CriterioTrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}