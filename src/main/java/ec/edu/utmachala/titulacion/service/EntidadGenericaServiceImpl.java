package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.EntidadGenerica;

@Service
public class EntidadGenericaServiceImpl extends GenericServiceImpl<EntidadGenerica, String>
		implements EntidadGenericaService, Serializable {

	private static final long serialVersionUID = 1L;

}