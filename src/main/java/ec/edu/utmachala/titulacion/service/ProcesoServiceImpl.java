package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.Proceso;

@Service
public class ProcesoServiceImpl extends GenericServiceImpl<Proceso, Integer> implements ProcesoService, Serializable {

	private static final long serialVersionUID = 1L;
}