package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Carrera;

public interface CarreraService extends GenericService<Carrera, Integer> {
	@Transactional(readOnly = true)
	public List<Carrera> obtenerPorCedula(String cedula);

	@Transactional(readOnly = true)
	public List<Carrera> obtenerPorEmail(String email);

	@Transactional(readOnly = true)
	public Carrera obtenerPorId(int carreraId);

	@Transactional(readOnly = true)
	public List<Carrera> obtenerPorUnidadAcademica(String unidadAcademica);

	@Transactional(readOnly = true)
	public List<Carrera> obtenerPorUsuario(String email);
}