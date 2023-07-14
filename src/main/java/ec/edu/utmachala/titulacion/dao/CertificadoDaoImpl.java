package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.Certificado;

@Repository
public class CertificadoDaoImpl extends GenericDaoImpl<Certificado, String> implements CertificadoDao, Serializable {
	private static final long serialVersionUID = 1L;
}