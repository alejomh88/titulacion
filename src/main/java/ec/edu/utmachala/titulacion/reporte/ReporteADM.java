package ec.edu.utmachala.titulacion.reporte;

import static ec.edu.utmachala.titulacion.utility.UtilsMath.rellenarCeros;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.PosibleRespuesta;
import ec.edu.utmachala.titulacion.entity.Pregunta;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadAcademica;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.entityAux.SiutmachTitulacion;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.PreguntaService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SiutmachTitulacionService;
import ec.edu.utmachala.titulacion.service.UnidadAcademicaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class ReporteADM {

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private SiutmachTitulacionService siutmachTitulacionService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CartillaService cartillaService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private PreguntaService preguntaService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private UnidadAcademicaService unidadAcademicaService;

	@Autowired
	private ProcesoService procesoService;

	private String emailUsuario = "";

	private List<Carrera> listCarreraComplexivo;
	private Integer carreraComplexivo;
	private List<Carrera> listCarreraTrabajoTitulacion;
	private Integer carreraTrabajoTitulacion;

	private String mensaje = "";

	public void actualizarActaGraduacion() {
		List<UnidadAcademica> listUA = unidadAcademicaService.obtenerTodas();
		Proceso p = procesoService.obtenerObjeto("select p from Proceso p where p.fechaInicio<=?1 and p.fechaCierre>?1",
				new Object[] { UtilsDate.timestamp() }, false, new Object[0]);
		for (UnidadAcademica ua : listUA) {
			int secActGra = ua.getSecuenciaActaGraduacion();

			List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua "
							+ "where ua.id=?1 and ett.seminarioTrabajoTitulacion is not null "
							+ "and ett.numeroActaGraduacion is null",
					new Object[] { ua.getId() }, 0, false, new Object[] {});

			for (EstudianteTrabajoTitulacion ett : listETT) {
				ett.setNumeroActaGraduacion(p.getId().replace("-", "") + "/" + String.format("%03d", secActGra));
				System.out.println(ett.getNumeroActaGraduacion());
				estudianteTrabajoTitulacionService.actualizar(ett);
				secActGra++;
			}

			List<EstudianteExamenComplexivoPP> listTP = temaPracticoService.obtenerLista(
					"select tp from TemaPractico tp inner join tp.estudiantePeriodo ep inner join ep.periodoExamen pe inner join pe.carrera c inner join c.unidadAcademica ua "
							+ "where ua.id=?1 and tp.estudiantePeriodo is not null "
							+ "and tp.numeroActaGraduacion is null",
					new Object[] { ua.getId() }, 0, false, new Object[] {});

			for (EstudianteExamenComplexivoPP tp : listTP) {
				tp.setNumeroActaGraduacion(p.getId().replace("-", "") + "/" + String.format("%03d", secActGra));
				System.out.println(tp.getNumeroActaGraduacion());
				temaPracticoService.actualizar(tp);
				secActGra++;
			}

			ua.setSecuenciaActaGraduacion(secActGra);
			unidadAcademicaService.actualizar(ua);
		}
	}

	public void actualizarCalificacionC(EstudianteExamenComplexivoPP tp, int consultaOrdGra) {
		List<Usuario> listEspecialistaESC = usuarioService.obtenerPorSql(
				"SELECT distinct d.* " + "FROM exetasi.calificaciones c "
						+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
						+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "inner join exetasi.\"temasPracticos\" tp on(tp.id=c.\"temaPractico\") "
						+ "inner join exetasi.usuarios d on(d.id=c.especialista) " + "where tp.id=" + tp.getId()
						+ " and fp.id='ESC' and c.\"tipoExamen\"='" + (consultaOrdGra == 1 ? "OR" : "GR") + "';",
				Usuario.class);
		if (listEspecialistaESC.size() == 3) {
			String consultaCartillaEsc = "select distinct cr.id as id, cr.nombre as criterio, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp1, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp2, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp3,"
					+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio " + "from exetasi.calificaciones c "
					+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
					+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
					+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
					+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "where tp.id=" + tp.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
					+ "' and fp.id='ESC' order by cr.id";

			List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
			BigDecimal totalEsc = UtilsMath.newBigDecimal();
			for (Cartilla c : listCartillaEsc) {
				c.setTotalCriterio(UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
				totalEsc = totalEsc.add(c.getTotalCriterio());
			}

			if (consultaOrdGra == 1) {
				if (tp.getCalificacionEscritaOrdinaria().compareTo(totalEsc) != 0)
					mensaje += "\nTP ESC OR: | " + tp.getId() + " DE: " + tp.getCalificacionEscritaOrdinaria() + " A: "
							+ totalEsc;

				// mensaje += "\nTP ESC OR: " +
				// tp.getEstudiantePeriodo().getEstudiante().getId() + " | " +
				// tp.getId()
				// + " DE: " + tp.getCalificacionEscritaOrdinaria() + " A: " +
				// totalEsc;
				tp.setCalificacionEscritaOrdinaria(totalEsc);
			} else {
				System.out.println(tp.getId());
				System.out.println(tp.getCalificacionEscritaGracia());
				System.out.println(totalEsc);
				if (tp.getCalificacionEscritaGracia().compareTo(totalEsc) != 0)
					mensaje += "\nTP ESC GR:  | " + tp.getId() + " DE: " + tp.getCalificacionEscritaGracia() + " A: "
							+ totalEsc;

				// mensaje += "\nTP ESC GR: " +
				// tp.getEstudiantePeriodo().getEstudiante().getId() + " | " +
				// tp.getId()
				// + " DE: " + tp.getCalificacionEscritaGracia() + " A: " +
				// totalEsc;
				tp.setCalificacionEscritaGracia(totalEsc);
			}
			temaPracticoService.actualizar(tp);
		}

		List<Usuario> listEspecialistaORA = usuarioService.obtenerPorSql(
				"SELECT distinct d.* " + "FROM exetasi.calificaciones c "
						+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
						+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
						+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
						+ "inner join exetasi.\"temasPracticos\" tp on(tp.id=c.\"temaPractico\") "
						+ "inner join exetasi.usuarios d on(d.id=c.especialista) " + "where tp.id=" + tp.getId()
						+ " and fp.id='ORA' and c.\"tipoExamen\"='" + (consultaOrdGra == 1 ? "OR" : "GR") + "';",
				Usuario.class);
		if (listEspecialistaORA.size() == 3) {
			String consultaCartillaOral = "select distinct cr.id as id, cr.nombre as criterio, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp1, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp2, "
					+ "COALESCE((select cal.calificacion from exetasi.calificaciones cal "
					+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
					+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
					+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id), 0.00) as esp3, "
					+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio " + "from exetasi.calificaciones c "
					+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
					+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
					+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
					+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "where tp.id=" + tp.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
					+ "' and fp.id='ORA' order by cr.id";

			List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
			BigDecimal totalOral = UtilsMath.newBigDecimal();
			for (Cartilla c : listCartillaEsc) {
				c.setTotalCriterio(UtilsMath.divideCalificaciones(c.getEsp1().add(c.getEsp2()).add(c.getEsp3()), 3));
				totalOral = totalOral.add(c.getTotalCriterio());
			}

			if (consultaOrdGra == 1) {
				if (tp.getCalificacionOralOrdinaria().compareTo(totalOral) != 0)
					mensaje += "\nTP ORA OR:  | " + tp.getId() + " DE: " + tp.getCalificacionOralOrdinaria() + " A: "
							+ totalOral;
				// mensaje += "\nTP ORA OR: " +
				// tp.getEstudiantePeriodo().getEstudiante().getId() + " | " +
				// tp.getId()
				// + " DE: " + tp.getCalificacionOralOrdinaria() + " A: " +
				// totalOral;
				tp.setCalificacionOralOrdinaria(totalOral);
			} else {
				if (tp.getCalificacionOralGracia().compareTo(totalOral) != 0)
					mensaje += "\nTP ORA GR:  | " + tp.getId() + " DE: " + tp.getCalificacionOralGracia() + " A: "
							+ totalOral;
				// mensaje += "\nTP ORA GR: " +
				// tp.getEstudiantePeriodo().getEstudiante().getId() + " | " +
				// tp.getId()
				// + " DE: " + tp.getCalificacionOralGracia() + " A: " +
				// totalOral;

				tp.setCalificacionOralGracia(totalOral);
			}
			temaPracticoService.actualizar(tp);
		}
	}

	public void actualizarCalificaciones() {
		List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett where ett.seminarioTrabajoTitulacion is not null",
				new Object[] {}, 0, false, new Object[] {});

		for (EstudianteTrabajoTitulacion ett : listETT) {
			actualizarCalificacionTT(ett);
		}

		List<EstudianteExamenComplexivoPP> listTP = temaPracticoService.obtenerLista(
				"select tp from TemaPractico tp where tp.estudiantePeriodo is not null", new Object[] {}, 0, false,
				new Object[] {});

		for (EstudianteExamenComplexivoPP tp : listTP) {
			actualizarCalificacionC(tp, 1);
			actualizarCalificacionC(tp, 2);
		}

		System.out.println(mensaje);
	}

	public void actualizarCalificacionTT(EstudianteTrabajoTitulacion ett) {
		List<Usuario> listEspecialistaESC = usuarioService.obtenerPorSql("SELECT distinct d.* "
				+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(ctt.\"indicadorTrabajoTitulacion\"=i.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
				+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) " + "where ett.id=" + ett.getId()
				+ " and fp.id='ESC';", Usuario.class);
		if (listEspecialistaESC.size() == 3) {
			String consultaCartillaEsc = "select distinct cr.id as id, cr.nombre as criterio, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista1 and cr1.id=cr.id), 0.00) as esp1, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista2 and cr1.id=cr.id), 0.00) as esp2, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista3 and cr1.id=cr.id), 0.00) as esp3, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente1\" and cr1.id=cr.id), 0.00) as espSupl1, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente2\" and cr1.id=cr.id), 0.00) as espSupl2, "
					+ "0.00 as totalCriterio " + "from exetasi.\"calificacionesTrabajosTitulacion\" c "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(c.\"estudianteTrabajoTitulacion\"=ett.id) "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(c.\"indicadorTrabajoTitulacion\"=i.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "where ett.id=" + ett.getId() + " and fp.id='ESC' order by cr.id";

			List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
			BigDecimal totalEsc = UtilsMath.newBigDecimal();
			for (Cartilla c : listCartillaEsc) {
				c.setTotalCriterio(UtilsMath.divideCalificaciones(
						c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()), 3));
				totalEsc = totalEsc.add(c.getTotalCriterio());
			}

			if (ett.getCalificacionEscrita().compareTo(totalEsc) != 0)
				mensaje += "\nETT ESC: " + ett.getEstudiante().getId() + " | " + ett.getId() + " DE: "
						+ ett.getCalificacionEscrita() + " A: " + totalEsc;
			ett.setCalificacionEscrita(totalEsc);

			estudianteTrabajoTitulacionService.actualizar(ett);
		}

		List<Usuario> listEspecialistaORA = usuarioService.obtenerPorSql("SELECT distinct d.* "
				+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
				+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(ctt.\"indicadorTrabajoTitulacion\"=i.id) "
				+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
				+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
				+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
				+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) " + "where ett.id=" + ett.getId()
				+ " and fp.id='ORA';", Usuario.class);
		if (listEspecialistaORA.size() == 3) {
			String consultaCartillaOral = "select distinct cr.id as id, cr.nombre as criterio, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista1 and cr1.id=cr.id), 0.00) as esp1, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista2 and cr1.id=cr.id), 0.00) as esp2, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.especialista3 and cr1.id=cr.id), 0.00) as esp3, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente1\" and cr1.id=cr.id), 0.00) as espSupl1, "
					+ "COALESCE((select cal.calificacion from exetasi.\"calificacionesTrabajosTitulacion\" cal "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" ind on(cal.\"indicadorTrabajoTitulacion\"=ind.id) "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(cal.\"estudianteTrabajoTitulacion\"=ett1.id) "
					+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr1 on(ind.\"criterioTrabajoTitulacion\"=cr1.id) "
					+ "where ett1.id=ett.id and u1.id=ett.\"especialistaSuplente2\" and cr1.id=cr.id), 0.00) as espSupl2, "
					+ "0.00 as totalCriterio " + "from exetasi.\"calificacionesTrabajosTitulacion\" c "
					+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(c.\"estudianteTrabajoTitulacion\"=ett.id) "
					+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(c.\"indicadorTrabajoTitulacion\"=i.id) "
					+ "inner join exetasi.\"criteriosTrabajosTitulacion\" cr on(i.\"criterioTrabajoTitulacion\"=cr.id) "
					+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
					+ "where ett.id=" + ett.getId() + " and fp.id='ORA' order by cr.id";

			List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
			BigDecimal totalOral = UtilsMath.newBigDecimal();
			for (Cartilla c : listCartillaEsc) {
				c.setTotalCriterio(UtilsMath.divideCalificaciones(
						c.getEsp1().add(c.getEsp2()).add(c.getEsp3()).add(c.getEspSupl1()).add(c.getEspSupl2()), 3));
				totalOral = totalOral.add(c.getTotalCriterio());
			}

			if (ett.getCalificacionOral().compareTo(totalOral) != 0)
				mensaje += "\nETT ORA: " + ett.getEstudiante().getId() + " | " + ett.getId() + " DE: "
						+ ett.getCalificacionOral() + " A: " + totalOral;

			ett.setCalificacionOral(totalOral);

			estudianteTrabajoTitulacionService.actualizar(ett);
		}
	}

	public Integer getCarreraComplexivo() {
		return carreraComplexivo;
	}

	public Integer getCarreraTrabajoTitulacion() {
		return carreraTrabajoTitulacion;
	}

	public List<Carrera> getListCarreraComplexivo() {
		return listCarreraComplexivo;
	}

	public List<Carrera> getListCarreraTrabajoTitulacion() {
		return listCarreraTrabajoTitulacion;
	}

	public void imagenes() {
		List<Pregunta> preguntas = preguntaService.obtenerLista(
				"select p from Pregunta p inner join fetch p.posiblesRespuestas pr where pr.imagenDescripcion is not null and pr.imagenDescripcion!='null'",
				new Object[] {}, 0, false, new Object[0]);
		for (Pregunta p : preguntas) {
			for (PosibleRespuesta pr : p.getPosiblesRespuestas()) {
				if (pr.getImagenDescripcion() != null && pr.getImagenDescripcion() != "null"
						&& !pr.getImagenDescripcion().isEmpty()) {
					File f = new File(pr.getImagenDescripcion());
					if (!f.exists()) {
						System.out.println(p.getId() + "--EL ARCHIVO NO EXISTE");
						pr.setImagenDescripcion(null);
						p.setActivo(false);
						preguntaService.actualizar(p);
						break;
					}
				}
			}
		}
	}

	@PostConstruct
	public void init() {
		emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();

		listCarreraComplexivo = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, Integer.valueOf(0), false, new Object[0]);
		if (listCarreraComplexivo.size() == 1) {
			carreraComplexivo = listCarreraComplexivo.get(0).getId();
		}
		listCarreraTrabajoTitulacion = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, Integer.valueOf(0), false, new Object[0]);
		if (listCarreraTrabajoTitulacion.size() == 1) {
			carreraTrabajoTitulacion = listCarreraTrabajoTitulacion.get(0).getId();
		}

	}

	public void reporteSiutmachComplexivo() {
		List<SiutmachTitulacion> listTemasPracticos = siutmachTitulacionService.obtenerPorSql("select e.id as id, "
				+ "translate(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end, 'ÁÉÍÓÚ', 'AEIOU') as \"titulo\", "
				+ "translate(mc.\"tipoTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"tipoTitulo\", translate(mc.\"detalleTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"detalleTipo\", "
				+ "translate(tp.\"tituloInvestigacion\", 'ÁÉÍÓÚ', 'AEIOU') as \"tituloInvestigacion\", "
				+ "(ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\") \"notaFinal\", "
				+ "tp.\"calificacionEscritaOrdinaria\" as \"notaEscrita\", "
				+ "tp.\"calificacionOralOrdinaria\" as \"notaOral\", " + "ex.calificacion as \"notaTeorica\", "
				+ "tp.\"fechaInicioClase\" as \"fechaInicioClase\", " + "tp.\"fechaFinClase\" as \"fechaFinClase\", "
				+ "tp.\"numeroActaIncorporacion\" as \"numeroActaIncorporacion\" "
				+ "from exetasi.\"temasPracticos\" tp "
				+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
				+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
				+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
				+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
				+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
				+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
				+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
				+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") " + "where c.id="
				+ carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp() + "' and p.\"fechaCierre\">'"
				+ UtilsDate.timestamp() + "' and ex.calificacion>=20 and (tp.\"validarCalificacionOrdinaria\"=true "
				+ "and (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\")>=69.5) "
				+ "and tp.\"tituloInvestigacion\" is not null and tp.\"numeroActaIncorporacion\" is not null "
				+ "order by COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre",
				SiutmachTitulacion.class);
		listTemasPracticos.addAll(siutmachTitulacionService.obtenerPorSql("select e.id as id, "
				+ "translate(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end, 'ÁÉÍÓÚ', 'AEIOU') as \"titulo\", "
				+ "translate(mc.\"tipoTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"tipoTitulo\", translate(mc.\"detalleTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"detalleTipo\", "
				+ "translate(tp.\"tituloInvestigacion\", 'ÁÉÍÓÚ', 'AEIOU') as \"tituloInvestigacion\", "
				+ "(ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\") \"notaFinal\", "
				+ "tp.\"calificacionEscritaGracia\" as \"notaEscrita\", "
				+ "tp.\"calificacionOralGracia\" as \"notaOral\", " + "ex.calificacion as \"notaTeorica\", "
				+ "tp.\"fechaInicioClase\" as \"fechaInicioClase\", " + "tp.\"fechaFinClase\" as \"fechaFinClase\", "
				+ "tp.\"numeroActaIncorporacion\" as \"numeroActaIncorporacion\" "
				+ "from exetasi.\"temasPracticos\" tp "
				+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
				+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
				+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
				+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
				+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
				+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
				+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
				+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") " + "where c.id="
				+ carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp() + "' and p.\"fechaCierre\">'"
				+ UtilsDate.timestamp() + "' and ex.calificacion>=20 and (tp.\"validarCalificacionGracia\"=true "
				+ "and (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\")>=69.5) "
				+ "and tp.\"tituloInvestigacion\" is not null and tp.\"numeroActaIncorporacion\" is not null "
				+ "order by COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre",
				SiutmachTitulacion.class));
		if (!listTemasPracticos.isEmpty()) {
			List<String> list = new ArrayList<String>();
			list.add(
					"Cedula;FechaInicio;FechaEgreso;NumeroActa;Titulo;TipoTitulo;DetalleTipo;TituloDeLaInvestigacion;NotaFinal;NotaEscrita;NotaOral;NotaTeorica");
			for (SiutmachTitulacion st : listTemasPracticos) {
				list.add(st.getId() + ";" + UtilsDate.fechaFormatoString(st.getFechaInicioClase(), "yyyy-MM-dd") + ";"
						+ UtilsDate.fechaFormatoString(st.getFechaFinClase(), "yyyy-MM-dd") + ";"
						+ st.getNumeroActaIncorporacion() + ";" + st.getTitulo() + ";" + st.getTipoTitulo() + ";"
						+ st.getDetalleTipo() + ";" + st.getTituloInvestigacion() + ";"
						+ rellenarCeros(st.getNotaFinal()) + ";" + rellenarCeros(st.getNotaEscrita()) + ";"
						+ rellenarCeros(st.getNotaOral()) + ";" + rellenarCeros(st.getNotaTeorica()));
			}
			Carrera c = carreraService.obtenerObjeto("select  c from Carrera c where c.id=?1",
					new Object[] { carreraComplexivo }, false, new Object[0]);
			reporteService.generarReporteCSV(list,
					"SubirSiutmach-" + c.getUnidadAcademica().getId() + "-" + c.getNombre() + "-C");
		}
	}

	public void reporteSiutmachTrabajoTitulacion() {
		List<SiutmachTitulacion> listEstudianteTrabajoTitulacion = siutmachTitulacionService.obtenerPorSql(
				"select e.id as id, "
						+ "translate(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end, 'ÁÉÍÓÚ', 'AEIOU') as \"titulo\", "
						+ "translate(mc.\"tipoTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"tipoTitulo\", translate(mc.\"detalleTitulo\", 'ÁÉÍÓÚ', 'AEIOU') as \"detalleTipo\", "
						+ "translate(ett.\"tituloInvestigacion\", 'ÁÉÍÓÚ', 'AEIOU') as \"tituloInvestigacion\", "
						+ "(ett.\"calificacionEscrita\"+ett.\"calificacionOral\") \"notaFinal\", "
						+ "ett.\"calificacionEscrita\" as \"notaEscrita\", "
						+ "ett.\"calificacionOral\" as \"notaOral\", 0.00 as \"notaTeorica\", "
						+ "ett.\"fechaInicioClase\" as \"fechaInicioClase\", "
						+ "ett.\"fechaFinClase\" as \"fechaFinClase\", "
						+ "ett.\"numeroActaIncorporacion\" as \"numeroActaIncorporacion\" "
						+ "from exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios e on(e.id=ett.estudiante) "
						+ "inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") "
						+ "inner join exetasi.seminarios s on(s.id=stt.seminario) "
						+ "inner join exetasi.procesos p on(p.id=s.proceso) "
						+ "inner join exetasi.carreras c on(c.id=ett.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=ett.\"fechaInicioClase\" and mc.\"fechaCierre\">ett.\"fechaInicioClase\") "
						+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
						+ "where c.id=" + carreraTrabajoTitulacion + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp()
						+ "' and (ett.\"validarCalificacion\"=true "
						+ "and (ett.\"calificacionEscrita\"+ett.\"calificacionOral\")>=69.5) "
						+ "and ett.\"tituloInvestigacion\" is not null and ett.\"numeroActaIncorporacion\" is not null "
						+ "order by COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre",
				SiutmachTitulacion.class);
		if (!listEstudianteTrabajoTitulacion.isEmpty()) {
			List<String> list = new ArrayList<String>();
			list.add(
					"Cedula;FechaInicio;FechaEgreso;NumeroActa;Titulo;TipoTitulo;DetalleTipo;TituloDeLaInvestigacion;NotaFinal;NotaEscrita;NotaOral");
			for (SiutmachTitulacion st : listEstudianteTrabajoTitulacion) {
				list.add(st.getId() + ";" + UtilsDate.fechaFormatoString(st.getFechaInicioClase(), "yyyy-MM-dd") + ";"
						+ UtilsDate.fechaFormatoString(st.getFechaFinClase(), "yyyy-MM-dd") + ";"
						+ st.getNumeroActaIncorporacion() + ";" + st.getTitulo() + ";" + st.getTipoTitulo() + ";"
						+ st.getDetalleTipo() + ";" + st.getTituloInvestigacion() + ";"
						+ rellenarCeros(st.getNotaFinal()) + ";" + rellenarCeros(st.getNotaEscrita()) + ";"
						+ rellenarCeros(st.getNotaOral()));
			}
			Carrera c = carreraService.obtenerObjeto("select  c from Carrera c where c.id=?1",
					new Object[] { carreraTrabajoTitulacion }, false, new Object[0]);
			reporteService.generarReporteCSV(list,
					"SubirSiutmach-" + c.getUnidadAcademica().getId() + "-" + c.getNombre() + "-TT");
		}
	}

	public void setCarreraComplexivo(Integer carreraComplexivo) {
		this.carreraComplexivo = carreraComplexivo;
	}

	public void setCarreraTrabajoTitulacion(Integer carreraTrabajoTitulacion) {
		this.carreraTrabajoTitulacion = carreraTrabajoTitulacion;
	}

	public void setListCarreraComplexivo(List<Carrera> listCarreraComplexivo) {
		this.listCarreraComplexivo = listCarreraComplexivo;
	}

	public void setListCarreraTrabajoTitulacion(List<Carrera> listCarreraTrabajoTitulacion) {
		this.listCarreraTrabajoTitulacion = listCarreraTrabajoTitulacion;
	}

}
