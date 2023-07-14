package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Usuario;

@Repository
public class UsuarioDaoImpl extends GenericDaoImpl<Usuario, String> implements UsuarioDao, Serializable {
	private static final long serialVersionUID = 1L;
}