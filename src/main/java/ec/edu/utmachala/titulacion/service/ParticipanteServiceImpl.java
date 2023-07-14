package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Participante;

@Service
public class ParticipanteServiceImpl extends GenericServiceImpl<Participante, String>
		implements ParticipanteService, Serializable {

	private static final long serialVersionUID = 1L;

}