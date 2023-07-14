package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.LineaInvestigacionSeminarioTutor;

@Repository
public class LineaInvestigacionSeminarioTutorDaoImpl extends GenericDaoImpl<LineaInvestigacionSeminarioTutor, Integer>
		implements LineaInvestigacionSeminarioTutorDao, Serializable {
	private static final long serialVersionUID = 1L;
}