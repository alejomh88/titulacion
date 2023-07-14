package ec.edu.utmachala.titulacion.seguridad;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.service.GenericServiceImpl;

@Service
public class LogServiceImpl extends GenericServiceImpl<Log, Integer> implements LogService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private LogDao logDao;

	public void insertar(Log log) {
		logDao.insertar(log);
	}

}