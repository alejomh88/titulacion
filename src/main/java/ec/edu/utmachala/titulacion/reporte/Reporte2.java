package ec.edu.utmachala.titulacion.reporte;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.html2text;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoDate;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;
import static ec.edu.utmachala.titulacion.utility.UtilsMath.rellenarCeros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.ActaGraduacion;
import ec.edu.utmachala.titulacion.entityAux.ActaIncorporacion;
import ec.edu.utmachala.titulacion.entityAux.ActaTemasTitulosResolucion;
import ec.edu.utmachala.titulacion.entityAux.CantidadReactivo;
import ec.edu.utmachala.titulacion.entityAux.Caratula;
import ec.edu.utmachala.titulacion.entityAux.Cartilla;
import ec.edu.utmachala.titulacion.entityAux.Cuestionario;
import ec.edu.utmachala.titulacion.entityAux.CumplimientoReactivo;
import ec.edu.utmachala.titulacion.entityAux.DetalleActaIncorporacion;
import ec.edu.utmachala.titulacion.entityAux.DetalleActaTemasTitulosResolucion;
import ec.edu.utmachala.titulacion.entityAux.DistribucionAsignatura;
import ec.edu.utmachala.titulacion.entityAux.EstudianteCalificacion;
import ec.edu.utmachala.titulacion.entityAux.NumeroFechaActaIncorporacion;
import ec.edu.utmachala.titulacion.entityAux.Texto;
import ec.edu.utmachala.titulacion.entityAux.TutoriaAux;
import ec.edu.utmachala.titulacion.service.ActaGraduacionService;
import ec.edu.utmachala.titulacion.service.CantidadReactivoService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CartillaService;
import ec.edu.utmachala.titulacion.service.CuestionarioService;
import ec.edu.utmachala.titulacion.service.CumplimientoReactivoService;
import ec.edu.utmachala.titulacion.service.DetalleActaIncorporacionService;
import ec.edu.utmachala.titulacion.service.DistribucionAsignaturaService;
import ec.edu.utmachala.titulacion.service.EstudianteCalificacionService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaService;
import ec.edu.utmachala.titulacion.service.NumeroFechaActaIncorporacionService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.Utils;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class Reporte2 {

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CuestionarioService cuestionarioService;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private CartillaService cartillaService;

	@Autowired
	private EstudianteCalificacionService estudianteCalificacionService;

	@Autowired
	private FechaService fechaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private DetalleActaIncorporacionService detalleActaIncorporacionService;

	@Autowired
	private DistribucionAsignaturaService distribucionAsignaturaService;

	@Autowired
	private CumplimientoReactivoService cumplimientoReactivoService;

	@Autowired
	private NumeroFechaActaIncorporacionService numeroFechaActaIncorporacionService;

	@Autowired
	private ActaGraduacionService actaGraduacionService;

	@Autowired
	private CantidadReactivoService cantidadReactivoService;

	private String emailUsuario = "";

	private List<Carrera> listCarreraComplexivo;
	private List<Carrera> listCarreraTrabajoTitulacion;
	private List<Carrera> listCarreras;

	private Integer carreraComplexivo;
	private Integer carreraTrabajoTitulacion;
	private Integer carreraDistribucionAsignatura;
	private Integer carreraCumplimientoReactivo;
	private Integer carreraCuestionario;

	private List<Texto> listFechasComplexivo;
	private String TextoActaComplexivo;
	private List<Texto> listFechasTrabajoTitulacion;
	private String fechaActaTrabajoTitulacion;
	private int consultaOrdGra;
	private int consultaNomCal;
	private int consultaPdfExcel;
	private int reporteCTT;
	private List<NumeroFechaActaIncorporacion> listNumeroFechaActaIncorporacion;
	private String numeroActa;
	private Date fechaAptitud;

	private List<Proceso> procesos;
	private Proceso proceso;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});
		proceso = procesos.get(0);

		emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();

		consultaOrdGra = 1;
		consultaNomCal = 1;
		consultaPdfExcel = 1;
		reporteCTT = 1;
		fechaAptitud = new Date();

		listCarreraComplexivo = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, 0, false, new Object[0]);
		if (listCarreraComplexivo.size() == 1) {
			carreraComplexivo = listCarreraComplexivo.get(0).getId();
			consultarFechaC();
			consultarNumeroActa();
		}
		listCarreraTrabajoTitulacion = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, 0, false, new Object[0]);
		if (listCarreraTrabajoTitulacion.size() == 1) {
			carreraTrabajoTitulacion = listCarreraTrabajoTitulacion.get(0).getId();
			consultarFechaTT();
		}

		listCarreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { emailUsuario }, 0, false, new Object[0]);
		if (listCarreras.size() == 1)
			carreraDistribucionAsignatura = listCarreras.get(0).getId();
	}

	public void consultarFechaC() {
		if (consultaOrdGra == 1)
			listFechasComplexivo = fechaService.obtenerPorSql(
					"SELECT distinct trim(to_char(date_part('day', tp.\"fechaSustentacionOrdinaria\" ), '00'))||'-'||trim(to_char(date_part('month', tp.\"fechaSustentacionOrdinaria\" ), '00'))||'-'||trim(to_char(date_part('year', tp.\"fechaSustentacionOrdinaria\" ), '0000')) as id FROM exetasi.\"temasPracticos\" tp "
							+ "inner join exetasi.\"estudiantesPeriodos\" ep on(tp.\"estudiantePeriodo\"=ep.id) "
							+ "inner join exetasi.\"periodosExamenes\" pe on(ep.\"periodoExamen\"=pe.id) "
							+ "inner join exetasi.carreras c on(pe.carrera=c.id) "
							+ "where tp.\"fechaSustentacionOrdinaria\" is not null and c.id=" + carreraComplexivo
							+ " order by trim(to_char(date_part('day', tp.\"fechaSustentacionOrdinaria\" ), '00'))||'-'||trim(to_char(date_part('month', tp.\"fechaSustentacionOrdinaria\" ), '00'))||'-'||trim(to_char(date_part('year', tp.\"fechaSustentacionOrdinaria\" ), '0000'));",
					Texto.class);
		if (consultaOrdGra == 2)
			listFechasComplexivo = fechaService.obtenerPorSql(
					"SELECT distinct trim(to_char(date_part('day', tp.\"fechaSustentacionGracia\" ), '00'))||'-'||trim(to_char(date_part('month', tp.\"fechaSustentacionGracia\" ), '00'))||'-'||trim(to_char(date_part('year', tp.\"fechaSustentacionGracia\" ), '0000')) as id FROM exetasi.\"temasPracticos\" tp "
							+ "inner join exetasi.\"estudiantesPeriodos\" ep on(tp.\"estudiantePeriodo\"=ep.id) "
							+ "inner join exetasi.\"periodosExamenes\" pe on(ep.\"periodoExamen\"=pe.id) "
							+ "inner join exetasi.carreras c on(pe.carrera=c.id) "
							+ "where tp.\"fechaSustentacionGracia\" is not null and c.id=" + carreraComplexivo + " "
							+ "order by trim(to_char(date_part('day', tp.\"fechaSustentacionGracia\" ), '00'))||'-'||trim(to_char(date_part('month', tp.\"fechaSustentacionGracia\" ), '00'))||'-'||trim(to_char(date_part('year', tp.\"fechaSustentacionGracia\" ), '0000'));",
					Texto.class);
	}

	public void consultarFechaTT() {
		listFechasTrabajoTitulacion = fechaService.obtenerPorSql(
				"SELECT distinct trim(to_char(date_part('day', ett.\"fechaSustentacion\" ), '00'))||'-'||trim(to_char(date_part('month', ett.\"fechaSustentacion\" ), '00'))||'-'||trim(to_char(date_part('year', ett.\"fechaSustentacion\" ), '0000')) as id "
						+ "FROM exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.carreras c on(ett.carrera=c.id) "
						+ "where ett.\"fechaSustentacion\" is not null and c.id=" + carreraTrabajoTitulacion + " "
						+ "order by trim(to_char(date_part('day', ett.\"fechaSustentacion\" ), '00'))||'-'||trim(to_char(date_part('month', ett.\"fechaSustentacion\" ), '00'))||'-'||trim(to_char(date_part('year', ett.\"fechaSustentacion\" ), '0000'));",
				Texto.class);
	}

	public void consultarNumeroActa() {
		listNumeroFechaActaIncorporacion = numeroFechaActaIncorporacionService.obtenerPorSql(
				"select tp.\"numeroActaIncorporacion\"||'¬'||tp.\"fechaActaIncorporacion\" as id, tp.\"numeroActaIncorporacion\" as \"numeroActa\", tp.\"fechaActaIncorporacion\" as \"fechaActa\" "
						+ "from exetasi.\"temasPracticos\" tp "
						+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
						+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
						+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
						+ "inner join exetasi.carreras c on(c.id=pe.carrera) " + "where c.id=" + carreraComplexivo
						+ " and p.\"fechaInicio\"<='" + UtilsDate.timestamp() + "' and p.\"fechaCierre\">'"
						+ UtilsDate.timestamp() + "' and tp.\"numeroActaIncorporacion\" is not null " + "union "
						+ "select ett.\"numeroActaIncorporacion\"||'¬'||ett.\"fechaActaIncorporacion\" as id, ett.\"numeroActaIncorporacion\" as \"numeroActa\", ett.\"fechaActaIncorporacion\" as \"fechaActa\" "
						+ "from exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") "
						+ "inner join exetasi.seminarios s on(s.id=stt.seminario) "
						+ "inner join exetasi.procesos p on(p.id=s.proceso) "
						+ "inner join exetasi.carreras c on(c.id=ett.carrera) " + "where c.id=" + carreraComplexivo
						+ " and p.\"fechaInicio\"<='" + UtilsDate.timestamp() + "' and p.\"fechaCierre\">'"
						+ UtilsDate.timestamp() + "' and ett.\"numeroActaIncorporacion\" is not null " + "order by id",
				NumeroFechaActaIncorporacion.class);
	}

	public Integer getCarreraComplexivo() {
		return carreraComplexivo;
	}

	public Integer getCarreraCuestionario() {
		return carreraCuestionario;
	}

	public Integer getCarreraCumplimientoReactivo() {
		return carreraCumplimientoReactivo;
	}

	public Integer getCarreraDistribucionAsignatura() {
		return carreraDistribucionAsignatura;
	}

	public Integer getCarreraTrabajoTitulacion() {
		return carreraTrabajoTitulacion;
	}

	public int getConsultaNomCal() {
		return consultaNomCal;
	}

	public int getConsultaOrdGra() {
		return consultaOrdGra;
	}

	public int getConsultaPdfExcel() {
		return consultaPdfExcel;
	}

	// public String getFechaActaComplexivo() {
	// return fechaActaComplexivo;
	// }

	public String getFechaActaTrabajoTitulacion() {
		return fechaActaTrabajoTitulacion;
	}

	public Date getFechaAptitud() {
		return fechaAptitud;
	}

	public List<Carrera> getListCarreraComplexivo() {
		return listCarreraComplexivo;
	}

	public List<Carrera> getListCarreras() {
		return listCarreras;
	}

	public List<Carrera> getListCarreraTrabajoTitulacion() {
		return listCarreraTrabajoTitulacion;
	}

	public List<Texto> getListFechasComplexivo() {
		return listFechasComplexivo;
	}

	public List<Texto> getListFechasTrabajoTitulacion() {
		return listFechasTrabajoTitulacion;
	}

	public List<NumeroFechaActaIncorporacion> getListNumeroFechaActaIncorporacion() {
		return listNumeroFechaActaIncorporacion;
	}

	public String getNumeroActa() {
		return numeroActa;
	}

	public Proceso getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public int getReporteCTT() {
		return reporteCTT;
	}

	public void reporteActaGraduacion() {
		String consultaActaGraduacion = "select e.id as id, 'EC' as tipo, ua.nombre as \"unidadAcademica\", c.nombre as carrera, tp.\"numeroActaGraduacion\", cast(tp.\"fechaSustentacionOrdinaria\" as Date) as \"fechaSustentacion\", '' as \"fechaSustentacion1\", "
				+ "esp1.id||'¬'||COALESCE(esp1.\"apellidoPaterno\"||' ', '') || COALESCE(esp1.\"apellidoMaterno\"||' ', '') || esp1.nombre as especialista1, esp2.id||'¬'||COALESCE(esp2.\"apellidoPaterno\"||' ', '') || COALESCE(esp2.\"apellidoMaterno\"||' ', '') || esp2.nombre as especialista2, esp3.id||'¬'||COALESCE(esp3.\"apellidoPaterno\"||' ', '') || COALESCE(esp3.\"apellidoMaterno\"||' ', '') || esp3.nombre as especialista3, "
				+ "tp.\"tituloInvestigacion\", (CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo , "
				+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, ex.calificacion as \"calExamen\", tp.\"calificacionEscritaOrdinaria\" as \"calEscrita\", tp.\"calificacionOralOrdinaria\" as \"calOral\", (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\") as \"calTotal\", "
				+ "ua.\"cedulaSecretarioAbogado\"||'¬'||ua.\"nombreSecretarioAbogado\" as \"secretarioAbogado\" "
				+ "from exetasi.\"temasPracticos\" tp inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") inner join exetasi.usuarios e on(e.id=ep.estudiante) inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") inner join exetasi.procesos p on(p.id=pe.proceso) inner join exetasi.carreras c on(c.id=pe.carrera) inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
				+ "inner join exetasi.usuarios esp1 on (esp1.id=tp.especialista1) inner join exetasi.usuarios esp2 on (esp2.id=tp.especialista2) inner join exetasi.usuarios esp3 on (esp3.id=tp.especialista3) "
				+ "where c.id=%d and p.\"fechaInicio\"<='%s' and p.\"fechaCierre\">'%s' "
				+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionOrdinaria\"=true and (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\")>=69.5)) "
				+ "union "
				+ "select e.id as id, 'EC' as tipo, ua.nombre as \"unidadAcademica\", c.nombre as carrera, tp.\"numeroActaGraduacion\", cast(tp.\"fechaSustentacionGracia\" as Date) as \"fechaSustentacion\", '' as \"fechaSustentacion1\", "
				+ "esp1.id||'¬'||COALESCE(esp1.\"apellidoPaterno\"||' ', '') || COALESCE(esp1.\"apellidoMaterno\"||' ', '') || esp1.nombre as especialista1, esp2.id||'¬'||COALESCE(esp2.\"apellidoPaterno\"||' ', '') || COALESCE(esp2.\"apellidoMaterno\"||' ', '') || esp2.nombre as especialista2, esp3.id||'¬'||COALESCE(esp3.\"apellidoPaterno\"||' ', '') || COALESCE(esp3.\"apellidoMaterno\"||' ', '') || esp3.nombre as especialista3, "
				+ "tp.\"tituloInvestigacion\", (CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo , "
				+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, ex.calificacion as \"calExamen\", tp.\"calificacionEscritaGracia\" as \"calEscrita\", tp.\"calificacionOralGracia\" as \"calOral\", (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\") \"calTotal\", "
				+ "ua.\"cedulaSecretarioAbogado\"||'¬'||ua.\"nombreSecretarioAbogado\" as \"secretarioAbogado\" "
				+ "from exetasi.\"temasPracticos\" tp inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") inner join exetasi.usuarios e on(e.id=ep.estudiante) inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") inner join exetasi.procesos p on(p.id=pe.proceso) inner join exetasi.carreras c on(c.id=pe.carrera) inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
				+ "inner join exetasi.usuarios esp1 on (esp1.id=tp.especialista1) inner join exetasi.usuarios esp2 on (esp2.id=tp.especialista2) inner join exetasi.usuarios esp3 on (esp3.id=tp.especialista3) "
				+ "where c.id=%d and p.\"fechaInicio\"<='%s' and p.\"fechaCierre\">'%s' "
				+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionGracia\"=true and (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\")>=69.5)) "
				+ "union "
				+ "select e.id as id, 'TT' as tipo, ua.nombre as \"unidadAcademica\", c.nombre as carrera, ett.\"numeroActaGraduacion\", cast(ett.\"fechaSustentacion\" as Date) as \"fechaSustentacion\", '' as \"fechaSustentacion1\", "
				+ "(SELECT distinct d.id||'¬'||COALESCE(d.\"apellidoPaterno\"||' ', '') || COALESCE(d.\"apellidoMaterno\"||' ', '') || d.nombre as especialista1 "
				+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(ett1.id=ctt.\"estudianteTrabajoTitulacion\") "
				+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) where ett1.id=ett.id limit 1 offset 0), "
				+ "(SELECT distinct d.id||'¬'||COALESCE(d.\"apellidoPaterno\"||' ', '') || COALESCE(d.\"apellidoMaterno\"||' ', '') || d.nombre as especialista2 "
				+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(ett1.id=ctt.\"estudianteTrabajoTitulacion\") "
				+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) where ett1.id=ett.id limit 1 offset 1), "
				+ "(SELECT distinct d.id||'¬'||COALESCE(d.\"apellidoPaterno\"||' ', '') || COALESCE(d.\"apellidoMaterno\"||' ', '') || d.nombre as especialista3 "
				+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt inner join exetasi.\"estudiantesTrabajosTitulacion\" ett1 on(ett1.id=ctt.\"estudianteTrabajoTitulacion\") "
				+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) where ett1.id=ett.id limit 1 offset 2), "
				+ "ett.\"tituloInvestigacion\", (CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo, "
				+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, 0.00 as \"calExamen\", ett.\"calificacionEscrita\" as \"calEscrita\", ett.\"calificacionOral\" as \"calOral\", (ett.\"calificacionEscrita\"+ett.\"calificacionOral\") \"calTotal\", "
				+ "ua.\"cedulaSecretarioAbogado\"||'¬'||ua.\"nombreSecretarioAbogado\" as \"secretarioAbogado\" "
				+ "from exetasi.\"estudiantesTrabajosTitulacion\" ett inner join exetasi.usuarios e on(e.id=ett.estudiante) inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") inner join exetasi.seminarios s on(s.id=stt.seminario) inner join exetasi.procesos p on(p.id=s.proceso) inner join exetasi.carreras c on(c.id=ett.carrera) inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=ett.\"fechaInicioClase\" and mc.\"fechaCierre\">ett.\"fechaInicioClase\") "
				+ "where c.id=%d and p.\"fechaInicio\"<='%s' and p.\"fechaCierre\">'%s' "
				+ "and (ett.\"validarCalificacion\"=true and (ett.\"calificacionEscrita\"+ett.\"calificacionOral\")>=69.5) "
				+ "order by estudiante";
		String fech = UtilsDate.timestamp().toString();
		consultaActaGraduacion = String.format(consultaActaGraduacion, carreraComplexivo, fech, fech, carreraComplexivo,
				fech, fech, carreraComplexivo, fech, fech);
		List<ActaGraduacion> list = actaGraduacionService.obtenerPorSql(consultaActaGraduacion, ActaGraduacion.class);
		if (!list.isEmpty()) {
			Carrera c = carreraService.obtenerObjeto("select c from Carrera c where c.id=?1",
					new Object[] { carreraComplexivo }, false, new Object[0]);
			for (ActaGraduacion ag : list) {
				ag.setCalTotal(UtilsMath.redondearCalificacion(ag.getCalTotal()));
				ag.setFechaSustentacion1(UtilsDate.convertirDateATexto(ag.getFechaSustentacion()));
			}
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(list, parametro, "ActaGraduacion",
					"ActaGraduacion-" + c.getUnidadAcademica().getId() + "-" + c.getNombre());
		}
	}

	public void reporteActaIncorporacion() {
		List<DetalleActaIncorporacion> listEstudiantes = detalleActaIncorporacionService
				.obtenerPorSql("select e.id as cedula, "
						+ "upper(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre) as estudiante, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo "
						+ "from exetasi.\"temasPracticos\" tp "
						+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
						+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
						+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
						+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
						+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
						+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' and tp.\"numeroActaIncorporacion\"='"
						+ numeroActa.split("¬")[0] + "' and ex.calificacion>=20 "
						+ "and (tp.\"validarCalificacionOrdinaria\"=true and (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\")>=69.5) "
						+ "union " + "select e.id as cedula, "
						+ "upper(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre) as estudiante, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo "
						+ "from exetasi.\"temasPracticos\" tp "
						+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
						+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
						+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
						+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
						+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
						+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' and tp.\"numeroActaIncorporacion\"='"
						+ numeroActa.split("¬")[0] + "' and ex.calificacion>=20 "
						+ "and (tp.\"validarCalificacionGracia\"=true and (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\")>=69.5) "
						+ "union " + "select e.id as cedula, "
						+ "upper(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre) as estudiante, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as titulo "
						+ "from exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios e on(e.id=ett.estudiante) "
						+ "inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") "
						+ "inner join exetasi.seminarios s on(s.id=stt.seminario) "
						+ "inner join exetasi.procesos p on(p.id=s.proceso) "
						+ "inner join exetasi.carreras c on(c.id=ett.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=ett.\"fechaInicioClase\" and mc.\"fechaCierre\">ett.\"fechaInicioClase\") "
						+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp()
						+ "' and ett.\"numeroActaIncorporacion\"='" + numeroActa.split("¬")[0]
						+ "' and (ett.\"validarCalificacion\"=true and (ett.\"calificacionEscrita\"+ett.\"calificacionOral\")>=69.5) "
						+ "order by estudiante", DetalleActaIncorporacion.class);

		if (!listEstudiantes.isEmpty()) {
			List<ActaIncorporacion> list = new ArrayList<ActaIncorporacion>();
			Carrera c = carreraService.obtenerObjeto("select c from Carrera c where c.id=?1",
					new Object[] { carreraComplexivo }, false, new Object[0]);
			ActaIncorporacion ai = new ActaIncorporacion();
			ai.setUnidadAcademica(c.getUnidadAcademica().getNombre());
			ai.setCarrera(c.getNombre());
			ai.setNumeroActa(numeroActa.split("¬")[0]);
			ai.setFechaActa(fechaFormatoString(fechaFormatoDate(numeroActa.split("¬")[1], "yyyy-MM-dd"),
					"dd 'de' MMMM 'de' yyyy"));
			ai.setDecano(c.getUnidadAcademica().getNombreDecano());
			ai.setSecretarioAbogado(c.getUnidadAcademica().getNombreSecretarioAbogado());
			ai.setListDetalleActaIncorporacion(listEstudiantes);

			list.add(ai);
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(list, parametro, "ActaIncorporacion", "ActaIncorporacion-"
					+ c.getUnidadAcademica().getId() + "-" + c.getNombre() + "-" + numeroActa.split("¬")[0]);
		}
	}

	public void reporteActasCalificacionComplexivo() {
		List<EstudianteExamenComplexivoPP> listTemasPracticos = new ArrayList<EstudianteExamenComplexivoPP>();
		// if (consultaOrdGra == 1) {
		// listTemasPracticos = temaPracticoService.obtenerLista(
		// "select tp from TemaPractico tp inner join tp.estudiantePeriodo ep "
		// + "inner join ep.estudiante e inner join ep.periodoExamen pe inner
		// join pe.carrera c "
		// + "inner join tp.especialista1 esp "
		// + "where c.id=?1 and tp.fechaSustentacionOrdinaria>=?2 and
		// tp.fechaSustentacionOrdinaria<=?3 "
		// + "and tp.especialista1 is not null and tp.especialista2 is not null
		// and tp.especialista3 is not null "
		// + "order by tp.fechaSustentacionOrdinaria",
		// new Object[] { carreraComplexivo,
		// UtilsDate.fechaFormatoDate(fechaActaComplexivo, "dd-MM-yyyy"),
		// UtilsDate.dateCompleto(UtilsDate.fechaFormatoDate(fechaActaComplexivo,
		// "dd-MM-yyyy")) },
		// 0, false, new Object[] {});
		// } else if (consultaOrdGra == 2) {
		// listTemasPracticos = this.temaPracticoService.obtenerLista(
		// "select tp from TemaPractico tp inner join tp.estudiantePeriodo ep "
		// + "inner join ep.estudiante e inner join ep.periodoExamen pe inner
		// join pe.carrera c "
		// + "where c.id=?1 and tp.fechaSustentacionGracia>=?2 and
		// tp.fechaSustentacionGracia<=?3 "
		// + "and tp.especialista1 is not null and tp.especialista2 is not null
		// and tp.especialista3 is not null "
		// + "order by tp.fechaSustentacionGracia",
		// new Object[] { carreraComplexivo,
		// UtilsDate.fechaFormatoDate(fechaActaComplexivo, "dd-MM-yyyy"),
		// UtilsDate.dateCompleto(UtilsDate.fechaFormatoDate(fechaActaComplexivo,
		// "dd-MM-yyyy")) },
		// 0, false, new Object[0]);
		// }
		try {
			List<File> listFile = new ArrayList<File>();
			Carrera cr = null;
			if (!listTemasPracticos.isEmpty()) {
				// cr =
				// listTemasPracticos.get(0).getEstudiantePeriodo().getPeriodoExamen().getCarrera();
				cr = null;
				Caratula c = new Caratula();
				c.setUnidadAcademica(cr.getUnidadAcademica().getNombre());
				c.setCarrera(cr.getNombre());
				if (consultaOrdGra == 1)
					c.setGenero(true);
				else if (consultaOrdGra == 2)
					c.setGenero(false);
				c.setSecretarioAbogado(cr.getUnidadAcademica().getCedulaSecretarioAbogado() + " - "
						+ cr.getUnidadAcademica().getNombreSecretarioAbogado().toUpperCase());
				for (EstudianteExamenComplexivoPP tp : listTemasPracticos) {
					// Usuario estu = tp.getEstudiantePeriodo().getEstudiante();
					Usuario estu = null;
					// c.setAutor1(estu.getId() + " - "
					// + (estu.getApellidoNombre());
					// c.setGradoAcademico(tp.getEstudiantePeriodo().getPeriodoExamen().getProceso().getId());
					c.setTitulo(tp.getTituloInvestigacion() == null ? "" : tp.getTituloInvestigacion().toUpperCase());
					if (consultaOrdGra == 1)
						c.setFechaSustentacion(
								fechaFormatoString(tp.getFechaSustentacionOrdinaria(), "dd 'de' MMMM 'de' yyyy HH:mm"));
					else if (consultaOrdGra == 2)
						c.setFechaSustentacion(
								fechaFormatoString(tp.getFechaSustentacionGracia(), "dd 'de' MMMM 'de' yyyy HH:mm"));

					Map<String, Object> parametro = new HashMap<String, Object>();
					parametro.put("logoUnidadAcademica", cr.getUnidadAcademica().getId());

					List<Caratula> list = new ArrayList<Caratula>();
					list.add(c);

					String consultaCartillaEsc = "select distinct cr.id as id, "
							+ "cr.nombre as criterio, (select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp1, "
							+ "(select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp2, "
							+ "(select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp3,"
							+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio "
							+ "from exetasi.calificaciones c "
							+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
							+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
							+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
							+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
							+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
							+ "where tp.id=" + tp.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
							+ "' and fp.id='ESC' order by cr.id";
					String consultaCartillaOral = "select distinct cr.id as id, "
							+ "cr.nombre as criterio, (select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista1 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp1, "
							+ "(select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista2 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp2, "
							+ "(select cal.calificacion from exetasi.calificaciones cal "
							+ "inner join exetasi.indicadores ind on(cal.indicador=ind.id) "
							+ "inner join exetasi.\"temasPracticos\" tp1 on(cal.\"temaPractico\"=tp1.id) "
							+ "inner join exetasi.usuarios u1 on(cal.especialista=u1.id) "
							+ "inner join exetasi.criterios cr1 on(ind.criterio=cr1.id) "
							+ "where tp1.id=tp.id and u1.id=tp.especialista3 and cr1.id=cr.id and cal.\"tipoExamen\"=te.id) as esp3, "
							+ "0.00 as espSupl1, 0.00 as espSupl2, 0.00 as totalCriterio "
							+ "from exetasi.calificaciones c "
							+ "inner join exetasi.\"temasPracticos\" tp on(c.\"temaPractico\"=tp.id) "
							+ "inner join exetasi.\"tiposExamenes\" te on(c.\"tipoExamen\"=te.id) "
							+ "inner join exetasi.indicadores i on(c.indicador=i.id) "
							+ "inner join exetasi.criterios cr on(i.criterio=cr.id) "
							+ "inner join exetasi.\"formasPresentaciones\" fp on(cr.\"formaPresentacion\"=fp.id) "
							+ "where tp.id=" + tp.getId() + " and te.id='" + (consultaOrdGra == 1 ? "OR" : "GR")
							+ "' and fp.id='ORA' order by cr.id";

					List<Cartilla> listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
					List<Cartilla> listCartillaOral = cartillaService.obtenerPorSql(consultaCartillaOral,
							Cartilla.class);

					Usuario tutor1 = tp.getEspecialista1();
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 1);
					int i = 1;
					for (Cartilla car : listCartillaEsc) {
						parametro.put("calCri" + i, car.getEsp1());
						i++;
					}
					File actaIndEscrEsp1 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvComplexivo", "espc1oral");
					i = 1;
					for (Cartilla car : listCartillaOral) {
						parametro.put("calCri" + i, car.getEsp1());
						i++;
					}
					File actaIndOralEsp1 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvComplexivo", "espc1oral");
					tutor1 = tp.getEspecialista2();
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 2);
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						parametro.put("calCri" + i, car.getEsp2());
						i++;
					}
					File actaIndEscrEsp2 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvComplexivo", "espc2oral");
					i = 1;
					for (Cartilla car : listCartillaOral) {
						parametro.put("calCri" + i, car.getEsp2());
						i++;
					}
					File actaIndOralEsp2 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvComplexivo", "espc2oral");
					tutor1 = tp.getEspecialista3();
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 3);
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						parametro.put("calCri" + i, car.getEsp3());
						i++;
					}
					File actaIndEscrEsp3 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvComplexivo", "espc3oral");
					i = 1;
					for (Cartilla car : listCartillaOral) {
						parametro.put("calCri" + i, car.getEsp3());
						i++;
					}
					File actaIndOralEsp3 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvComplexivo", "espc3oral");

					Usuario tutorCons1 = tp.getEspecialista1();
					// c.setTutor1(tutorCons1.getId() + " - "
					// + (tutorCons1.getApellidoPaterno() == null ? ""
					// : tutorCons1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons1.getApellidoMaterno() == null ? ""
					// : tutorCons1.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons1.getNombre().toUpperCase());
					Usuario tutorCons2 = tp.getEspecialista2();
					// c.setTutor2(tutorCons2.getId() + " - "
					// + (tutorCons2.getApellidoPaterno() == null ? ""
					// : tutorCons2.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons2.getApellidoMaterno() == null ? ""
					// : tutorCons2.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons2.getNombre().toUpperCase());
					Usuario tutorCons3 = tp.getEspecialista3();
					// c.setTutor3(tutorCons3.getId() + " - "
					// + (tutorCons3.getApellidoPaterno() == null ? ""
					// : tutorCons3.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons3.getApellidoMaterno() == null ? ""
					// : tutorCons3.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons3.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						if (car.getEsp1() != null && car.getEsp2() != null && car.getEsp3() != null)
							parametro.put("calCri" + i, UtilsMath
									.divideCalificaciones(car.getEsp1().add(car.getEsp2()).add(car.getEsp3()), 3));
						i++;
					}
					File actaConEscr = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrConsComplexivo", "ConEscr");
					i = 1;
					for (Cartilla car : listCartillaOral) {
						if (car.getEsp1() != null && car.getEsp2() != null && car.getEsp3() != null)
							parametro.put("calCri" + i, UtilsMath
									.divideCalificaciones(car.getEsp1().add(car.getEsp2()).add(car.getEsp3()), 3));
						i++;
					}
					File actaConOral = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralConsComplexivo", "ConOral");

					// Unir pdf por alumno
					File fileEstu = File.createTempFile("fileEstu", ".pdf");
					PdfCopyFields concat = new PdfCopyFields(new FileOutputStream(fileEstu));
					PdfReader pdfActaIndEscrEsp1 = new PdfReader(new FileInputStream(actaIndEscrEsp1));
					PdfReader pdfActaIndOralEsp1 = new PdfReader(new FileInputStream(actaIndOralEsp1));
					PdfReader pdfActaIndEscrEsp2 = new PdfReader(new FileInputStream(actaIndEscrEsp2));
					PdfReader pdfActaIndOralEsp2 = new PdfReader(new FileInputStream(actaIndOralEsp2));
					PdfReader pdfActaIndEscrEsp3 = new PdfReader(new FileInputStream(actaIndEscrEsp3));
					PdfReader pdfActaIndOralEsp3 = new PdfReader(new FileInputStream(actaIndOralEsp3));
					PdfReader pdfActaConEscr = new PdfReader(new FileInputStream(actaConEscr));
					PdfReader pdfActaConOral = new PdfReader(new FileInputStream(actaConOral));

					concat.addDocument(pdfActaIndEscrEsp1);
					concat.addDocument(pdfActaIndOralEsp1);
					concat.addDocument(pdfActaIndEscrEsp2);
					concat.addDocument(pdfActaIndOralEsp2);
					concat.addDocument(pdfActaIndEscrEsp3);
					concat.addDocument(pdfActaIndOralEsp3);
					concat.addDocument(pdfActaConEscr);
					concat.addDocument(pdfActaConOral);
					concat.close();
					listFile.add(fileEstu);
				}

				// añadir pdf al archivo final
				File file = File.createTempFile("listaActas-" + cr.getUnidadAcademica().getId() + "-" + cr.getNombre(),
						".pdf");
				PdfCopyFields concatFinal = new PdfCopyFields(new FileOutputStream(file));
				for (File f : listFile) {
					PdfReader pdfFile = new PdfReader(new FileInputStream(f));
					concatFinal.addDocument(pdfFile);
				}
				concatFinal.close();

				reporteService.responderServidor(file,
						"ACTAS DE CALIFICACION COMPLEXIVO-" + cr.getUnidadAcademica().getId() + "-" + cr.getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reporteActasCalificacionTrabajoTitulacion() {
		List<EstudianteTrabajoTitulacion> listETT = new ArrayList<EstudianteTrabajoTitulacion>();
		listETT = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.carrera c "
						+ "inner join ett.opcionTitulacion ot "
						+ "where c.id=?1 and ett.fechaSustentacion>=?2 and ett.fechaSustentacion<=?3 "
						+ "and ett.especialista1 is not null and ett.especialista2 is not null and ett.especialista3 is not null and ot.id is not null "
						+ "order by ett.fechaSustentacion",
				new Object[] { carreraTrabajoTitulacion,
						UtilsDate.fechaFormatoDate(fechaActaTrabajoTitulacion, "dd-MM-yyyy"),
						UtilsDate.dateCompleto(UtilsDate.fechaFormatoDate(fechaActaTrabajoTitulacion, "dd-MM-yyyy")) },
				0, false, new Object[] {});
		try {
			List<File> listFile = new ArrayList<File>();
			Carrera cr = null;
			if (!listETT.isEmpty()) {

				cr = listETT.get(0).getCarrera();
				Caratula c = new Caratula();
				c.setUnidadAcademica(cr.getUnidadAcademica().getNombre());
				c.setCarrera(cr.getNombre());
				c.setGenero(true);
				c.setSecretarioAbogado(cr.getUnidadAcademica().getCedulaSecretarioAbogado() + " - "
						+ cr.getUnidadAcademica().getNombreSecretarioAbogado().toUpperCase());
				for (EstudianteTrabajoTitulacion ett : listETT) {
					Usuario estu = ett.getEstudiante();
					// c.setAutor1(estu.getId() + " - "
					// + (estu.getApellidoPaterno() == null ? "" :
					// estu.getApellidoPaterno().toUpperCase()) + " "
					// + (estu.getApellidoMaterno() == null ? "" :
					// estu.getApellidoMaterno().toUpperCase()) + " "
					// + estu.getNombre().toUpperCase());
					c.setGradoAcademico(ett.getSeminarioTrabajoTitulacion() == null ? "PT-030615"
							: ett.getSeminarioTrabajoTitulacion().getSeminario().getProceso().getId());
					c.setTitulo(
							ett.getTituloInvestigacion() == null ? " " : ett.getTituloInvestigacion().toUpperCase());
					c.setFechaSustentacion(
							fechaFormatoString(ett.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy HH:mm"));
					List<Caratula> list = new ArrayList<Caratula>();
					list.add(c);

					List<Cartilla> listCartillaEsc = new ArrayList<Cartilla>();
					List<Cartilla> listCartillaOral = new ArrayList<Cartilla>();
					Map<String, Object> parametro = new HashMap<String, Object>();
					parametro.put("logoUnidadAcademica", cr.getUnidadAcademica().getId());

					List<Usuario> listEspecialistaEsc = usuarioService.obtenerPorSql("SELECT distinct d.* "
							+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
							+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(i.id=ctt.\"indicadorTrabajoTitulacion\") "
							+ "inner join exetasi.\"criteriosTrabajosTitulacion\" c on(c.id=i.\"criterioTrabajoTitulacion\") "
							+ "inner join exetasi.\"formasPresentaciones\" fp on(fp.id=c.\"formaPresentacion\") "
							+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
							+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) "
							+ "where fp.id='ESC' and ett.id=" + ett.getId() + ";", Usuario.class);
					List<Usuario> listEspecialistaOral = usuarioService.obtenerPorSql("SELECT distinct d.* "
							+ "FROM exetasi.\"calificacionesTrabajosTitulacion\" ctt "
							+ "inner join exetasi.\"indicadoresTrabajosTitulacion\" i on(i.id=ctt.\"indicadorTrabajoTitulacion\") "
							+ "inner join exetasi.\"criteriosTrabajosTitulacion\" c on(c.id=i.\"criterioTrabajoTitulacion\") "
							+ "inner join exetasi.\"formasPresentaciones\" fp on(fp.id=c.\"formaPresentacion\") "
							+ "inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on(ett.id=ctt.\"estudianteTrabajoTitulacion\") "
							+ "inner join exetasi.usuarios d on(d.id=ctt.especialista) "
							+ "where fp.id='ORA' and ett.id=" + ett.getId() + ";", Usuario.class);
					if (listEspecialistaEsc.size() == 3) {
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
						listCartillaEsc = cartillaService.obtenerPorSql(consultaCartillaEsc, Cartilla.class);
					} else {
						listEspecialistaEsc = new ArrayList<Usuario>();
						listEspecialistaEsc.add(ett.getEspecialista1());
						listEspecialistaEsc.add(ett.getEspecialista2());
						listEspecialistaEsc.add(ett.getEspecialista3());
					}

					if (listEspecialistaOral.size() == 3) {
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
						listCartillaOral = cartillaService.obtenerPorSql(consultaCartillaOral, Cartilla.class);
					} else {
						listEspecialistaOral = new ArrayList<Usuario>();
						listEspecialistaOral.add(ett.getEspecialista1());
						listEspecialistaOral.add(ett.getEspecialista2());
						listEspecialistaOral.add(ett.getEspecialista3());
					}
					/////////////////////////////////////////
					Usuario tutor1 = listEspecialistaEsc.get(0);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 1);
					int i = 1;
					for (Cartilla car : listCartillaEsc) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndEscrEsp1 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvTT" + ett.getOpcionTitulacion().getId(), "espc1esc");
					tutor1 = listEspecialistaOral.get(0);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaOral) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndOralEsp1 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvTT", "espc1oral");
					//////////////////////////////////
					tutor1 = listEspecialistaEsc.get(1);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 2);
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndEscrEsp2 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvTT" + ett.getOpcionTitulacion().getId(), "espc2esc");
					tutor1 = listEspecialistaOral.get(1);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaOral) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndOralEsp2 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvTT", "espc2oral");
					////////////////////////////////////////
					tutor1 = listEspecialistaEsc.get(2);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					parametro.put("numEsp", 3);
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndEscrEsp3 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrIndvTT" + ett.getOpcionTitulacion().getId(), "espc3esc");
					tutor1 = listEspecialistaOral.get(2);
					// c.setTutor1(
					// tutor1.getId() + " - "
					// + (tutor1.getApellidoPaterno() == null ? ""
					// : tutor1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutor1.getApellidoMaterno() == null ? ""
					// : tutor1.getApellidoMaterno().toUpperCase())
					// + " " + tutor1.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaOral) {
						if (tutor1.getId().compareTo(ett.getEspecialista1().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp1());
						else if (tutor1.getId().compareTo(ett.getEspecialista2().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp2());
						else if (tutor1.getId().compareTo(ett.getEspecialista3().getId()) == 0)
							parametro.put("calCri" + i, car.getEsp3());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente1().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl1());
						else if (tutor1.getId().compareTo(ett.getEspecialistaSuplente2().getId()) == 0)
							parametro.put("calCri" + i, car.getEspSupl2());
						i++;
					}
					File actaIndOralEsp3 = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralIndvTT", "espc3oral");
					////////////////////////////////////////
					Usuario tutorCons1 = listEspecialistaEsc.get(0);
					// c.setTutor1(tutorCons1.getId() + " - "
					// + (tutorCons1.getApellidoPaterno() == null ? ""
					// : tutorCons1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons1.getApellidoMaterno() == null ? ""
					// : tutorCons1.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons1.getNombre().toUpperCase());
					Usuario tutorCons2 = listEspecialistaEsc.get(1);
					// c.setTutor2(tutorCons2.getId() + " - "
					// + (tutorCons2.getApellidoPaterno() == null ? ""
					// : tutorCons2.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons2.getApellidoMaterno() == null ? ""
					// : tutorCons2.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons2.getNombre().toUpperCase());
					Usuario tutorCons3 = listEspecialistaEsc.get(2);
					// c.setTutor3(tutorCons3.getId() + " - "
					// + (tutorCons3.getApellidoPaterno() == null ? ""
					// : tutorCons3.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons3.getApellidoMaterno() == null ? ""
					// : tutorCons3.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons3.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaEsc) {
						if (car.getEsp1() != null && car.getEsp2() != null && car.getEsp3() != null)
							parametro
									.put("calCri" + i,
											UtilsMath.divideCalificaciones(car.getEsp1().add(car.getEsp2())
													.add(car.getEsp3()).add(car.getEspSupl1()).add(car.getEspSupl2()),
													3));
						i++;
					}
					File actaConEscr = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionEscrConsTT" + ett.getOpcionTitulacion().getId(), "ConEscr");
					tutorCons1 = listEspecialistaOral.get(0);
					// c.setTutor1(tutorCons1.getId() + " - "
					// + (tutorCons1.getApellidoPaterno() == null ? ""
					// : tutorCons1.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons1.getApellidoMaterno() == null ? ""
					// : tutorCons1.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons1.getNombre().toUpperCase());
					tutorCons2 = listEspecialistaOral.get(1);
					// c.setTutor2(tutorCons2.getId() + " - "
					// + (tutorCons2.getApellidoPaterno() == null ? ""
					// : tutorCons2.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons2.getApellidoMaterno() == null ? ""
					// : tutorCons2.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons2.getNombre().toUpperCase());
					tutorCons3 = listEspecialistaOral.get(2);
					// c.setTutor3(tutorCons3.getId() + " - "
					// + (tutorCons3.getApellidoPaterno() == null ? ""
					// : tutorCons3.getApellidoPaterno().toUpperCase())
					// + " "
					// + (tutorCons3.getApellidoMaterno() == null ? ""
					// : tutorCons3.getApellidoMaterno().toUpperCase())
					// + " " + tutorCons3.getNombre().toUpperCase());
					i = 1;
					for (Cartilla car : listCartillaOral) {
						if (car.getEsp1() != null && car.getEsp2() != null && car.getEsp3() != null)
							parametro
									.put("calCri" + i,
											UtilsMath.divideCalificaciones(car.getEsp1().add(car.getEsp2())
													.add(car.getEsp3()).add(car.getEspSupl1()).add(car.getEspSupl2()),
													3));
						i++;
					}
					File actaConOral = reporteService.generarReportePDFFile(list, parametro,
							"ActaCalificacionOralConsTT", "ConOral");

					// Unir pdf por alumno
					File fileEstu = File.createTempFile("fileEstu", ".pdf");
					PdfCopyFields concat = new PdfCopyFields(new FileOutputStream(fileEstu));
					PdfReader pdfActaIndEscrEsp1 = new PdfReader(new FileInputStream(actaIndEscrEsp1));
					PdfReader pdfActaIndOralEsp1 = new PdfReader(new FileInputStream(actaIndOralEsp1));
					PdfReader pdfActaIndEscrEsp2 = new PdfReader(new FileInputStream(actaIndEscrEsp2));
					PdfReader pdfActaIndOralEsp2 = new PdfReader(new FileInputStream(actaIndOralEsp2));
					PdfReader pdfActaIndEscrEsp3 = new PdfReader(new FileInputStream(actaIndEscrEsp3));
					PdfReader pdfActaIndOralEsp3 = new PdfReader(new FileInputStream(actaIndOralEsp3));
					PdfReader pdfActaConEscr = new PdfReader(new FileInputStream(actaConEscr));
					PdfReader pdfActaConOral = new PdfReader(new FileInputStream(actaConOral));

					concat.addDocument(pdfActaIndEscrEsp1);
					concat.addDocument(pdfActaIndOralEsp1);
					concat.addDocument(pdfActaIndEscrEsp2);
					concat.addDocument(pdfActaIndOralEsp2);
					concat.addDocument(pdfActaIndEscrEsp3);
					concat.addDocument(pdfActaIndOralEsp3);
					concat.addDocument(pdfActaConEscr);
					concat.addDocument(pdfActaConOral);
					concat.close();
					listFile.add(fileEstu);
				}

				// añadir pdf al archivo final
				File file = File.createTempFile("listaActas-" + cr.getUnidadAcademica().getId() + "-" + cr.getNombre(),
						".pdf");
				PdfCopyFields concatFinal = new PdfCopyFields(new FileOutputStream(file));
				for (File f : listFile) {
					PdfReader pdfFile = new PdfReader(new FileInputStream(f));
					concatFinal.addDocument(pdfFile);
				}
				concatFinal.close();

				reporteService.responderServidor(file, "ACTAS DE CALIFICACION TRABAJO TITULACION-"
						+ cr.getUnidadAcademica().getId() + "-" + cr.getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reporteAptitudLegal() {
		List<EstudianteCalificacion> listEstudiantes = estudianteCalificacionService.obtenerPorSql(
				"select e.id as id, ua.id||'¬'||ua.nombre as \"unidadAcademica\", c.nombre as carrera, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as proceso, "
						+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, "
						+ "0.00 as \"calExamen\", 0.00 as \"calEscrita\", 0.00 as \"calOral\", "
						+ "0.00 as \"calTotal\" " + "from exetasi.\"temasPracticos\" tp "
						+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
						+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
						+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
						+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
						+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
						+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
						+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' "
						+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionOrdinaria\"=true "
						+ "and (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\")>=69.5)) "
						+ "order by (COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)",
				EstudianteCalificacion.class);
		listEstudiantes.addAll(estudianteCalificacionService.obtenerPorSql(
				"select e.id as id, ua.id||'¬'||ua.nombre as \"unidadAcademica\", c.nombre as carrera, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as proceso, "
						+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, "
						+ "0.00 as \"calExamen\", 0.00 as \"calEscrita\", 0.00 as \"calOral\", "
						+ "0.00 as \"calTotal\" " + "from exetasi.\"temasPracticos\" tp "
						+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
						+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
						+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
						+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
						+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
						+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=tp.\"fechaInicioClase\" and mc.\"fechaCierre\">tp.\"fechaInicioClase\") "
						+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
						+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' "
						+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionGracia\"=true "
						+ "and (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\")>=69.5)) "
						+ "order by (COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)",
				EstudianteCalificacion.class));
		listEstudiantes.addAll(estudianteCalificacionService.obtenerPorSql(
				"select e.id as id, ua.id||'¬'||ua.nombre as \"unidadAcademica\", c.nombre as carrera, "
						+ "(CASE e.genero WHEN 'F' then mc.\"nomenclaturaTituloFemenino\" else mc.\"nomenclaturaTituloMasculino\" end)||COALESCE(' '||mc.\"tipoTitulo\"||' ', '')||COALESCE(mc.\"detalleTitulo\"||' ', '') as proceso, "
						+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, "
						+ "0.00 as \"calExamen\", 0.00 as \"calEscrita\", 0.00 as \"calOral\", "
						+ "0.00 as \"calTotal\" " + "from exetasi.\"estudiantesTrabajosTitulacion\" ett "
						+ "inner join exetasi.usuarios e on(e.id=ett.estudiante) "
						+ "inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") "
						+ "inner join exetasi.seminarios s on(s.id=stt.seminario) "
						+ "inner join exetasi.procesos p on(p.id=s.proceso) "
						+ "inner join exetasi.carreras c on(c.id=ett.carrera) "
						+ "inner join exetasi.\"mallasCurriculares\" mc on(c.id=mc.carrera and mc.\"fechaInicio\"<=ett.\"fechaInicioClase\" and mc.\"fechaCierre\">ett.\"fechaInicioClase\") "
						+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
						+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp() + "' "
						+ "and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' and (ett.\"validarCalificacion\"=true "
						+ "and (ett.\"calificacionEscrita\"+ett.\"calificacionOral\")>=69.5) "
						+ "order by (COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)",
				EstudianteCalificacion.class));

		try {
			List<File> listFile = new ArrayList<File>();
			listEstudiantes = estudianteCalificacionService.quicksortNombre(listEstudiantes, 0,
					listEstudiantes.size() - 1);
			if (!listEstudiantes.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				Caratula c = new Caratula();
				c.setUnidadAcademica(listEstudiantes.get(0).getUnidadAcademica().split("¬")[1]);
				parametro.put("fechaAptitud", "Machala, " + fechaFormatoString(fechaAptitud, "dd 'de' MMMM 'de' yyyy"));
				for (EstudianteCalificacion ec : listEstudiantes) {
					c.setAutor1(ec.getId() + "¬" + ec.getEstudiante().toUpperCase());
					c.setGradoAcademico(listEstudiantes.get(0).getProceso());
					Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
							new Object[] { emailUsuario }, false, new Object[0]);
					// c.setTutor1(u.getId() + "¬" + u.getNombre() + " "
					// + (u.getApellidoPaterno() == null ? "" :
					// u.getApellidoPaterno()) + " "
					// + (u.getApellidoMaterno() == null ? "" :
					// u.getApellidoMaterno()));

					List<Caratula> list = new ArrayList<Caratula>();
					list.add(c);
					listFile.add(reporteService.generarReportePDFFile(list, parametro, "AptitudLegal", "AptitudLegal"));
				}
				File file = File
						.createTempFile("aptitudLegal-" + listEstudiantes.get(0).getUnidadAcademica().split("¬")[0]
								+ "-" + listEstudiantes.get(0).getCarrera(), ".pdf");
				PdfCopyFields concatFinal = new PdfCopyFields(new FileOutputStream(file));
				for (File f : listFile) {
					PdfReader pdfFile = new PdfReader(new FileInputStream(f));
					concatFinal.addDocument(pdfFile);
				}
				concatFinal.close();

				reporteService.responderServidor(file,
						"APTITUD LEGAL-" + listEstudiantes.get(0).getUnidadAcademica().split("¬")[0] + "-"
								+ listEstudiantes.get(0).getCarrera());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reporteCantidadReactivo() {
		List<CantidadReactivo> listCantidadReactivo = cantidadReactivoService
				.obtenerPorSql("select c.id as id, ua.id as \"unidadAcademica\", c.nombre as carrera, "
						+ "(select count(p.id) from preguntas p "
						+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
						+ "inner join asignaturas a on (a.id=ud.asignatura) "
						+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
						+ "inner join carreras ca on (ca.id=mc.carrera) "
						+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") "
						+ "where ca.id=c.id and p.activo=true and ud.activo=true and a.activo=true and uc.id='HU'"
						+ ") as \"preguntasEjeHumano\", " + "(select count(p.id) from preguntas p "
						+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
						+ "inner join asignaturas a on (a.id=ud.asignatura) "
						+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
						+ "inner join carreras ca on (ca.id=mc.carrera) "
						+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") "
						+ "where ca.id=c.id and p.activo=true and ud.activo=true and a.activo=true and uc.id='BA'"
						+ ") as \"preguntasEjeBasico\", " + "(select count(p.id) from preguntas p "
						+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
						+ "inner join asignaturas a on (a.id=ud.asignatura) "
						+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
						+ "inner join carreras ca on (ca.id=mc.carrera) "
						+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") "
						+ "where ca.id=c.id and p.activo=true and ud.activo=true and a.activo=true and uc.id='PR'"
						+ ") as \"preguntasEjeProfesional\" " + "from carreras c "
						+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
						+ "where c.nombre not like '%-SP-' order by ua.id, c.nombre", CantidadReactivo.class);
		if (!listCantidadReactivo.isEmpty()) {
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(listCantidadReactivo, parametro, "CantidadReactivos", "CantidadReactivos");
		}
	}

	public void reporteCuestionario() {
		try {
			List<Cuestionario> preguntas = null;

			File cuestionario = File.createTempFile("Cuestionario", ".pdf");
			PdfCopyFields concatenar = new PdfCopyFields(new FileOutputStream(cuestionario));
			String so = "";
			if (Utils.so().compareTo("WINDOWS") == 0)
				so = "'c://'||";

			preguntas = cuestionarioService.obtenerPorSql("select pr.id as id, p.id as pregunta, "
					+ "regexp_replace(trim(c.nombre), E'[\\n\\r]+', ' ', 'g') as carrera, "
					+ "regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura, "
					+ "regexp_replace(trim(uc.nombre), E'[\\n\\r]+', ' ', 'g') as \"ejeFormacion\", "
					+ "regexp_replace(trim(ud.nombre), E'[\\n\\r]+', ' ', 'g') as \"unidadDidactica\", "
					+ "regexp_replace(trim(p.\"ejeTematico\"), E'[\\n\\r]+', ' ', 'g') as \"ejeTematico\", "
					+ "regexp_replace(trim(p.bibliografia), E'[\\n\\r]+', ' ', 'g') as bibliografia, "
					+ "replace(replace(replace(p.enunciado,chr(10),'\n'),chr(13),''),'<tab>','\t') as planteamiento, null as \"imagenPlanteamiento\", "
					+ "pr.literal as literal, replace(replace(replace(pr.descripcion,chr(10),'\n'),chr(13),''),'<tab>','\t') as opcion, null as \"imagenOpcion\" "
					+ "from preguntas p inner join \"posiblesRespuestas\" pr on (pr.pregunta=p.id) "
					+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
					+ "inner join asignaturas a on (a.id=ud.asignatura) "
					+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
					+ "inner join carreras c on (c.id=mc.carrera) "
					+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
					+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") where c.id="
					+ carreraCuestionario + " and a.activo=true and ud.activo=true and p.activo=true "
					+ "and (p.\"imagenEnunciado\" is null or p.\"imagenEnunciado\"='null') and (pr.\"imagenDescripcion\" is null or pr.\"imagenDescripcion\"='null') "
					+ "order by a.nombre, ud.nombre, p.id, pr.literal", Cuestionario.class);
			if (!preguntas.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				if (Utils.so().compareTo("WINDOWS") == 0)
					parametro.put("OS", "WINDOWS");
				else if (Utils.so().compareTo("LINUX") == 0)
					parametro.put("OS", "LINUX");

				concatenar.addDocument(new PdfReader(new FileInputStream(reporteService.generarReportePDFFile(preguntas,
						parametro, "CuestionarioNormal", "Cuestionario"))));
			}

			preguntas = cuestionarioService.obtenerPorSql("select pr.id as id, p.id as pregunta, "
					+ "regexp_replace(trim(c.nombre), E'[\\n\\r]+', ' ', 'g') as carrera, "
					+ "regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura, "
					+ "regexp_replace(trim(uc.nombre), E'[\\n\\r]+', ' ', 'g') as \"ejeFormacion\", "
					+ "regexp_replace(trim(ud.nombre), E'[\\n\\r]+', ' ', 'g') as \"unidadDidactica\", "
					+ "regexp_replace(trim(p.\"ejeTematico\"), E'[\\n\\r]+', ' ', 'g') as \"ejeTematico\", "
					+ "regexp_replace(trim(p.bibliografia), E'[\\n\\r]+', ' ', 'g') as bibliografia, "
					+ "replace(replace(replace(p.enunciado,chr(10),'\n'),chr(13),''),'<tab>','\t') as planteamiento, "
					+ so + "p.\"imagenEnunciado\" as \"imagenPlanteamiento\", "
					+ "pr.literal as literal, replace(replace(replace(pr.descripcion,chr(10),'\n'),chr(13),''),'<tab>','\t') as opcion, null as \"imagenOpcion\" "
					+ "from preguntas p inner join \"posiblesRespuestas\" pr on (pr.pregunta=p.id) "
					+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
					+ "inner join asignaturas a on (a.id=ud.asignatura) "
					+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
					+ "inner join carreras c on (c.id=mc.carrera) "
					+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
					+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") where c.id="
					+ carreraCuestionario + " and a.activo=true and ud.activo=true and p.activo=true "
					+ "and (p.\"imagenEnunciado\" is not null and p.\"imagenEnunciado\"!='null') and (pr.\"imagenDescripcion\" is null or pr.\"imagenDescripcion\"='null') "
					+ "order by a.nombre, ud.nombre, p.id, pr.literal", Cuestionario.class);
			if (!preguntas.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				if (Utils.so().compareTo("WINDOWS") == 0)
					parametro.put("OS", "WINDOWS");
				else if (Utils.so().compareTo("LINUX") == 0)
					parametro.put("OS", "LINUX");

				concatenar.addDocument(new PdfReader(new FileInputStream(reporteService.generarReportePDFFile(preguntas,
						parametro, "CuestionarioImagenNormal", "Cuestionario"))));
			}

			preguntas = cuestionarioService.obtenerPorSql("select pr.id as id, p.id as pregunta, "
					+ "regexp_replace(trim(c.nombre), E'[\\n\\r]+', ' ', 'g') as carrera, "
					+ "regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura, "
					+ "regexp_replace(trim(uc.nombre), E'[\\n\\r]+', ' ', 'g') as \"ejeFormacion\", "
					+ "regexp_replace(trim(ud.nombre), E'[\\n\\r]+', ' ', 'g') as \"unidadDidactica\", "
					+ "regexp_replace(trim(p.\"ejeTematico\"), E'[\\n\\r]+', ' ', 'g') as \"ejeTematico\", "
					+ "regexp_replace(trim(p.bibliografia), E'[\\n\\r]+', ' ', 'g') as bibliografia, "
					+ "replace(replace(replace(p.enunciado,chr(10),'\n'),chr(13),''),'<tab>','\t') as planteamiento, null as \"imagenPlanteamiento\", "
					+ "pr.literal as literal, replace(replace(replace(pr.descripcion,chr(10),'\n'),chr(13),''),'<tab>','\t') as opcion, "
					+ so + "pr.\"imagenDescripcion\" as \"imagenOpcion\" "
					+ "from preguntas p inner join \"posiblesRespuestas\" pr on (pr.pregunta=p.id) "
					+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
					+ "inner join asignaturas a on (a.id=ud.asignatura) "
					+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
					+ "inner join carreras c on (c.id=mc.carrera) "
					+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
					+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") where c.id="
					+ carreraCuestionario + " and a.activo=true and ud.activo=true and p.activo=true "
					+ "and (p.\"imagenEnunciado\" is null or p.\"imagenEnunciado\"='null') and (pr.\"imagenDescripcion\" is not null and pr.\"imagenDescripcion\" != 'null') "
					+ "order by a.nombre, ud.nombre, p.id, pr.literal", Cuestionario.class);

			if (!preguntas.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				if (Utils.so().compareTo("WINDOWS") == 0)
					parametro.put("OS", "WINDOWS");
				else if (Utils.so().compareTo("LINUX") == 0)
					parametro.put("OS", "LINUX");

				concatenar.addDocument(new PdfReader(new FileInputStream(reporteService.generarReportePDFFile(preguntas,
						parametro, "CuestionarioNormalImagen", "Cuestionario"))));
			}

			preguntas = cuestionarioService.obtenerPorSql("select pr.id as id, p.id as pregunta, "
					+ "regexp_replace(trim(c.nombre), E'[\\n\\r]+', ' ', 'g') as carrera, "
					+ "regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura, "
					+ "regexp_replace(trim(uc.nombre), E'[\\n\\r]+', ' ', 'g') as \"ejeFormacion\", "
					+ "regexp_replace(trim(ud.nombre), E'[\\n\\r]+', ' ', 'g') as \"unidadDidactica\", "
					+ "regexp_replace(trim(p.\"ejeTematico\"), E'[\\n\\r]+', ' ', 'g') as \"ejeTematico\", "
					+ "regexp_replace(trim(p.bibliografia), E'[\\n\\r]+', ' ', 'g') as bibliografia, "
					+ "replace(replace(replace(p.enunciado,chr(10),'\n'),chr(13),''),'<tab>','\t') as planteamiento, "
					+ so + "p.\"imagenEnunciado\" as \"imagenPlanteamiento\", "
					+ "pr.literal as literal, replace(replace(replace(pr.descripcion,chr(10),'\n'),chr(13),''),'<tab>','\t') as opcion, "
					+ so + "pr.\"imagenDescripcion\" as \"imagenOpcion\" "
					+ "from preguntas p inner join \"posiblesRespuestas\" pr on (pr.pregunta=p.id) "
					+ "inner join \"unidadesDidacticas\" ud on (ud.id=p.\"unidadDidactica\") "
					+ "inner join asignaturas a on (a.id=ud.asignatura) "
					+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
					+ "inner join carreras c on (c.id=mc.carrera) "
					+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
					+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") where c.id="
					+ carreraCuestionario + " and a.activo=true and ud.activo=true and p.activo=true "
					+ "and (p.\"imagenEnunciado\" is not null and p.\"imagenEnunciado\"!='null') and (pr.\"imagenDescripcion\" is not null and pr.\"imagenDescripcion\"!='null') "
					+ "order by a.nombre, ud.nombre, p.id, pr.literal", Cuestionario.class);
			if (!preguntas.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				if (Utils.so().compareTo("WINDOWS") == 0)
					parametro.put("OS", "WINDOWS");
				else if (Utils.so().compareTo("LINUX") == 0)
					parametro.put("OS", "LINUX");

				concatenar.addDocument(new PdfReader(new FileInputStream(reporteService.generarReportePDFFile(preguntas,
						parametro, "CuestionarioImagen", "Cuestionario"))));
			}

			concatenar.close();
			reporteService.responderServidor(cuestionario, "BANCO DE REACTIVOS PARA EXAMEN COMPLEXIVO DE LA CARRERA DE "
					+ carreraService.obtenerPorId(carreraCuestionario).getNombre());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public void reporteCumplimientoReactivo() {
		List<CumplimientoReactivo> listCumplimientoReactivo = cumplimientoReactivoService
				.obtenerPorSql("select u.id||ud.id as id, ua.id as \"unidadAcademica\" ,c.nombre as carrera, "
						+ "regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura, "
						+ "uc.nombre as \"unidadCurricular\", regexp_replace(trim(ud.nombre), E'[\\n\\r]+', ' ', 'g') as \"unidadDidactica\", "
						+ "u.id as cedula, u.telefono as telefono, u.email as email, "
						+ "upper(replace(coalesce(u.\"apellidoPaterno\",'')||' '||coalesce(u.\"apellidoMaterno\",'')||' '||coalesce(u.nombre,''),'  ',' ')) as \"nombreDocente\", "
						+ "(select count(p.id) from preguntas p inner join \"unidadesDidacticas\" uds on (uds.id=p.\"unidadDidactica\") "
						+ "where uds=ud and p.activo=true) as \"numeroPreguntasRealizadas\" "
						+ "from usuarios u inner join \"docentesAsignaturas\" da on (da.docente=u.id) "
						+ "inner join asignaturas a on (a.id=da.asignatura) inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
						+ "inner join carreras c on (c.id=mc.carrera) inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
						+ "inner join \"unidadesDidacticas\" ud on (ud.asignatura=a.id) "
						+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") "
						+ "where a.activo=true and ud.activo=true and da.activo=true and c.id="
						+ carreraCumplimientoReactivo
						+ " order by ua.id, replace(coalesce(u.\"apellidoPaterno\",'')||' '||coalesce(u.\"apellidoMaterno\",'')||' '||coalesce(u.nombre,''),'  ',' '), "
						+ "c.nombre, a.nombre, ud.id", CumplimientoReactivo.class);

		if (!listCumplimientoReactivo.isEmpty()) {
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(listCumplimientoReactivo, parametro, "CumplimientoReactivo",
					"CumplimientoReactivo");
		}
	}

	public void reporteDistribucionAsignatura() {
		List<DistribucionAsignatura> listDistribucionAsignatura = distribucionAsignaturaService.obtenerPorSql(
				"select da.id as id, ua.id as \"unidadAcademica\" ,c.nombre as carrera "
						+ ",uc.nombre as \"unidadCurricular\" "
						+ ",regexp_replace(trim(a.nombre), E'[\\n\\r]+', ' ', 'g') as asignatura "
						+ ",replace(coalesce(u.\"apellidoPaterno\",'')||' '||coalesce(u.\"apellidoMaterno\",'')||' '||coalesce(u.nombre,''),'  ',' ') as docente "
						+ ",u.telefono as telefono ,u.email as email from usuarios u "
						+ "inner join \"docentesAsignaturas\" da on (da.docente=u.id) "
						+ "inner join asignaturas a on (a.id=da.asignatura) "
						+ "inner join \"mallasCurriculares\" mc on (mc.id=a.\"mallaCurricular\") "
						+ "inner join carreras c on (c.id=mc.carrera) "
						+ "inner join \"unidadesAcademicas\" ua on (ua.id=c.\"unidadAcademica\") "
						+ "inner join \"unidadesCurriculares\" uc on (uc.id=a.\"unidadCurricular\") "
						+ "where a.activo=true and da.activo=true and c.id=" + carreraDistribucionAsignatura
						+ " order by ua.id, c.nombre, uc.nombre, a.nombre, replace(coalesce(u.\"apellidoPaterno\",'')||' '||coalesce(u.\"apellidoMaterno\",'')||' '||coalesce(u.nombre,''),'  ',' ')",
				DistribucionAsignatura.class);

		if (!listDistribucionAsignatura.isEmpty()) {
			Map<String, Object> parametro = new HashMap<String, Object>();
			reporteService.generarReportePDF(listDistribucionAsignatura, parametro, "DistribucionAsignaturas",
					"DistribucionAsignaturas");
		}
	}

	public void reporteEstudiantesCalificaciones() {
		Map<String, Object> parametro = new HashMap<String, Object>();
		if (reporteCTT == 1) {
			List<EstudianteCalificacion> list = estudianteCalificacionService.obtenerPorSql(
					"select e.id as id, ua.nombre as \"unidadAcademica\", c.nombre as carrera, p.id as proceso, "
							+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, ex.calificacion as \"calExamen\", tp.\"calificacionEscritaOrdinaria\" as \"calEscrita\", tp.\"calificacionOralOrdinaria\" as \"calOral\", "
							+ "(ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\") as \"calTotal\" "
							+ "from exetasi.\"temasPracticos\" tp "
							+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
							+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
							+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
							+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
							+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
							+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
							+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
							+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
							+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' "
							+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionOrdinaria\"=true "
							+ "and (ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\")>=69.5)) "
							+ "order by "
							+ (consultaNomCal == 1
									? "(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)"
									: "(ex.calificacion+tp.\"calificacionEscritaOrdinaria\"+tp.\"calificacionOralOrdinaria\") desc"),
					EstudianteCalificacion.class);
			list.addAll(estudianteCalificacionService.obtenerPorSql(
					"select e.id as id, ua.nombre as \"unidadAcademica\", c.nombre as carrera, p.id as proceso, "
							+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, "
							+ "ex.calificacion as \"calExamen\", tp.\"calificacionEscritaGracia\" as \"calEscrita\", tp.\"calificacionOralGracia\" as \"calOral\", "
							+ "(ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\") \"calTotal\" "
							+ "from exetasi.\"temasPracticos\" tp "
							+ "inner join exetasi.\"estudiantesPeriodos\" ep on(ep.id=tp.\"estudiantePeriodo\") "
							+ "inner join exetasi.usuarios e on(e.id=ep.estudiante) "
							+ "inner join exetasi.examenes ex on(ex.\"estudiantePeriodo\"=ep.id) "
							+ "inner join exetasi.\"periodosExamenes\" pe on (pe.id=ep.\"periodoExamen\") "
							+ "inner join exetasi.procesos p on(p.id=pe.proceso) "
							+ "inner join exetasi.carreras c on(c.id=pe.carrera) "
							+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
							+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
							+ "' and p.\"fechaCierre\">'" + UtilsDate.timestamp() + "' "
							+ "and ex.calificacion>=20 and ((tp.\"validarCalificacionGracia\"=true "
							+ "and (ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\")>=69.5)) "
							+ "order by "
							+ (consultaNomCal == 1
									? "(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)"
									: "(ex.calificacion+tp.\"calificacionEscritaGracia\"+tp.\"calificacionOralGracia\") desc"),
					EstudianteCalificacion.class));
			if (!list.isEmpty()) {
				EstudianteCalificacion ec = list.get(0);
				for (EstudianteCalificacion ecAux : list) {
					ecAux.setCalTotal(UtilsMath.redondearCalificacion(ecAux.getCalTotal()));
				}
				Carrera c = carreraService.obtenerObjeto("select  c from Carrera c where c.id=?1",
						new Object[] { carreraComplexivo }, false, new Object[0]);
				if (consultaPdfExcel == 1) {
					reporteService
							.generarReportePDF(
									consultaNomCal == 1
											? estudianteCalificacionService.quicksortNombre(list, 0, list.size() - 1)
											: estudianteCalificacionService.quicksortCalificacion(list, 0,
													list.size() - 1),
									parametro, "ListadoCalificacionComplexivo",
									"CalificacionesC-" + c.getUnidadAcademica().getId() + "-" + ec.getCarrera());
				} else if (consultaPdfExcel == 2) {
					list = consultaNomCal == 1 ? estudianteCalificacionService.quicksortNombre(list, 0, list.size() - 1)
							: estudianteCalificacionService.quicksortCalificacion(list, 0, list.size() - 1);
					List<String> listFinal = new ArrayList<String>();
					listFinal.add("SN°¬SCédula¬SEstudiante¬STeórica¬SEscrita¬SOral¬STotal");
					int i = 1;
					for (EstudianteCalificacion ecAux : list) {
						listFinal.add("S" + i + "¬S" + ecAux.getId() + "¬S" + ecAux.getEstudiante() + "¬D"
								+ rellenarCeros(ecAux.getCalExamen()) + "¬D" + rellenarCeros(ecAux.getCalEscrita())
								+ "¬D" + rellenarCeros(ecAux.getCalOral()) + "¬D" + rellenarCeros(ecAux.getCalTotal()));
						i++;
					}
					reporteService.generarReporteXLSSencillo(listFinal,
							"CalificacionesC-" + c.getUnidadAcademica().getId() + "-" + ec.getCarrera(),
							list.get(0).getUnidadAcademica(), list.get(0).getCarrera(),
							"CALIFICACIONES EXAMEN COMPLEXIVO | PROCESO:  " + list.get(0).getProceso(), 7);
				}
			}
		} else if (reporteCTT == 2) {
			List<EstudianteCalificacion> list = estudianteCalificacionService.obtenerPorSql(
					"select e.id as id, ua.nombre as \"unidadAcademica\", c.nombre as carrera, p.id as proceso, "
							+ "COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre as estudiante, "
							+ "0.00 as \"calExamen\", ett.\"calificacionEscrita\" as \"calEscrita\", ett.\"calificacionOral\" as \"calOral\", "
							+ "(ett.\"calificacionEscrita\"+ett.\"calificacionOral\") \"calTotal\" "
							+ "from exetasi.\"estudiantesTrabajosTitulacion\" ett "
							+ "inner join exetasi.usuarios e on(e.id=ett.estudiante) "
							+ "inner join exetasi.\"seminariosTrabajosTitulacion\" stt on(stt.id=ett.\"seminarioTrabajoTitulacion\") "
							+ "inner join exetasi.seminarios s on(s.id=stt.seminario) "
							+ "inner join exetasi.procesos p on(p.id=s.proceso) "
							+ "inner join exetasi.carreras c on(c.id=ett.carrera) "
							+ "inner join exetasi.\"unidadesAcademicas\" ua on(ua.id=c.\"unidadAcademica\") "
							+ "where c.id=" + carreraComplexivo + " and p.\"fechaInicio\"<='" + UtilsDate.timestamp()
							+ "' " + "and p.\"fechaCierre\">'" + UtilsDate.timestamp()
							+ "' and (ett.\"validarCalificacion\"=true "
							+ "and (ett.\"calificacionEscrita\"+ett.\"calificacionOral\")>=69.5) order by "
							+ (consultaNomCal == 1
									? "(COALESCE(e.\"apellidoPaterno\"||' ', '') || COALESCE(e.\"apellidoMaterno\"||' ', '') || e.nombre)"
									: "(ett.\"calificacionEscrita\"+ett.\"calificacionOral\") desc"),
					EstudianteCalificacion.class);
			if (!list.isEmpty()) {
				EstudianteCalificacion ec = list.get(0);
				for (EstudianteCalificacion ecAux : list) {
					ecAux.setCalTotal(UtilsMath.redondearCalificacion(ecAux.getCalTotal()));
				}
				Carrera c = carreraService.obtenerObjeto("select  c from Carrera c where c.id=?1",
						new Object[] { carreraComplexivo }, false, new Object[0]);
				if (consultaPdfExcel == 1)
					reporteService
							.generarReportePDF(
									consultaNomCal == 1
											? estudianteCalificacionService.quicksortNombre(list, 0, list.size() - 1)
											: estudianteCalificacionService.quicksortCalificacion(list, 0,
													list.size() - 1),
									parametro, "ListadoCalificacionTitulacion",
									"CalificacionesTT-" + c.getUnidadAcademica().getId() + "-" + ec.getCarrera());
				else if (consultaPdfExcel == 2) {
					list = consultaNomCal == 1 ? estudianteCalificacionService.quicksortNombre(list, 0, list.size() - 1)
							: estudianteCalificacionService.quicksortCalificacion(list, 0, list.size() - 1);
					List<String> listFinal = new ArrayList<String>();
					listFinal.add("SN°¬SCédula¬SEstudiante¬SEscrita¬SOral¬STotal");
					int i = 1;
					for (EstudianteCalificacion ecAux : list) {
						listFinal.add("S" + i + "¬S" + ecAux.getId() + "¬S" + ecAux.getEstudiante() + "¬D"
								+ rellenarCeros(ecAux.getCalEscrita()) + "¬D" + rellenarCeros(ecAux.getCalOral()) + "¬D"
								+ rellenarCeros(ecAux.getCalTotal()));
						i++;
					}
					reporteService.generarReporteXLSSencillo(listFinal,
							"CalificacionesTT-" + c.getUnidadAcademica().getId() + "-" + ec.getCarrera(),
							list.get(0).getUnidadAcademica(), list.get(0).getCarrera(),
							"CALIFICACIONES TRABAJO DE TITULACIÓN | PROCESO:  " + list.get(0).getProceso(), 6);
				}
			}
		}
	}

	public void reporteResolucionComplexivo() {
		Map<String, Object> parametro = new HashMap<String, Object>();
		List<ActaTemasTitulosResolucion> list = new ArrayList<ActaTemasTitulosResolucion>();
		List<EstudianteExamenComplexivoPP> listTemasPracticos = temaPracticoService.obtenerLista(
				"select tp from TemaPractico tp inner join fetch tp.estudiantePeriodo ep "
						+ "inner join ep.estudiante e inner join ep.periodoExamen pe inner join pe.carrera c "
						+ "where c.id=?1 and tp.tituloInvestigacion is not null "
						+ "and tp.especialista1 is not null and tp.especialista2 is not null and tp.especialista3 is not null "
						+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
				new Object[] { carreraComplexivo }, 0, false, new Object[] {});
		String nombre = "";
		if (!listTemasPracticos.isEmpty()) {
			// Carrera c =
			// listTemasPracticos.get(0).getEstudiantePeriodo().getPeriodoExamen().getCarrera();
			Carrera c = null;
			ActaTemasTitulosResolucion attr = new ActaTemasTitulosResolucion();
			attr.setUnidadAcademica(c.getUnidadAcademica().getNombre());
			attr.setCarrera(c.getNombre());
			// attr.setTitulo("PARTE PRÁCTICA DEL EXAMEN COMPLEXIVO PROCESO: "
			// +
			// listTemasPracticos.get(0).getEstudiantePeriodo().getPeriodoExamen().getProceso().getId());
			attr.setDescripcion("TÍTULOS Y COMITÉS EVALUADORES");
			List<DetalleActaTemasTitulosResolucion> listDATTR = new ArrayList<DetalleActaTemasTitulosResolucion>();
			int i = 1;
			for (EstudianteExamenComplexivoPP tp : listTemasPracticos) {
				DetalleActaTemasTitulosResolucion dattr = new DetalleActaTemasTitulosResolucion();
				dattr.setNo(i);
				Usuario estu = null;
				// Usuario estu = tp.getEstudiantePeriodo().getEstudiante();
				// dattr.setEgresado((estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase())
				// + " " + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase());
				dattr.setCedula(estu.getId());
				dattr.setTitulo(tp.getTituloInvestigacion().toUpperCase());
				dattr.setCasoPractico(html2text(tp.getCasoInvestigacion()).toUpperCase());
				dattr.setFechaSustentacion(fechaFormatoString(tp.getFechaSustentacionOrdinaria(), "dd/MM/yyyy HH:mm"));
				Usuario p1 = tp.getEspecialista1();
				Usuario p2 = tp.getEspecialista2();
				Usuario p3 = tp.getEspecialista3();
				// String nmb1 = (p1.getApellidoPaterno() == null ? "" :
				// p1.getApellidoPaterno()) + " "
				// + (p1.getApellidoMaterno() == null ? "" :
				// p1.getApellidoMaterno()) + " " + p1.getNombre();
				// String nmb2 = (p2.getApellidoPaterno() == null ? "" :
				// p2.getApellidoPaterno()) + " "
				// + (p2.getApellidoMaterno() == null ? "" :
				// p2.getApellidoMaterno()) + " " + p2.getNombre();
				// String nmb3 = (p3.getApellidoPaterno() == null ? "" :
				// p3.getApellidoPaterno()) + " "
				// + (p3.getApellidoMaterno() == null ? "" :
				// p3.getApellidoMaterno()) + " " + p3.getNombre();
				String nmb1 = null;
				String nmb2 = null;
				String nmb3 = null;
				dattr.setEspecialista1(nmb1.toUpperCase());
				dattr.setEspecialista2(nmb2.toUpperCase());
				dattr.setEspecialista3(nmb3.toUpperCase());
				listDATTR.add(dattr);
				i++;
			}

			attr.setListActaTemasTitulosResolucion(listDATTR);
			list.add(attr);
			nombre = "ResolucionComplexivo-" + c.getUnidadAcademica().getId() + "-" + c.getNombre();
			parametro.put("logoUnidadAcademica", c.getUnidadAcademica().getId());
		}

		reporteService.generarReportePDF(list, parametro, "ActaTemasTitulosResolucionComplexivo", nombre);
	}

	public void reporteResolucionTrabajoTitulacion() {
		Map<String, Object> parametro = new HashMap<String, Object>();
		List<ActaTemasTitulosResolucion> list = new ArrayList<ActaTemasTitulosResolucion>();
		List<EstudianteTrabajoTitulacion> listEstudianteTrabajoTitulacion = estudianteTrabajoTitulacionService
				.obtenerLista(
						"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e "
								+ "inner join ett.especialista1 e1 inner join ett.carrera c "
								+ "where c.id=?1 and ett.seminarioTrabajoTitulacion is not null "
								+ "and e1 is not null order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
						new Object[] { carreraTrabajoTitulacion }, 0, false, new Object[] {});
		String nombre = "";
		if (!listEstudianteTrabajoTitulacion.isEmpty()) {
			Carrera c = listEstudianteTrabajoTitulacion.get(0).getCarrera();
			ActaTemasTitulosResolucion attr = new ActaTemasTitulosResolucion();
			attr.setUnidadAcademica(c.getUnidadAcademica().getNombre());
			attr.setCarrera(c.getNombre());
			attr.setTitulo("TRABAJO DE TITULACIÓN PROCESO: " + listEstudianteTrabajoTitulacion.get(0)
					.getSeminarioTrabajoTitulacion().getSeminario().getProceso().getId());
			attr.setDescripcion("TÍTULOS Y COMITÉS EVALUADORES");
			List<DetalleActaTemasTitulosResolucion> listDATTR = new ArrayList<DetalleActaTemasTitulosResolucion>();
			int i = 1;
			for (EstudianteTrabajoTitulacion ett : listEstudianteTrabajoTitulacion) {
				DetalleActaTemasTitulosResolucion dattr = new DetalleActaTemasTitulosResolucion();
				dattr.setNo(i);
				Usuario estu = ett.getEstudiante();
				// dattr.setEgresado((estu.getApellidoPaterno() == null ? "" :
				// estu.getApellidoPaterno().toUpperCase())
				// + " " + (estu.getApellidoMaterno() == null ? "" :
				// estu.getApellidoMaterno().toUpperCase()) + " "
				// + estu.getNombre().toUpperCase());
				dattr.setCedula(estu.getId());
				Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
				// dattr.setTutor((tutor.getApellidoPaterno() == null ? "" :
				// tutor.getApellidoPaterno()) + " "
				// + (tutor.getApellidoMaterno() == null ? "" :
				// tutor.getApellidoMaterno()) + " "
				// + tutor.getNombre());
				dattr.setCasoPractico(ett.getSeminarioTrabajoTitulacion().getTrabajoTitulacion().getCasoInvestigacion()
						.toUpperCase());
				Usuario p1 = ett.getEspecialista1();
				Usuario p2 = ett.getEspecialista2();
				Usuario p3 = ett.getEspecialista3();
				Usuario ps1 = ett.getEspecialistaSuplente1();
				Usuario ps2 = ett.getEspecialistaSuplente2();

				// if (p1 != null)
				// dattr.setEspecialista1((p1.getApellidoPaterno() == null ? ""
				// : p1.getApellidoPaterno()) + " "
				// + (p1.getApellidoMaterno() == null ? "" :
				// p1.getApellidoMaterno()) + " "
				// + p1.getNombre().toUpperCase());
				// if (p2 != null)
				// dattr.setEspecialista2((p2.getApellidoPaterno() == null ? ""
				// : p2.getApellidoPaterno()) + " "
				// + (p2.getApellidoMaterno() == null ? "" :
				// p2.getApellidoMaterno()) + " "
				// + p2.getNombre().toUpperCase());
				// if (p3 != null)
				// dattr.setEspecialista3((p3.getApellidoPaterno() == null ? ""
				// : p3.getApellidoPaterno()) + " "
				// + (p3.getApellidoMaterno() == null ? "" :
				// p3.getApellidoMaterno()) + " "
				// + p3.getNombre().toUpperCase());
				// if (ps1 != null)
				// dattr.setEspecialistaSuplente1((ps1.getApellidoPaterno() ==
				// null ? "" : ps1.getApellidoPaterno())
				// + " " + (ps1.getApellidoMaterno() == null ? "" :
				// ps1.getApellidoMaterno()) + " "
				// + ps1.getNombre().toUpperCase());
				// if (ps2 != null)
				// dattr.setEspecialistaSuplente2((ps2.getApellidoPaterno() ==
				// null ? "" : ps2.getApellidoPaterno())
				// + " " + (ps2.getApellidoMaterno() == null ? "" :
				// ps2.getApellidoMaterno()) + " "
				// + ps2.getNombre().toUpperCase());
				listDATTR.add(dattr);
				i++;
			}

			attr.setListActaTemasTitulosResolucion(listDATTR);
			list.add(attr);
			nombre = "ResolucionTrabajoTitulación-" + c.getUnidadAcademica().getId() + "-" + c.getNombre();
			parametro.put("logoUnidadAcademica", c.getUnidadAcademica().getId());
		}
		reporteService.generarReportePDF(list, parametro, "ActaTemasTitulosResolucionTrabajoTitulacion", nombre);
	}

	public void reporteTutorias() {
		List<EstudianteTrabajoTitulacion> listETT = new ArrayList<EstudianteTrabajoTitulacion>();
		listETT = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.carrera c "
						+ "inner join ett.opcionTitulacion ot inner join fetch ett.tutorias t "
						+ "where c.id=?1 and ett.especialista1 is not null and ett.especialista2 is not null and ett.especialista3 is not null and ot.id is not null "
						+ "order by ett.fechaSustentacion",
				new Object[] { carreraTrabajoTitulacion }, 0, false, new Object[] {});
		try {
			List<File> listFile = new ArrayList<File>();
			Carrera cr = null;
			if (!listETT.isEmpty()) {
				Map<String, Object> parametro = new HashMap<String, Object>();
				cr = listETT.get(0).getCarrera();
				TutoriaAux tAux = new TutoriaAux();
				tAux.setUnidadAcademica(cr.getUnidadAcademica().getNombre());
				tAux.setCarrera(cr.getNombre());
				tAux.setProceso(listETT.get(0).getSeminarioTrabajoTitulacion().getSeminario().getProceso().getId());
				for (EstudianteTrabajoTitulacion ett : listETT) {
					tAux.setCasoInvestigacion(
							ett.getSeminarioTrabajoTitulacion().getTrabajoTitulacion().getCasoInvestigacion());
					tAux.setOpcionTitulacion(ett.getOpcionTitulacion().getNombre());
					Usuario estu = ett.getEstudiante();
					// tAux.setEstudiante(estu.getId() + " - "
					// + (estu.getApellidoPaterno() == null ? "" :
					// estu.getApellidoPaterno().toUpperCase()) + " "
					// + (estu.getApellidoMaterno() == null ? "" :
					// estu.getApellidoMaterno().toUpperCase()) + " "
					// + estu.getNombre().toUpperCase());
					Usuario tutor = ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente();
					// tAux.setTutor(tutor.getId() + " - "
					// + (tutor.getApellidoPaterno() == null ? "" :
					// tutor.getApellidoPaterno().toUpperCase()) + " "
					// + (tutor.getApellidoMaterno() == null ? "" :
					// tutor.getApellidoMaterno().toUpperCase()) + " "
					// + tutor.getNombre().toUpperCase());
					tAux.setTutoria(ett.getTutorias().get(0).getActividad());
					tAux.setFecha(UtilsDate.fechaFormatoString(ett.getTutorias().get(0).getFecha(), "dd/MM/yyy"));
					List<TutoriaAux> list = new ArrayList<TutoriaAux>();
					list.add(tAux);
					listFile.add(
							reporteService.generarReportePDFFile(list, parametro, "Tutoria", "tutoria" + ett.getId()));
				}

				// añadir pdf al archivo final
				File file = File.createTempFile("tutorias-" + cr.getUnidadAcademica().getId() + "-" + cr.getNombre(),
						".pdf");
				PdfCopyFields concatFinal = new PdfCopyFields(new FileOutputStream(file));
				for (File f : listFile) {
					PdfReader pdfFile = new PdfReader(new FileInputStream(f));
					concatFinal.addDocument(pdfFile);
				}
				concatFinal.close();

				reporteService.responderServidor(file,
						"TUTORIAS-" + cr.getUnidadAcademica().getId() + "-" + cr.getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setCarreraComplexivo(Integer carreraComplexivo) {
		this.carreraComplexivo = carreraComplexivo;
	}

	public void setCarreraCuestionario(Integer carreraCuestionario) {
		this.carreraCuestionario = carreraCuestionario;
	}

	public void setCarreraCumplimientoReactivo(Integer carreraCumplimientoReactivo) {
		this.carreraCumplimientoReactivo = carreraCumplimientoReactivo;
	}

	public void setCarreraDistribucionAsignatura(Integer carreraDistribucionAsignatura) {
		this.carreraDistribucionAsignatura = carreraDistribucionAsignatura;
	}

	public void setCarreraTrabajoTitulacion(Integer carreraTrabajoTitulacion) {
		this.carreraTrabajoTitulacion = carreraTrabajoTitulacion;
	}

	public void setConsultaNomCal(int consultaNomCal) {
		this.consultaNomCal = consultaNomCal;
	}

	public void setConsultaOrdGra(int consultaOrdGra) {
		this.consultaOrdGra = consultaOrdGra;
	}

	public void setConsultaPdfExcel(int consultaPdfExcel) {
		this.consultaPdfExcel = consultaPdfExcel;
	}

	// public void setFechaActaComplexivo(String fechaActaComplexivo) {
	// this.fechaActaComplexivo = fechaActaComplexivo;
	// }

	public void setFechaActaTrabajoTitulacion(String fechaActaTrabajoTitulacion) {
		this.fechaActaTrabajoTitulacion = fechaActaTrabajoTitulacion;
	}

	public void setFechaAptitud(Date fechaAptitud) {
		this.fechaAptitud = fechaAptitud;
	}

	public void setListCarreraComplexivo(List<Carrera> listCarreraComplexivo) {
		this.listCarreraComplexivo = listCarreraComplexivo;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListCarreraTrabajoTitulacion(List<Carrera> listCarreraTrabajoTitulacion) {
		this.listCarreraTrabajoTitulacion = listCarreraTrabajoTitulacion;
	}

	public void setListFechasComplexivo(List<Texto> listFechasComplexivo) {
		this.listFechasComplexivo = listFechasComplexivo;
	}

	public void setListFechasTrabajoTitulacion(List<Texto> listFechasTrabajoTitulacion) {
		this.listFechasTrabajoTitulacion = listFechasTrabajoTitulacion;
	}

	public void setListNumeroFechaActaIncorporacion(
			List<NumeroFechaActaIncorporacion> listNumeroFechaActaIncorporacion) {
		this.listNumeroFechaActaIncorporacion = listNumeroFechaActaIncorporacion;
	}

	public void setNumeroActa(String numeroActa) {
		this.numeroActa = numeroActa;
	}

	public void setProceso(Proceso proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setReporteCTT(int reporteCTT) {
		this.reporteCTT = reporteCTT;
	}

}
