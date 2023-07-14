package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;

@Repository
public class LineaInvestigacionCarreraDaoImpl extends GenericDaoImpl<LineaInvestigacionCarrera, Integer>
		implements LineaInvestigacionCarreraDao, Serializable {
	private static final long serialVersionUID = 1L;
}