package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.PreguntaDao;
import ec.edu.utmachala.titulacion.entity.Pregunta;

@Service
public class PreguntaServiceImpl extends GenericServiceImpl<Pregunta, Integer>
		implements PreguntaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PreguntaDao preguntaDao;

	public void actualizar(Pregunta pregunta) {
		preguntaDao.actualizar(pregunta);
	}

	public void insertar(Pregunta pregunta) {
		preguntaDao.insertar(pregunta);
	}

	public List<Pregunta> obtener(Integer carrera, String ejeProfesional, Integer numeroPreguntas) {
		return preguntaDao.obtenerLista(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr "
						+ "inner join p.unidadDidactica ud inner join ud.asignatura a "
						+ "inner join a.mallaCurricular mc inner join mc.carreraMallaProceso cmp "
						+ "inner join cmp.carrera c inner join a.unidadCurricular uc inner join p.nivelDificultad nd "
						+ "where c.id=?1 and uc.nombre=?2 and p.activo=true order by rand()",
				new Object[] { carrera, ejeProfesional }, numeroPreguntas, false, new Object[] {});
	}

	public List<Pregunta> obtener(Integer carrera, String ejeProfesional, String dificultad, Integer numeroPreguntas) {
		return preguntaDao.obtenerLista(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr "
						+ "inner join p.unidadDidactica ud inner join ud.asignatura a "
						+ "inner join a.mallaCurricular mc inner join mc.carreraMallaProceso cmp "
						+ "inner join cmp.carrera c inner join a.unidadCurricular uc inner join p.nivelDificultad nd "
						+ "where c.id=?1 and uc.nombre=?2 and nd.nombre=?3 and p.activo=true order by rand()",
				new Object[] { carrera, ejeProfesional, dificultad }, numeroPreguntas, false, new Object[] {});
	}

	public Pregunta obtenerPorCarrera(Integer carrera) {
		return preguntaDao.obtenerObjeto(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr "
						+ "inner join p.unidadDidactica ud inner join ud.asignatura a "
						+ "inner join a.mallaCurricular mc inner join mc.carreraMallaProceso cmp "
						+ "inner join cmp.carrera c inner join a.unidadCurricular uc inner join p.nivelDificultad nd "
						+ "where c.id=?1 and p.activo=true order by rand()",
				new Object[] { carrera }, false, new Object[] {});
	}

	public Pregunta obtenerPorId(Integer id) {
		return preguntaDao.obtenerObjeto(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr where p.id=?1", new Object[] { id },
				false, new Object[] {});
	}

	public List<Pregunta> obtenerSecretariado(Integer carrera, Integer numeroPreguntas) {
		return preguntaDao.obtenerLista(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr "
						+ "inner join p.unidadDidactica ud inner join ud.asignatura a "
						+ "inner join a.mallaCurricular mc inner join mc.carrera c "
						+ "where c.id=?1 and p.activo=true order by rand()",
				new Object[] { carrera }, numeroPreguntas, false, new Object[] {});
	}
}