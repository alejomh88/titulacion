package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.FechaProceso;

@Service
public class FechaProcesoServiceImpl extends GenericServiceImpl<FechaProceso, Integer>
		implements FechaProcesoService, Serializable {

	private static final long serialVersionUID = 1L;

}