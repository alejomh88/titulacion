package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;

@Repository
public class EstudianteTrabajoTitulacionDaoImpl extends GenericDaoImpl<EstudianteTrabajoTitulacion, Integer>
		implements EstudianteTrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}