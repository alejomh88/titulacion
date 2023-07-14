package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;

public interface CertificadoEstudianteService extends GenericService<CertificadoEstudiante, Integer> {

	@Transactional
	public void actualizar(CertificadoEstudiante certificadoEstudiante);

}