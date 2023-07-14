package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.dao.SecuenciaActaGraduacionDao;
import ec.edu.utmachala.titulacion.entity.SecuenciaActaGraduacion;

@Service
public class SecuenciaActaGraduacionServiceImpl extends GenericServiceImpl<SecuenciaActaGraduacion, Integer>
		implements SecuenciaActaGraduacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired // para inyectar la dependencia
	private SecuenciaActaGraduacionDao secuenciaActaGraduacionDao;

	@Transactional // readonly solo para leer como los select
	public void insertar(SecuenciaActaGraduacion secuenciaActaGraduacion) {
		secuenciaActaGraduacionDao.insertar(secuenciaActaGraduacion);
	}

}