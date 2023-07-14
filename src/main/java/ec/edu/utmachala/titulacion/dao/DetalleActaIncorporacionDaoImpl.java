package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.DetalleActaIncorporacion;

@Repository
public class DetalleActaIncorporacionDaoImpl extends GenericDaoImpl<DetalleActaIncorporacion, String>
		implements DetalleActaIncorporacionDao, Serializable {
	private static final long serialVersionUID = 1L;
}