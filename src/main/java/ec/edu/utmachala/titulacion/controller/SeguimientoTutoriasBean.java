package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.DocenteSeminario;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Seminario;
import ec.edu.utmachala.titulacion.entity.Tutoria;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.DocenteSeminarioService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SeminarioService;
import ec.edu.utmachala.titulacion.service.TutoriaService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class SeguimientoTutoriasBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private DocenteSeminarioService docenteSeminarioService;

	@Autowired
	private TutoriaService tutoriaService;

	@Autowired
	private SeminarioService seminarioService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProcesoService procesoService;

	private List<FechaProceso> listadoFechasProcesos;

	private List<DocenteSeminario> docentesSeminarios;
	private DocenteSeminario docenteSeminarioSeleccionado;

	private List<Seminario> seminarios;
	private Seminario seminarioSeleccionado;

	private List<EstudianteTrabajoTitulacion> estudiantesTrabajoTitulacion;
	private List<Proceso> procesos;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;
	private boolean panelPrincipal;
	private boolean panelTutorias;
	private boolean panelInsertarTutoria;
	private boolean panelEditarTutoria;
	private boolean panelValidarEstudiante;
	private Tutoria tutoria;
	private Date fecha;
	private Date fechaFinTutoria;
	private Date fechaProximaTutoria;
	private Date fechaFinProximaTutoria;

	private String fortaleza;
	private String debilidad;
	private String observacion;
	private int numTutoria;
	private String pass = "";
	private int indexTutoria;

	private String procesoSeleccionado;

	private String iconoValidarTrue = "fa fa-check white";
	private String iconoValidarFalse = "fa fa-check-square-o white";

	private Integer totalHoras;
	private Integer totalMinutos;
	private String numeroHorasTutorias;
	private Integer numeroHorasRestantes;
	private Integer numeroMinutosRestantes;
	private String numeroTotalHorasMinutosRestantes;

	private boolean aprobarTutoriasDentroFecha;

	private StreamedContent documento;

	private String usuarioEmail;

	@PostConstruct
	public void a() {

		procesos = procesoService.obtenerLista("select distinct p from Proceso p order by p.fechaInicio asc",
				new Object[] {}, 0, false, new Object[] {});

		panelPrincipal = true;
		panelValidarEstudiante = false;

		aprobarTutoriasDentroFecha = false;
	}

	public void actualizar() {
		if (fecha == null || fechaFinTutoria == null || fechaProximaTutoria == null || fechaFinProximaTutoria == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese las fechas de tutoria");
		else if (estudianteTrabajoTitulacion.getTutorias() != null
				&& (tutoria.getTareasRealizadas().compareToIgnoreCase("") == 0
						|| tutoria.getTareasNoRealizadas().compareToIgnoreCase("") == 0
						|| tutoria.getActividad().compareToIgnoreCase("") == 0
						|| tutoria.getTarea().compareToIgnoreCase("") == 0))
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Falta algún dato por ingresar");
		else if (fecha.compareTo(fechaFinTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha y hora de fecha inicio de tutoria no puede ser mayor a la fecha y hora de fin de la tutoría.");
		else if (fechaProximaTutoria.compareTo(fechaFinProximaTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha y hora de inicio de la próxima tutoria no puede ser mayor a la fecha y hora de fin de la tutoría.");
		else if (fechaFinTutoria.compareTo(fechaFinProximaTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha tutoria no puede ser mayor a la fecha de la próxima tutoría.");
		else {

			Usuario docente = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
					new Object[] { UtilSeguridad.obtenerUsuario() }, false, new Object[] {});

			tutoria.setFecha(UtilsDate.timestamp(fecha));
			tutoria.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
			tutoria.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
			tutoria.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));
			tutoria.setDocente(docente);
			tutoriaService.actualizar(tutoria);

			EstudianteTrabajoTitulacion estudianteTrabajoTitulacion2 = new EstudianteTrabajoTitulacion();
			estudianteTrabajoTitulacion2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt "
							+ "inner join stt.trabajoTitulacion tt inner join ett.estudiante e left join fetch ett.tutorias t "
							+ "where tt.id=?1 and e.id!=?2 order by t.fecha asc",
					new Object[] {
							estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (estudianteTrabajoTitulacion2 != null) {
				int c = 0;
				for (Tutoria tutoria2 : estudianteTrabajoTitulacion2.getTutorias()) {
					System.out.println(c + " - " + indexTutoria);
					if (c == indexTutoria) {
						System.out.println("entre");
						System.out.println(tutoria.getId() + " - " + tutoria2.getId());

						tutoria2.setTareasRealizadas(tutoria.getTareasRealizadas());
						tutoria2.setTareasNoRealizadas(tutoria.getTareasNoRealizadas());
						tutoria2.setActividad(tutoria.getActividad());
						tutoria2.setTarea(tutoria.getTarea());
						tutoria2.setFecha(UtilsDate.timestamp(fecha));
						tutoria2.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
						tutoria2.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
						tutoria2.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));
						tutoria2.setDocente(docente);
						tutoriaService.actualizar(tutoria2);
						onRowSelect();
					}
					c++;
				}
			}

			onRowSelect();
			obtenerHorasTutorias();

			panelTutorias = true;
			panelEditarTutoria = false;

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente la tutoría");
		}
	}

	public void cargarSeminarios() {

		if (procesoSeleccionado.compareTo("0") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoger un proceso por favor.");
		} else {

			usuarioEmail = UtilSeguridad.obtenerUsuario();

			if (UtilSeguridad.obtenerUsuario().compareTo("jcelleri@utmachala.edu.ec") == 0) {
				// docentesSeminarios = docenteSeminarioService.obtenerLista(
				// "select ds from DocenteSeminario ds inner join ds.docente d "
				// + "inner join ds.seminario s inner join s.proceso p where
				// d.email=?1 order by s.nombre",
				// new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false,
				// new
				// Object[0]);
				docentesSeminarios = docenteSeminarioService.obtenerLista(
						"select distinct ds from DocenteSeminario ds inner join ds.docente d "
								+ "inner join ds.seminario s inner join s.proceso p inner join s.seminariosTrabajosTitulacion stt "
								+ "inner join stt.estudiantesTrabajosTitulacion ett inner join ett.carrera ca left join ett.cotutor co "
								+ "where (d.email=?1 or co.email=?1) and p.id=?2 ",
						new Object[] { UtilSeguridad.obtenerUsuario(), procesoSeleccionado }, 0, false, new Object[0]);

				seminarios = seminarioService.obtenerLista(
						"select distinct s from Seminario s inner join s.docentesSeminarios ds inner join ds.docente d "
								+ "inner join s.proceso p inner join s.seminariosTrabajosTitulacion stt "
								+ "inner join stt.estudiantesTrabajosTitulacion ett inner join ett.carrera ca left join ett.cotutor co "
								+ "where (d.email=?1 or co.email=?1) and p.id=?2 ",
						new Object[] { UtilSeguridad.obtenerUsuario(), procesoSeleccionado }, 0, false, new Object[0]);

				if (estudiantesTrabajoTitulacion != null) {
					estudiantesTrabajoTitulacion.clear();
				}

			} else {
				docentesSeminarios = docenteSeminarioService.obtenerLista(
						"select distinct ds from DocenteSeminario ds inner join ds.docente d "
								+ "inner join ds.seminario s inner join s.proceso p inner join s.seminariosTrabajosTitulacion stt "
								+ "inner join stt.estudiantesTrabajosTitulacion ett inner join ett.carrera ca left join ett.cotutor co "
								+ "where (d.email=?1 or co.email=?1) and p.id=?2 ",
						new Object[] { UtilSeguridad.obtenerUsuario(), procesoSeleccionado }, 0, false, new Object[0]);

				seminarios = seminarioService.obtenerLista(
						"select distinct s from Seminario s inner join s.docentesSeminarios ds inner join ds.docente d "
								+ "inner join s.proceso p inner join s.seminariosTrabajosTitulacion stt "
								+ "inner join stt.estudiantesTrabajosTitulacion ett inner join ett.carrera ca left join ett.cotutor co "
								+ "where (d.email=?1 or co.email=?1) and p.id=?2 ",
						new Object[] { UtilSeguridad.obtenerUsuario(), procesoSeleccionado }, 0, false, new Object[0]);

				if (estudiantesTrabajoTitulacion != null) {
					estudiantesTrabajoTitulacion.clear();
				}
			}
		}
	}

	public String getDebilidad() {
		return debilidad;
	}

	public DocenteSeminario getDocenteSeminarioSeleccionado() {
		return docenteSeminarioSeleccionado;
	}

	public List<DocenteSeminario> getDocentesSeminarios() {
		return docentesSeminarios;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public List<EstudianteTrabajoTitulacion> getEstudiantesTrabajoTitulacion() {
		return estudiantesTrabajoTitulacion;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public Date getFechaFinProximaTutoria() {
		return fechaFinProximaTutoria;
	}

	public Date getFechaFinTutoria() {
		return fechaFinTutoria;
	}

	public Date getFechaProximaTutoria() {
		return fechaProximaTutoria;
	}

	public String getFortaleza() {
		return fortaleza;
	}

	public String getIconoValidarFalse() {
		return iconoValidarFalse;
	}

	public String getIconoValidarTrue() {
		return iconoValidarTrue;
	}

	public int getIndexTutoria() {
		return indexTutoria;
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

	public int getNumTutoria() {
		return numTutoria;
	}

	public String getObservacion() {
		return observacion;
	}

	public String getPass() {
		return pass;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public String getProcesoSeleccionado() {
		return procesoSeleccionado;
	}

	public List<Seminario> getSeminarios() {
		return seminarios;
	}

	public Seminario getSeminarioSeleccionado() {
		return seminarioSeleccionado;
	}

	public Integer getTotalHoras() {
		return totalHoras;
	}

	public Integer getTotalMinutos() {
		return totalMinutos;
	}

	public Tutoria getTutoria() {
		return tutoria;
	}

	public String getUsuarioEmail() {
		return usuarioEmail;
	}

	public void guardarValidacionEstudiante() {
		try {
			estudianteTrabajoTitulacion.setAprobado(true);
			// estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			estudianteTrabajoTitulacionService.actualizarSQL(
					"UPDATE \"estudiantesTrabajosTitulacion\" SET aprobado=" + estudianteTrabajoTitulacion.getAprobado()
							+ " WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ha aprobado las tutorías y trabajo de titulación del estudiante.");
			EstudianteTrabajoTitulacion ett2 = new EstudianteTrabajoTitulacion();
			ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.email!=?2",
					new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getEmail() },
					false, new Object[] {});
			if (ett2 != null) {
				ett2.setAprobado(true);
				estudianteTrabajoTitulacionService
						.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET aprobado=" + ett2.getAprobado()
								+ " WHERE id=" + ett2.getId() + ";");
			}

			if (ett2 != null) {
				String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
						ett2.getEstudiante().getEmail(), estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
						estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
						estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
						estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
				String asunto = "Aprobación de tutorías de los estudiantes "
						+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " y "
						+ ett2.getEstudiante().getApellidoNombre() + " de la modalidad trabajo de titulación.";
				String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la/el docente tutor/a "
						+ estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente()
								.getApellidoNombre()
						+ " ha culminado con la fase de tutorías y consiente que el trabajo realizado cumple con las normas académicas establecidas, ha procedido a validarlo con la finalidad de continuar en el proceso de titulación."
						+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre() + " y "
						+ ett2.getEstudiante().getApellidoNombre()
						+ "<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>Por consiguiente los estudiantes quedan habilitados para presentar el documento escrito a los miembros del comité evaluador y así el comité poder revisar y registrar la calificación en la plataforma de titulación según las fechas que se especifican en la hoja de ruta.<br /> <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);
			} else {
				String[] destinatarios = { estudianteTrabajoTitulacion.getEstudiante().getEmail(),
						estudianteTrabajoTitulacion.getEspecialista1().getEmail(),
						estudianteTrabajoTitulacion.getEspecialista2().getEmail(),
						estudianteTrabajoTitulacion.getEspecialista3().getEmail(),
						estudianteTrabajoTitulacion.getEspecialistaSuplente1().getEmail() };
				String asunto = "Aprobación de tutorías del estudiante "
						+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
						+ " de la modalidad trabajo de titulación.";
				String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la/el docente tutor/a "
						+ estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente()
								.getApellidoNombre()
						+ " ha culminado con la fase de tutorías y consiente que el trabajo realizado cumple con las normas académicas establecidas, ha procedido a validarlo con la finalidad de continuar en el proceso de titulación."
						+ estudianteTrabajoTitulacion.getEstudiante().getApellidoNombre()
						+ "<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>Por consiguiente el estudiante queda habilitado para presentar el documento escrito a los miembros del comité evaluador y así el comité poder revisar y registrar la calificación en la plataforma de titulación según las fechas que se especifican en la hoja de ruta.<br /> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);
			}
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
		onRowSelect();
		panelPrincipal = true;
		panelValidarEstudiante = false;
	}

	public void imprimir(Tutoria tuto) {
		tutoria = tuto;
		System.out.println("Ingreso a imprimir, tutoria: " + tutoria.getActividad());
		List<Tutoria> tutorias = new ArrayList<Tutoria>();
		tutorias.add(tutoria);
		if (tutorias.isEmpty()) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "No existen datos para generar el reporte.");
		} else {

			Report report = new Report();
			report.setFormat("PDF");
			report.setPath(UtilsReport.serverPathReport);

			EstudianteTrabajoTitulacion estudianteTrabajoTitulacion2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt "
							+ "inner join stt.trabajoTitulacion tt inner join ett.estudiante e left join fetch ett.tutorias t "
							+ "where tt.id=?1 and e.id!=?2",
					new Object[] {
							estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (estudianteTrabajoTitulacion2 != null) {

				report.setName("TT_TutoriaPareja");

			} else {
				report.setName("TT_TutoriaSimple");
			}

			report.addParameter("idTutoria", String.valueOf(tutoria.getId()));

			report.addParameter("NUMERO_TUTORIA",
					String.valueOf(estudianteTrabajoTitulacion.getTutorias().indexOf(tutoria) + 1));
			report.addParameter("PROCESO", estudianteTrabajoTitulacion.getProceso().getId());

			documento = UtilsReport.ejecutarReporte(report);
		}
	}

	public void insertarTutoria() {
		if (fecha == null || fechaFinTutoria == null || fechaProximaTutoria == null || fechaFinProximaTutoria == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese las fechas de tutoria");
		else if (estudianteTrabajoTitulacion.getTutorias() != null
				&& (tutoria.getTareasRealizadas().compareToIgnoreCase("") == 0
						|| tutoria.getTareasNoRealizadas().compareToIgnoreCase("") == 0
						|| tutoria.getActividad().compareToIgnoreCase("") == 0
						|| tutoria.getTarea().compareToIgnoreCase("") == 0))
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Falta algún dato por ingresar");
		else if (fecha.compareTo(fechaFinTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha y hora de fecha inicio de tutoria no puede ser mayor a la fecha y hora de fin de la tutoría.");
		else if (fechaProximaTutoria.compareTo(fechaFinProximaTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha y hora de inicio de la próxima tutoria no puede ser mayor a la fecha y hora de fin de la tutoría.");
		else if (fechaFinTutoria.compareTo(fechaFinProximaTutoria) >= 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La fecha tutoria no puede ser mayor a la fecha de la próxima tutoría.");
		else {
			if (estudianteTrabajoTitulacion.getTutorias() == null)
				estudianteTrabajoTitulacion.setTutorias(new ArrayList<Tutoria>());

			Usuario docente = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
					new Object[] { UtilSeguridad.obtenerUsuario() }, false, new Object[] {});

			tutoria.setEstudianteTrabajoTitulacion(estudianteTrabajoTitulacion);
			tutoria.setFecha(UtilsDate.timestamp(fecha));
			tutoria.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
			tutoria.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
			tutoria.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));
			tutoria.setDocente(docente);

			estudianteTrabajoTitulacion.getTutorias().add(tutoria);

			tutoriaService.insertar(tutoria);

			EstudianteTrabajoTitulacion estudianteTrabajoTitulacion2 = new EstudianteTrabajoTitulacion();
			estudianteTrabajoTitulacion2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt "
							+ "inner join stt.trabajoTitulacion tt inner join ett.estudiante e left join fetch ett.tutorias t "
							+ "where tt.id=?1 and e.id!=?2",
					new Object[] {
							estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getId() },
					false, new Object[] {});

			if (estudianteTrabajoTitulacion2 != null) {
				Tutoria tutoria2 = new Tutoria();
				tutoria2.setTareasRealizadas(tutoria.getTareasRealizadas());
				tutoria2.setTareasNoRealizadas(tutoria.getTareasNoRealizadas());
				tutoria2.setActividad(tutoria.getActividad());
				tutoria2.setTarea(tutoria.getTarea());
				tutoria2.setEstudianteTrabajoTitulacion(estudianteTrabajoTitulacion2);
				tutoria2.setFecha(UtilsDate.timestamp(fecha));
				tutoria2.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
				tutoria2.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
				tutoria2.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));
				tutoria2.setDocente(docente);

				estudianteTrabajoTitulacion2.getTutorias().add(tutoria2);

				tutoriaService.insertar(tutoria2);
			}

			onRowSelect();
			obtenerHorasTutorias();

			panelTutorias = true;
			panelInsertarTutoria = false;

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Insertó correctamente la tutoría");
		}
	}

	public boolean isPanelEditarTutoria() {
		return panelEditarTutoria;
	}

	public boolean isPanelInsertarTutoria() {
		return panelInsertarTutoria;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPanelTutorias() {
		return panelTutorias;
	}

	public boolean isPanelValidarEstudiante() {
		return panelValidarEstudiante;
	}

	public void mostrarPanelInsertarTutoria() {
		verificarDentroFechas();
		if (aprobarTutoriasDentroFecha) {
			panelTutorias = false;
			panelInsertarTutoria = true;
			tutoria = new Tutoria();
			fecha = null;
			fechaFinTutoria = null;
			fechaProximaTutoria = null;
			fechaFinProximaTutoria = null;
		} else
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede ingresar tutorías porque no se encuentra dentro del período de fechas establecidas en la hoja de ruta.");

	}

	public void mostrarPanelTutoria() {
		panelPrincipal = false;
		panelTutorias = true;

		obtenerHorasTutorias();
	}

	public void obtenerHorasTutorias() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (estudianteTrabajoTitulacion.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "48 horas y 0 minutos.";
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
				System.out.println("1. totalHoras: " + totalHoras + " - totalMinutos: " + totalMinutos);
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

		System.out.println("1. numerohorasrestantes: " + numeroHorasRestantes + "    -     numeroMinutosRestantes: "
				+ numeroMinutosRestantes);

		numeroTotalHorasMinutosRestantes = numeroHorasRestantes <= 0 && numeroMinutosRestantes <= 0
				? "Ha cumplido con las horas de tutorías establecidas."
				: numeroHorasRestantes + " horas y " + numeroMinutosRestantes + " minutos.";
	}

	public void onRowSelect() {
		// System.out.println("id del docenteseminario selecciona: " +
		// docenteSeminarioSeleccionado.getId());
		estudiantesTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
				"select distinct ett from EstudianteTrabajoTitulacion ett left join fetch ett.tutorias t "
						+ "inner join ett.seminarioTrabajoTitulacion stt inner join stt.trabajoTitulacion tt "
						+ "inner join stt.docenteSeminario ds inner join ds.docente d inner join ett.estudiante e "
						+ "inner join ds.seminario s left join ett.cotutor co inner join ett.proceso p "
						+ "where (d.email=?1 or co.email=?1) and p.id=?2 and s.id=?3 order by t.fecha",
				new Object[] { UtilSeguridad.obtenerUsuario(), seminarioSeleccionado.getProceso().getId(),
						seminarioSeleccionado.getId() },
				0, false, new Object[0]);

	}

	public void prepararEditar() {
		verificarDentroFechas();
		if (!aprobarTutoriasDentroFecha) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede editar la tutoría seleccionada porque esta fuera de las fechas determinadas en el cronograma del proceso actual.");
		} else {
			indexTutoria = estudianteTrabajoTitulacion.getTutorias().indexOf(tutoria);
			System.out.println(indexTutoria);
			fecha = tutoria.getFecha();
			fechaFinTutoria = tutoria.getFechaFinTutoria();
			fechaProximaTutoria = tutoria.getFechaProximaTutoria();
			fechaFinProximaTutoria = tutoria.getFechaFinProximaTutoria();
			panelTutorias = false;
			panelEditarTutoria = true;
		}
	}

	public void presentarPrincipalValidarEstudiante() {
		panelPrincipal = true;
		panelValidarEstudiante = false;
		onRowSelect();
	}

	public void regresarPanelPrincipal() {
		onRowSelect();
		panelPrincipal = true;
		panelTutorias = false;
	}

	public void regresarPanelTutorias() {
		panelTutorias = true;
		panelInsertarTutoria = false;
		panelEditarTutoria = false;

		onRowSelect();
		obtenerHorasTutorias();
	}

	public void setDebilidad(String debilidad) {
		this.debilidad = debilidad;
	}

	public void setDocenteSeminarioSeleccionado(DocenteSeminario docenteSeminarioSeleccionado) {
		this.docenteSeminarioSeleccionado = docenteSeminarioSeleccionado;
	}

	public void setDocentesSeminarios(List<DocenteSeminario> docentesSeminarios) {
		this.docentesSeminarios = docentesSeminarios;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setEstudiantesTrabajoTitulacion(List<EstudianteTrabajoTitulacion> estudiantesTrabajoTitulacion) {
		this.estudiantesTrabajoTitulacion = estudiantesTrabajoTitulacion;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public void setFechaFinProximaTutoria(Date fechaFinProximaTutoria) {
		this.fechaFinProximaTutoria = fechaFinProximaTutoria;
	}

	public void setFechaFinTutoria(Date fechaFinTutoria) {
		this.fechaFinTutoria = fechaFinTutoria;
	}

	public void setFechaProximaTutoria(Date fechaProximaTutoria) {
		this.fechaProximaTutoria = fechaProximaTutoria;
	}

	public void setFortaleza(String fortaleza) {
		this.fortaleza = fortaleza;
	}

	public void setIconoValidarFalse(String iconoValidarFalse) {
		this.iconoValidarFalse = iconoValidarFalse;
	}

	public void setIconoValidarTrue(String iconoValidarTrue) {
		this.iconoValidarTrue = iconoValidarTrue;
	}

	public void setIndexTutoria(int indexTutoria) {
		this.indexTutoria = indexTutoria;
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

	public void setNumTutoria(int numTutoria) {
		this.numTutoria = numTutoria;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public void setPanelEditarTutoria(boolean panelEditarTutoria) {
		this.panelEditarTutoria = panelEditarTutoria;
	}

	public void setPanelInsertarTutoria(boolean panelInsertarTutoria) {
		this.panelInsertarTutoria = panelInsertarTutoria;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPanelTutorias(boolean panelTutorias) {
		this.panelTutorias = panelTutorias;
	}

	public void setPanelValidarEstudiante(boolean panelValidarEstudiante) {
		this.panelValidarEstudiante = panelValidarEstudiante;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setProcesoSeleccionado(String procesoSeleccionado) {
		this.procesoSeleccionado = procesoSeleccionado;
	}

	public void setSeminarios(List<Seminario> seminarios) {
		this.seminarios = seminarios;
	}

	public void setSeminarioSeleccionado(Seminario seminarioSeleccionado) {
		this.seminarioSeleccionado = seminarioSeleccionado;
	}

	public void setTotalHoras(Integer totalHoras) {
		this.totalHoras = totalHoras;
	}

	public void setTotalMinutos(Integer totalMinutos) {
		this.totalMinutos = totalMinutos;
	}

	public void setTutoria(Tutoria tutoria) {
		this.tutoria = tutoria;
	}

	public void setUsuarioEmail(String usuarioEmail) {
		this.usuarioEmail = usuarioEmail;
	}

	public void validarEstudiante() {
		if (estudianteTrabajoTitulacion.getEspecialista1() == null
				|| estudianteTrabajoTitulacion.getEspecialista2() == null
				|| estudianteTrabajoTitulacion.getEspecialista3() == null
				|| estudianteTrabajoTitulacion.getEspecialistaSuplente1() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta asignarle el comité evaluador.");
		} else if (estudianteTrabajoTitulacion.getTituloInvestigacion() == null
				|| estudianteTrabajoTitulacion.getTituloInvestigacion() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar el título de la investigación.");
		} else if (estudianteTrabajoTitulacion.getPalabrasClaves() == null
				|| estudianteTrabajoTitulacion.getPalabrasClaves() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar las palabras claves de la investigación.");
		} else if (estudianteTrabajoTitulacion.getResumen() == null || estudianteTrabajoTitulacion.getResumen() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar el resumen de la investigación.");
			/*
			 * } else if (estudianteTrabajoTitulacion.getAntiplagio() == null ||
			 * estudianteTrabajoTitulacion.getCitas1() == null) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, falta ingresar el porcentaje del antiplagio o las citas bibliográficas."
			 * ); } else if (estudianteTrabajoTitulacion.getAntiplagio().compareTo(new
			 * BigDecimal("10")) > 0) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, ya que su porcentaje de urkund supera el 10%."
			 * ); } else if (estudianteTrabajoTitulacion.getCitas1().compareTo(new
			 * Integer(25)) < 0 &&
			 * estudianteTrabajoTitulacion.getOpcionTitulacion().getId(). compareTo(4) != 0)
			 * { UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, ya que el número de citas es menor a 25."
			 * );
			 */ } else {

			verificarDentroFechas();

			if (!aprobarTutoriasDentroFecha) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No puede aprobar al estudiante debido que excedió la fecha determinada en la hoja de ruta.");
			} else {
				obtenerHorasTutorias();
				if (totalHoras >= 48) {
					panelPrincipal = false;
					panelValidarEstudiante = true;
				} else
					presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Aún no cumple con las horas necesarias (48), el estudiante posee: " + totalHoras
									+ " horas.");
			}
		}
	}

	public void verificarDentroFechas() {
		if (estudiantesTrabajoTitulacion != null && estudiantesTrabajoTitulacion.size() >= 1) {

			listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
					new Object[] { estudianteTrabajoTitulacion.getProceso().getId() }, 0, false, new Object[0]);

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
				// existe fechas de procesos
				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
					aprobarTutoriasDentroFecha = true;
					System.out.println("General: ||| Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
							+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
							+ new Date() + " - valor bool: " + aprobarTutoriasDentroFecha);
				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
									seminarioSeleccionado.getLineaInvestigacionCarrera().getCarrera().getId() },
							0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
							aprobarTutoriasDentroFecha = true;
						} else {
							aprobarTutoriasDentroFecha = false;
						}
						System.out.println("Carrera ||| Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
								+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
								+ new Date() + " - valor bool: " + aprobarTutoriasDentroFecha);
					} else
						aprobarTutoriasDentroFecha = false;

				}
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteTrabajoTitulacion.getProceso().getId(), docenteSeminarioSeleccionado
								.getSeminario().getLineaInvestigacionCarrera().getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
						aprobarTutoriasDentroFecha = true;
					} else {
						aprobarTutoriasDentroFecha = false;
					}
					System.out.println("Carrera ||| Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
							+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
							+ new Date() + " - valor bool: " + aprobarTutoriasDentroFecha);
				} else
					aprobarTutoriasDentroFecha = false;
			}

		}
	}

}
