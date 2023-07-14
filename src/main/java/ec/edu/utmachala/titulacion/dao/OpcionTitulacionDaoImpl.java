package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.OpcionTitulacion;

@Repository
public class OpcionTitulacionDaoImpl extends GenericDaoImpl<OpcionTitulacion, Integer>
		implements OpcionTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}