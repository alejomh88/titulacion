package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.UnidadCurricularDao;
import ec.edu.utmachala.titulacion.entity.UnidadCurricular;

@Service
public class UnidadCurricularServiceImpl extends GenericServiceImpl<UnidadCurricular, String>
		implements UnidadCurricularService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UnidadCurricularDao unidadCurricularDao;

	public UnidadCurricular obtenerPorNombre(String nombre) {
		return unidadCurricularDao.obtenerObjeto("select uc from UnidadCurricular uc where uc.nombre=?1",
				new Object[] { nombre }, false, new Object[] {});
	}
}