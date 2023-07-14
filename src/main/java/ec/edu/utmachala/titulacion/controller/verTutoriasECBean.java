package ec.edu.utmachala.titulacion.controller;

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
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.TutoriaEC;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.TutoriaECService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMail;;

@Controller
@Scope("session")
public class verTutoriasECBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoService;

	@Autowired
	private TutoriaECService tutoriaECService;

	@Autowired
	private ParametroService parametroService;

	private List<EstudianteExamenComplexivoPP> listaPP;
	private Integer ppId;
	private EstudianteExamenComplexivoPP pp;

	private boolean panelPrincipal;
	private boolean panelInsertarTutorias;

	private TutoriaEC tutoriaEC;

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

	public verTutoriasECBean() {
	}

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelInsertarTutorias = false;
		listaPP = estudianteExamenComplexivoService.obtenerLista(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e inner join pp.proceso p where e.email=?1 order by p.fechaInicio asc",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});
		System.out.println("numero de participaciones: " + listaPP.size());
		if (listaPP != null && listaPP.size() >= 1) {
			ppId = listaPP.get(listaPP.size() - 1).getId();
			calificaciones();
			obtenerHorasTutorias();
		} else
			pp = new EstudianteExamenComplexivoPP();
	}

	public void activarPanelPrincipal() {
		calificaciones();
		obtenerHorasTutorias();
		panelPrincipal = true;
		panelInsertarTutorias = false;
	}

	public void actualizar() {
		if (tutoriaEC.getComentarioEstudiante() == null
				|| tutoriaEC.getComentarioEstudiante().compareToIgnoreCase("") == 0
				|| tutoriaEC.getComentarioEstudiante().compareToIgnoreCase(" ") == 0
				|| tutoriaEC.getComentarioEstudiante().compareToIgnoreCase("   ") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No deje espacios en blanco.");
		else {
			tutoriaECService.actualizar(tutoriaEC);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Se ha ingresado el comentario satisfactoriamente.");
			calificaciones();
			presentarInsertarComentario();

			panelPrincipal = true;
			panelInsertarTutorias = false;
		}
	}

	void calificaciones() {
		pp = estudianteExamenComplexivoService.obtenerObjeto(
				"select pp from EstudianteExamenComplexivoPP pp left join fetch pp.tutorias t where pp.id=?1 order by t.fecha desc",
				new Object[] { ppId }, false, new Object[] {});
		System.out.println("datos del tt " + pp.getEstudiante().getApellidoNombre());
	}

	public void enviarUrkund() throws IOException {
		if (pp.getTutor().getUsuarioUrkund() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Su tutor aún no posee una cuenta URKUND. Notifique esta novedad a su tutor para solucionar el inconveniente.");
		} else {

			System.out.println("Empezo a llamar al drive.exportarDocumento()");
			String nombreDocumento = EjemploDrive.exportarDocumento(pp.getEstudiante().getId(), pp.getUrlDrive(),
					pp.getProceso().getId(), "EC");
			System.out.println("Continúo después del drive.exportarDocumento()");
			java.io.File f = new java.io.File(
					UtilsArchivos.getRutaCuerpoDocumentoDriveEC(pp.getProceso().getId()) + nombreDocumento);

			System.out.println((f.exists() ? "El archivo si existe." : "El archivo no existe."));

			System.out.println("Nombre documento: " + nombreDocumento);

			System.out.println("El docente email es: " + pp.getTutor().getUsuarioUrkund());

			String destinatario = pp.getTutor().getUsuarioUrkund();
			String destinatario1 = pp.getTutor().getEmail();
			String asunto = "DOCUMENTO PARA URKUND DEL ESTUDIANTE " + pp.getEstudiante().getApellidoNombre();
			String detalle = "<div dir='ltr'><span style='color: #000000;'>El documento adjunto pertenece a "
					+ pp.getEstudiante().getApellidoNombre() + " en el actual proceso de titulación.";
			List<File> listAdjunto = new ArrayList<File>();
			listAdjunto.add(f);
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			try {
				UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				UtilsMail.envioCorreo(destinatario1, asunto, detalle, listAdjunto, parametros);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Se ha enviado el documento al Urkund satisfactoriamente.");
				System.out.println("Se ha enviado el documento al Urkund satisfactoriamente.");
			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor: " + e.getClass());
				System.out.println("Ingreso al error del método envío urkund, con el correo electrónico.");
				e.printStackTrace();
			}
		}
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

	public List<EstudianteExamenComplexivoPP> getlistaPP() {
		return listaPP;
	}

	public List<EstudianteExamenComplexivoPP> getListaPP() {
		return listaPP;
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

	public EstudianteExamenComplexivoPP getPp() {
		return pp;
	}

	public Integer getppId() {
		return ppId;
	}

	public Integer getPpId() {
		return ppId;
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
			System.out.println("Indica retorno de valor.");
		} else {
			System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public void obtenerHorasTutorias() {
		totalHoras = 0;
		totalMinutos = 0;
		Calendar f1 = Calendar.getInstance();
		Calendar f2 = Calendar.getInstance();
		if (pp.getTutorias().size() == 0) {
			numeroHorasTutorias = "Aún no han brindado tutorías.";
			numeroTotalHorasMinutosRestantes = "12 horas y 0 minutos.";
		} else {
			System.out.println("número de tutorias: " + pp.getTutorias().size());
			for (int i = 0; i < pp.getTutorias().size(); i++) {
				f1.setTime(pp.getTutorias().get(i).getFecha());
				f2.setTime(pp.getTutorias().get(i).getFechaFinTutoria());

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

	public void obtenerTT() {
		if (ppId != 0)
			calificaciones();
	}

	public void presentarInsertarComentario() {
		indexTutoria = pp.getTutorias().indexOf(tutoriaEC);
		System.out.println(indexTutoria);
		fecha = tutoriaEC.getFecha();
		fechaFinTutoria = tutoriaEC.getFechaFinTutoria();
		fechaProximaTutoria = tutoriaEC.getFechaProximaTutoria();
		fechaFinProximaTutoria = tutoriaEC.getFechaFinProximaTutoria();
		panelPrincipal = false;
		panelInsertarTutorias = true;
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

	public void setlistaPP(List<EstudianteExamenComplexivoPP> listaPP) {
		this.listaPP = listaPP;
	}

	public void setListaPP(List<EstudianteExamenComplexivoPP> listaPP) {
		this.listaPP = listaPP;
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

	public void setPp(EstudianteExamenComplexivoPP pp) {
		this.pp = pp;
	}

	public void setppId(Integer ppId) {
		this.ppId = ppId;
	}

	public void setPpId(Integer ppId) {
		this.ppId = ppId;
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

}