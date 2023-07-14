package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.SeminarioDao;
import ec.edu.utmachala.titulacion.entity.Seminario;

@Service
public class SeminarioServiceImpl extends GenericServiceImpl<Seminario, Integer>
		implements SeminarioService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SeminarioDao seminarioDao;

	public void actualizar(Seminario seminario) {
		seminarioDao.actualizar(seminario);
	}

	public void eliminar(Seminario seminario) {
		seminarioDao.eliminar(seminario);
	}

	public void insertar(Seminario seminario) {
		seminarioDao.insertar(seminario);
	}
}