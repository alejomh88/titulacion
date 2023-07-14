package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.TrabajoTitulacion;

@Repository
public class TrabajoTitulacionDaoImpl extends GenericDaoImpl<TrabajoTitulacion, Integer>
		implements TrabajoTitulacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}