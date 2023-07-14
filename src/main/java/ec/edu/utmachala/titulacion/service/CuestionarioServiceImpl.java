package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Cuestionario;

@Service
public class CuestionarioServiceImpl extends GenericServiceImpl<Cuestionario, Integer>
		implements CuestionarioService, Serializable {

	private static final long serialVersionUID = 1L;

}