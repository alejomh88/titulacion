package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.PeriodoReactivo;

@Repository
public class PeriodoReactivoDaoImpl extends GenericDaoImpl<PeriodoReactivo, Integer>
		implements PeriodoReactivoDao, Serializable {
	private static final long serialVersionUID = 1L;
}