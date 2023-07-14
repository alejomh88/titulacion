package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;

@Repository
public class EstudiantesExamenComplexivoPPDaoImpl extends GenericDaoImpl<EstudianteExamenComplexivoPP, Integer>
		implements EstudiantesExamenComplexivoPPDao, Serializable {
	private static final long serialVersionUID = 1L;
}