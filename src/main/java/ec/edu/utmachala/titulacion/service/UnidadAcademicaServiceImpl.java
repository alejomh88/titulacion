package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.UnidadAcademicaDao;
import ec.edu.utmachala.titulacion.entity.UnidadAcademica;

@Service
public class UnidadAcademicaServiceImpl extends GenericServiceImpl<UnidadAcademica, String>
		implements UnidadAcademicaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UnidadAcademicaDao unidadAcademicaDao;

	public void actualizar(UnidadAcademica unidadAcademica) {
		unidadAcademicaDao.actualizar(unidadAcademica);
	}

	public List<UnidadAcademica> obtenerTodas() {
		return unidadAcademicaDao.obtenerLista("select ua from UnidadAcademica ua order by ua.nombre", new Object[] {},
				0, false, new Object[] {});
	}

	public List<UnidadAcademica> obtenerUnidades() {
		return unidadAcademicaDao.obtenerLista(
				"select ua from UnidadAcademica ua where ua.id like 'U%' order by ua.nombre ", new Object[] {}, 0,
				false, new Object[] {});
	}

}