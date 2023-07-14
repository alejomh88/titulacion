package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.PreguntaHistorico;

@Repository
public class PreguntaHistoricoDaoImpl extends GenericDaoImpl<PreguntaHistorico, Integer>
		implements PreguntaHistoricoDao, Serializable {
	private static final long serialVersionUID = 1L;
}