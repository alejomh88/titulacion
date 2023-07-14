package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.DistribucionAsignatura;

@Service
public class DistribucionAsignaturaServiceImpl extends GenericServiceImpl<DistribucionAsignatura, Integer>
		implements DistribucionAsignaturaService, Serializable {

	private static final long serialVersionUID = 1L;

}