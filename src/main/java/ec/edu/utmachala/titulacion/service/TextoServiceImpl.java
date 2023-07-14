package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Texto;

@Service
public class TextoServiceImpl extends GenericServiceImpl<Texto, String> implements TextoService, Serializable {

	private static final long serialVersionUID = 1L;

}