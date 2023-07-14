package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.RolDao;
import ec.edu.utmachala.titulacion.entity.Rol;

@Service
public class RolServiceImpl extends GenericServiceImpl<Rol, String> implements RolService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private RolDao rolDao;

	public Rol obtenerPorNombre(String nombre) {
		return rolDao.obtenerObjeto("select r from Rol r where r.id=?1", new Object[] { nombre }, false,
				new Object[] {});
	}
}