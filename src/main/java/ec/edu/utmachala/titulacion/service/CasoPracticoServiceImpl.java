package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.CasoPractico;

@Service
public class CasoPracticoServiceImpl extends GenericServiceImpl<CasoPractico, Integer>
		implements CasoPracticoService, Serializable {

	private static final long serialVersionUID = 1L;

}