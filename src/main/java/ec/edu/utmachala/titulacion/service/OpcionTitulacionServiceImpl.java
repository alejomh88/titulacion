package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.OpcionTitulacion;

@Service
public class OpcionTitulacionServiceImpl extends GenericServiceImpl<OpcionTitulacion, Integer>
		implements OpcionTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

}