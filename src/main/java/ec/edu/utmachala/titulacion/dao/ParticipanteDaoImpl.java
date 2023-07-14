package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Participante;

@Repository
public class ParticipanteDaoImpl extends GenericDaoImpl<Participante, String> implements ParticipanteDao, Serializable {
	private static final long serialVersionUID = 1L;
}