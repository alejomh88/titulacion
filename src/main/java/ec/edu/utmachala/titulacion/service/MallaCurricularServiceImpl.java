package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.MallaCurricularDao;
import ec.edu.utmachala.titulacion.entity.MallaCurricular;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Service
public class MallaCurricularServiceImpl extends GenericServiceImpl<MallaCurricular, Integer>
		implements MallaCurricularService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private MallaCurricularDao mallaCurricularDao;

	public MallaCurricular obtenerVigentePorAlumno(int carreraId, Date fechaInicio) {
		return mallaCurricularDao.obtenerObjeto(
				"select m from MallaCurricular m inner join m.carrera c where c.id=?1 and m.fechaInicio<=?2 and m.fechaCierre>?2 ",
				new Object[] { carreraId, UtilsDate.timestamp(fechaInicio) }, false, new Object[] {});
	}

	public MallaCurricular obtenerVigentePorCarrera(String carrera) {
		return mallaCurricularDao.obtenerObjeto(
				"select m from MallaCurricular m inner join m.carrera c where c.nombre=?1", new Object[] { carrera },
				false, new Object[] {});
	}
}