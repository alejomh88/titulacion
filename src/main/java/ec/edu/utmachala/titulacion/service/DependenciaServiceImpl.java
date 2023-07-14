package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.Dependencia;

@Service
public class DependenciaServiceImpl extends GenericServiceImpl<Dependencia, String>
		implements DependenciaService, Serializable {

	private static final long serialVersionUID = 1L;

}