package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.ActaGraduacion;

@Repository
public class ActaGraduacionDaoImpl extends GenericDaoImpl<ActaGraduacion, String>
		implements ActaGraduacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}