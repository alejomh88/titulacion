package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.mail.internet.AddressException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.SecuenciaActaCalificacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DetalleEntidadGenericaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.GrupoService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SecuenciaActaCalificacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class PrincipalECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private DetalleEntidadGenericaService detalleEntidadGenericaService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private SecuenciaActaCalificacionService secuenciaActaCalificacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private GrupoService grupoService;

	@Autowired
	private ParametroService parametroService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private boolean panelPrincipal;
	private boolean panelTutorias;
	private boolean panelSeleccionar;
	private boolean panelDashboard;
	private boolean panelComite;
	private boolean panelTrabajoEscrito;
	private List<Usuario> especialistas;

	private String criterioBusquedaEspecialista;
	private String criterioBusquedaEstudiante;

	private boolean boolTutor;
	private boolean boolEspecialista1;
	private boolean boolEspecialista2;
	private boolean boolEspecialista3;
	private boolean boolEspecialistaSuplente1;

	private Usuario tutor;
	private Usuario especialista;
	private Usuario especialista1;
	private Usuario especialista2;
	private Usuario especialista3;
	private Usuario especialistaSuplente1;

	private BigDecimal calificacionParteTeoricaFinal;
	private BigDecimal calificacionParteEscritaFinal;
	private BigDecimal calificacionParteOralFinal;
	private BigDecimal calificacionFinal;

	private Date fechaParteTeorica;
	private Grupo grupoExamen;

	private boolean activarEspecialistaE;
	private boolean activarEspecialistaO;
	public String especialistaSuplantadoInterfazE;
	public String especialistaSuplantadoInterfazO;

	private boolean esUmmog;
	private boolean validacionArchivo;
	private String urlDocumento;
	private StreamedContent streamedContent;

	private String textoInconsistencia;

	private FileInputStream fileInputStream;
	private InputStream stream;
	private StreamedContent documento;

	private Integer totalHoras;
	private Integer totalMinutos;
	private String numeroHorasTutorias;
	private Integer numeroHorasRestantes;
	private Integer numeroMinutosRestantes;
	private String numeroTotalHorasMinutosRestantes;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTrabajoEscrito = false;
		especialista1 = new Usuario();
		especialista2 = new Usuario();
		especialista3 = new Usuario();
		especialistaSuplente1 = new Usuario();
		boolEspecialista1 = true;

		esUmmog = UtilSeguridad.obtenerRol("UMMO");
	}

	public void actaCalificaciones(EstudianteExamenComplexivoPP epp) {
		estudianteExamenComplexivoPP = epp;

		String calificaciones = estudiantesExamenComplexivoPPService.traerCalificaciones(estudianteExamenComplexivoPP);
		BigDecimal notaTeorica = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		System.out.println("Nota Teórica: " + notaTeorica);
		BigDecimal notaEscrita = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		System.out.println("Nota Escrita: " + notaEscrita);
		BigDecimal notaOral = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
		System.out.println("Nota Oral: " + notaOral);
		BigDecimal notaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[3]);
		System.out.println("Nota Final: " + notaFinal);

		Boolean[] bn = { false, false, false, false };

		if (notaFinal.compareTo(new BigDecimal("69.5")) >= 0) {

			try {
				estudiantesExamenComplexivoPPService.actualizarSQL("select asignar_certificados_estudiante_epp("
						+ epp.getId() + ", " + epp.getCarrera().getId() + ");");

				SecuenciaActaCalificacion sac = new SecuenciaActaCalificacion();
				sac.setIdt(estudianteExamenComplexivoPP.getId());
				sac.setProceso(estudianteExamenComplexivoPP.getProceso().getId());
				sac.setTitulacion("EC");
				secuenciaActaCalificacionService.insertar(sac);
				estudianteExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerObjeto(
						"select pp from EstudianteExamenComplexivoPP pp inner join fetch pp.carrera c "
								+ "where pp.id=?1",
						new Object[] { estudianteExamenComplexivoPP.getId() }, false, new Object[] { "getTutorias" });

				Report report = new Report();
				report.setFormat("PDF");
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_ActaCalificacion");

				// presentaMensaje(FacesMessage.SEVERITY_INFO, "Se generó el
				// acta de
				// calificaciones satisfactoriamente");
				// Map<String, Object> parametro = new HashMap<String,
				// Object>();
				String nomenclaturaTitulo = "";
				if (estudianteExamenComplexivoPP.getEstudiante().getGenero().compareTo("M") == 0) {
					nomenclaturaTitulo = estudianteExamenComplexivoPP.getCarrera().getNomenclaturaTituloMasculino();

					if (estudianteExamenComplexivoPP.getCarrera().getTipoTitulo() != null)
						nomenclaturaTitulo = nomenclaturaTitulo.concat(" ")
								.concat(estudianteExamenComplexivoPP.getCarrera().getTipoTitulo()).concat(" ")
								.concat(estudianteExamenComplexivoPP.getCarrera().getDetalleTitulo());
				} else {
					nomenclaturaTitulo = estudianteExamenComplexivoPP.getCarrera().getNomenclaturaTituloFemenino();

					if (estudianteExamenComplexivoPP.getCarrera().getTipoTitulo() != null)
						nomenclaturaTitulo = nomenclaturaTitulo.concat(" ")
								.concat(estudianteExamenComplexivoPP.getCarrera().getTipoTitulo()).concat(" ")
								.concat(estudianteExamenComplexivoPP.getCarrera().getDetalleTitulo());
				}

				report.addParameter("idEC", String.valueOf(estudianteExamenComplexivoPP.getId()));
				report.addParameter("estudiante", estudianteExamenComplexivoPP.getEstudiante().getGenero() + "-"
						+ estudianteExamenComplexivoPP.getEstudiante().getId() + "-"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre() + "-" + nomenclaturaTitulo);
				// parametro.put("estudiante",
				// estudianteExamenComplexivoPP.getEstudiante().getGenero() +
				// "-"
				// + estudianteExamenComplexivoPP.getEstudiante().getId() + "-"
				// +
				// estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
				// + "-" + nomenclaturaTitulo);

				report.addParameter("encabezado",
						"Firman la presente acta "
								+ (estudianteExamenComplexivoPP.getEstudiante().getGenero().compareTo("F") == 0 ? "la"
										: "el")
								+ " estudiante, los miembros del comité evaluador y "
								+ (estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
										.split("-")[0].compareTo("F") == 0 ? "la jefa" : "el jefe")
								+ " de la UMMOG.");
				// parametro
				// .put("encabezado",
				// "Firman la presente acta "
				// +
				// (estudianteExamenComplexivoPP.getEstudiante().getGenero().compareTo("F")
				// == 0
				// ? "la" : "el")
				// + " estudiante, los miembros del comité evaluador y "
				// +
				// (estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica()
				// .getNombreJefeUmmog().split("-")[0].compareTo("F") == 0 ? "la
				// jefa"
				// : "el jefe")
				// + " de la UMMOG.");

				report.addParameter("unidadAcademica",
						estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombre());
				// parametro.put("unidadAcademica",
				// estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombre());

				report.addParameter("carrera", estudianteExamenComplexivoPP.getCarrera().getNombre());
				// parametro.put("carrera",
				// estudianteExamenComplexivoPP.getCarrera().getNombre());

				report.addParameter("numeroActa", estudianteExamenComplexivoPP.getProceso().getId() + "/"
						+ String.format("%04d", estudianteExamenComplexivoPP.getNumeroActaCalificacion()));
				// parametro.put("numeroActa",
				// estudianteExamenComplexivoPP.getProceso().getId() + "/"
				// + String.format("%04d",
				// estudianteExamenComplexivoPP.getNumeroActaCalificacion()));

				int r = 1;

				if (estudianteExamenComplexivoPP.getEe1() != null || estudianteExamenComplexivoPP.getOoe1() != null) {
					report.addParameter("parametro" + (r++),
							estudianteExamenComplexivoPP.getEspecialista1().getApellidoNombre() + "-"
									+ estudianteExamenComplexivoPP.getEspecialista1().getId()
									+ "-ESPECIALISTA PRINCIPAL 1");
					// parametro.put("parametro" + (r++),
					// estudianteExamenComplexivoPP.getEspecialista1().getApellidoNombre()
					// + "\nC.I.: "
					// + estudianteExamenComplexivoPP.getEspecialista1().getId()
					// + "\nESPECIALISTA PRINCIPAL 1");
				}
				if (estudianteExamenComplexivoPP.getEe2() != null || estudianteExamenComplexivoPP.getOoe2() != null) {
					report.addParameter("parametro" + (r++),
							estudianteExamenComplexivoPP.getEspecialista2().getApellidoNombre() + "-"
									+ estudianteExamenComplexivoPP.getEspecialista2().getId()
									+ "-ESPECIALISTA PRINCIPAL 2");
					// parametro.put("parametro" + (r++),
					// estudianteExamenComplexivoPP.getEspecialista2().getApellidoNombre()
					// + "\nC.I.: "
					// + estudianteExamenComplexivoPP.getEspecialista2().getId()
					// + "\nESPECIALISTA PRINCIPAL 2");
				}
				if (estudianteExamenComplexivoPP.getEe3() != null || estudianteExamenComplexivoPP.getOoe3() != null) {
					report.addParameter("parametro" + (r++),
							estudianteExamenComplexivoPP.getEspecialista3().getApellidoNombre() + "-"
									+ estudianteExamenComplexivoPP.getEspecialista3().getId()
									+ "-ESPECIALISTA PRINCIPAL 3");
					// parametro.put("parametro" + (r++),
					// estudianteExamenComplexivoPP.getEspecialista3().getApellidoNombre()
					// + "\nC.I.: "
					// + estudianteExamenComplexivoPP.getEspecialista3().getId()
					// + "\nESPECIALISTA PRINCIPAL 3");
				}
				if (estudianteExamenComplexivoPP.getEs1() != null || estudianteExamenComplexivoPP.getOos1() != null) {
					report.addParameter("parametro" + (r++),
							estudianteExamenComplexivoPP.getEspecialistaSuplente1().getApellidoNombre() + "-"
									+ estudianteExamenComplexivoPP.getEspecialistaSuplente1().getId()
									+ "-ESPECIALISTA SUPLENTE");
					// parametro.put("parametro" + (r++),
					// estudianteExamenComplexivoPP.getEspecialistaSuplente1().getApellidoNombre()
					// + "\nC.I.: "
					// +
					// estudianteExamenComplexivoPP.getEspecialistaSuplente1().getId()
					// + "\nESPECIALISTA SUPLENTE");
				}
				report.addParameter("parametro" + r++, estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "-" + estudianteExamenComplexivoPP.getEstudiante().getId() + "-ESTUDIANTE");
				// parametro.put("parametro" + r++,
				// estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
				// + "\nC.I.: " +
				// estudianteExamenComplexivoPP.getEstudiante().getId() +
				// "\nESTUDIANTE");

				report.addParameter("parametro" + r++,
						estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
								.split("-")[2]
								+ "-"
								+ estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
										.split("-")[1]
								+ "-"
								+ (estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
										.split("-")[0].compareTo("F") == 0 ? "JEFA DE UMMOG" : "JEFE DE UMMOG"));
				// parametro
				// .put("parametro" + r,
				// estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
				// .split("-")[2]
				// + "\nC.I.: "
				// +
				// estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica()
				// .getNombreJefeUmmog().split("-")[1]
				// + "\n"
				// +
				// (estudianteExamenComplexivoPP.getCarrera().getUnidadAcademica()
				// .getNombreJefeUmmog().split("-")[0].compareTo("F") == 0 ?
				// "JEFA
				// DE UMMOG"
				// : "JEFE DE UMMOG"));

				report.addParameter("notaTeorica", String.valueOf(notaTeorica));
				// parametro.put("notaTeorica", notaTeorica);

				report.addParameter("notaEscrita", String.valueOf(notaEscrita));
				// parametro.put("notaEscrita", notaEscrita);

				report.addParameter("notaOral", String.valueOf(notaOral));
				// parametro.put("notaOral", notaOral);

				report.addParameter("notaFinal", String.valueOf(notaFinal));
				// parametro.put("notaFinal", notaFinal);

				if (estudianteExamenComplexivoPP.getFechaSustentacionGracia() == null) {
					report.addParameter("fechaActa", UtilsDate
							.convertirDateATexto(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria()));
					// parametro.put("fechaActa",
					// UtilsDate.convertirDateATexto(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria()));
				} else {
					report.addParameter("fechaActa",
							UtilsDate.convertirDateATexto(estudianteExamenComplexivoPP.getFechaSustentacionGracia()));
					// parametro.put("fechaActa",
					// UtilsDate.convertirDateATexto(estudianteExamenComplexivoPP.getFechaSustentacionGracia()));
				}

				// List<DetalleEntidadGenerica> deg =
				// detalleEntidadGenericaService.obtenerPorSql(
				// "select 12 as id, " + "'' as \"atributoString1\", '' as
				// \"atributoString2\", "
				// + "'' as \"atributoString3\", '' as \"atributoString4\", ''
				// as
				// \"atributoString5\", '' as \"atributoString6\", "
				// + "'' as \"atributoString7\", '' as \"atributoString8\", ''
				// as
				// \"atributoString9\", '' as \"atributoString10\" "
				// + "from exetasi.\"estudiantesExamenComplexivoPP\" eec limit
				// 1",
				// DetalleEntidadGenerica.class);
				//
				// reporteService.generarReportePDF(deg, parametro,
				// "ActaCalificacionEC",
				// "ActaCalificacion_EC_" + proceso + "_" +
				// estudianteExamenComplexivoPP.getEstudiante().getId());

				documento = UtilsReport.ejecutarReporte(report);

			} catch (Exception e) {
				System.out.println("Error del certificado");
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El estudiante no completa el puntaje base para generar el acta de calificaciones (70 puntos) o falta una calificación por parte del comité evaluador.");
			}
		} else {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El estudiante no completa el puntaje base para generar el acta de calificaciones (70 puntos).");
		}

	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join fetch epp.carrera c "
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[] { "getTutorias" });
		else
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join fetch epp.carrera c "
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or translate(e.apellidoNombre,'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate(upper(?3),'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[] { "getTutorias" });
	}

	public void buscarEspecialista() {
		especialistas = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where (u.id=?1 or translate(lower(u.apellidoNombre),'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
						+ " like translate(lower(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) and "
						+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU') order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaEspecialista + "%" }, 0, false, new Object[] {});
	}

	public void descargarArchivo() {

		if (estudianteExamenComplexivoPP.getArchivo() != null) {
			try {
				System.out.println("Ingreso al descargar archivo.");
				fileInputStream = new FileInputStream(estudianteExamenComplexivoPP.getArchivo());
				if (fileInputStream.read() == -1)
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "El archivo se encuentra dañado.");
				else {
					stream = new FileInputStream(estudianteExamenComplexivoPP.getArchivo());
					documento = new DefaultStreamedContent(stream, "application/pdf",
							"TT_" + estudianteExamenComplexivoPP.getEstudiante().getId() + "_"
									+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre() + ".pdf");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "El estudiante aún no sube el documento a la plataforma.");
		}

	}

	public void desvalidarDocumentoE3() {
		estudianteExamenComplexivoPP.setValidarDocumentoE3(null);
		estudiantesExamenComplexivoPPService
				.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"validarDocumentoE3\" = "
						+ estudianteExamenComplexivoPP.getValidarDocumentoE3() + " WHERE id="
						+ estudianteExamenComplexivoPP.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha desvalidado la estructura del documento correctamente.");
	}

	public void eliminarCitas() {
		estudianteExamenComplexivoPP.setCitas1(null);
		estudianteExamenComplexivoPP.setObservacionesCitas(null);
		estudiantesExamenComplexivoPPService.actualizarSQL(
				"UPDATE \"estudiantesExamenComplexivoPP\" SET citas1 = " + estudianteExamenComplexivoPP.getCitas1()
						+ ", \"observacionesCitas\"=" + estudianteExamenComplexivoPP.getObservacionesCitas()
						+ " WHERE id=" + estudianteExamenComplexivoPP.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha eliminado el número de citas correctamente.");
	}

	public void eliminarNotaEscrita1() {
		estudianteExamenComplexivoPP.setEe1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee1="
				+ estudianteExamenComplexivoPP.getEe1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscrita2() {
		estudianteExamenComplexivoPP.setEe2(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee2="
				+ estudianteExamenComplexivoPP.getEe2() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscrita3() {
		estudianteExamenComplexivoPP.setEe3(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ee3="
				+ estudianteExamenComplexivoPP.getEe3() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscritaSuplente1() {
		estudianteExamenComplexivoPP.setEs1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET es1="
				+ estudianteExamenComplexivoPP.getEs1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia1() {
		estudianteExamenComplexivoPP.setOge1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET oge1="
				+ estudianteExamenComplexivoPP.getOge1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia2() {
		estudianteExamenComplexivoPP.setOge2(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET oge2="
				+ estudianteExamenComplexivoPP.getOge2() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia3() {
		estudianteExamenComplexivoPP.setOge3(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET oge3="
				+ estudianteExamenComplexivoPP.getOge3() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGraciaSuplente1() {
		estudianteExamenComplexivoPP.setOgs1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ogs1="
				+ estudianteExamenComplexivoPP.getOgs1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario1() {
		estudianteExamenComplexivoPP.setOoe1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe1="
				+ estudianteExamenComplexivoPP.getOoe1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario2() {
		estudianteExamenComplexivoPP.setOoe2(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe2="
				+ estudianteExamenComplexivoPP.getOoe2() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario3() {
		estudianteExamenComplexivoPP.setOoe3(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET ooe3="
				+ estudianteExamenComplexivoPP.getOoe3() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinarioSuplente1() {
		estudianteExamenComplexivoPP.setOos1(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET oos1="
				+ estudianteExamenComplexivoPP.getOos1() + " WHERE id=" + estudianteExamenComplexivoPP.getId());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarPocentajeUrkund() {
		estudianteExamenComplexivoPP.setReporteUrkund(null);
		estudianteExamenComplexivoPP.setAntiplagio(null);
		estudianteExamenComplexivoPP.setObservacionesUrkund(null);
		estudiantesExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET antiplagio = "
				+ estudianteExamenComplexivoPP.getAntiplagio() + ", \"reporteUrkund\" = "
				+ estudianteExamenComplexivoPP.getReporteUrkund() + ", \"observacionesUrkund\" = "
				+ estudianteExamenComplexivoPP.getObservacionesUrkund() + " WHERE id="
				+ estudianteExamenComplexivoPP.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha eliminado el porcentaje de coincidencia correctamente.");
	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
		panelTutorias = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTrabajoEscrito = false;
	}

	public void establecerPrincipalDesdeTrabajo() {
		try {
			estudianteExamenComplexivoPP.setValidarArchivo(validacionArchivo);
			estudiantesExamenComplexivoPPService
					.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"validarArchivo\"="
							+ estudianteExamenComplexivoPP.getValidarArchivo() + " WHERE id="
							+ estudianteExamenComplexivoPP.getId() + ";");

			if (validacionArchivo == true) {
				String destinatario = estudianteExamenComplexivoPP.getEstudiante().getEmail();
				String asunto = "VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo ha sido validado de forma correcta, evidenciando que cumple con lo establecido en el artículo 46 de la guía de titulación.</span></div><br /><div>En los próximos días recibirá una notificación cuando su archivo se encuentre publicado en el repositorio digital de la Universidad Técnica de Machala.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
			} else if (validacionArchivo == false) {
				String destinatario = estudianteExamenComplexivoPP.getEstudiante().getEmail();
				String asunto = "URGENTE TITULACIÓN: VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Una vez realizada la revisi&oacute;n del documento digital proporcionado por usted(es) se encuentran las siguientes inconsistencias:</span></div><br /><div><span style='color: #000000;'>&nbsp;"
						+ textoInconsistencia
						+ "&nbsp;</span></div><div><span style='color: #000000;'>&nbsp;</span></div><div>Le animamos a examinar el instructivo para la carga del archivo en la plataforma, corregir las inconsistencias antes notificadas y volver a realizar el respectivo procedimiento.</div><br/><div><a href='https://docs.google.com/a/utmachala.edu.ec/document/d/1TYZe8EKMRCZATf82BoUtjD9K2xIRCN6Y-RVFND5DVpA/edit?usp=drive_web' target='_blank'><img src='https://ssl.gstatic.com/docs/doclist/images/icon_11_document_list.png' alt='' />&nbsp;<span dir='ltr'>instructivoSubidaArchivoRepositorioDigital</span></a></div><br /><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
			}

			buscar();
			textoInconsistencia = "";
			panelPrincipal = true;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = false;
			panelTrabajoEscrito = false;
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha validado el documento.");
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}
	}

	public BigDecimal getCalificacionFinal() {
		return calificacionFinal;
	}

	public BigDecimal getCalificacionParteEscritaFinal() {
		return calificacionParteEscritaFinal;
	}

	public BigDecimal getCalificacionParteOralFinal() {
		return calificacionParteOralFinal;
	}

	public BigDecimal getCalificacionParteTeoricaFinal() {
		return calificacionParteTeoricaFinal;
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCriterioBusquedaEspecialista() {
		return criterioBusquedaEspecialista;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public Usuario getEspecialista() {
		return especialista;
	}

	public Usuario getEspecialista1() {
		return especialista1;
	}

	public Usuario getEspecialista2() {
		return especialista2;
	}

	public Usuario getEspecialista3() {
		return especialista3;
	}

	public List<Usuario> getEspecialistas() {
		return especialistas;
	}

	public String getEspecialistaSuplantadoInterfazE() {
		return especialistaSuplantadoInterfazE;
	}

	public String getEspecialistaSuplantadoInterfazO() {
		return especialistaSuplantadoInterfazO;
	}

	public Usuario getEspecialistaSuplente1() {
		return especialistaSuplente1;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public Date getFechaParteTeorica() {
		return fechaParteTeorica;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public Grupo getGrupoExamen() {
		return grupoExamen;
	}

	public List<EstudianteExamenComplexivoPP> getListadoEstudiantesExamenComplexivoPP() {
		return listadoEstudiantesExamenComplexivoPP;
	}

	public Integer getNumeroHorasRestantes() {
		return numeroHorasRestantes;
	}

	public String getNumeroHorasTutorias() {
		return numeroHorasTutorias;
	}

	public Integer getNumeroMinutosRestantes() {
		return numeroMinutosRestantes;
	}

	public String getNumeroTotalHorasMinutosRestantes() {
		return numeroTotalHorasMinutosRestantes;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public InputStream getStream() {
		return stream;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public String getTextoInconsistencia() {
		return textoInconsistencia;
	}

	public Integer getTotalHoras() {
		return totalHoras;
	}

	public Integer getTotalMinutos() {
		return totalMinutos;
	}

	public Usuario getTutor() {
		return tutor;
	}

	public String getUrlDocumento() {
		return urlDocumento;
	}

	public void guardar() {
		// estudianteExamenComplexivoPP.setRecepcionPPDE(true);
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
		estudiantesExamenComplexivoPPService
				.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"recepcionPPDE\"=" + true + " WHERE id="
						+ estudianteExamenComplexivoPP.getId() + ";");
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Recibido entrega recepción");
		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		buscar();
	}

	public void guardarComite() {
		try {
			if (tutor.getId() == null || especialista1.getId() == null || especialista2.getId() == null
					|| especialista3.getId() == null || especialistaSuplente1.getId() == null)
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Falta de seleccionar un especialista del comité evaluador");
			else if ((especialista1.getId().compareTo(especialista2.getId()) == 0
					|| especialista1.getId().compareTo(especialista3.getId()) == 0
					|| especialista1.getId().compareTo(especialistaSuplente1.getId()) == 0)
					|| (especialista2.getId().compareTo(especialista3.getId()) == 0
							|| especialista2.getId().compareTo(especialistaSuplente1.getId()) == 0)
					|| (especialista3.getId().compareTo(especialistaSuplente1.getId()) == 0)) {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "Algún especialista está considerado mas de una vez");
			} else if (tutor.getId().compareTo(especialista1.getId()) != 0) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Registro no guardado. Recuerde que el tutor es el especialista 1.");
			} else {

				if (estudianteExamenComplexivoPP.getTutor() == null
						&& estudianteExamenComplexivoPP.getEspecialista1() == null
						&& estudianteExamenComplexivoPP.getEspecialista2() == null
						&& estudianteExamenComplexivoPP.getEspecialista3() == null
						&& estudianteExamenComplexivoPP.getEspecialistaSuplente1() == null) {

					String[] destinatario = { estudianteExamenComplexivoPP.getEstudiante().getEmail(), tutor.getEmail(),
							especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
							especialistaSuplente1.getEmail() };
					String asunto = "Asignación de comité evaluador.";
					String detalle = "<div dir='ltr'><span style='color:#000000;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar la asignación del comité evaluador de su parte práctica del examen complexivo (trabajo escrito y sustentación). Quendando de la siguiente manera:<br /><b>Estudiante:</b>"
							+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre() + " - ("
							+ estudianteExamenComplexivoPP.getEstudiante().getId() + ")<br /><b>Tutor: </b>"
							+ tutor.getApellidoNombre() + " - (" + tutor.getId() + ")<br/><b>Especialista 1: </b> "
							+ especialista1.getApellidoNombre() + " - (" + especialista1.getId()
							+ ")<br /><b>Especialista 2: </b> " + especialista2.getApellidoNombre() + " - ("
							+ especialista2.getId() + ") <br /><b>Especialista 3: </b> "
							+ especialista3.getApellidoNombre() + " - (" + especialista3.getId()
							+ ")<br /><b>Especialista Suplente: </b> " + especialistaSuplente1.getApellidoNombre()
							+ " - (" + especialistaSuplente1.getId()
							+ ") <br />En la plataforma podrá encontrar dicha información, específicamente en <b>Dashboard estudiante</b>. Los especialistas podrán observar a quienes evaluarán en el menú Evaluador y se escoje examen complexivo o trabajo de titulación</span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatario, asunto, detalle, listAdjunto,
							parametros);
				}
				estudianteExamenComplexivoPP.setTutor(tutor);
				estudianteExamenComplexivoPP.setEspecialista1(especialista1);
				estudianteExamenComplexivoPP.setEspecialista2(especialista2);
				estudianteExamenComplexivoPP.setEspecialista3(especialista3);
				estudianteExamenComplexivoPP.setEspecialistaSuplente1(especialistaSuplente1);

				if (activarEspecialistaE) {
					if (especialistaSuplantadoInterfazE
							.equals(estudianteExamenComplexivoPP.getEspecialista1().getId())) {
						if (estudianteExamenComplexivoPP.getEe1() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoE(especialista1);
							estudianteExamenComplexivoPP.setActivarSuplenteE(true);

						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista1.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazE
							.equals(estudianteExamenComplexivoPP.getEspecialista2().getId())) {
						if (estudianteExamenComplexivoPP.getEe2() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoE(especialista2);
							estudianteExamenComplexivoPP.setActivarSuplenteE(true);

						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazE
							.equals(estudianteExamenComplexivoPP.getEspecialista3().getId())) {
						if (estudianteExamenComplexivoPP.getEe3() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoE(especialista3);
							estudianteExamenComplexivoPP.setActivarSuplenteE(true);

						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista3.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					}
				} else {
					estudianteExamenComplexivoPP.setActivarSuplenteE(null);
					estudianteExamenComplexivoPP.setEspecialistaSuplantadoE(null);
				}

				if (activarEspecialistaO) {
					if (especialistaSuplantadoInterfazO
							.equals(estudianteExamenComplexivoPP.getEspecialista1().getId())) {
						if (estudianteExamenComplexivoPP.getOoe1() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(especialista1);
							estudianteExamenComplexivoPP.setActivarSuplenteO(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista1.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteExamenComplexivoPP.getEspecialista2().getId())) {
						if (estudianteExamenComplexivoPP.getOoe2() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(especialista2);
							estudianteExamenComplexivoPP.setActivarSuplenteO(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteExamenComplexivoPP.getEspecialista3().getId())) {
						if (estudianteExamenComplexivoPP.getOoe3() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(especialista3);
							estudianteExamenComplexivoPP.setActivarSuplenteO(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista3.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					}
				} else {
					estudianteExamenComplexivoPP.setActivarSuplenteO(null);
					estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(null);
				}

				estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);

				try {
					System.out.println("Entro en el crear archivo.");
					estudiantesExamenComplexivoPPService.creacionDocumentoDrive(estudianteExamenComplexivoPP);
				} catch (Exception e) {
					e.printStackTrace();
					presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
				}

				if (estudianteExamenComplexivoPP.getActivarSuplenteE() != null) {
					String[] destinatario = { estudianteExamenComplexivoPP.getEstudiante().getEmail(), tutor.getEmail(),
							especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
							especialistaSuplente1.getEmail() };
					String asunto = "Notificación sobre activación del especialista suplente examen complexivo parte práctica dimensión escrita de estudiante "
							+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEstudiante().getId() + ").";
					String detalle = "<div dir='ltr'><span style='color:#000000;font-size:16px;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar que el especialista <strong>"
							+ estudianteExamenComplexivoPP.getEspecialistaSuplantadoE().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEspecialistaSuplantadoE().getId()
							+ ")</strong> no podrá actuar en la parte práctica del examen complexivo dimensión escrita, por lo que el especialista suplente <strong>"
							+ estudianteExamenComplexivoPP.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEspecialistaSuplente1().getId()
							+ ")</strong> asumirá dicho rol. <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute;	 dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted	 recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatario, asunto, detalle, listAdjunto,
							parametros);
				}
				if (estudianteExamenComplexivoPP.getActivarSuplenteO() != null) {
					String[] destinatario = { estudianteExamenComplexivoPP.getEstudiante().getEmail(), tutor.getEmail(),
							especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
							especialistaSuplente1.getEmail() };
					String asunto = "Notificación sobre activación del especialista suplente examen complexivo parte práctica dimensión oral de estudiante "
							+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEstudiante().getId() + ").";
					String detalle = "<div dir='ltr'><span style='color:#000000;font-size:16px;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar que el especialista <strong>"
							+ estudianteExamenComplexivoPP.getEspecialistaSuplantadoO().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEspecialistaSuplantadoO().getId()
							+ ")</strong> no podrá actuar en la parte práctica del examen complexivo dimensión oral, por lo que el especialista suplente <strong>"
							+ estudianteExamenComplexivoPP.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ estudianteExamenComplexivoPP.getEspecialistaSuplente1().getId()
							+ ")<strong> asumirá dicho rol. <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute;	 dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted	 recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatario, asunto, detalle, listAdjunto,
							parametros);
				}

				panelPrincipal = true;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;
				presentaMensaje(FacesMessage.SEVERITY_INFO, "El comité evaluador fue asignado de manera correcta");
				buscar();
			}
		} catch (AddressException ae) {
			panelPrincipal = true;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = false;
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Dirección de correo electrónico del estudiante incorrecto.");
			ae.printStackTrace();
		} catch (Exception e) {
			panelPrincipal = true;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = false;
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}
	}

	public void habilitarSuplenteE() {
		System.out.println("Entro");
	}

	public void habilitarSuplenteO() {
		System.out.println("Entro");
	}

	public void insertarFecha() {
		try {
			if (estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria().equals(null)
					|| estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria().equals("")
					|| estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria().equals(null)
					|| estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria().equals("")) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No deje el campo vacío de fecha o lugar de sustentación. Por favor ingrese los campos requeridos.");
			} else {

				DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				estudiantesExamenComplexivoPPService
						.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"fechaSustentacionOrdinaria\"='"
								+ hourdateFormat.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
								+ "', \"lugarSustentacionOrdinaria\"='"
								+ estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria() + "' WHERE id="
								+ estudianteExamenComplexivoPP.getId() + ";");

				DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
				DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
				String[] destinatarios = { estudianteExamenComplexivoPP.getEstudiante().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista1().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista2().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista3().getEmail(),
						estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail() };

				String asunto = "ASIGNACIÓN FECHA Y LUGAR DE SUSTENTACIÓN DEL ESTUDIANTE "
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ " DE LA MODALIDAD EXAMEN COMPLEXIVO.";
				String detalle = "<div dir='ltr'><span style='color:#00000; font-size:16px;'>El presente email es para informar que la fecha de sustentación del estudiante <strong>"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> a las <strong>"
						+ formatoHora.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> en el lugar <strong>"
						+ estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria()
						+ "</strong>. <br /> <br /><div><div><br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br/><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Fecha y lugar de sustentación ordinaria guardada correctamente");
				buscar();

			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Por favor determine una fecha de sustentación ordinaria antes de guardarla.");
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void insertarFechaGracia() {
		// System.out.println(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria());
		// estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
		try {
			if (estudianteExamenComplexivoPP.getFechaSustentacionGracia().equals(null)
					|| estudianteExamenComplexivoPP.getLugarSustentacionGracia().equals("")) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Por favor ingrese una fecha de sustentación de gracia antes de guardar.");
			} else {
				DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				estudiantesExamenComplexivoPPService
						.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"fechaSustentacionGracia\"='"
								+ hourdateFormat.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
								+ "', \"lugarSustentacionGracia\"='"
								+ estudianteExamenComplexivoPP.getLugarSustentacionGracia() + "' WHERE id="
								+ estudianteExamenComplexivoPP.getId() + ";");

				DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
				DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
				String[] destinatarios = { estudianteExamenComplexivoPP.getEstudiante().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista1().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista2().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista3().getEmail(),
						estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail() };
				String asunto = "ASIGNACIÓN FECHA DE SUSTENTACIÓN GRACIA DEL ESTUDIANTE  "
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ " DE LA MODALIDAD EXAMEN COMPLEXIVO.";
				String detalle = "<div dir='ltr'><span style='color:#000000; font-size:16px;'>El presente email es para informar que la fecha de sustentación de gracia del estudiante <strong>"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
						+ "</strong> a las <strong>"
						+ formatoHora.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
						+ "</strong> en el lugar <strong>" + estudianteExamenComplexivoPP.getLugarSustentacionGracia()
						+ "</strong>. <br /> <br /><div><div> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br/> <font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Fecha y lugar de sustentación de gracia guardada correctamente");
				buscar();
			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Por favor determine una fecha de sustentación antes de guardarla.");
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}

	}

	public boolean isActivarEspecialistaE() {
		return activarEspecialistaE;
	}

	public boolean isActivarEspecialistaO() {
		return activarEspecialistaO;
	}

	public boolean isBoolEspecialista1() {
		return boolEspecialista1;
	}

	public boolean isBoolEspecialista2() {
		return boolEspecialista2;
	}

	public boolean isBoolEspecialista3() {
		return boolEspecialista3;
	}

	public boolean isBoolEspecialistaSuplente1() {
		return boolEspecialistaSuplente1;
	}

	public boolean isBoolTutor() {
		return boolTutor;
	}

	public boolean isEsUmmog() {
		return esUmmog;
	}

	public boolean isPanelComite() {
		return panelComite;
	}

	public boolean isPanelDashboard() {
		return panelDashboard;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPanelSeleccionar() {
		return panelSeleccionar;
	}

	public boolean isPanelTrabajoEscrito() {
		return panelTrabajoEscrito;
	}

	public boolean isPanelTutorias() {
		return panelTutorias;
	}

	public boolean isValidacionArchivo() {
		return validacionArchivo;
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
			if (listadoEstudiantesExamenComplexivoPP != null)
				listadoEstudiantesExamenComplexivoPP.clear();
			a();
		}
	}

	public void obtenerCarreras() {
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "inner join c.estudiantesExamenComplexivoPT ec inner join ec.proceso p "
						+ "where u.email=?1 and p.id=?2 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						proceso },
				0, false, new Object[] {});
	}

	public void obtenerFechaParteTeorica() {
		grupoExamen = grupoService.obtenerObjeto(
				"select gr from Grupo gr inner join gr.examen ex inner join ex.estudianteExamenComplexivoPT ept inner join ept.estudiante e where e.id=?1",
				new Object[] { estudianteExamenComplexivoPP.getEstudiante().getId() }, false, new Object[] {});
		fechaParteTeorica = grupoExamen.getFechaInicio();
	}

	public void obtenerHorasTutorias() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (estudianteExamenComplexivoPP.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "12 horas y 0 minutos.";
		} else {
			for (int i = 0; i < estudianteExamenComplexivoPP.getTutorias().size(); i++) {
				f1.setTime(estudianteExamenComplexivoPP.getTutorias().get(i).getFecha());
				f2.setTime(estudianteExamenComplexivoPP.getTutorias().get(i).getFechaFinTutoria());

				Integer minutosTI = 0;
				Integer minutosTF = 0;
				Integer horasTI = 0;
				Integer horasTF = 0;

				minutosTI = f1.get(Calendar.MINUTE);
				minutosTF = f2.get(Calendar.MINUTE);
				horasTI = f1.get(Calendar.HOUR_OF_DAY);
				horasTF = f2.get(Calendar.HOUR_OF_DAY);

				if (minutosTF >= minutosTI) {
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				} else {
					horasTF = horasTF - 1;
					minutosTF = minutosTF + 60;
					totalMinutos = totalMinutos + (minutosTF - minutosTI);
				}
				if (totalMinutos >= 60) {
					totalHoras = totalHoras + 1;
					totalMinutos = totalMinutos - 60;
				}
				totalHoras = totalHoras + (horasTF - horasTI);
			}
		}
		numeroHorasTutorias = totalHoras + " horas y " + totalMinutos + " minutos.";

		Integer ht = 12;
		Integer mt = 0;
		if (mt >= totalMinutos)
			numeroMinutosRestantes = mt - totalMinutos;
		else {
			ht = ht - 1;
			mt = mt + 60;
			numeroMinutosRestantes = mt - totalMinutos;
		}
		numeroHorasRestantes = ht - totalHoras;

		numeroTotalHorasMinutosRestantes = numeroHorasRestantes <= 0
				? "Ha cumplido con las horas de tutorías establecidas."
				: numeroHorasRestantes + " horas y " + numeroMinutosRestantes + " minutos.";
	}

	public void presentarDashboardEstudiante() {

		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = true;
		panelComite = false;

		String calificaciones = estudiantesExamenComplexivoPPService.traerCalificaciones(estudianteExamenComplexivoPP);
		calificacionParteTeoricaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
		calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[3]);

		obtenerFechaParteTeorica();

	}

	public void presentarDocumento() {
		try {

			if (estudianteExamenComplexivoPP.getValidarArchivo() == null)
				validacionArchivo = false;
			else
				validacionArchivo = estudianteExamenComplexivoPP.getValidarArchivo();

			if (estudianteExamenComplexivoPP.getArchivo() == null) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El estudiante no ha subido ningún documento de su trabajo escrito del exámen complexivo.");
			} else {
				urlDocumento = estudianteExamenComplexivoPP.getArchivo();
				InputStream is;

				is = new FileInputStream(urlDocumento);
				streamedContent = new DefaultStreamedContent(is, "application/pdf");
				System.out.println(urlDocumento);
				System.out.println(streamedContent);
				textoInconsistencia = "";

				panelPrincipal = false;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;
				panelTrabajoEscrito = true;

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La ruta del archivo, se encuentra incorrecta.");
		}
	}

	public void presentarTutoriasEstudiante() {
		if (estudianteExamenComplexivoPP.getTutorias().size() == 0) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "El estudiante no posee tutorias para mostrar.");
		} else {
			panelPrincipal = false;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = false;
			panelTutorias = true;
			panelTrabajoEscrito = false;

			obtenerHorasTutorias();
		}
	}

	public void presentarValidacion() {
		if (estudianteExamenComplexivoPP.getAprobado() == null)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "El estudiante aún no ha sido aprobado por el tutor.");
		else {
			panelPrincipal = false;
			panelSeleccionar = true;
			panelDashboard = false;
			panelComite = false;
		}
	}

	public void seleccionarComiteEvaluador() {
		tutor = new Usuario();
		especialista1 = new Usuario();
		especialista2 = new Usuario();
		especialista3 = new Usuario();
		especialistaSuplente1 = new Usuario();

		if (estudianteExamenComplexivoPP.getTutor() != null) {
			tutor = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getTutor().getId() }, false, new Object[] {});
		}
		if (estudianteExamenComplexivoPP.getEspecialista1() != null) {
			especialista1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getEspecialista1().getId() }, false, new Object[] {});
		}
		if (estudianteExamenComplexivoPP.getEspecialista2() != null) {
			especialista2 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getEspecialista2().getId() }, false, new Object[] {});
		}
		if (estudianteExamenComplexivoPP.getEspecialista3() != null) {
			especialista3 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getEspecialista3().getId() }, false, new Object[] {});
		}
		if (estudianteExamenComplexivoPP.getEspecialistaSuplente1() != null) {
			especialistaSuplente1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { estudianteExamenComplexivoPP.getEspecialistaSuplente1().getId() }, false,
					new Object[] {});
		}

		if (estudianteExamenComplexivoPP.getActivarSuplenteE() == null)
			activarEspecialistaE = false;
		else if (estudianteExamenComplexivoPP.getActivarSuplenteE() == false)
			activarEspecialistaE = false;
		else if (estudianteExamenComplexivoPP.getActivarSuplenteE() == true)
			activarEspecialistaE = true;

		if (estudianteExamenComplexivoPP.getActivarSuplenteO() == null)
			activarEspecialistaO = false;
		else if (estudianteExamenComplexivoPP.getActivarSuplenteO() == false)
			activarEspecialistaO = false;
		else if (estudianteExamenComplexivoPP.getActivarSuplenteO() == true)
			activarEspecialistaO = true;

		if (estudianteExamenComplexivoPP.getEspecialistaSuplantadoE() != null) {
			especialistaSuplantadoInterfazE = estudianteExamenComplexivoPP.getEspecialistaSuplantadoE().getId();
		} else if (estudianteExamenComplexivoPP.getEspecialista1() != null) {
			especialistaSuplantadoInterfazE = estudianteExamenComplexivoPP.getEspecialista1().getId();
		} else {
			especialistaSuplantadoInterfazE = "Debe agregar el comité evaluador";
		}

		if (estudianteExamenComplexivoPP.getEspecialistaSuplantadoO() != null) {
			especialistaSuplantadoInterfazO = estudianteExamenComplexivoPP.getEspecialistaSuplantadoO().getId();
		} else if (estudianteExamenComplexivoPP.getEspecialista1() != null) {
			especialistaSuplantadoInterfazO = estudianteExamenComplexivoPP.getEspecialista1().getId();
		} else {
			especialistaSuplantadoInterfazE = "Debe agregar el comité evaluador";
		}

		System.out.println("Valor del activar especialista " + activarEspecialistaE);

		boolTutor = true;
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = true;
		// }
	}

	public void seleccionarE1() {
		boolTutor = false;
		boolEspecialista1 = true;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarE2() {
		boolTutor = false;
		boolEspecialista1 = false;
		boolEspecialista2 = true;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarE3() {
		boolTutor = false;
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = true;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarEspecialista() {
		if (boolTutor) {
			tutor = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { especialista.getId() }, false, new Object[] {});
		} else if (boolEspecialista1) {
			especialista1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { especialista.getId() }, false, new Object[] {});
		} else if (boolEspecialista2) {
			especialista2 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { especialista.getId() }, false, new Object[] {});
		} else if (boolEspecialista3) {
			especialista3 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { especialista.getId() }, false, new Object[] {});
		} else if (boolEspecialistaSuplente1) {
			especialistaSuplente1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { especialista.getId() }, false, new Object[] {});
		}
	}

	public void seleccionarS1() {
		boolTutor = false;
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = true;
	}

	public void seleccionarS2() {
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarTutor() {
		boolTutor = true;
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void setActivarEspecialistaE(boolean activarEspecialistaE) {
		this.activarEspecialistaE = activarEspecialistaE;
	}

	public void setActivarEspecialistaO(boolean activarEspecialistaO) {
		this.activarEspecialistaO = activarEspecialistaO;
	}

	public void setBoolEspecialista1(boolean boolEspecialista1) {
		this.boolEspecialista1 = boolEspecialista1;
	}

	public void setBoolEspecialista2(boolean boolEspecialista2) {
		this.boolEspecialista2 = boolEspecialista2;
	}

	public void setBoolEspecialista3(boolean boolEspecialista3) {
		this.boolEspecialista3 = boolEspecialista3;
	}

	public void setBoolEspecialistaSuplente1(boolean boolEspecialistaSuplente1) {
		this.boolEspecialistaSuplente1 = boolEspecialistaSuplente1;
	}

	public void setBoolTutor(boolean boolTutor) {
		this.boolTutor = boolTutor;
	}

	public void setCalificacionFinal(BigDecimal calificacionFinal) {
		this.calificacionFinal = calificacionFinal;
	}

	public void setCalificacionParteEscritaFinal(BigDecimal calificacionParteEscritaFinal) {
		this.calificacionParteEscritaFinal = calificacionParteEscritaFinal;
	}

	public void setCalificacionParteOralFinal(BigDecimal calificacionParteOralFinal) {
		this.calificacionParteOralFinal = calificacionParteOralFinal;
	}

	public void setCalificacionParteTeoricaFinal(BigDecimal calificacionParteTeoricaFinal) {
		this.calificacionParteTeoricaFinal = calificacionParteTeoricaFinal;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCriterioBusquedaEspecialista(String criterioBusquedaEspecialista) {
		this.criterioBusquedaEspecialista = criterioBusquedaEspecialista;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setEspecialista(Usuario especialista) {
		this.especialista = especialista;
	}

	public void setEspecialista1(Usuario especialista1) {
		this.especialista1 = especialista1;
	}

	public void setEspecialista2(Usuario especialista2) {
		this.especialista2 = especialista2;
	}

	public void setEspecialista3(Usuario especialista3) {
		this.especialista3 = especialista3;
	}

	public void setEspecialistas(List<Usuario> especialistas) {
		this.especialistas = especialistas;
	}

	public void setEspecialistaSuplantadoInterfazE(String especialistaSuplantadoInterfazE) {
		this.especialistaSuplantadoInterfazE = especialistaSuplantadoInterfazE;
	}

	public void setEspecialistaSuplantadoInterfazO(String especialistaSuplantadoInterfazO) {
		this.especialistaSuplantadoInterfazO = especialistaSuplantadoInterfazO;
	}

	public void setEspecialistaSuplente1(Usuario especialistaSuplente1) {
		this.especialistaSuplente1 = especialistaSuplente1;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEsUmmog(boolean esUmmog) {
		this.esUmmog = esUmmog;
	}

	public void setFechaParteTeorica(Date fechaParteTeorica) {
		this.fechaParteTeorica = fechaParteTeorica;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public void setGrupoExamen(Grupo grupoExamen) {
		this.grupoExamen = grupoExamen;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setNumeroHorasRestantes(Integer numeroHorasRestantes) {
		this.numeroHorasRestantes = numeroHorasRestantes;
	}

	public void setNumeroHorasTutorias(String numeroHorasTutorias) {
		this.numeroHorasTutorias = numeroHorasTutorias;
	}

	public void setNumeroMinutosRestantes(Integer numeroMinutosRestantes) {
		this.numeroMinutosRestantes = numeroMinutosRestantes;
	}

	public void setNumeroTotalHorasMinutosRestantes(String numeroTotalHorasMinutosRestantes) {
		this.numeroTotalHorasMinutosRestantes = numeroTotalHorasMinutosRestantes;
	}

	public void setPanelComite(boolean panelComite) {
		this.panelComite = panelComite;
	}

	public void setPanelDashboard(boolean panelDashboard) {
		this.panelDashboard = panelDashboard;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPanelSeleccionar(boolean panelSeleccionar) {
		this.panelSeleccionar = panelSeleccionar;
	}

	public void setPanelTrabajoEscrito(boolean panelTrabajoEscrito) {
		this.panelTrabajoEscrito = panelTrabajoEscrito;
	}

	public void setPanelTutorias(boolean panelTutorias) {
		this.panelTutorias = panelTutorias;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public void setTextoInconsistencia(String textoInconsistencia) {
		this.textoInconsistencia = textoInconsistencia;
	}

	public void setTotalHoras(Integer totalHoras) {
		this.totalHoras = totalHoras;
	}

	public void setTotalMinutos(Integer totalMinutos) {
		this.totalMinutos = totalMinutos;
	}

	public void setTutor(Usuario tutor) {
		this.tutor = tutor;
	}

	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}

	public void setValidacionArchivo(boolean validacionArchivo) {
		this.validacionArchivo = validacionArchivo;
	}

	public void verificarDocumento() {
		System.out.println(validacionArchivo);
		// estudianteTrabajoTitulacion.setValidarArchivo(validacionArchivo);
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
		// validacionArchivo = estudianteTrabajoTitulacion.getValidarArchivo();
	}

}
