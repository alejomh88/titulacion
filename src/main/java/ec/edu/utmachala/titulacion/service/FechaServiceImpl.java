package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.Date;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Texto;

@Service
public class FechaServiceImpl extends GenericServiceImpl<Texto, Date> implements FechaService, Serializable {

	private static final long serialVersionUID = 1L;

}