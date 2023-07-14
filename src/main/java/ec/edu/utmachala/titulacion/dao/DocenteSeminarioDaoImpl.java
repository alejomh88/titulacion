package ec.edu.utmachala.titulacion.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.entity.DocenteSeminario;

@Repository
public class DocenteSeminarioDaoImpl extends GenericDaoImpl<DocenteSeminario, Integer>
		implements DocenteSeminarioDao, Serializable {
	private static final long serialVersionUID = 1L;
}