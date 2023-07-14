package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.IOException;
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

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.TutoriaEC;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.TutoriaECService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class SeguimientoTutoriasECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private TutoriaECService tutoriaECService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	@Autowired
	private ProcesoService procesoService;

	private List<FechaProceso> listadoFechasProcesos;

	private List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private List<Proceso> procesos;
	private String procesoSeleccionado;

	private boolean panelPrincipal;
	private boolean panelTutorias;
	private boolean panelInsertarTutoria;
	private boolean panelEditarTutoria;
	private boolean panelValidarEstudiante;

	private TutoriaEC tutoriaEC;
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

	private String iconoValidarTrue = "fa fa-check white";
	private String iconoValidarFalse = "fa fa-check-square-o white";

	private List<Carrera> carreras;
	private Carrera carrera;

	private Integer totalHoras;
	private Integer totalMinutos;
	private String numeroHorasTutorias;
	private Integer numeroHorasRestantes;
	private Integer numeroMinutosRestantes;
	private String numeroTotalHorasMinutosRestantes;

	private boolean aprobarTutoriasDentroFecha;

	private StreamedContent documento;

	@PostConstruct
	public void a() {

		procesos = procesoService.obtenerLista("select distinct p from Proceso p order by p.fechaInicio asc",
				new Object[] {}, 0, false, new Object[] {});

		procesoSeleccionado = "0";

		panelPrincipal = true;
		panelTutorias = false;
		panelInsertarTutoria = false;
		panelEditarTutoria = false;
		panelValidarEstudiante = false;

		aprobarTutoriasDentroFecha = false;
	}

	public void actualizar() {
		if (fecha == null || fechaFinTutoria == null || fechaProximaTutoria == null || fechaFinProximaTutoria == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese las fechas de tutoria");
		else if (estudianteExamenComplexivoPP.getTutorias() != null
				&& (tutoriaEC.getTareasRealizadas().compareToIgnoreCase("") == 0
						|| tutoriaEC.getTareasNoRealizadas().compareToIgnoreCase("") == 0
						|| tutoriaEC.getActividad().compareToIgnoreCase("") == 0
						|| tutoriaEC.getTarea().compareToIgnoreCase("") == 0))
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
			tutoriaEC.setFecha(UtilsDate.timestamp(fecha));
			tutoriaEC.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
			tutoriaEC.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
			tutoriaEC.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));
			tutoriaECService.actualizar(tutoriaEC);

		}

		onRowSelect();
		obtenerHorasTutorias();

		panelTutorias = true;
		panelEditarTutoria = false;

		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente la tutoría");

	}

	public void cargarCarreras() {
		try {

			carreras = carreraService.obtenerLista(
					"select distinct ca from Carrera ca inner join ca.estudiantesExamenComplexivoPP epp inner join epp.tutor t inner join epp.proceso p where t.email=?1 and p.id=?2",
					new Object[] { UtilSeguridad.obtenerUsuario(), procesoSeleccionado }, 0, false, new Object[] {});
		} catch (Exception ex) {
			ex.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + ex.getClass());
		}
	}

	public Carrera getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getDebilidad() {
		return debilidad;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public List<EstudianteExamenComplexivoPP> getEstudiantesExamenComplexivoPP() {
		return estudiantesExamenComplexivoPP;
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

	public Integer getTotalHoras() {
		return totalHoras;
	}

	public Integer getTotalMinutos() {
		return totalMinutos;
	}

	public TutoriaEC getTutoriaEC() {
		return tutoriaEC;
	}

	public void guardarValidacionEstudiante() {
		try {
			estudianteExamenComplexivoPP.setAprobado(true);
			estudianteExamenComplexivoPPService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET aprobado="
					+ estudianteExamenComplexivoPP.getAprobado() + " WHERE id=" + estudianteExamenComplexivoPP.getId()
					+ ";");
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El tutor ha aprobado las tutorías y la dimensión escrita del examen complexivo del estudiante.");

			String[] destinatarios = { estudianteExamenComplexivoPP.getEstudiante().getEmail(),
					estudianteExamenComplexivoPP.getEspecialista1().getEmail(),
					estudianteExamenComplexivoPP.getEspecialista2().getEmail(),
					estudianteExamenComplexivoPP.getEspecialista3().getEmail(),
					estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail() };

			String asunto = "Aprobación del trabajo escrito del estudiante "
					+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
					+ " de la modalidad examen complexivo - dimensión escrita.";
			String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la/el docente tutor/a "
					+ estudianteExamenComplexivoPP.getTutor().getApellidoNombre()
					+ " ha culminado con la fase de tutorías y consciente que el trabajo realizado cumple con las normas académicas establecidas, ha procedido a validarlo con la finalidad de continuar en el proceso de titulación de su tutoriza/o"
					+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
					+ "<br /></span></div><div dir='ltr'><span style='color:#000000;'>&nbsp;</span></div><div><span style='color:#000000;'>Por consiguiente el estudiante queda habilitado para que el documento escrito del google drive o impreso (opcional) sea evaluado por parte de los miembros del comité evaluador con respecto al URKUND (especialista1), CITAS PAPERS (especialista 2) Y VALIDACIÓN DEL DOCUMENTO (especialista 3).<br /> <br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
			List<File> listAdjunto = null;
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto, parametros);
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor del tipo: " + e.getClass());
		}
		onRowSelect();
		panelPrincipal = true;
		panelValidarEstudiante = false;
	}

	public void imprimir(TutoriaEC tutoEC) {
		tutoriaEC = tutoEC;
		List<TutoriaEC> tutoriasEC = new ArrayList<TutoriaEC>();
		tutoriasEC.add(tutoriaEC);
		if (tutoriasEC.isEmpty()) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "No existen datos para generar el reporte");
		} else {
			System.out.println("El id: " + tutoriaEC.getId());
			System.out.println("El error que se genera: " + tutoriaEC.getFecha() + " | "
					+ tutoriaEC.getFechaFinTutoria() + " | " + tutoriaEC.getFecha());

			Report report = new Report();
			report.setFormat("PDF");
			report.setPath(UtilsReport.serverPathReport);
			report.setName("EC_Tutoria");

			report.addParameter("NUMERO_TUTORIA",
					String.valueOf(estudianteExamenComplexivoPP.getTutorias().indexOf(tutoriaEC) + 1));
			report.addParameter("PROCESO", estudianteExamenComplexivoPP.getProceso().getId());
			report.addParameter("idTutoria", String.valueOf(tutoriaEC.getId()));

			documento = UtilsReport.ejecutarReporte(report);

		}
	}

	public void insertarTutoria() {
		if (fecha == null || fechaFinTutoria == null || fechaProximaTutoria == null || fechaFinProximaTutoria == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese las fechas de tutoria");
		else if (estudianteExamenComplexivoPP.getTutorias() != null
				&& (tutoriaEC.getTareasRealizadas().compareToIgnoreCase("") == 0
						|| tutoriaEC.getTareasNoRealizadas().compareToIgnoreCase("") == 0
						|| tutoriaEC.getActividad().compareToIgnoreCase("") == 0
						|| tutoriaEC.getTarea().compareToIgnoreCase("") == 0))
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
			if (estudianteExamenComplexivoPP.getTutorias() == null)
				estudianteExamenComplexivoPP.setTutorias(new ArrayList<TutoriaEC>());

			tutoriaEC.setEstudianteExamenComplexivoPP(estudianteExamenComplexivoPP);
			tutoriaEC.setFecha(UtilsDate.timestamp(fecha));
			tutoriaEC.setFechaFinTutoria(UtilsDate.timestamp(fechaFinTutoria));
			tutoriaEC.setFechaProximaTutoria(UtilsDate.timestamp(fechaProximaTutoria));
			tutoriaEC.setFechaFinProximaTutoria(UtilsDate.timestamp(fechaFinProximaTutoria));

			estudianteExamenComplexivoPP.getTutorias().add(tutoriaEC);

			tutoriaECService.insertar(tutoriaEC);

			// estudianteExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);

			fecha = null;
			fechaFinTutoria = null;
			fechaProximaTutoria = null;
			fechaFinProximaTutoria = null;

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
			tutoriaEC = new TutoriaEC();
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
		if (estudianteExamenComplexivoPP.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no ha brindado tutorías." + 0;
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
				System.out
						.println((i + 1) + " pasada, total horas: " + totalHoras + " y total minutos: " + totalMinutos);
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

		numeroTotalHorasMinutosRestantes = numeroHorasRestantes <= 0 && numeroMinutosRestantes <= 0
				? "Ha cumplido con las horas de tutorías establecidas."
				: numeroHorasRestantes + " horas y " + numeroMinutosRestantes + " minutos.";
	}

	public void onRowSelect() {
		estudiantesExamenComplexivoPP = estudianteExamenComplexivoPPService.obtenerLista(
				"select distinct epp from EstudianteExamenComplexivoPP epp left join fetch epp.tutorias tu "
						+ "inner join epp.estudiante e inner join epp.tutor t inner join epp.carrera ca inner join epp.proceso p where p.id=?1 and t.email=?2 and ca.id=?3 order by tu.fecha",
				new Object[] { procesoSeleccionado, UtilSeguridad.obtenerUsuario(), carrera.getId() }, 0, false,
				new Object[0]);

	}

	public void prepararEditar() {
		verificarDentroFechas();
		if (!aprobarTutoriasDentroFecha) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede ingresar tutorías porque no se encuentra dentro del período de fechas establecidas en la hoja de ruta.");
		} else {
			indexTutoria = estudianteExamenComplexivoPP.getTutorias().indexOf(tutoriaEC);
			System.out.println(indexTutoria);
			fecha = tutoriaEC.getFecha();
			fechaFinTutoria = tutoriaEC.getFechaFinTutoria();
			fechaProximaTutoria = tutoriaEC.getFechaProximaTutoria();
			fechaFinProximaTutoria = tutoriaEC.getFechaFinProximaTutoria();
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

	public void setCarrera(Carrera carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setDebilidad(String debilidad) {
		this.debilidad = debilidad;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudiantesExamenComplexivoPP(List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP) {
		this.estudiantesExamenComplexivoPP = estudiantesExamenComplexivoPP;
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

	public void setTotalHoras(Integer totalHoras) {
		this.totalHoras = totalHoras;
	}

	public void setTotalMinutos(Integer totalMinutos) {
		this.totalMinutos = totalMinutos;
	}

	public void setTutoriaEC(TutoriaEC tutoriaEC) {
		this.tutoriaEC = tutoriaEC;
	}

	public void validarEstudiante() throws IOException {
		if (estudianteExamenComplexivoPP.getEspecialista1() == null
				|| estudianteExamenComplexivoPP.getEspecialista2() == null
				|| estudianteExamenComplexivoPP.getEspecialista3() == null
				|| estudianteExamenComplexivoPP.getEspecialistaSuplente1() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta asignarle el comité evaluador.");
		} else if (estudianteExamenComplexivoPP.getTituloInvestigacion() == null
				|| estudianteExamenComplexivoPP.getTituloInvestigacion() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar el título de la investigación en la plataforma de titulación.");
		} else if (estudianteExamenComplexivoPP.getPalabrasClaves() == null
				|| estudianteExamenComplexivoPP.getPalabrasClaves() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar las palabras claves de la investigación en la plataforma de tituación.");
		} else if (estudianteExamenComplexivoPP.getResumen() == null
				|| estudianteExamenComplexivoPP.getResumen() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede validar al estudiante, falta ingresar el resumen de la investigación en la plataforma de titulación.");
			/*
			 * } else if (estudianteExamenComplexivoPP.getAntiplagio() == null ||
			 * estudianteExamenComplexivoPP.getCitas1() == null) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, falta ingresar el porcentaje del antiplagio o las citas bibliográficas."
			 * ); } else if (estudianteExamenComplexivoPP.getAntiplagio().compareTo(new
			 * BigDecimal("10")) > 0) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, ya que su porcentaje de urkund supera el 10%."
			 * ); } else if (estudianteExamenComplexivoPP.getCitas1().compareTo(new
			 * Integer(10)) < 0) {
			 * UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
			 * "No se puede validar al estudiante, ya que el número de citas es menor a 10."
			 * );
			 */} else {

			verificarDentroFechas();

			if (!aprobarTutoriasDentroFecha) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No puede aprobar al estudiante debido que excedió la fecha determinada en el cronograma.");
			} else {
				obtenerHorasTutorias();
				if (totalHoras >= 12) {
					panelPrincipal = false;
					panelValidarEstudiante = true;

				} else
					presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Aún no cumple con las horas necesarias (12 horas), el estudiante posee: " + totalHoras
									+ " horas.");
			}
		}
	}

	public void verificarDentroFechas() {
		if (estudiantesExamenComplexivoPP != null && estudiantesExamenComplexivoPP.size() >= 1) {

			listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
					new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[0]);

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
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
									estudiantesExamenComplexivoPP.get(0).getCarrera().getId() },
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
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='APRTUT' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
								estudiantesExamenComplexivoPP.get(0).getCarrera().getId() },
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
