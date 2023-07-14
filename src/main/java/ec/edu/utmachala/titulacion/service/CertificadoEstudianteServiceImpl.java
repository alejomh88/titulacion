package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CertificadoEstudianteDao;
import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;

@Service
public class CertificadoEstudianteServiceImpl extends GenericServiceImpl<CertificadoEstudiante, Integer>
		implements CertificadoEstudianteService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CertificadoEstudianteDao certificadoEstudianteDao;

	public void actualizar(CertificadoEstudiante certificadoEstudiante) {
		certificadoEstudianteDao.actualizar(certificadoEstudiante);
	}

}