package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.dao.SecuenciaActaCalificacionDao;
import ec.edu.utmachala.titulacion.entity.SecuenciaActaCalificacion;

@Service
public class SecuenciaActaCalificacionServiceImpl extends GenericServiceImpl<SecuenciaActaCalificacion, Integer>
		implements SecuenciaActaCalificacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired // para inyectar la dependencia
	private SecuenciaActaCalificacionDao secuenciaActaCalificacionDao;

	@Transactional // readonly solo para leer como los select
	public void insertar(SecuenciaActaCalificacion secuenciaActaCalificacion) {
		secuenciaActaCalificacionDao.insertar(secuenciaActaCalificacion);
	}

}