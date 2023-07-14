package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.EstudianteHistorial;

@Service
public class EstudianteHistorialServiceImpl extends GenericServiceImpl<EstudianteHistorial, String>
		implements EstudianteHistorialService, Serializable {

	private static final long serialVersionUID = 1L;

}