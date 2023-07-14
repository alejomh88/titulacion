package ec.edu.utmachala.titulacion.service;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.MallaCurricular;

public interface MallaCurricularService extends GenericService<MallaCurricular, Integer> {

	@Transactional(readOnly = true)
	public MallaCurricular obtenerVigentePorAlumno(int carreraId, Date fechaInicio);

	@Transactional(readOnly = true)
	public MallaCurricular obtenerVigentePorCarrera(String carrera);
}