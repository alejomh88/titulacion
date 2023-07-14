package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.NivelDificultadDao;
import ec.edu.utmachala.titulacion.entity.NivelDificultad;

@Service
public class NivelDificultadServiceImpl extends GenericServiceImpl<NivelDificultad, String>
		implements NivelDificultadService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private NivelDificultadDao nivelDificultadDao;

	public NivelDificultad obtenerPorNombre(String nombre) {
		return nivelDificultadDao.obtenerObjeto("select nd from NivelDificultad nd where nd.nombre=?1",
				new Object[] { nombre }, false, new Object[] {});
	}

	public List<NivelDificultad> obtenerTodos() {
		return nivelDificultadDao.obtenerLista("select nd from NivelDificultad nd", new Object[] {}, 0, false,
				new Object[] {});
	}
}