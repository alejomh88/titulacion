package ec.edu.utmachala.titulacion.seguridad;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.service.GenericServiceImpl;

@Service
public class AopServiceImpl extends GenericServiceImpl<Aop, Integer> implements AopService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private AopDao aopDao;

	public void insertar(Aop aop) {
		aopDao.insertar(aop);
	}

}