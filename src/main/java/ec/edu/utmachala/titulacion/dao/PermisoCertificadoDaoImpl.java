package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.PermisoCertificado;

@Repository
public class PermisoCertificadoDaoImpl extends GenericDaoImpl<PermisoCertificado, Integer>
		implements PermisoCertificadoDao, Serializable {
	private static final long serialVersionUID = 1L;
}