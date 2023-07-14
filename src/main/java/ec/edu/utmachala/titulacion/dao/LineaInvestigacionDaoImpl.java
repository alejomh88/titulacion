package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.LineaInvestigacion;

@Repository
public class LineaInvestigacionDaoImpl extends GenericDaoImpl<LineaInvestigacion, Integer>
		implements LineaInvestigacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}