package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.GrupoDao;
import ec.edu.utmachala.titulacion.entity.Grupo;

@Service
public class GrupoServiceImpl extends GenericServiceImpl<Grupo, Integer> implements GrupoService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private GrupoDao grupoDao;

	public void actualizar(Grupo grupo) {
		grupoDao.actualizar(grupo);
	}

	public void insertar(Grupo grupo) {
		grupoDao.insertar(grupo);
	}

}