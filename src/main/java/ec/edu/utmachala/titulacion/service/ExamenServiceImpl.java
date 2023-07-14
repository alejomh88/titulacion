package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.ExamenDao;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.PreguntaExamen;
import ec.edu.utmachala.titulacion.entity.PreguntaHistorico;
import ec.edu.utmachala.titulacion.entity.TipoExamen;

@Service
public class ExamenServiceImpl extends GenericServiceImpl<Examen, Integer> implements ExamenService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ExamenDao examenDao;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantePeriodoService;

	@Autowired
	private GrupoService grupoService;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantesExamenComplexivoPTService;

	@Autowired
	private TipoExamenService tipoExamenService;

	@Autowired
	private PreguntaService preguntaService;

	@Autowired
	private PreguntaHistoricoService preguntaHistoricoService;

	@Autowired
	private ParametroService parametroService;

	private List<Pregunta> preguntas;

	public void actualizar(Examen examen) {
		examenDao.actualizar(examen);
	}

	public void asignarGrupo() {
		List<Grupo> grupos = grupoService.obtenerLista(
				"select g from Grupo g inner join g.proceso p where p.id='PT-011119' order by g.id", new Object[] {}, 0,
				false, new Object[] {});
		System.out.println(grupos.size());

		for (Grupo grupo : grupos) {
			System.out.println("CC=" + grupo.getComputadoras());
			List<Examen> examenes = examenDao.obtenerLista(
					"select e from Examen e where e.grupo is null order by random()", new Object[] {},
					grupo.getComputadoras(), false, new Object[] {});

			for (Examen exa : examenes) {
				exa.setGrupo(grupo);
				examenDao.actualizar(exa);
			}
		}
	}

	public void cincoPreguntas() {
		List<Examen> list = examenDao.obtenerLista(
				"select e from Examen e "
						+ "inner join fetch e.preguntasExamenes pre inner join e.estudiantePeriodo ep "
						+ "inner join ep.periodoExamen pe inner join pe.carrera c where c.nombre=?1",
				new Object[] { "INGENIERÍA CIVIL" }, 0, false, new Object[] {});
		for (Examen e : list) {
			if (e.getPreguntasExamenes().size() == 45) {
				int n = 0;
				do {
					Pregunta p = preguntaService
							.obtenerPorCarrera(e.getEstudianteExamenComplexivoPT().getCarrera().getId());
					boolean bn = true;
					for (PreguntaExamen pe : e.getPreguntasExamenes()) {
						if (pe.getPregunta().getId() == p.getId()) {
							bn = false;
							break;
						}
					}
					if (bn == true) {
						PreguntaExamen preguntaExamen = new PreguntaExamen();
						// preguntaExamen.setPregunta(p);
						// p.getPreguntasExamenes().add(preguntaExamen);
						// preguntaExamen.setExamen(e);
						// preguntaExamen.setTiempoRestante(p.getNivelDificultad()
						// .getTiempo());
						e.getPreguntasExamenes().add(preguntaExamen);
						n++;
					}
				} while (n < 5);
				actualizar(e);
			}
		}
	}

	public Examen comprobarExamenAperturado(String email, Integer carrera) {
		return examenDao.obtenerObjeto(
				"select e from Examen e left join fetch e.preguntasExamenes pre "
						+ "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
						+ "inner join pe.carrera c inner join ep.estudiante es "
						+ "inner join es.permisos p inner join p.rol r "
						+ "where c.id=?1 and es.email=?2 and pe.fechaInicio<?3 and pe.fechaCierre>=?3 and r.id='ESTU'",
				new Object[] { carrera, email, timestamp() }, false, new Object[] {});
	}

	public Examen comprobarExamenAperturado(String cedula, String carrera) {
		return examenDao.obtenerObjeto("select e from Examen e left join fetch e.preguntasExamenes pre "
				+ "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
				+ "inner join pe.carrera c inner join ep.estudiante es "
				+ "inner join es.permisos p inner join p.rol r where c.nombre=?1 and es.id=?2 and pe.fechaInicio<?3 and pe.fechaCierre>=?3 and r.id='ESTU'",
				new Object[] { carrera, cedula, timestamp() }, false, new Object[] {});
	}

	public Examen crearExamen(String email, Integer carrera) {
		EstudianteExamenComplexivoPT estudiantePeriodo = null;
		// estudiantePeriodoService.obtenerObjeto(
		// "select ep from EstudiantePeriodo ep inner join ep.estudiante u "
		// + "inner join ep.periodoExamen pe inner join pe.carrera c "
		// + "where u.email=?1 and c.id=?2 and pe.fechaInicio<=?3 and
		// pe.fechaCierre>?3",
		// new Object[] { email, carrera, timestamp() }, false, new Object[]
		// {});

		Examen examen = new Examen();
		examen.setEstudianteExamenComplexivoPT(estudiantePeriodo);
		// examen.setIntento(1);
		// examen.setIntentoMaximo(estudiantePeriodo.getPeriodoExamen().getProceso().getNumeroIntentoExamen());
		// examen.setCalificacion(0);
		// examen.setFecha(timestamp());
		// examen.setTiempo(estudiantePeriodo.getProceso().getTiempoExamen());

		preguntas = new ArrayList<Pregunta>();
		if (carrera == 12) {
			preguntas.addAll(
					preguntaService.obtenerSecretariado(carrera, estudiantePeriodo.getProceso().getNumeroTutoria()));
		} else {
			Parametro parametro = parametroService.obtener();
			preguntas.addAll(preguntaService.obtener(carrera, "PROFESIONAL", "ALTA",
					parametro.getNumeroPreguntaProfesionalDificil()));
			preguntas.addAll(preguntaService.obtener(carrera, "PROFESIONAL", "MEDIA",
					parametro.getNumeroPreguntaProfesionalMedio()));
			preguntas.addAll(preguntaService.obtener(carrera, "PROFESIONAL", "SENCILLA",
					parametro.getNumeroPreguntaProfesionalSencillo()));
			preguntas.addAll(
					preguntaService.obtener(carrera, "BÁSICO", "ALTA", parametro.getNumeroPreguntaBasicaDificil()));
			preguntas.addAll(
					preguntaService.obtener(carrera, "BÁSICO", "MEDIA", parametro.getNumeroPreguntaBasicaMedio()));
			preguntas.addAll(preguntaService.obtener(carrera, "BÁSICO", "SENCILLA",
					parametro.getNumeroPreguntaBasicaSencillo()));
			preguntas.addAll(preguntaService.obtener(carrera, "HUMANO", "ALTA",
					parametro.getNumeroPreguntaHumanoTitulacionDificil()));
			preguntas.addAll(preguntaService.obtener(carrera, "HUMANO", "MEDIA",
					parametro.getNumeroPreguntaHumanoTitulacionMedio()));
			preguntas.addAll(preguntaService.obtener(carrera, "HUMANO", "SENCILLA",
					parametro.getNumeroPreguntaHumanoTitulacionSencillo()));
		}
		examen.setPreguntasExamenes(new ArrayList<PreguntaExamen>());
		for (Pregunta p : preguntas) {
			PreguntaExamen preguntaExamen = new PreguntaExamen();
			PreguntaHistorico ph = preguntaHistoricoService.insertar(p);
			// preguntaExamen.setPregunta(ph);
			if (ph.getPreguntasExamenes() == null)
				ph.setPreguntasExamenes(new ArrayList<PreguntaExamen>());
			ph.getPreguntasExamenes().add(preguntaExamen);
			preguntaExamen.setExamen(examen);
			// preguntaExamen.setTiempoRestante(ph.getTiempo());
			examen.getPreguntasExamenes().add(preguntaExamen);
		}

		examenDao.insertar(examen);
		return examen;
	}

	public void crearRegistroExamen() {
		List<EstudianteExamenComplexivoPT> le = estudiantesExamenComplexivoPTService.obtenerLista(
				"select e from EstudianteExamenComplexivoPT e inner join e.proceso p inner join e.carrera c where p.id='PT-011119'",
				new Object[] {}, 0, false, new Object[] {});
		System.out.println("numero de estudiantes: " + le.size());
		TipoExamen te = tipoExamenService.obtenerObjeto("select te from TipoExamen te where te.id='OR'",
				new Object[] {}, false, new Object[] {});
		int cont = 0;
		for (EstudianteExamenComplexivoPT e : le) {
			List<Examen> lx = new ArrayList<Examen>();
			Examen x = new Examen();
			x.setEstudianteExamenComplexivoPT(e);
			x.setTipoExamen(te);
			lx.add(x);
			e.setExamenes(lx);

			estudiantesExamenComplexivoPTService.actualizar(e);
			cont++;
		}
		System.out.println("numero examenes ingresados: " + cont);
	}

	public Examen obtenerCalificacionExamen(int examenId) {
		int calificacion = 0;
		Examen e = examenDao.obtenerObjeto(
				"select e from Examen e inner join fetch e.preguntasExamenes pre where e.id=?1",
				new Object[] { examenId }, false, new Object[] {});
		// for (PreguntaExamen pe : e.getPreguntasExamenes())
		// if (pe.getCorrectaIncorrecta() != null && pe.getCorrectaIncorrecta()
		// == true)
		// calificacion++;
		// e.setCalificacion(calificacion);
		actualizar(e);
		return e;
	}

	public Examen obtenerExamen(String email, Integer carrera) {
		return examenDao.obtenerObjeto(
				"select e from Examen e inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
						+ "inner join pe.carrera c inner join ep.estudiante es "
						+ "inner join es.permisos p inner join p.rol r "
						+ "where c.id=?1 and es.email=?2 and pe.fechaInicio<?3 and pe.fechaCierre>=?3 and r.id='ESTU'",
				new Object[] { carrera, email, timestamp() }, false, new Object[] {});
	}

	public Examen obtenerMejorExamen(String email, Integer carrera) {
		return examenDao.obtenerObjeto(
				"select distinct e from Examen e left join fetch e.preguntasExamenes pre "
						+ "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
						+ "inner join pe.carrera c inner join ep.estudiante es "
						+ "inner join es.permisos p inner join p.rol r "
						+ "where c.id=?1 and es.email=?2 and r.id='ESTU' order by e.calificacion",
				new Object[] { carrera, email }, false, new Object[] {});
	}

	public Examen obtenerPreguntasFaltantes(String email, Integer carrera) {
		return examenDao.obtenerObjeto("select e from Examen e left join fetch e.preguntasExamenes pre "
				+ "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
				+ "inner join pe.carrera c inner join ep.estudiante es "
				+ "inner join es.permisos p inner join p.rol r "
				+ "where pre.correctaIncorrecta is null and c.id=?1 and es.email=?2 and pe.fechaInicio<?3 and pe.fechaCierre>=?3 and r.id='ESTU'",
				new Object[] { carrera, email, timestamp() }, false, new Object[] {});
	}

	public boolean validarVerCalificacionExamen(int examenId) {
		Examen e = examenDao.obtenerObjeto(
				"select e from Examen e inner join fetch e.preguntasExamenes pre " + "where e.id=?1",
				new Object[] { examenId }, false, new Object[] {});
		// if (e.getIntento() >= 2)
		// return false;
		// for (PreguntaExamen pe : e.getPreguntasExamenes())
		// if (pe.getCorrectaIncorrecta() == null)
		// return true;
		return false;
	}

}