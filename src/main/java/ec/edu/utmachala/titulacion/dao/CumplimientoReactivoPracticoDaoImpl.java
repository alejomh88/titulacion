package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entityAux.CumplimientoReactivoPractico;

@Repository
public class CumplimientoReactivoPracticoDaoImpl extends GenericDaoImpl<CumplimientoReactivoPractico, Integer>
		implements CumplimientoReactivoPracticoDao, Serializable {
	private static final long serialVersionUID = 1L;
}