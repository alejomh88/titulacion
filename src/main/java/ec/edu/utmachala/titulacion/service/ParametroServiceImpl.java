package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.timestamp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.ParametroDao;
import ec.edu.utmachala.titulacion.entity.Parametro;

@Service
public class ParametroServiceImpl implements ParametroService {

	@Autowired
	private ParametroDao parametroDao;

	public void actualizar(Parametro parametro) {
		parametroDao.actualizar(parametro);
	}

	public Parametro obtener() {
		return parametroDao.obtenerObjeto("select p from Parametro p " + "where p.fechaInicio<?1 and p.fechaCierre>=?1",
				new Object[] { timestamp() }, false, new Object[] {});
	}

	public Map<String, String> traerMap(Parametro p) {
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("emailEmisor", p.getEmailEmisor());
		parametros.put("passEmail", p.getPassEmail());
		parametros.put("mailSmtpHost", p.getMailSmtpHost());
		parametros.put("mailSmtpPort", p.getMailSmtpPort());
		parametros.put("mailSmtpAuth", String.valueOf(p.getMailSmtpAuth()));
		parametros.put("mailSmtpSslTrust", p.getMailSmtpSslTrust());
		parametros.put("mailSmtpStartTlsEnable", String.valueOf(p.getMailSmtpStartTlsEnable()));

		return parametros;
	}

}
