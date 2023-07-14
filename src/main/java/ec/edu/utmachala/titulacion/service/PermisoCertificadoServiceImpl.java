package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.PermisoCertificado;

@Service
public class PermisoCertificadoServiceImpl extends GenericServiceImpl<PermisoCertificado, Integer>
		implements PermisoCertificadoService, Serializable {

	private static final long serialVersionUID = 1L;

}