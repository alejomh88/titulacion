package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.Certificado;

@Service
public class CertificadoServiceImpl extends GenericServiceImpl<Certificado, String>
		implements CertificadoService, Serializable {

	private static final long serialVersionUID = 1L;

}