package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.timestamp;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.CarreraDao;
import ec.edu.utmachala.titulacion.entity.Carrera;

@Service
public class CarreraServiceImpl extends GenericServiceImpl<Carrera, Integer> implements CarreraService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraDao carreraDao;

	public List<Carrera> obtenerPorCedula(String cedula) {
		return carreraDao.obtenerLista(
				"select c from Carrera c inner join c.peridosExamenes pe " + "inner join pe.estudiantesPeriodos ep "
						+ "inner join ep.estudiante e "
						+ "where e.id=?1 and pe.fechaInicio<?2 and pe.fechaCierre>=?2 order by c.nombre",
				new Object[] { cedula, timestamp() }, 0, false, new Object[] {});
	}

	public List<Carrera> obtenerPorEmail(String email) {
		return carreraDao.obtenerLista("select distinct c from Carrera c " + "inner join c.peridosExamenes pe "
				+ "inner join pe.estudiantesPeriodos ep " + "inner join ep.estudiante e "
				+ "where e.email=?1 order by c.nombre", new Object[] { email }, 0, false, new Object[] {});
	}

	public Carrera obtenerPorId(int carreraId) {
		return carreraDao.obtenerObjeto("select c from Carrera c " + "where c.id=?1", new Object[] { carreraId }, false,
				new Object[] {});
	}

	public List<Carrera> obtenerPorUnidadAcademica(String unidadAcademica) {
		return carreraDao.obtenerLista(
				"select c from Carrera c inner join c.unidadAcademica ua where ua.id=?1 order by c.nombre",
				new Object[] { unidadAcademica }, 0, false, new Object[] {});
	}

	public List<Carrera> obtenerPorUsuario(String email) {
		return carreraDao.obtenerLista(
				"select c from Carrera c inner join c.peridosExamenes pe " + "inner join pe.estudiantesPeriodos ep "
						+ "inner join ep.estudiante e "
						+ "where e.email=?1 and pe.fechaInicio<?2 and pe.fechaCierre>=?2 order by c.nombre",
				new Object[] { email, timestamp() }, 0, false, new Object[] {});
	}

}