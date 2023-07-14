package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.SiutmachTitulacion;

@Service
public class SiutmachTitulacionServiceImpl extends GenericServiceImpl<SiutmachTitulacion, String>
		implements SiutmachTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

}