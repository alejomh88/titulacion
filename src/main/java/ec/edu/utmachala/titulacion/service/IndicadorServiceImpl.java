package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.IndicadorDao;
import ec.edu.utmachala.titulacion.entity.Indicador;

@Service
public class IndicadorServiceImpl extends GenericServiceImpl<Indicador, Integer>
		implements IndicadorService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private IndicadorDao temaPracticoDao;

	public void actualizar(Indicador indicador) {
		temaPracticoDao.actualizar(indicador);
	}

	public void insertar(Indicador indicador) {
		temaPracticoDao.insertar(indicador);
	}

}