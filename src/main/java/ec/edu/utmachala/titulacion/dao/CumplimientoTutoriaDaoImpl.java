package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CumplimientoTutoria;

@Repository
public class CumplimientoTutoriaDaoImpl extends GenericDaoImpl<CumplimientoTutoria, String>
		implements CumplimientoTutoriaDao, Serializable {
	private static final long serialVersionUID = 1L;
}