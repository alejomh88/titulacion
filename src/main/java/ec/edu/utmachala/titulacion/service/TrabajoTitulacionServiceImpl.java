package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.TrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.entity.TrabajoTitulacion;

@Service
public class TrabajoTitulacionServiceImpl extends GenericServiceImpl<TrabajoTitulacion, Integer>
		implements TrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private TrabajoTitulacionDao trabajoTitulacionDao;

	public void actualizar(TrabajoTitulacion trabajoTitulacion) {
		trabajoTitulacionDao.actualizar(trabajoTitulacion);
	}

	public void eliminar(TrabajoTitulacion trabajoTitulacion) {
		trabajoTitulacionDao.eliminar(trabajoTitulacion);
	}

	public void insertar(TrabajoTitulacion trabajoTitulacion) {
		trabajoTitulacionDao.insertar(trabajoTitulacion);
	}

}