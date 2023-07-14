package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.DocenteSeminarioDao;
import ec.edu.utmachala.titulacion.entity.DocenteSeminario;

@Service
public class DocenteSeminarioServiceImpl extends GenericServiceImpl<DocenteSeminario, Integer>
		implements DocenteSeminarioService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private DocenteSeminarioDao docenteSeminarioDao;

	public void actualizar(DocenteSeminario docenteSeminario) {
		docenteSeminarioDao.actualizar(docenteSeminario);
	}

	public void eliminar(DocenteSeminario docenteSeminario) {
		docenteSeminarioDao.eliminar(docenteSeminario);
	}

	public void insertar(DocenteSeminario docenteSeminario) {
		docenteSeminarioDao.insertar(docenteSeminario);
	}

}