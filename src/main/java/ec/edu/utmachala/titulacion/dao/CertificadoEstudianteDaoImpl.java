package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;

@Repository
public class CertificadoEstudianteDaoImpl extends GenericDaoImpl<CertificadoEstudiante, Integer>
		implements CertificadoEstudianteDao, Serializable {
	private static final long serialVersionUID = 1L;
}