package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.PeriodoReactivoDao;
import ec.edu.utmachala.titulacion.entity.PeriodoReactivo;

@Service
public class PeriodoReactivoServiceImpl extends GenericServiceImpl<PeriodoReactivo, Integer>
		implements PeriodoReactivoService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PeriodoReactivoDao periodoReactivoDao;

	public PeriodoReactivo obtenerPorPeriodoYCarrera(String carrera) {
		return periodoReactivoDao.obtenerObjeto(
				"select pr from PeriodoReactivo pr " + "inner join pr.carrera c " + "where c.nombre=?1",
				new Object[] { carrera }, false, new Object[] {});
	}
}