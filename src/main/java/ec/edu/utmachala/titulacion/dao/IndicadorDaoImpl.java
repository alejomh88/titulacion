package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Indicador;

@Repository
public class IndicadorDaoImpl extends GenericDaoImpl<Indicador, Integer> implements IndicadorDao, Serializable {
	private static final long serialVersionUID = 1L;
}