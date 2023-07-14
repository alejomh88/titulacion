package ec.edu.utmachala.titulacion.seguridad;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

import ec.edu.utmachala.titulacion.dao.GenericDaoImpl;

@Repository
public class AopDaoImpl extends GenericDaoImpl<Aop, Integer> implements AopDao, Serializable {
	private static final long serialVersionUID = 1L;
}