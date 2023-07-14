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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.SecuenciaActaCalificacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DetalleEntidadGenericaService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
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
public class PrincipalTTBean implements Serializable {
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
	private EstudianteTrabajoTitulacionService estudiantesTrabajoTitulacionService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion2;

	private boolean panelPrincipal;
	private boolean panelSeleccionar;
	private boolean panelDashboard;
	private boolean panelComite;
	private boolean panelTutorias;
	private boolean panelTrabajoEscrito;
	private boolean panelArticulo;

	private List<Usuario> especialistas;

	private HashMap<String, String> especialistasDropdown = new HashMap<String, String>();

	private boolean activarEspecialistaE;
	private boolean activarEspecialistaO;
	private String especialistaSuplantadoInterfazE;
	private String especialistaSuplantadoInterfazO;

	private String criterioBusquedaEspecialista;
	private String criterioBusquedaEstudiante;

	private boolean boolEspecialista1;
	private boolean boolEspecialista2;
	private boolean boolEspecialista3;
	private boolean boolEspecialistaSuplente1;

	private Usuario especialista;
	private Usuario especialista1;
	private Usuario especialista2;
	private Usuario especialista3;
	private Usuario especialistaSuplente1;

	private BigDecimal calificacionParteEscritaFinal;
	private BigDecimal calificacionParteOralFinal;
	private BigDecimal calificacionFinal;

	private Date fechaParteTeorica;
	private Grupo grupoExamen;

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
		panelTutorias = false;
		panelTrabajoEscrito = false;
		panelArticulo = false;
		especialista1 = new Usuario();
		especialista2 = new Usuario();
		especialista3 = new Usuario();
		especialistaSuplente1 = new Usuario();
		boolEspecialista1 = true;

		esUmmog = UtilSeguridad.obtenerRol("UMMO");

	}

	public void actaCalificaciones(EstudianteTrabajoTitulacion ett) {
		estudianteTrabajoTitulacion = ett;
		String calificaciones = estudiantesTrabajoTitulacionService.traerCalificaciones(estudianteTrabajoTitulacion);
		BigDecimal notaEscrita = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		BigDecimal notaOral = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		BigDecimal notaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);

		Boolean[] bn = { false, false, false, false };

		if (notaFinal.compareTo(new BigDecimal("69.5")) >= 0) {

			try {
				estudiantesTrabajoTitulacionService.actualizarSQL("select asignar_certificados_estudiante_ett("
						+ ett.getId() + ", " + ett.getCarrera().getId() + ");");
			} catch (Exception e) {
				System.out.println("Error del certificado");
			}

			SecuenciaActaCalificacion sac = new SecuenciaActaCalificacion();
			sac.setIdt(estudianteTrabajoTitulacion.getId());
			sac.setProceso(estudianteTrabajoTitulacion.getProceso().getId());
			sac.setTitulacion("TT");
			secuenciaActaCalificacionService.insertar(sac);

			estudianteTrabajoTitulacion = estudiantesTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join fetch ett.carrera c "
							+ " inner join ett.estudiante e where ett.id=?1",
					new Object[] { estudianteTrabajoTitulacion.getId() }, false, new Object[] { "getTutorias" });

			Report report = new Report();
			report.setFormat("PDF");
			report.setPath(UtilsReport.serverPathReport);
			report.setName("TT_ActaCalificacion");

			// presentaMensaje(FacesMessage.SEVERITY_INFO, "Se generó el acta de
			// calificaciones satisfactoriamente");
			// Map<String, Object> parametro = new HashMap<String, Object>();
			String genero = "";
			if (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareTo("M") == 0) {
				genero = estudianteTrabajoTitulacion.getCarrera().getNomenclaturaTituloMasculino();

				if (estudianteTrabajoTitulacion.getCarrera().getTipoTitulo() != null)
					genero = genero.concat(" ").concat(estudianteTrabajoTitulacion.getCarrera().getTipoTitulo())
							.concat(" ").concat(estudianteTrabajoTitulacion.getCarrera().getDetalleTitulo());
			} else {
				genero = estudianteTrabajoTitulacion.getCarrera().getNomenclaturaTituloFemenino();

				if (estudianteTrabajoTitulacion.getCarrera().getTipoTitulo() != null)
					genero = genero.concat(" ").concat(estudianteTrabajoTitulacion.getCarrera().getTipoTitulo())
							.concat(" ").concat(estudianteTrabajoTitulacion.getCarrera().getDetalleTitulo());
			}

			// parametro.put("estudiante",
			// estudianteTrabajoTitulacion.getEstudiante().getGenero() + "-"
			// + estudianteTrabajoTitulacion.getEstudiante().getId() + "-"
			// + estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
			// + "-" + genero);

			report.addParameter("idTT", String.valueOf(estudianteTrabajoTitulacion.getId()));

			report.addParameter("estudiante",
					estudianteTrabajoTitulacion.getEstudiante().getGenero() + "-"
							+ estudianteTrabajoTitulacion.getEstudiante().getId() + "-"
							+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + "-" + genero);

			// parametro
			// .put("encabezado",
			// "Firman la presente acta "
			// +
			// (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareTo("F")
			// == 0
			// ? "la" : "el")
			// + " estudiante, los miembros del comité evaluador y "
			// + (estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica()
			// .getNombreJefeUmmog().split("-")[0].compareTo("F") == 0 ? "la
			// jefa"
			// : "el jefe")
			// + " de la UMMOG.");

			report.addParameter("encabezado", ""
					+ (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareTo("F") == 0 ? "la" : "el")
					+ " estudiante, los miembros del comité evaluador y "
					+ (estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombreJefeUmmog().split("-")[0]
							.compareTo("F") == 0 ? "la jefa" : "el jefe")
					+ " de la UMMOG.");

			report.addParameter("unidadAcademica",
					estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombre());
			report.addParameter("carrera", estudianteTrabajoTitulacion.getCarrera().getNombre());
			report.addParameter("numeroActa", estudianteTrabajoTitulacion.getProceso().getId() + "/"
					+ String.format("%04d", estudianteTrabajoTitulacion.getNumeroActaCalificacion()));
			report.addParameter("fechaActa",
					UtilsDate.convertirDateATexto(estudianteTrabajoTitulacion.getFechaSustentacion()));

			// parametro.put("unidadAcademica",
			// estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombre());
			// parametro.put("carrera",
			// estudianteTrabajoTitulacion.getCarrera().getNombre());
			// parametro.put("numeroActa",
			// estudianteTrabajoTitulacion.getProceso().getId() + "/"
			// + String.format("%04d",
			// estudianteTrabajoTitulacion.getNumeroActaCalificacion()));

			int r = 1;

			if (estudianteTrabajoTitulacion.getEe1() != null || estudianteTrabajoTitulacion.getOe1() != null) {
				report.addParameter("parametro" + (r++),
						estudianteTrabajoTitulacion.getEspecialista1().getApellidoNombre() + "-"
								+ estudianteTrabajoTitulacion.getEspecialista1().getId() + "-ESPECIALISTA PRINCIPAL 1");
				// parametro.put("parametro" + (r++),
				// estudianteTrabajoTitulacion.getEspecialista1().getApellidoNombre()
				// + "\nC.I.: "
				// + estudianteTrabajoTitulacion.getEspecialista1().getId()
				// + "\nESPECIALISTA PRINCIPAL 1");
			}
			if (estudianteTrabajoTitulacion.getEe2() != null || estudianteTrabajoTitulacion.getOe2() != null) {
				report.addParameter("parametro" + (r++),
						estudianteTrabajoTitulacion.getEspecialista2().getApellidoNombre() + "-"
								+ estudianteTrabajoTitulacion.getEspecialista2().getId() + "-ESPECIALISTA PRINCIPAL 2");
				// parametro.put("parametro" + (r++),
				// estudianteTrabajoTitulacion.getEspecialista2().getApellidoNombre()
				// + "\nC.I.: "
				// + estudianteTrabajoTitulacion.getEspecialista2().getId()
				// + "\nESPECIALISTA PRINCIPAL 2");
			}
			if (estudianteTrabajoTitulacion.getEe3() != null || estudianteTrabajoTitulacion.getOe3() != null) {
				report.addParameter("parametro" + (r++),
						estudianteTrabajoTitulacion.getEspecialista3().getApellidoNombre() + "-"
								+ estudianteTrabajoTitulacion.getEspecialista3().getId() + "-ESPECIALISTA PRINCIPAL 3");
				// parametro.put("parametro" + (r++),
				// estudianteTrabajoTitulacion.getEspecialista3().getApellidoNombre()
				// + "\nC.I.: "
				// + estudianteTrabajoTitulacion.getEspecialista3().getId()
				// + "\nESPECIALISTA PRINCIPAL 3");
			}
			if (estudianteTrabajoTitulacion.getEs1() != null || estudianteTrabajoTitulacion.getOs1() != null) {
				report.addParameter("parametro" + (r++),
						estudianteTrabajoTitulacion.getEspecialistaSuplente1().getApellidoNombre() + "-"
								+ estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId()
								+ "-ESPECIALISTA SUPLENTE");
				// parametro.put("parametro" + (r++),
				// estudianteTrabajoTitulacion.getEspecialistaSuplente1().getApellidoNombre()
				// + "\nC.I.: "
				// +
				// estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId()
				// + "\nESPECIALISTA SUPLENTE");
			}

			report.addParameter("parametro" + r++, estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + "-"
					+ estudianteTrabajoTitulacion.getEstudiante().getId() + "-ESTUDIANTE");

			// parametro.put("parametro" + r++,
			// estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
			// + "\nC.I.: " +
			// estudianteTrabajoTitulacion.getEstudiante().getId() +
			// "\nESTUDIANTE");

			report.addParameter("parametro" + r, estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica()
					.getNombreJefeUmmog().split("-")[2] + "-"
					+ estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombreJefeUmmog().split("-")[1]
					+ "-"
					+ (estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombreJefeUmmog().split("-")[0]
							.compareTo("F") == 0 ? "JEFA DE UMMOG" : "JEFE DE UMMOG"));

			// parametro
			// .put("parametro" + r,
			// estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
			// .split("-")[2]
			// + "\nC.I.: "
			// +
			// estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica().getNombreJefeUmmog()
			// .split("-")[1]
			// + "\n"
			// + (estudianteTrabajoTitulacion.getCarrera().getUnidadAcademica()
			// .getNombreJefeUmmog().split("-")[0].compareTo("F") == 0 ? "JEFA
			// DE UMMOG"
			// : "JEFE DE UMMOG"));

			report.addParameter("notaEscrita", String.valueOf(notaEscrita));
			// parametro.put("notaEscrita", notaEscrita);

			report.addParameter("notaOral", String.valueOf(notaOral));
			// parametro.put("notaOral", notaOral);

			report.addParameter("notaFinal", String.valueOf(notaFinal));
			// parametro.put("notaFinal", notaFinal);

			report.addParameter("tipoTrabajoTitulacion", estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre());
			// parametro.put("tipoTrabajoTitulacion",
			// estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre());

			report.addParameter("fechaActa",
					UtilsDate.convertirDateATexto(estudianteTrabajoTitulacion.getFechaSustentacion()));
			// parametro.put("fechaActa",
			// UtilsDate.convertirDateATexto(estudianteTrabajoTitulacion.getFechaSustentacion()));

			// List<DetalleEntidadGenerica> deg =
			// detalleEntidadGenericaService.obtenerPorSql(
			// "select 12 as id, " + "'' as \"atributoString1\", '' as
			// \"atributoString2\", "
			// + "'' as \"atributoString3\", '' as \"atributoString4\", '' as
			// \"atributoString5\", '' as \"atributoString6\", "
			// + "'' as \"atributoString7\", '' as \"atributoString8\", '' as
			// \"atributoString9\", '' as \"atributoString10\" "
			// + "from exetasi.\"estudiantesExamenComplexivoPP\" eec limit 1",
			// DetalleEntidadGenerica.class);
			System.out
					.println("opción de titulación: " + estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre());
			System.out.println("LLegó antes ejecutar reporte");
			documento = UtilsReport.ejecutarReporte(report);
			System.out.println("LLegó después ejecutar reporte");
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Descargado con éxito.");

			// reporteService.generarReportePDF(deg, parametro,
			// "ActaCalificacionTT",
			// "ActaCalificacion_TT_" + proceso + "_" +
			// estudianteTrabajoTitulacion.getEstudiante().getId());
		} else {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede generar el acta de calificaciones por qué el estudiante ha reprobado el proceso (Nota menor a 70).");
		}

	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			listadoEstudiantesTrabajoTitulacion = estudiantesTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join fetch ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[] { "getTutorias" });
		else
			listadoEstudiantesTrabajoTitulacion = estudiantesTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join fetch ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or translate(e.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate(upper(?3), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[] { "getTutorias" });

	}

	public void buscarEspecialista() {
		especialistas = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where (u.id=?1 or translate(lower(u.apellidoNombre), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
						+ " like translate(lower(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) and "
						+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU') order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaEspecialista + "%" }, 0, false, new Object[] {});
	}

	public void descargarArchivo() {

		if (estudianteTrabajoTitulacion.getArchivo() != null) {
			try {
				System.out.println("Ingreso al descargar archivo.");
				fileInputStream = new FileInputStream(estudianteTrabajoTitulacion.getArchivo());
				if (fileInputStream.read() == -1)
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "El archivo se encuentra dañado.");
				else {
					stream = new FileInputStream(estudianteTrabajoTitulacion.getArchivo());
					documento = new DefaultStreamedContent(stream, "application/pdf",
							"TT_" + estudianteTrabajoTitulacion.getEstudiante().getId() + "_"
									+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + ".pdf");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "El estudiante aún no sube el documento a la plataforma.");
		}

	}

	public void desvalidarDocumentoE3() {
		estudianteTrabajoTitulacion.setValidarDocumentoE3(null);
		estudiantesTrabajoTitulacionService
				.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarDocumentoE3\" = "
						+ estudianteTrabajoTitulacion.getValidarDocumentoE3() + " WHERE id="
						+ estudianteTrabajoTitulacion.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha desvalidado la estructura del documento correctamente.");
	}

	public void elegirSuplantado() {
		if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista1().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista1);
		else if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista2().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista2);
		else if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista3().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista3);
	}

	public void eliminarCitas() {
		estudianteTrabajoTitulacion.setCitas1(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET citas1 = "
				+ estudianteTrabajoTitulacion.getCitas1() + " WHERE id=" + estudianteTrabajoTitulacion.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha eliminado el número de citas correctamente.");
	}

	public void eliminarNotaEscrita1() {
		estudianteTrabajoTitulacion.setEe1(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee1="
				+ estudianteTrabajoTitulacion.getEe1() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscrita2() {
		estudianteTrabajoTitulacion.setEe2(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee2="
				+ estudianteTrabajoTitulacion.getEe2() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscrita3() {
		estudianteTrabajoTitulacion.setEe3(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee3="
				+ estudianteTrabajoTitulacion.getEe3() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscritaSuplente1() {
		estudianteTrabajoTitulacion.setEs1(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET es1="
				+ estudianteTrabajoTitulacion.getEs1() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral1() {
		estudianteTrabajoTitulacion.setOe1(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe1="
				+ estudianteTrabajoTitulacion.getOe1() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral2() {
		estudianteTrabajoTitulacion.setOe2(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe2="
				+ estudianteTrabajoTitulacion.getOe2() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral3() {
		estudianteTrabajoTitulacion.setOe3(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET oe3="
				+ estudianteTrabajoTitulacion.getOe3() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOralSuplente1() {
		estudianteTrabajoTitulacion.setOs1(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET os1="
				+ estudianteTrabajoTitulacion.getOs1() + " WHERE id=" + estudianteTrabajoTitulacion.getId());
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarPocentajeUrkund() {
		estudianteTrabajoTitulacion.setReporteUrkund(null);
		estudianteTrabajoTitulacion.setAntiplagio(null);
		estudianteTrabajoTitulacion.setObservacionesUrkund(null);
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET antiplagio = "
				+ estudianteTrabajoTitulacion.getAntiplagio() + ", \"reporteUrkund\" = "
				+ estudianteTrabajoTitulacion.getReporteUrkund() + ", \"observacionesUrkund\" = "
				+ estudianteTrabajoTitulacion.getObservacionesUrkund() + " WHERE id="
				+ estudianteTrabajoTitulacion.getId());

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha eliminado el porcentaje de coincidencia correctamente.");
	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTutorias = false;
		panelTrabajoEscrito = false;
		panelArticulo = false;
	}

	public void establecerPrincipalDesdeTrabajo() {
		try {
			estudianteTrabajoTitulacion.setValidarArchivo(validacionArchivo);
			estudiantesTrabajoTitulacionService
					.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\" = "
							+ estudianteTrabajoTitulacion.getValidarArchivo() + " WHERE id="
							+ estudianteTrabajoTitulacion.getId() + ";");

			if (estudianteTrabajoTitulacion2 != null) {
				estudiantesTrabajoTitulacionService
						.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\"="
								+ validacionArchivo + " WHERE id=" + estudianteTrabajoTitulacion2.getId() + ";");
			}
			if (validacionArchivo == true) {
				if (estudianteTrabajoTitulacion2 == null) {
					String destinatario = estudianteTrabajoTitulacion.getEstudiante().getEmail();
					String asunto = "VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo ha sido validado de forma correcta, evidenciando que cumple con lo establecido en el artículo 46 de la guía de titulación.</span></div><br /><div>En los próximos días recibirá una notificación cuando su archivo se encuentre publicado en el repositorio digital de la Universidad Técnica de Machala.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} else {
					String destinatarios[] = new String[2];
					destinatarios[0] = estudianteTrabajoTitulacion.getEstudiante().getEmail();
					destinatarios[1] = estudianteTrabajoTitulacion2.getEstudiante().getEmail();
					String asunto = "VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo ha sido validado de forma correcta, evidenciando que cumple con lo establecido en el artículo 46 de la guía de titulación.</span></div><br /><div>En los próximos días recibirá una notificación cuando su archivo se encuentre publicado en el repositorio digital de la Universidad Técnica de Machala.</div><br /><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(destinatarios, asunto, detalle, listAdjunto, parametros);
				}
			} else if (validacionArchivo == false) {
				if (estudianteTrabajoTitulacion2 == null) {
					String destinatario = estudianteTrabajoTitulacion.getEstudiante().getEmail();
					String asunto = "URGENTE TITULACIÓN: VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Una vez realizada la revisi&oacute;n del documento digital proporcionado por usted(es) se encuentran las siguientes inconsistencias:</span></div><br /><div><span style='color: #000000;'>&nbsp;"
							+ textoInconsistencia
							+ "&nbsp;</span></div><div><span style='color: #000000;'>&nbsp;</span></div><div>Le animamos a examinar el instructivo para la carga del archivo en la plataforma, corregir las inconsistencias antes notificadas y volver a realizar el respectivo procedimiento.</div><br/><div><a href='https://docs.google.com/a/utmachala.edu.ec/document/d/1TYZe8EKMRCZATf82BoUtjD9K2xIRCN6Y-RVFND5DVpA/edit?usp=drive_web' target='_blank'><img src='https://ssl.gstatic.com/docs/doclist/images/icon_11_document_list.png' alt='' />&nbsp;<span dir='ltr'>instructivoSubidaArchivoRepositorioDigital</span></a></div><br /><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br/><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} else {
					String destinatarios[] = new String[2];
					destinatarios[0] = estudianteTrabajoTitulacion.getEstudiante().getEmail();
					destinatarios[1] = estudianteTrabajoTitulacion2.getEstudiante().getEmail();
					String asunto = "URGENTE TITULACIÓN: VALIDACIÓN DE ARCHIVO PARA PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Una vez realizada la revisi&oacute;n del documento digital proporcionado por usted(es) se encuentran las siguientes inconsistencias:</span></div><br /><div><span style='color: #000000;'>&nbsp;"
							+ textoInconsistencia
							+ "&nbsp;</span></div><div><span style='color: #000000;'>&nbsp;</span></div><div>Le animamos a examinar el instructivo para la carga del archivo en la plataforma, corregir las inconsistencias antes notificadas y volver a realizar el respectivo procedimiento.</div><br/><div><a href='https://docs.google.com/a/utmachala.edu.ec/document/d/1TYZe8EKMRCZATf82BoUtjD9K2xIRCN6Y-RVFND5DVpA/edit?usp=drive_web' target='_blank'><img src='https://ssl.gstatic.com/docs/doclist/images/icon_11_document_list.png' alt='' />&nbsp;<span dir='ltr'>instructivoSubidaArchivoRepositorioDigital</span></a></div><br /><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(destinatarios, asunto, detalle, listAdjunto, parametros);
				}
			}

			buscar();
			textoInconsistencia = "";
			panelPrincipal = true;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = false;
			panelTutorias = false;
			panelTrabajoEscrito = false;
			panelArticulo = false;
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

	public HashMap<String, String> getEspecialistasDropdown() {
		return especialistasDropdown;
	}

	public String getespecialistaSuplantadoInterfazE() {
		return especialistaSuplantadoInterfazE;
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

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion2() {
		return estudianteTrabajoTitulacion2;
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

	public List<EstudianteTrabajoTitulacion> getListadoEstudiantesTrabajoTitulacion() {
		return listadoEstudiantesTrabajoTitulacion;
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

	public String getUrlDocumento() {
		return urlDocumento;
	}

	public void guardar() {
		estudianteTrabajoTitulacion.setRecepcionDE(true);
		estudiantesTrabajoTitulacionService
				.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"recepcionDE\"="
						+ estudianteTrabajoTitulacion.getRecepcionDE() + " WHERE id="
						+ estudianteTrabajoTitulacion.getId() + ";");
		EstudianteTrabajoTitulacion ett2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.email!=?2",
				new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
						estudianteTrabajoTitulacion.getEstudiante().getId() },
				false, new Object[] {});
		if (ett2 != null) {
			estudiantesTrabajoTitulacionService
					.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"recepcionDE\"="
							+ estudianteTrabajoTitulacion.getRecepcionDE() + " WHERE id=" + ett2.getId() + ";");
		}
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Recibido entrega recepción");
		buscar();
		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTrabajoEscrito = false;
		panelArticulo = false;
	}

	public void guardarComite() {
		try {
			if (especialista1.getId() == null || especialista2.getId() == null || especialista3.getId() == null
					|| especialistaSuplente1.getId() == null)
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Falta de seleccionar un especialista del comité evaluador");
			else if ((especialista1.getId().compareTo(especialista2.getId()) == 0
					|| especialista1.getId().compareTo(especialista3.getId()) == 0
					|| especialista1.getId().compareTo(especialistaSuplente1.getId()) == 0)
					|| (especialista2.getId().compareTo(especialista3.getId()) == 0
							|| especialista2.getId().compareTo(especialistaSuplente1.getId()) == 0)
					|| (especialista3.getId().compareTo(especialistaSuplente1.getId()) == 0)) {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "Algún especialista está considerado mas de una vez");
			} else {

				if (estudianteTrabajoTitulacion.getEspecialista1() == null
						&& estudianteTrabajoTitulacion.getEspecialista2() == null
						&& estudianteTrabajoTitulacion.getEspecialista3() == null
						&& estudianteTrabajoTitulacion.getEspecialistaSuplente1() == null) {

					if (estudianteTrabajoTitulacion2 == null) {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
								especialistaSuplente1.getEmail() };
						String asunto = "Asignación de comité evaluador.";
						String detalle = "<div dir='ltr'><span style='color:#000000;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar la asignación del comité evaluador de su trabajo de titulación en cualquiera de las opciones escogidas. Quendando de la siguiente manera:<br /><b>Estudiante:</b>"
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " - ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId()
								+ ")<br /><b>Especialista 1: </b> " + especialista1.getApellidoNombre() + " - ("
								+ especialista1.getId() + ")<br /><b>Especialista 2: </b> "
								+ especialista2.getApellidoNombre() + " - (" + especialista2.getId()
								+ ") <br /><b>Especialista 3: </b> " + especialista3.getApellidoNombre() + " - ("
								+ especialista3.getId() + ")<br /><b>Especialista Suplente: </b> "
								+ especialistaSuplente1.getApellidoNombre() + " - (" + especialistaSuplente1.getId()
								+ ") <br />En la plataforma podrá encontrar dicha información, específicamente en <b>Dashboard estudiante</b>. Los especialistas podrán observar a quienes evaluarán en el menú Evaluador y se escoje examen complexivo o trabajo de titulación</span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
						List<File> listAdjunto = null;
						Parametro parametro = parametroService.obtener();
						Map<String, String> parametros = parametroService.traerMap(parametro);
						UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatario, asunto, detalle, listAdjunto,
								parametros);
					} else {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								estudianteTrabajoTitulacion2.getEstudiante().getEmail(), especialista1.getEmail(),
								especialista2.getEmail(), especialista3.getEmail(), especialistaSuplente1.getEmail() };
						String asunto = "Asignación de comité evaluador.";
						String detalle = "<div dir='ltr'><span style='color:#000000;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar la asignación del comité evaluador de su trabajo de titulación en cualquiera de las opciones escogidas. Quendando de la siguiente manera:<br /><b>Estudiante:</b>"
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " - ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId() + ")<br /><b>Estudiante: </b> "
								+ estudianteTrabajoTitulacion2.getEstudiante().getApellidoNombre() + " - ("
								+ estudianteTrabajoTitulacion2.getEstudiante().getId()
								+ ")<br /><b>Especialista 1: </b> " + especialista1.getApellidoNombre() + " - ("
								+ especialista1.getId() + ")<br /><b>Especialista 2: </b> "
								+ especialista2.getApellidoNombre() + " - (" + especialista2.getId()
								+ ") <br /><b>Especialista 3: </b> " + especialista3.getApellidoNombre() + " - ("
								+ especialista3.getId() + ")<br /><b>Especialista Suplente: </b> "
								+ especialistaSuplente1.getApellidoNombre() + " - (" + especialistaSuplente1.getId()
								+ ") <br />En la plataforma podrá encontrar dicha información, específicamente en <b>Dashboard estudiante</b>. Los especialistas podrán observar a quienes evaluarán en el menú Evaluador y se escoje examen complexivo o trabajo de titulación</span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
						List<File> listAdjunto = null;
						Parametro parametro = parametroService.obtener();
						Map<String, String> parametros = parametroService.traerMap(parametro);
						UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatario, asunto, detalle, listAdjunto,
								parametros);
					}
				}

				estudianteTrabajoTitulacion.setEspecialista1(especialista1);
				estudianteTrabajoTitulacion.setEspecialista2(especialista2);
				estudianteTrabajoTitulacion.setEspecialista3(especialista3);
				estudianteTrabajoTitulacion.setEspecialistaSuplente1(especialistaSuplente1);

				if (activarEspecialistaE) {
					if (especialistaSuplantadoInterfazE
							.equals(estudianteTrabajoTitulacion.getEspecialista1().getId())) {
						if (estudianteTrabajoTitulacion.getEe1() == null) {
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista1);
							estudianteTrabajoTitulacion.setActivarSuplenteE(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista1.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito.");

					} else if (especialistaSuplantadoInterfazE
							.equals(estudianteTrabajoTitulacion.getEspecialista2().getId())) {
						if (estudianteTrabajoTitulacion.getEe2() == null) {
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista2);
							estudianteTrabajoTitulacion.setActivarSuplenteE(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito.");

					} else if (especialistaSuplantadoInterfazE
							.equals(estudianteTrabajoTitulacion.getEspecialista3().getId())) {
						if (estudianteTrabajoTitulacion.getEe3() == null) {
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista3);
							estudianteTrabajoTitulacion.setActivarSuplenteE(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista3.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito.");

					}
				} else {
					estudianteTrabajoTitulacion.setActivarSuplenteE(null);
					estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(null);
				}

				if (activarEspecialistaO) {
					if (especialistaSuplantadoInterfazO
							.equals(estudianteTrabajoTitulacion.getEspecialista1().getId())) {
						if (estudianteTrabajoTitulacion.getOe1() == null) {
							estudianteTrabajoTitulacion.setActivarSuplenteO(true);
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoO(especialista1);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista1.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación en la sustentación del estudiante.");

					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteTrabajoTitulacion.getEspecialista2().getId())) {
						if (estudianteTrabajoTitulacion.getOe2() == null) {
							estudianteTrabajoTitulacion.setActivarSuplenteO(true);
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoO(especialista2);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación en la sustentación del estudiante.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteTrabajoTitulacion.getEspecialista3().getId())) {
						if (estudianteTrabajoTitulacion.getOe3() == null) {
							estudianteTrabajoTitulacion.setActivarSuplenteO(true);
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoO(especialista3);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista3.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación en la sustentación del estudiante.");
					}
				} else {
					estudianteTrabajoTitulacion.setActivarSuplenteO(null);
					estudianteTrabajoTitulacion.setEspecialistaSuplantadoO(null);
				}

				if (estudianteTrabajoTitulacion2 != null) {
					estudianteTrabajoTitulacion2.setEspecialista1(estudianteTrabajoTitulacion.getEspecialista1());
					estudianteTrabajoTitulacion2.setEspecialista2(estudianteTrabajoTitulacion.getEspecialista2());
					estudianteTrabajoTitulacion2.setEspecialista3(estudianteTrabajoTitulacion.getEspecialista3());
					estudianteTrabajoTitulacion2
							.setEspecialistaSuplente1(estudianteTrabajoTitulacion.getEspecialistaSuplente1());
					estudianteTrabajoTitulacion2.setActivarSuplenteE(estudianteTrabajoTitulacion.getActivarSuplenteE());
					estudianteTrabajoTitulacion2
							.setEspecialistaSuplantadoE(estudianteTrabajoTitulacion.getEspecialistaSuplantadoE());
					estudianteTrabajoTitulacion2.setActivarSuplenteO(estudianteTrabajoTitulacion.getActivarSuplenteO());
					estudianteTrabajoTitulacion2
							.setEspecialistaSuplantadoO(estudianteTrabajoTitulacion.getEspecialistaSuplantadoO());

					estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion2);

				}

				estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);

				try {
					estudiantesTrabajoTitulacionService.creacionDocumentoDrive(estudianteTrabajoTitulacion,
							estudianteTrabajoTitulacion2);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (estudianteTrabajoTitulacion.getActivarSuplenteE() != null) {
					String[] destinatarios;
					String asunto = "";
					if (estudianteTrabajoTitulacion2 != null) {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								estudianteTrabajoTitulacion2.getEstudiante().getEmail(), especialista1.getEmail(),
								especialista2.getEmail(), especialista3.getEmail(), especialistaSuplente1.getEmail() };
						destinatarios = destinatario;
						String asunto1 = "Notificación sobre activación del especialista suplente trabajo de titulación dimensión escrita de los estudiantes "
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId() + ") y "
								+ estudianteTrabajoTitulacion2.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion2.getEstudiante().getId() + ")";
						asunto = asunto1;
					} else {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
								especialistaSuplente1.getEmail() };
						destinatarios = destinatario;
						String asunto1 = "Notificación sobre activación del especialista suplente trabajo de titulación dimensión escrita del estudiante "
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId() + ").";
						asunto = asunto1;
					}

					String detalle = "<div dir='ltr'><span style='color:#000000;font-size:16px;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar que el especialista <strong>"
							+ estudianteTrabajoTitulacion.getEspecialistaSuplantadoE().getApellidoNombre() + " ("
							+ estudianteTrabajoTitulacion.getEspecialistaSuplantadoE().getId()
							+ ")</strong> no podrá actuar en la dimensión escrita, por lo que el especialista suplente <strong>"
							+ estudianteTrabajoTitulacion.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId()
							+ ")</strong> asumirá dicho rol. <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute;	 dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted	 recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
							parametros);
				}

				if (estudianteTrabajoTitulacion.getActivarSuplenteO() != null) {
					String[] destinatarios;
					String asunto = "";
					if (estudianteTrabajoTitulacion2 != null) {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								estudianteTrabajoTitulacion2.getEstudiante().getEmail(), especialista1.getEmail(),
								especialista2.getEmail(), especialista3.getEmail(), especialistaSuplente1.getEmail() };
						String asunto1 = "Notificación sobre activación del especialista suplente trabajo de titulación dimensión oral de los estudiantes "
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId() + ") y "
								+ estudianteTrabajoTitulacion2.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion2.getEstudiante().getId() + ").";
						destinatarios = destinatario;
						asunto = asunto1;
					} else {
						String[] destinatario = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
								especialista1.getEmail(), especialista2.getEmail(), especialista3.getEmail(),
								especialistaSuplente1.getEmail() };
						String asunto1 = "Notificación sobre activación del especialista suplente trabajo de titulación dimensión oral de estudiante "
								+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " ("
								+ estudianteTrabajoTitulacion.getEstudiante().getId() + ").";
						destinatarios = destinatario;
						asunto = asunto1;
					}

					String detalle = "<div dir='ltr'><span style='color:#000000;font-size:16px;'>Estimad@(s) estudiante y docentes evaluadores.<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>El presente email es para notificar que el especialista <strong>"
							+ estudianteTrabajoTitulacion.getEspecialistaSuplantadoO().getApellidoNombre() + " ("
							+ estudianteTrabajoTitulacion.getEspecialistaSuplantadoO().getId()
							+ ")</strong> no podrá actuar en la dimensión oral del trabajo de titulación, por lo que el especialista suplente <strong>"
							+ estudianteTrabajoTitulacion.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId()
							+ ")<strong> asumirá dicho rol. <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute;	 dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted	 recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
							parametros);
				}

				panelPrincipal = true;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;
				panelTrabajoEscrito = false;
				panelArticulo = false;
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El comité evaluador fue asignado de manera correcta y todos los campos han sido guardados correctamente.");
				buscar();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			if (estudianteTrabajoTitulacion.getFechaSustentacion().equals(null)
					|| estudianteTrabajoTitulacion.getFechaSustentacion().equals("")
					|| estudianteTrabajoTitulacion.getLugarSustentacion().equals(null)
					|| estudianteTrabajoTitulacion.getLugarSustentacion().equals("")) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Por favor llene todos los campos antes de guardar. Falta fecha o lugar de sustentación.");
			} else {

				DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
				estudiantesTrabajoTitulacionService
						.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"fechaSustentacion\"='"
								+ hourdateFormat.format(estudianteTrabajoTitulacion.getFechaSustentacion())
								+ "', \"lugarSustentacion\"='" + estudianteTrabajoTitulacion.getLugarSustentacion()
								+ "' WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
				presentaMensaje(FacesMessage.SEVERITY_INFO, "Fecha y lugar de sustentación guardada correctamente");
				EstudianteTrabajoTitulacion ett2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
						"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
						new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
								estudianteTrabajoTitulacion.getEstudiante().getEmail() },
						false, new Object[] {});
				if (ett2 != null) {
					// ett2.setFechaSustentacion(estudianteTrabajoTitulacion.getFechaSustentacion());
					// estudiantesTrabajoTitulacionService.actualizar(ett2);
					estudiantesTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"fechaSustentacion\"='"
									+ hourdateFormat.format(estudianteTrabajoTitulacion.getFechaSustentacion())
									+ "', \"lugarSustentacion\"='" + estudianteTrabajoTitulacion.getLugarSustentacion()
									+ "' WHERE id=" + ett2.getId() + ";");

					DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
					DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
					String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
							ett2.getEstudiante().getEmail(), estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
							estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
							estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
							estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
					String asunto = "ASIGNACIÓN FECHA DE SUSTENTACIÓN DE LOS ESTUDIANTES  "
							+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " - "
							+ ett2.getEstudiante().getApellidoNombre() + " MODALIDAD TRABAJO DE TITULACIÓN.";
					String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la fecha de sustentación de los estudiantes <strong>"
							+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + "</strong> y <strong>"
							+ ett2.getEstudiante().getApellidoNombre()
							+ "</strong> ha sido determinada para el <strong>"
							+ formatoFecha.format(estudianteTrabajoTitulacion.getFechaSustentacion())
							+ "</strong> a las <strong>"
							+ formatoHora.format(estudianteTrabajoTitulacion.getFechaSustentacion())
							+ "</strong> en el lugar <strong>" + estudianteTrabajoTitulacion.getLugarSustentacion()
							+ "</strong>. <br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p>  <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
							parametros);

				} else {
					DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
					DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
					String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
							estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
							estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
							estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
							estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
					String asunto = "ASIGNACIÓN FECHA DE SUSTENTACIÓN DEL ESTUDIANTE  "
							+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
							+ " DE LA MODALIDAD TRABAJO DE TITULACIÓN.";
					String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la fecha de sustentación del estudiante <strong>"
							+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
							+ "</strong> ha sido determinada para el <strong>"
							+ formatoFecha.format(estudianteTrabajoTitulacion.getFechaSustentacion())
							+ "</strong> a las <strong>"
							+ formatoHora.format(estudianteTrabajoTitulacion.getFechaSustentacion())
							+ "</strong> en el lugar <strong>" + estudianteTrabajoTitulacion.getLugarSustentacion()
							+ "</strong>. <br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
							parametros);
				}
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

	public boolean isEsUmmog() {
		return esUmmog;
	}

	public boolean isPanelArticulo() {
		return panelArticulo;
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
			System.out.println("Indica retorno de valor.");
		} else {
			System.out.println("He ingresado desde un método get.");
			if (procesos != null)
				procesos.clear();
			if (carreras != null)
				carreras.clear();
			criterioBusquedaEstudiante = "";
			if (listadoEstudiantesTrabajoTitulacion != null)
				listadoEstudiantesTrabajoTitulacion.clear();
		}
		a();

	}

	public void mostrarpanelArticulo() {
		// if (estudianteTrabajoTitulacion.getAprobado() == null) {
		// presentaMensaje(FacesMessage.SEVERITY_INFO, "El estudiante aún no ha
		// sido aprobado por el tutor.");
		buscar();
		// } else {
		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTrabajoEscrito = false;
		panelArticulo = true;
		// }
	}

	public void obtenerCarreras() {
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "inner join c.estudiantesTrabajosTitulaciones tt inner join tt.proceso p "
						+ "where u.email=?1 and p.id=?2 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						proceso },
				0, false, new Object[] {});
	}

	public void obtenerHorasTutorias() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (estudianteTrabajoTitulacion.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "12 horas y 0 minutos.";
		} else {
			for (int i = 0; i < estudianteTrabajoTitulacion.getTutorias().size(); i++) {
				f1.setTime(estudianteTrabajoTitulacion.getTutorias().get(i).getFecha());
				f2.setTime(estudianteTrabajoTitulacion.getTutorias().get(i).getFechaFinTutoria());

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

		Integer ht = 48;
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
		panelTrabajoEscrito = false;
		panelArticulo = false;

		String calificaciones = estudiantesTrabajoTitulacionService.traerCalificaciones(estudianteTrabajoTitulacion);
		calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);

	}

	public void presentarDocumento() {
		try {

			estudianteTrabajoTitulacion2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1 and e.id!=?2",
					new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (estudianteTrabajoTitulacion2 == null)
				System.out.println("ett2 vacío");
			else
				System.out.println("ett2 lleno");

			if (estudianteTrabajoTitulacion.getValidarArchivo() == null)
				validacionArchivo = false;
			else
				validacionArchivo = estudianteTrabajoTitulacion.getValidarArchivo();

			if (estudianteTrabajoTitulacion.getArchivo() == null) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El estudiante no ha subido ningún documento de su trabajo de titulación.");
			} else {
				urlDocumento = estudianteTrabajoTitulacion.getArchivo();
				InputStream is;

				is = new FileInputStream(urlDocumento);
				streamedContent = new DefaultStreamedContent(is, "application/pdf");
				System.out.println(urlDocumento);

				textoInconsistencia = "";

				panelPrincipal = false;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;
				panelTutorias = false;
				panelTrabajoEscrito = true;
				panelArticulo = false;

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La ruta del archivo, se encuentra incorrecta.");
		}
	}

	public void presentarTutoriasEstudiante() {
		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTutorias = true;
		panelTrabajoEscrito = false;
		panelArticulo = false;

		obtenerHorasTutorias();
	}

	public void presentarValidacion() {

		if (estudianteTrabajoTitulacion.getAprobado() == null) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "El estudiante aún no ha sido aprobado por el tutor.");
		} else {
			panelPrincipal = false;
			panelSeleccionar = true;
			panelDashboard = false;
			panelComite = false;
			panelTrabajoEscrito = false;
		}

	}

	public void seleccionarComiteEvaluador() {

		if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null
				|| estudianteTrabajoTitulacion.getOpcionTitulacion() == null) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede asignar a los evaluadores por qué el estudiante no tiene el seminario o la opción de titulación.");
		} else {

			especialista1 = new Usuario();
			especialista2 = new Usuario();
			especialista3 = new Usuario();
			especialistaSuplente1 = new Usuario();

			estudianteTrabajoTitulacion2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1 and e.id!=?2",
					new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (estudianteTrabajoTitulacion.getEspecialista1() != null) {
				especialista1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteTrabajoTitulacion.getEspecialista1().getId() }, false,
						new Object[] {});
			} else {
				especialista1 = usuarioService.obtenerObjeto(
						"select u from Usuario u where u.id=?1", new Object[] { estudianteTrabajoTitulacion
								.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getId() },
						false, new Object[] {});
			}
			if (estudianteTrabajoTitulacion.getEspecialista2() != null) {
				especialista2 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteTrabajoTitulacion.getEspecialista2().getId() }, false,
						new Object[] {});
			}
			if (estudianteTrabajoTitulacion.getEspecialista3() != null) {
				especialista3 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteTrabajoTitulacion.getEspecialista3().getId() }, false,
						new Object[] {});
			}
			if (estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) {
				especialistaSuplente1 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId() }, false,
						new Object[] {});
			}

			if (estudianteTrabajoTitulacion.getActivarSuplenteE() == null)
				activarEspecialistaE = false;
			else if (estudianteTrabajoTitulacion.getActivarSuplenteE() == false)
				activarEspecialistaE = false;
			else if (estudianteTrabajoTitulacion.getActivarSuplenteE() == true)
				activarEspecialistaE = true;

			if (estudianteTrabajoTitulacion.getActivarSuplenteO() == null)
				activarEspecialistaO = false;
			else if (estudianteTrabajoTitulacion.getActivarSuplenteO() == false)
				activarEspecialistaO = false;
			else if (estudianteTrabajoTitulacion.getActivarSuplenteO() == true)
				activarEspecialistaO = true;

			if (estudianteTrabajoTitulacion.getEspecialistaSuplantadoE() != null) {
				especialistaSuplantadoInterfazE = estudianteTrabajoTitulacion.getEspecialistaSuplantadoE().getId();
			} else if (estudianteTrabajoTitulacion.getEspecialista1() != null) {
				especialistaSuplantadoInterfazE = estudianteTrabajoTitulacion.getEspecialista1().getId();
			} else {
				especialistaSuplantadoInterfazE = "Debe añadir el cómite evaluador.";
			}

			if (estudianteTrabajoTitulacion.getEspecialistaSuplantadoO() != null) {
				especialistaSuplantadoInterfazO = estudianteTrabajoTitulacion.getEspecialistaSuplantadoO().getId();
			} else if (estudianteTrabajoTitulacion.getEspecialista1() != null) {
				especialistaSuplantadoInterfazO = estudianteTrabajoTitulacion.getEspecialista1().getId();
			} else {
				especialistaSuplantadoInterfazO = "Debe añadir el cómite evaluador.";
			}

			panelPrincipal = false;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = true;
			panelTrabajoEscrito = false;
			panelArticulo = false;

		}
	}

	public void seleccionarE1() {
		boolEspecialista1 = true;
		boolEspecialista2 = false;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarE2() {
		boolEspecialista1 = false;
		boolEspecialista2 = true;
		boolEspecialista3 = false;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarE3() {
		boolEspecialista1 = false;
		boolEspecialista2 = false;
		boolEspecialista3 = true;
		boolEspecialistaSuplente1 = false;
	}

	public void seleccionarEspecialista() {
		if (boolEspecialista1) {
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

	public void setCalificacionFinal(BigDecimal calificacionFinal) {
		this.calificacionFinal = calificacionFinal;
	}

	public void setCalificacionParteEscritaFinal(BigDecimal calificacionParteEscritaFinal) {
		this.calificacionParteEscritaFinal = calificacionParteEscritaFinal;
	}

	public void setCalificacionParteOralFinal(BigDecimal calificacionParteOralFinal) {
		this.calificacionParteOralFinal = calificacionParteOralFinal;
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

	public void setEspecialistasDropdown(HashMap<String, String> especialistasDropdown) {
		this.especialistasDropdown = especialistasDropdown;
	}

	public void setespecialistaSuplantadoInterfazE(String especialistaSuplantadoInterfazE) {
		this.especialistaSuplantadoInterfazE = especialistaSuplantadoInterfazE;
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

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setEstudianteTrabajoTitulacion2(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion2) {
		this.estudianteTrabajoTitulacion2 = estudianteTrabajoTitulacion2;
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

	public void setListadoEstudiantesTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion) {
		this.listadoEstudiantesTrabajoTitulacion = listadoEstudiantesTrabajoTitulacion;
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

	public void setPanelArticulo(boolean panelArticulo) {
		this.panelArticulo = panelArticulo;
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

	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}

	public void setValidacionArchivo(boolean validacionArchivo) {
		this.validacionArchivo = validacionArchivo;
	}

	public void validarArticuloEnsayoAcademico() {
		estudianteTrabajoTitulacion.setEe1(new BigDecimal(70));
		estudianteTrabajoTitulacion.setEe2(new BigDecimal(70));
		estudianteTrabajoTitulacion.setEe3(new BigDecimal(70));
		estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee1 = "
				+ estudianteTrabajoTitulacion.getEe1() + ", ee2 = " + estudianteTrabajoTitulacion.getEe2() + ", ee3 = "
				+ estudianteTrabajoTitulacion.getEe3() + " WHERE id=" + estudianteTrabajoTitulacion.getId());

		EstudianteTrabajoTitulacion ett2 = estudiantesTrabajoTitulacionService.obtenerObjeto(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
				new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
						estudianteTrabajoTitulacion.getEstudiante().getEmail() },
				false, new Object[] {});

		if (ett2 != null) {
			estudiantesTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET ee1 = "
					+ estudianteTrabajoTitulacion.getEe1() + ", ee2 = " + estudianteTrabajoTitulacion.getEe2()
					+ ", ee3 = " + estudianteTrabajoTitulacion.getEe3() + " WHERE id=" + ett2.getId());

			String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
					ett2.getEstudiante().getEmail(), estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
					estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
					estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
					estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
			String asunto = "COMPROBANTE DE RECEPCIÓN Y APROBACIÓN ARTÍCULO O ENSAYO ACADÉMICO DE LOS ESTUDIANTES  "
					+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " - "
					+ ett2.getEstudiante().getApellidoNombre() + " MODALIDAD TRABAJO DE TITULACIÓN.";
			String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que se ha consolidado los requisitos para aprobación del trabajo de titulación opción <strong>Artículo o Ensayo Académico</strong> de los estudiantes <strong>"
					+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + "</strong> y <strong>"
					+ ett2.getEstudiante().getApellidoNombre() + "</strong> pertenecientes a las carrera <strong>"
					+ estudianteTrabajoTitulacion.getCarrera().getNombre()
					+ "</strong>, y atendiendo al artículo 30 de la guía de instrumentalización del sistema de titulación, los estudiantes obtienen una calificación de 70 puntos en la parte escrita, pasando directamente a la parte oral (sustentación del artículo publicado) que se evaluará sobre 30 puntos. <strong>"
					+ "La notificación sobre el lugar, fecha y hora de la sustentación será notificada en los próximos días bajo este mismo medio por lo que se sugiere estar pendiente.</strong>.<br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
			List<File> listAdjunto = null;
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			try {
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
					estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
					estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
					estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
					estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
			String asunto = "COMPROBANTE DE RECEPCIÓN Y APROBACIÓN ARTÍCULO O ENSAYO ACADÉMICO "
					+ (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareToIgnoreCase("F") == 0 ? "DE LA"
							: "DEL")
					+ " ESTUDIANTE  " + estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " - "
					+ " MODALIDAD TRABAJO DE TITULACIÓN.";
			String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que se ha consolidado los requisitos para aprobación del trabajo de titulación opción <strong>Artículo o Ensayo Académico</strong> "
					+ (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareToIgnoreCase("F") == 0 ? "de la"
							: "del")
					+ " estudiante <strong>" + estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
					+ "</strong> perteneciente a la carrera <strong>"
					+ estudianteTrabajoTitulacion.getCarrera().getNombre()
					+ "</strong>, y atendiendo al artículo 30 de la guía de instrumentalización del sistema de titulación, "
					+ (estudianteTrabajoTitulacion.getEstudiante().getGenero().compareToIgnoreCase("F") == 0 ? "la"
							: "el")
					+ " estudiante obtiene una calificación de 70 puntos en la parte escrita, pasando directamente a la parte oral (sustentación del artículo publicado) que se evaluará sobre 30 puntos. <br /> <strong>La notificación sobre el lugar, fecha y hora de la sustentación será notificada en los próximos días bajo este mismo medio por lo que se sugiere estar pendiente.</strong>"
					+ "<br /><div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
			List<File> listAdjunto = null;
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			try {
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		buscar();
		establecerPanelPrincipal();
		presentaMensaje(FacesMessage.SEVERITY_INFO,
				"Se ha validado el artículo o ensayo académico del estudiante de forma correcta.");
	}

	public void verificarDocumento() {
		System.out.println(validacionArchivo);
		// estudianteTrabajoTitulacion.setValidarArchivo(validacionArchivo);
		// estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
		// validacionArchivo = estudianteTrabajoTitulacion.getValidarArchivo();
	}

}
