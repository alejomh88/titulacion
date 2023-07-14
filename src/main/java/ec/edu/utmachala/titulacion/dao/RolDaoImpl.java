package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Rol;

@Repository
public class RolDaoImpl extends GenericDaoImpl<Rol, String> implements RolDao, Serializable {
	private static final long serialVersionUID = 1L;
}