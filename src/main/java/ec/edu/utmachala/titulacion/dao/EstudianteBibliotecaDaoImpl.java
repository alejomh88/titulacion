package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;

@Repository
public class EstudianteBibliotecaDaoImpl extends GenericDaoImpl<EstudianteBiblioteca, Integer>
		implements EstudianteBibliotecaDao, Serializable {
	private static final long serialVersionUID = 1L;
}