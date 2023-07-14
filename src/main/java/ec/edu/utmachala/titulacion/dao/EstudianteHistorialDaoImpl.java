package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.EstudianteHistorial;

@Repository
public class EstudianteHistorialDaoImpl extends GenericDaoImpl<EstudianteHistorial, String>
		implements EstudianteHistorialDao, Serializable {
	private static final long serialVersionUID = 1L;
}