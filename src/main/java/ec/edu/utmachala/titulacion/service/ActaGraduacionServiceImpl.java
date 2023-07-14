package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.ActaGraduacion;

@Service
public class ActaGraduacionServiceImpl extends GenericServiceImpl<ActaGraduacion, String>
		implements ActaGraduacionService, Serializable {

	private static final long serialVersionUID = 1L;

}