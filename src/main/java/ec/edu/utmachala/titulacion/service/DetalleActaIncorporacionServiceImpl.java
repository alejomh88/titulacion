package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.DetalleActaIncorporacion;

@Service
public class DetalleActaIncorporacionServiceImpl extends GenericServiceImpl<DetalleActaIncorporacion, String>
		implements DetalleActaIncorporacionService, Serializable {

	private static final long serialVersionUID = 1L;

}