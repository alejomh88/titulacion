package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.CantidadReactivo;

@Service
public class CantidadReactivoServiceImpl extends GenericServiceImpl<CantidadReactivo, Integer>
		implements CantidadReactivoService, Serializable {

	private static final long serialVersionUID = 1L;

}