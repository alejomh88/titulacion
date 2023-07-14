package ec.edu.utmachala.titulacion.service;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;

public interface EstudianteBibliotecaService extends GenericService<EstudianteBiblioteca, Integer> {

	@Transactional
	public String obtenerCita(EstudianteBiblioteca estudianteBiblioteca);

}