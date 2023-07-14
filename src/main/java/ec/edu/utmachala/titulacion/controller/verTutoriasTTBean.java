package ec.edu.utmachala.titulacion.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Tutoria;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.TutoriaService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;;

@Controller
@Scope("session")
public class verTutoriasTTBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private TutoriaService tutoriaService;

	@Autowired
	private ParametroService parametroService;

	private List<EstudianteTrabajoTitulacion> listaTT;
	private Integer ttId;
	private EstudianteTrabajoTitulacion tt;

	private BigDecimal calificacionParteEscritaFinal;

	private BigDecimal calificacionParteOralFinal;

	private BigDecimal calificacionFinal;

	private boolean panelPrincipal;
	private boolean panelInsertarTutorias;

	private Tutoria tutoria;

	private int indexTutoria;

	private Date fecha;
	private Date fechaFinTutoria;
	private Date fechaProximaTutoria;
	private Date fechaFinProximaTutoria;

	private Integer totalHoras;
	private Integer totalMinutos;
	private String numeroHorasTutorias;
	private Integer numeroHorasRestantes;
	private Integer numeroMinutosRestantes;
	private String numeroTotalHorasMinutosRestantes;

	public verTutoriasTTBean() {
	}

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelInsertarTutorias = false;
		listaTT = estudianteTrabajoTitulacionService.obtenerLista(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.proceso p inner join tt.seminarioTrabajoTitulacion stt inner join stt.docenteSeminario ds inner join ds.docente d where e.email=?1 order by tt.id asc",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});
		// System.out.println("Número de datos: " + listaTT.size());
		if (listaTT != null && listaTT.size() >= 1) {
			// System.out.println("Proceso: " + listaTT.get(listaTT.size() -
			// 1).getProceso().getId());
			ttId = listaTT.get(listaTT.size() - 1).getId();
			calificaciones();
			obtenerHorasTutorias();
		} else
			tt = new EstudianteTrabajoTitulacion();
	}

	public void activarPanelPrincipal() {
		calificaciones();
		obtenerHorasTutorias();
		panelPrincipal = true;
		panelInsertarTutorias = false;
	}

	public void actualizar() {
		if (tutoria.getComentarioEstudiante() == null || tutoria.getComentarioEstudiante().compareToIgnoreCase("") == 0
				|| tutoria.getComentarioEstudiante().compareToIgnoreCase(" ") == 0
				|| tutoria.getComentarioEstudiante().compareToIgnoreCase("   ") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No deje espacios en blanco.");
		else {
			tutoriaService.actualizar(tutoria);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Se ha ingresado el comentario satisfactoriamente.");
			calificaciones();
			obtenerHorasTutorias();

			panelPrincipal = true;
			panelInsertarTutorias = false;
		}
	}

	void calificaciones() {
		tt = estudianteTrabajoTitulacionService.obtenerObjeto(
				"select tt from EstudianteTrabajoTitulacion tt left join fetch tt.tutorias t where tt.id=?1 order by t.fecha desc",
				new Object[] { ttId }, false, new Object[] {});
		// System.out.println("datos del tt " +
		// tt.getEstudiante().getApellidoNombre());
		String calificaciones = estudianteTrabajoTitulacionService.traerCalificaciones(tt);
		calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
	}

	public void enviarUrkund() throws IOException {
		if (tt.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getUsuarioUrkund() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Su tutor aún no posee una cuenta URKUND. Notifique esta novedad a su tutor para solucionar el inconveniente.");
		} else {

			// System.out.println("Empezo a llamar al
			// drive.exportarDocumento()");
			String nombreDocumento = EjemploDrive.exportarDocumento(tt.getEstudiante().getId(), tt.getUrlDrive(),
					tt.getProceso().getId(), "TT");
			// System.out.println("Continúo después del
			// drive.exportarDocumento()");
			java.io.File f = new java.io.File(
					UtilsArchivos.getRutaCuerpoDocumentoDriveTT(tt.getProceso().getId()) + nombreDocumento);

			// System.out.println((f.exists() ? "El archivo si existe." : "El
			// archivo no existe."));

			// System.out.println("Nombre documento: " + nombreDocumento);

			// System.out.println("El docente email es: "
			// +
			// tt.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getUsuarioUrkund());

			String destinatario = tt.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente()
					.getUsuarioUrkund();
			String destinatario1 = tt.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getEmail();
			String asunto = "DOCUMENTO PARA URKUND DEL ESTUDIANTE " + tt.getEstudiante().getApellidoNombre();
			String detalle = "<div dir='ltr'><span style='color: #000000;'>El documento adjunto pertenece a "
					+ tt.getEstudiante().getApellidoNombre() + " en el actual proceso de titulación.";
			List<File> listAdjunto = new ArrayList<File>();
			listAdjunto.add(f);
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			try {
				UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				UtilsMail.envioCorreo(destinatario1, asunto, detalle, listAdjunto, parametros);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Se ha enviado el documento al Urkund satisfactoriamente.");
				// System.out.println("Se ha enviado el documento al Urkund
				// satisfactoriamente.");
			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor: " + e.getClass());
				// System.out.println("Ingreso al error del método envío urkund,
				// con el correo electrónico.");
				e.printStackTrace();
			}
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

	public EstudianteTrabajoTitulacionService getEstudianteTrabajoTitulacionService() {
		return estudianteTrabajoTitulacionService;
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

	public int getIndexTutoria() {
		return indexTutoria;
	}

	public List<EstudianteTrabajoTitulacion> getListaTT() {
		return listaTT;
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

	public Integer getTotalHoras() {
		return totalHoras;
	}

	public Integer getTotalMinutos() {
		return totalMinutos;
	}

	public EstudianteTrabajoTitulacion getTt() {
		return tt;
	}

	public Integer getTtId() {
		return ttId;
	}

	public Tutoria getTutoria() {
		return tutoria;
	}

	public boolean ispanelInsertarTutorias() {
		return panelInsertarTutorias;
	}

	public boolean isPanelInsertarTutorias() {
		return panelInsertarTutorias;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public void obtenerHorasTutorias() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (tt.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "12 horas y 0 minutos.";
		} else {
			for (int i = 0; i < tt.getTutorias().size(); i++) {
				f1.setTime(tt.getTutorias().get(i).getFecha());
				f2.setTime(tt.getTutorias().get(i).getFechaFinTutoria());

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

	public void obtenerTT() {
		if (ttId != 0)
			calificaciones();
	}

	public void presentarInsertarComentario() {
		indexTutoria = tt.getTutorias().indexOf(tutoria);
		// System.out.println(indexTutoria);
		fecha = tutoria.getFecha();
		fechaFinTutoria = tutoria.getFechaFinTutoria();
		fechaProximaTutoria = tutoria.getFechaProximaTutoria();
		fechaFinProximaTutoria = tutoria.getFechaFinProximaTutoria();
		panelPrincipal = false;
		panelInsertarTutorias = true;
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

	public void setEstudianteTrabajoTitulacionService(
			EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService) {
		this.estudianteTrabajoTitulacionService = estudianteTrabajoTitulacionService;
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

	public void setIndexTutoria(int indexTutoria) {
		this.indexTutoria = indexTutoria;
	}

	public void setListaTT(List<EstudianteTrabajoTitulacion> listaTT) {
		this.listaTT = listaTT;
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

	public void setPanelInsertarTutorias(boolean panelInsertarTutorias) {
		this.panelInsertarTutorias = panelInsertarTutorias;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setTotalHoras(Integer totalHoras) {
		this.totalHoras = totalHoras;
	}

	public void setTotalMinutos(Integer totalMinutos) {
		this.totalMinutos = totalMinutos;
	}

	public void setTt(EstudianteTrabajoTitulacion tt) {
		this.tt = tt;
	}

	public void setTtId(Integer ttId) {
		this.ttId = ttId;
	}

	public void setTutoria(Tutoria tutoria) {
		this.tutoria = tutoria;
	}

}