package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Asistencia;

@Service
public class AsistenciaServiceImpl extends GenericServiceImpl<Asistencia, String>
		implements AsistenciaService, Serializable {

	private static final long serialVersionUID = 1L;

}