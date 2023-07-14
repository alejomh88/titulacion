package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.timestamp;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.EstudianteExamenComplexivoPTDao;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;

@Service
public class EstudiantesExamenComplexivoPTImpl extends GenericServiceImpl<EstudianteExamenComplexivoPT, Integer>
		implements EstudiantesExamenComplexivoPTService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteExamenComplexivoPTDao estudiantePeriodoDao;

	public void actualizar(EstudianteExamenComplexivoPT estudiantePeriodo) {
		estudiantePeriodoDao.actualizar(estudiantePeriodo);
	}

	public EstudianteExamenComplexivoPT obtenerPeriodoActivoPorCarrera(String email, Integer carrera) {
		return estudiantePeriodoDao.obtenerObjeto(
				"select ep from EstudiantePeriodo ep inner join ep.estudiante u "
						+ "inner join ep.periodoExamen pe inner join pe.carrera c "
						+ "where u.email=?1 and c.id=?2 and pe.fechaInicio<=?3 and pe.fechaCierre>?3",
				new Object[] { email, carrera, timestamp() }, false, new Object[] {});
	}

	public EstudianteExamenComplexivoPT obtenerPeriodoActivoPorCarrera(String cedula, String carrera) {
		return estudiantePeriodoDao.obtenerObjeto(
				"select ep from EstudiantePeriodo ep left join fetch ep.examenes e" + "inner join ep.estudiante u "
						+ "inner join ep.periodoExamen pe inner join pe.carrera c " + "where u.id=?1 and c.nombre=?2",
				new Object[] { cedula, carrera }, false, new Object[] {});
	}

	public EstudianteExamenComplexivoPT obtenerPeriodoPorCarrera(String email, Integer carrera) {
		return estudiantePeriodoDao.obtenerObjeto(
				"select ep from EstudiantePeriodo ep " + "left join fetch ep.temasPracticos tp "
						+ "inner join ep.estudiante u " + "inner join ep.periodoExamen pe " + "inner join pe.carrera c "
						+ "where u.email=?1 and c.id=?2 order by ep.id desc",
				new Object[] { email, carrera }, false, new Object[] {});
	}
}