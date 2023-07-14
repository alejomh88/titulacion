package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Permiso;

@Repository
public class PermisoDaoImpl extends GenericDaoImpl<Permiso, Integer> implements PermisoDao, Serializable {
	private static final long serialVersionUID = 1L;
}