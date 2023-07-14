package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entityAux.Cartilla;

@Service
public class CartillaServiceImpl extends GenericServiceImpl<Cartilla, Integer>
		implements CartillaService, Serializable {

	private static final long serialVersionUID = 1L;

}