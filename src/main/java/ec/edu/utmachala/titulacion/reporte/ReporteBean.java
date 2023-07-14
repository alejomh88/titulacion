package ec.edu.utmachala.titulacion.reporte;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.MallaCurricular;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadAcademica;
import ec.edu.utmachala.titulacion.entityAux.Texto;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.MallaCurricularService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.TextoService;
import ec.edu.utmachala.titulacion.service.UnidadAcademicaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class ReporteBean {

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private TextoService textoService;

	@Autowired
	private MallaCurricularService mallaCurricularService;

	@Autowired
	private UnidadAcademicaService unidadAcademicaService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private UsuarioService usuarioService;

	private String formatoReporte;

	private List<UnidadAcademica> unidadesAcademicas;
	private String unidadAcademica;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<MallaCurricular> mallas;
	private Integer malla;

	private List<Texto> actasGraduacion;
	private String actaGraduacion;

	private StreamedContent documento;

	private boolean esCoordinador;
	private boolean esUmmog;
	private boolean esAnalista;
	private boolean esAdministrador;

	private String numeroOficioUMMOGTesoreria;
	private String numeroOficioUMMOGSecretaria;

	private Date fechaActaGraduacion;
	private Date fechaOficioUMMOGSecretaria;

	private Boolean estudiosHomologaciones;

	@PostConstruct
	public void a() {
		carrera = 0;
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		esCoordinador = (UtilSeguridad.obtenerRol("COCA") || UtilSeguridad.obtenerRol("COTI") ? true : false);

		esUmmog = UtilSeguridad.obtenerRol("UMMO");

		esAnalista = UtilSeguridad.obtenerRol("ANAL");

		esAdministrador = UtilSeguridad.obtenerRol("ADMI");

		numeroOficioUMMOGTesoreria = "";

		numeroOficioUMMOGSecretaria = "";

		estudiosHomologaciones = false;

		unidadesAcademicas = unidadAcademicaService.obtenerPorSql(
				"select distinct ua.* from \"unidadesAcademicas\" ua inner join carreras c on (c.\"unidadAcademica\"=ua.id)"
						+ " inner join	\"permisosCarreras\" pc on (pc.carrera=c.id) inner join	usuarios u on (pc.usuario =u.id) where u.email = '"
						+ UtilSeguridad.obtenerUsuario() + "'",
				UnidadAcademica.class);

	}

	public void actaGraduacion() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("ActaGraduacion");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void bancoReactivosPracticos() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ReactivosPracticosDisponibles");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void bancoReactivosTeoricos() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("BancoReactivos");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void calificacionExamenComplexivoPT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ResultadosCalificacionesPT");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", String.valueOf(carrera));

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cambioMalla() {
		System.out.println("malla: " + malla);
	}

	public void cargarDatosReportes() {
		actasGraduacion = textoService.obtenerPorSql(
				"select distinct to_char(\"fechaActaGraduacion\", 'dd.mm.yyyy')||'-'||\"numeroActaGraduacion\"||'-'||e.genero as id "
						+ "from \"estudiantesTrabajosTitulacion\" eet inner join usuarios e on (e.id=eet.estudiante) "
						+ "where proceso='" + proceso + "' and carrera=" + carrera
						+ " and \"fechaActaGraduacion\" is not null and \"numeroActaGraduacion\" is not null "
						+ "union select distinct to_char(\"fechaActaGraduacion\", 'dd.mm.yyyy')||'-'||\"numeroActaGraduacion\"||'-'||e.genero as id "
						+ "from \"estudiantesExamenComplexivoPP\" eet inner join usuarios e on (e.id=eet.estudiante) "
						+ "where proceso='" + proceso + "' and carrera=" + carrera
						+ " and \"fechaActaGraduacion\" is not null and \"numeroActaGraduacion\" is not null "
						+ "order by id",
				Texto.class);
		if (!actasGraduacion.isEmpty() && actasGraduacion.size() == 1)
			actaGraduacion = actasGraduacion.get(0).getId();

		if (esCoordinador)
			mallas = mallaCurricularService
					.obtenerPorSql("select distinct mc.* from \"mallasCurriculares\" mc inner join "
							+ "\"carrerasMallasProcesos\" cmp on (cmp.\"mallaCurricular\"=mc.id) inner join carreras c on (cmp.carrera=c.id) where c.id="
							+ carrera + " order by mc.id asc;", MallaCurricular.class);

	}

	public void cumplimientoGeneralReactivosPracticos() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CumplimientoGeneralReactivosPracticos");
				report.addParameter("PROCESO", proceso);

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cumplimientoGeneralReactivosTeoricos() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CumplimientoGeneralReactivosTeoricos");
				report.addParameter("PROCESO", proceso);

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cumplimientoReactivosPracticosDocentes() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CumplimientoReactivosPracticosDocentes");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void cumplimientoReactivosTeoricosDocentes() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CumplimientoReactivosTeoricosDocentes");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estadisticaEC_PP() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else if (malla == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la malla");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ResultadosPPCarrera");
				report.addParameter("PROCESO", proceso);
				report.addParameter("MALLA", malla.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estadisticaEC_PT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else if (malla == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la malla");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ResultadosPTCarrera");
				report.addParameter("PROCESO", proceso);
				report.addParameter("MALLA", malla.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estadisticasNoAdeudarUmmog_EC() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_NoAdeudarUMMOG");
				report.addParameter("proceso", proceso);
				report.addParameter("unidadAcademica", unidadesAcademicas.get(0).getId());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estadisticasNoAdeudarUmmog_TT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_NoAdeudarUMMOG");
				report.addParameter("proceso", proceso);
				report.addParameter("unidadAcademica", unidadesAcademicas.get(0).getId());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estadisticaTT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else if (malla == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la malla");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_ResultadosTTCarrera");
				report.addParameter("PROCESO", proceso);
				report.addParameter("MALLA", malla.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesAprobadosArchivosyBiblioteca_EC() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ListadoEstudiantesAprobadosUMMOGArchivosYBiblioteca");
				report.addParameter("proceso", proceso);
				report.addParameter("unidadAcademica", unidadesAcademicas.get(0).getId());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesAprobadosArchivosyBiblioteca_TT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_ListadoEstudiantesAprobadosUMMOGArchivosYBiblioteca");
				report.addParameter("proceso", proceso);
				report.addParameter("unidadAcademica", unidadesAcademicas.get(0).getId());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesAprobadosEnlaceBiblioteca() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EstudiantesAprobadosEnlaceBiblioteca");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesAprobadosSecretaria() {
		if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
			System.out.println("Seleccione el proceso.");
		} else if (numeroOficioUMMOGSecretaria.compareTo("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un número de oficio de UMMOG.");
			System.out.println("Ingrese un número de oficio de UMMOG.");
		} else {
			String numeroOficioUMMOGSecretariaCompleto = "";

			numeroOficioUMMOGSecretariaCompleto = "UTMACH-" + unidadesAcademicas.get(0).getId() + "-UMMOG-"
					+ numeroOficioUMMOGSecretaria + "-OF";

			List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionVerificarSecretaria = estudianteTrabajoTitulacionService
					.obtenerLista(
							"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua"
									+ " inner join ett.proceso p where p.id=?1 and ua.id=?2 and ett.fechaActaGraduacion is not null and ett.nroOficioTesoreria is not null"
									+ " and ett.numeroActaGraduacion is not null and ett.nroOficioSecretaria=?3",
							new Object[] { proceso, unidadesAcademicas.get(0).getId(),
									numeroOficioUMMOGSecretariaCompleto },
							0, false, new Object[] {});
			List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPPVerificarSecretaria = estudianteExamenComplexivoPPService
					.obtenerLista(
							"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua"
									+ " inner join epp.proceso p where p.id=?1 and ua.id=?2 and epp.fechaActaGraduacion is not null and epp.nroOficioTesoreria is not null"
									+ " and epp.numeroActaGraduacion is not null and epp.nroOficioSecretaria=?3",
							new Object[] { proceso, unidadesAcademicas.get(0).getId(),
									numeroOficioUMMOGSecretariaCompleto },
							0, false, new Object[] {});

			System.out.println("Número de estudiantes TT numeroOficioUMMOGSecretaria: "
					+ estudiantesTrabajosTitulacionVerificarSecretaria.size());
			System.out.println("Número de estudiantes PP numeroOficioUMMOGSecretaria: "
					+ estudiantesExamenComplexivoPPVerificarSecretaria.size());

			if (estudiantesTrabajosTitulacionVerificarSecretaria.size() == 0
					&& estudiantesExamenComplexivoPPVerificarSecretaria.size() == 0) {
				System.out.println("No existen registros, se debe registrar en la base de datos.");
				List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionAprobadosConActaGraduacion = estudianteTrabajoTitulacionService
						.obtenerLista(
								"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.proceso p"
										+ " where p.id=?1 and ua.id=?2 and ett.fechaActaGraduacion is not null and ett.nroOficioTesoreria is not null and ett.numeroActaGraduacion is not null"
										+ " and ett.nroOficioSecretaria is null",
								new Object[] { proceso, unidadesAcademicas.get(0).getId() }, 0, false, new Object[] {});
				List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPPAprobadoConActaGraduacion = estudianteExamenComplexivoPPService
						.obtenerLista(
								"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua inner join epp.proceso p"
										+ " where p.id=?1 and ua.id=?2 and epp.fechaActaGraduacion is not null and epp.nroOficioTesoreria is not null and epp.numeroActaGraduacion is not null"
										+ " and epp.nroOficioSecretaria is null",
								new Object[] { proceso, unidadesAcademicas.get(0).getId() }, 0, false, new Object[] {});

				System.out.println(
						"Numero de estudiantes con numero acta de graduaicon que tendrán numero de oficio tesorería: "
								+ estudiantesTrabajosTitulacionAprobadosConActaGraduacion.size());
				System.out.println(
						"Numero de estudiantes con numero acta de graduaicon que tendrán numero de oficio tesorería: "
								+ estudiantesExamenComplexivoPPAprobadoConActaGraduacion.size());

				SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");

				for (EstudianteTrabajoTitulacion ett : estudiantesTrabajosTitulacionAprobadosConActaGraduacion) {
					ett.setNroOficioSecretaria(numeroOficioUMMOGSecretariaCompleto);
					estudianteTrabajoTitulacionService.actualizar(ett);
				}
				for (EstudianteExamenComplexivoPP epp : estudiantesExamenComplexivoPPAprobadoConActaGraduacion) {
					epp.setNroOficioSecretaria(numeroOficioUMMOGSecretariaCompleto);
					estudianteExamenComplexivoPPService.actualizar(epp);
				}
				// System.out.println("Se imprime el reporte para secretaria.");
				String fechaOficio = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(fechaOficioUMMOGSecretaria);
				// System.out.println("Valor del checkbox:
				// "+estudiosHomologaciones);
				String homologaciones = (estudiosHomologaciones == true ? "1" : "0");
				// System.out.println("Valor del checkbox en String:
				// "+homologaciones);
				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EstudiantesAprobadosParaSecretaria");
					report.addParameter("PROCESO", proceso);
					report.addParameter("UA", unidadesAcademicas.get(0).getId());
					report.addParameter("FECHA", fechaOficio);
					report.addParameter("NRO_OFICIO", numeroOficioUMMOGSecretariaCompleto);
					report.addParameter("HOMOLOGACIONES", homologaciones);

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// System.out.println("Fecha del oficio: " + fechaOficio);
				// System.out.println("Ummog del oficio: " +
				// numeroOficioUMMOGSecretariaCompleto);

				numeroOficioUMMOGSecretaria = "";
				estudiosHomologaciones = false;

			} else {
				System.out.println("Existen datos con los estudiantes.");
				System.out.println("Se imprime el reporte para secretaria.");
				String fechaOficio = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(fechaOficioUMMOGSecretaria);
				// System.out.println("Valor del checkbox:
				// "+estudiosHomologaciones);
				String homologaciones = (estudiosHomologaciones == true ? "1" : "0");
				// System.out.println("Valor del checkbox en String:
				// "+homologaciones);
				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EstudiantesAprobadosParaSecretaria");
					report.addParameter("PROCESO", proceso);
					report.addParameter("UA", unidadesAcademicas.get(0).getId());
					report.addParameter("FECHA", fechaOficio);
					report.addParameter("NRO_OFICIO", numeroOficioUMMOGSecretariaCompleto);
					report.addParameter("HOMOLOGACIONES", homologaciones);

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println("Fecha del oficio: " + fechaOficio);
				// System.out.println("Ummog del oficio: " +
				// numeroOficioUMMOGSecretariaCompleto);
				numeroOficioUMMOGSecretaria = "";
				estudiosHomologaciones = false;
			}
		}
	}

	public void estudiantesAprobadosTesoreria() {
		if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
			System.out.println("Seleccione el proceso.");
		} else if (numeroOficioUMMOGTesoreria.compareTo("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un número de oficio de UMMOG.");
			System.out.println("Ingrese un número de oficio de UMMOG.");
		} else {
			String numeroOficioUMMOGTesoreriaCompleto = "";

			numeroOficioUMMOGTesoreriaCompleto = "UTMACH-" + unidadesAcademicas.get(0).getId() + "-UMMOG-"
					+ numeroOficioUMMOGTesoreria + "-OF";

			List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionAprobadoEnlaceOficioTesoreria = estudianteTrabajoTitulacionService
					.obtenerLista(
							"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua"
									+ " inner join ett.proceso p where p.id=?1 and ua.id=?2 and ett.nroOficioTesoreria=?3",
							new Object[] { proceso, unidadesAcademicas.get(0).getId(),
									numeroOficioUMMOGTesoreriaCompleto },
							0, false, new Object[] {});
			List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPPAprobadoEnlaceOficioTesoreria = estudianteExamenComplexivoPPService
					.obtenerLista(
							"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua"
									+ " inner join epp.proceso p where p.id=?1 and ua.id=?2 and epp.nroOficioTesoreria=?3",
							new Object[] { proceso, unidadesAcademicas.get(0).getId(),
									numeroOficioUMMOGTesoreriaCompleto },
							0, false, new Object[] {});

			System.out.println("Número de estudiantes TT con fecha de graduación y numeroOficioUMMOG: "
					+ estudiantesTrabajosTitulacionAprobadoEnlaceOficioTesoreria.size());
			System.out.println("Número de estudiantes PP con fecha de graduación y numeroOficioUMMOG: "
					+ estudiantesExamenComplexivoPPAprobadoEnlaceOficioTesoreria.size());

			if (estudiantesTrabajosTitulacionAprobadoEnlaceOficioTesoreria.size() == 0
					&& estudiantesExamenComplexivoPPAprobadoEnlaceOficioTesoreria.size() == 0) {
				System.out.println("No existen registros, se debe registrar en la base de datos.");
				List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionAprobadoConEnlace = estudianteTrabajoTitulacionService
						.obtenerLista(
								"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.proceso p"
										+ " where p.id=?1 and ua.id=?2 and ett.urlBiblioteca is not null and ett.nroOficioTesoreria is null",
								new Object[] { proceso, unidadesAcademicas.get(0).getId() }, 0, false, new Object[] {});
				List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPPAprobadoConEnlace = estudianteExamenComplexivoPPService
						.obtenerLista(
								"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua inner join epp.proceso p"
										+ " where p.id=?1 and ua.id=?2 and epp.urlBiblioteca is not null and epp.nroOficioTesoreria is null",
								new Object[] { proceso, unidadesAcademicas.get(0).getId() }, 0, false, new Object[] {});

				System.out.println(
						"Numero de estudiantes con numero acta de graduaicon que tendrán numero de oficio tesorería: "
								+ estudiantesTrabajosTitulacionAprobadoConEnlace.size());
				System.out.println(
						"Numero de estudiantes con numero acta de graduaicon que tendrán numero de oficio tesorería: "
								+ estudiantesExamenComplexivoPPAprobadoConEnlace.size());

				SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");

				for (EstudianteTrabajoTitulacion ett : estudiantesTrabajosTitulacionAprobadoConEnlace) {
					ett.setNroOficioTesoreria(numeroOficioUMMOGTesoreriaCompleto);
					ett.setFechaActaGraduacion(fechaActaGraduacion);
					estudianteTrabajoTitulacionService.actualizar(ett);
				}
				for (EstudianteExamenComplexivoPP epp : estudiantesExamenComplexivoPPAprobadoConEnlace) {
					epp.setNroOficioTesoreria(numeroOficioUMMOGTesoreriaCompleto);
					epp.setFechaActaGraduacion(fechaActaGraduacion);
					estudianteExamenComplexivoPPService.actualizar(epp);
				}
				System.out.println("Se imprime el reporte para tesorería.");
				System.out.println("la fecha es: " + fechaActaGraduacion);
				System.out.println("la fecha es: " + dt1.format(fechaActaGraduacion));
				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EstudiantesAprobadosParaTesoreria");
					report.addParameter("PROCESO", proceso);
					report.addParameter("UNIDAD_ACADEMICA", unidadesAcademicas.get(0).getId());
					report.addParameter("NUMERO_OFICIO_UMMOG", numeroOficioUMMOGTesoreriaCompleto);

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}
				numeroOficioUMMOGTesoreria = "";

			} else {
				System.out.println("Existen datos con los estudiantes.");
				System.out.println("Se imprime el reporte para tesorería.");
				System.out.println("la fecha es: " + fechaActaGraduacion);
				SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
				System.out.println("la fecha es: " + dt1.format(fechaActaGraduacion));
				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EstudiantesAprobadosParaTesoreria");
					report.addParameter("PROCESO", proceso);
					report.addParameter("UNIDAD_ACADEMICA", unidadesAcademicas.get(0).getId());
					report.addParameter("NUMERO_OFICIO_UMMOG", numeroOficioUMMOGTesoreriaCompleto);

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}
				numeroOficioUMMOGTesoreria = "";
			}
		}

	}

	public void estudiantesExamenComplexivoCalificaciones() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EstudiantesExamenComplexivoCalificaciones");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void estudiantesExamenComplexivoSustentaciones() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} /*
			 * else if (carrera == 0) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "Seleccione la carrera"); }
			 */ else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ListadoEstudiantesSustentaciones");
				report.addParameter("proceso", proceso);
				report.addParameter("unidadAcademica", unidadesAcademicas.get(0).getId());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesExamenComplexivoTemas() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_EstudiantesGraduadosTitulos");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesTrabajosTitulacionGraduadosLineasSeminariosTemas() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_LineasInvestigacionEstudianteGraduadoTitulo");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesTrabajosTitulacionLineasSeminariosTemas() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_LineasSeminariosTemasComite");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesTrabajoTitulacionCalificaciones() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EstudiantesTrabajosTitulacionCalificaciones");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void estudiantesUrkundCitas() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EstudiantesUrkundCitas");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public String getActaGraduacion() {
		return actaGraduacion;
	}

	public List<Texto> getActasGraduacion() {
		return actasGraduacion;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public Boolean getEstudiosHomologaciones() {
		return estudiosHomologaciones;
	}

	public Date getFechaActaGraduacion() {
		return fechaActaGraduacion;
	}

	public Date getFechaOficioUMMOGSecretaria() {
		return fechaOficioUMMOGSecretaria;
	}

	public String getFormatoReporte() {
		return formatoReporte;
	}

	public Integer getMalla() {
		return malla;
	}

	public List<MallaCurricular> getMallas() {
		return mallas;
	}

	public String getNumeroOficioUMMOGSecretaria() {
		return numeroOficioUMMOGSecretaria;
	}

	public String getNumeroOficioUMMOGTesoreria() {
		return numeroOficioUMMOGTesoreria;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public void imprimirAptitudLegal() {
		if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {

			List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionOficioTesoreria = estudianteTrabajoTitulacionService
					.obtenerLista(
							"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua"
									+ " inner join ett.proceso p where p.id=?1 and c.id=?2 and ett.fechaActaGraduacion is not null and ett.nroOficioTesoreria is not null"
									+ " and ett.numeroActaGraduacion is not null",
							new Object[] { proceso, carrera }, 0, false, new Object[] {});
			List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPPOficioTesoreria = estudianteExamenComplexivoPPService
					.obtenerLista(
							"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua"
									+ " inner join epp.proceso p where p.id=?1 and c.id=?2 and epp.fechaActaGraduacion is not null and epp.nroOficioTesoreria is not null"
									+ " and epp.numeroActaGraduacion is not null",
							new Object[] { proceso, carrera }, 0, false, new Object[] {});

			System.out.println("Número de estudiantes TT con fecha de graduación y numeroOficioUMMOG: "
					+ estudiantesTrabajosTitulacionOficioTesoreria.size());
			System.out.println("Número de estudiantes PP con fecha de graduación y numeroOficioUMMOG: "
					+ estudiantesExamenComplexivoPPOficioTesoreria.size());

			if (estudiantesTrabajosTitulacionOficioTesoreria.size() == 0
					&& estudiantesExamenComplexivoPPOficioTesoreria.size() == 0) {
				System.out.println("Se genera el numero de acta de graduacion.");
				usuarioService.actualizarSQL_Actualizar(
						"select exetasi.asignar_numero_acta_graduacion(\'" + proceso + "\'," + carrera + ");");

				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("AptitudLegalCarrera");
					report.addParameter("PROCESO", proceso);
					report.addParameter("CARRERA", String.valueOf(carrera));

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("AptitudLegalCarrera");
					report.addParameter("PROCESO", proceso);
					report.addParameter("CARRERA", String.valueOf(carrera));

					documento = UtilsReport.ejecutarReporte(report);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	public boolean isEsAdministrador() {
		return esAdministrador;
	}

	public boolean isEsAnalista() {
		return esAnalista;
	}

	public boolean isEsCoordinador() {
		return esCoordinador;
	}

	public boolean isEsUmmog() {
		return esUmmog;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			if (procesos != null)
				procesos.clear();
			if (carreras != null)
				carreras.clear();
			if (mallas != null)
				mallas.clear();
			a();
		}
	}

	public void listadoAsistenciaExamenComplexivoPT() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ListadoAsistenciaPT");
				report.addParameter("PROCESO", proceso);
				report.addParameter("TIPO_EXAMEN", "OR");

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void listadoEstudiantesTitulosInvetigacionExamenComplexivo() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_EstudiantesGraduadosTitulos");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", String.valueOf(carrera));

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void listadoEstudiantesTitulosInvetigacionTrabajoTitulacion() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_LineasInvestigacionEstudianteGraduadoTitulo");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", String.valueOf(carrera));

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void obtenerCarreras() {
		carrera = 0;
		carreras = carreraService.obtenerPorSql("select distinct c.* from \"estudiantesTrabajosTitulacion\" eet "
				+ "inner join \"permisosCarreras\" pc on (pc.carrera=eet.carrera) inner join usuarios u on (u.id=pc.usuario) "
				+ " inner join carreras c on (c.id=pc.carrera) where proceso='" + proceso + "' and u.email='"
				+ UtilSeguridad.obtenerUsuario() + "' "
				+ "union select distinct c.* from \"estudiantesExamenComplexivoPT\" eet "
				+ "inner join \"permisosCarreras\" pc on (pc.carrera=eet.carrera) inner join usuarios u on (u.id=pc.usuario) "
				+ "inner join carreras c on (c.id=pc.carrera) where proceso='" + proceso + "' and u.email='"
				+ UtilSeguridad.obtenerUsuario() + "' " + "order by id", Carrera.class);

		if (carreras != null && carreras.size() == 0) {
			carreras = carreraService.obtenerPorSql(
					"select distinct c.* from \"permisosCarreras\" pc inner join usuarios u on (u.id=pc.usuario) "
							+ " inner join carreras c on (c.id=pc.carrera) where u.email='"
							+ UtilSeguridad.obtenerUsuario() + "' "
							+ "union select distinct c.* from \"permisosCarreras\" pc inner join usuarios u on (u.id=pc.usuario) "
							+ "inner join carreras c on (c.id=pc.carrera) where u.email='"
							+ UtilSeguridad.obtenerUsuario() + "' " + "order by id",
					Carrera.class);
		}

		unidadesAcademicas = unidadAcademicaService.obtenerPorSql(
				"select distinct ua.* from \"unidadesAcademicas\" ua inner join carreras c on (c.\"unidadAcademica\"=ua.id)"
						+ " inner join	\"permisosCarreras\" pc on (pc.carrera=c.id) inner join	usuarios u on (pc.usuario =u.id) where u.email = '"
						+ UtilSeguridad.obtenerUsuario() + "'",
				UnidadAcademica.class);
	}

	public void participantes() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("ListadoParticipantes");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reactivosPracticosSeleccionados() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ReactivosPracticosSeleccionados");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void resultadoExamenComplexivoTeorico() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ResultadosPT");
				report.addParameter("PROCESO", proceso);

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setActaGraduacion(String actaGraduacion) {
		this.actaGraduacion = actaGraduacion;
	}

	public void setActasGraduacion(List<Texto> actasGraduacion) {
		this.actasGraduacion = actasGraduacion;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setEsAdministrador(boolean esAdministrador) {
		this.esAdministrador = esAdministrador;
	}

	public void setEsAnalista(boolean esAnalista) {
		this.esAnalista = esAnalista;
	}

	public void setEsCoordinador(boolean esCoordinador) {
		this.esCoordinador = esCoordinador;
	}

	public void setEstudiosHomologaciones(Boolean estudiosHomologaciones) {
		this.estudiosHomologaciones = estudiosHomologaciones;
	}

	public void setEsUmmog(boolean esUmmog) {
		this.esUmmog = esUmmog;
	}

	public void setFechaActaGraduacion(Date fechaActaGraduacion) {
		this.fechaActaGraduacion = fechaActaGraduacion;
	}

	public void setFechaOficioUMMOGSecretaria(Date fechaOficioUMMOGSecretaria) {
		this.fechaOficioUMMOGSecretaria = fechaOficioUMMOGSecretaria;
	}

	public void setFormatoReporte(String formatoReporte) {
		this.formatoReporte = formatoReporte;
	}

	public void setMalla(Integer malla) {
		this.malla = malla;
	}

	public void setMallas(List<MallaCurricular> mallas) {
		this.mallas = mallas;
	}

	public void setNumeroOficioUMMOGSecretaria(String numeroOficioUMMOGSecretaria) {
		this.numeroOficioUMMOGSecretaria = numeroOficioUMMOGSecretaria;
	}

	public void setNumeroOficioUMMOGTesoreria(String numeroOficioUMMOGTesoreria) {
		this.numeroOficioUMMOGTesoreria = numeroOficioUMMOGTesoreria;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void tutorComiteEvaluadorConsejoDirectivo() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {

			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TutorComiteEvaluadorConsejoDirectivo");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void tutorComiteEvaluadorConsejoDirectivoTrabajoTitulacion() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {

			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_TutorComiteEvaluadorConsejoDirectivo");
				report.addParameter("PROCESO", proceso);
				report.addParameter("CARRERA", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void tutoriasExamenComplexivo() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CantidadTutorias");
				report.addParameter("proceso", proceso);
				report.addParameter("carrera", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void tutoriasTrabajoTitulacion() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione la carrera");
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_CantidadTutorias");
				report.addParameter("proceso", proceso);
				report.addParameter("carrera", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
