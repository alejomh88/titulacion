package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;

@Repository
public class SeminarioTrabajoTitulacionDaoImpl extends GenericDaoImpl<SeminarioTrabajoTitulacion, Integer>
		implements SeminarioTrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}