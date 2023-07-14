package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.DetalleEntidadGenerica;

@Service
public class DetalleEntidadGenericaServiceImpl extends GenericServiceImpl<DetalleEntidadGenerica, String>
		implements DetalleEntidadGenericaService, Serializable {

	private static final long serialVersionUID = 1L;

}