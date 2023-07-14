package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Proceso;

@Repository
public class ProcesoDaoImpl extends GenericDaoImpl<Proceso, Integer> implements ProcesoDao, Serializable {
	private static final long serialVersionUID = 1L;
}