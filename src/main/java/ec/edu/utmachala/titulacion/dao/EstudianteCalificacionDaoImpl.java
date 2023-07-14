package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.EstudianteCalificacion;

@Repository
public class EstudianteCalificacionDaoImpl extends GenericDaoImpl<EstudianteCalificacion, String>
		implements EstudianteCalificacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}