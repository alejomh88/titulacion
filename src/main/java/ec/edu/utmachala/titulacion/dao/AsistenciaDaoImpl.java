package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.Asistencia;

@Repository
public class AsistenciaDaoImpl extends GenericDaoImpl<Asistencia, String> implements AsistenciaDao, Serializable {
	private static final long serialVersionUID = 1L;
}