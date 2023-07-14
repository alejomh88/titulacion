package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Cartilla;

@Repository
public class CartillaDaoImpl extends GenericDaoImpl<Cartilla, Integer> implements CartillaDao, Serializable {
	private static final long serialVersionUID = 1L;
}