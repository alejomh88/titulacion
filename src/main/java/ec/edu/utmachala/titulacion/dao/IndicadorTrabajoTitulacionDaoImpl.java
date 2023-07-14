package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.IndicadorTrabajoTitulacion;

@Repository
public class IndicadorTrabajoTitulacionDaoImpl extends GenericDaoImpl<IndicadorTrabajoTitulacion, Integer>
		implements IndicadorTrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}