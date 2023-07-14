package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.Permiso;

@Service
public class PermisoServiceImpl extends GenericServiceImpl<Permiso, Integer> implements PermisoService, Serializable {

	private static final long serialVersionUID = 1L;

}