package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;

@Repository
public class EstudianteExamenComplexivoPTDaoImpl extends GenericDaoImpl<EstudianteExamenComplexivoPT, Integer>
		implements EstudianteExamenComplexivoPTDao, Serializable {
	private static final long serialVersionUID = 1L;
}