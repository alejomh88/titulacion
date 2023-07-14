package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Tutoria;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DetalleEntidadGenericaService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SecuenciaActaCalificacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class GestionCoordinadorTTBean implements Serializable {
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
	private FechaProcesoService fechaProcesoService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	private List<FechaProceso> listadoFechasProcesos;

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

	private boolean comiteDentroFecha;

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
		especialista1 = new Usuario();
		especialista2 = new Usuario();
		especialista3 = new Usuario();
		especialistaSuplente1 = new Usuario();
		boolEspecialista1 = true;

		esUmmog = UtilSeguridad.obtenerRol("UMMO");

		comiteDentroFecha = false;

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
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or e.apellidoNombre like upper(?3)) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[] { "getTutorias" });

		if (listadoEstudiantesTrabajoTitulacion != null && listadoEstudiantesTrabajoTitulacion.size() >= 1) {

			listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='COMEVA' and fp.activo='true' order by fp.id asc",
					new Object[] { listadoEstudiantesTrabajoTitulacion.get(0).getProceso().getId() }, 0, false,
					new Object[0]);

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
				// existe fechas de procesos
				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
					comiteDentroFecha = true;
					System.out.println("General: ||| Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
							+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
							+ new Date() + " - valor bool: " + comiteDentroFecha);
				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='COMEVA' and fp.activo='true' order by fp.id asc",
							new Object[] { listadoEstudiantesTrabajoTitulacion.get(0).getProceso().getId(),
									listadoEstudiantesTrabajoTitulacion.get(0).getCarrera().getId() },
							0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {
							comiteDentroFecha = true;
						} else {
							comiteDentroFecha = false;
						}
						System.out.println("Carrera ||| Fecha inicio: " + listadoFechasProcesos.get(0).getFechaInicio()
								+ " - fecha fin: " + listadoFechasProcesos.get(0).getFechaFinal() + " - fecha actual: "
								+ new Date() + " - valor bool: " + comiteDentroFecha);
					} else
						comiteDentroFecha = false;

				}
			} else
				comiteDentroFecha = false;
		}
	}

	public void buscarEspecialista() {
		especialistas = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where (u.id=?1 or lower(u.apellidoNombre) like lower(?1)) and "
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

	public void elegirSuplantado() {
		if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista1().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista1);
		else if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista2().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista2);
		else if (especialistaSuplantadoInterfazE.equals(estudianteTrabajoTitulacion.getEspecialista3().getId()))
			estudianteTrabajoTitulacion.setEspecialistaSuplantadoE(especialista3);
	}

	public void eliminarNotaEscrita1() {
		estudianteTrabajoTitulacion.setEe1(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscrita2() {
		estudianteTrabajoTitulacion.setEe2(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscrita3() {
		estudianteTrabajoTitulacion.setEe3(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaEscritaSuplente1() {
		estudianteTrabajoTitulacion.setEs1(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral1() {
		estudianteTrabajoTitulacion.setOe1(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral2() {
		estudianteTrabajoTitulacion.setOe2(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOral3() {
		estudianteTrabajoTitulacion.setOe3(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void eliminarNotaOralSuplente1() {
		estudianteTrabajoTitulacion.setOs1(null);
		estudiantesTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTutorias = false;
		panelTrabajoEscrito = false;
		panelTutorias = false;
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

	public String getUrlDocumento() {
		return urlDocumento;
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
						if (estudianteTrabajoTitulacion.getOe1() == null) {
							estudianteTrabajoTitulacion.setActivarSuplenteO(true);
							estudianteTrabajoTitulacion.setEspecialistaSuplantadoO(especialista2);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación en la sustentación del estudiante.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteTrabajoTitulacion.getEspecialista3().getId())) {
						if (estudianteTrabajoTitulacion.getOe1() == null) {
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
							.setEspecialistaSuplantadoE(estudianteTrabajoTitulacion2.getEspecialistaSuplantadoE());
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

				panelPrincipal = true;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;
				panelTrabajoEscrito = false;
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

	public void imprimir(Tutoria tuto) {
		Tutoria tutoria = tuto;
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

	public void obtenerCarreras() {
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "inner join c.estudiantesTrabajosTitulaciones tt inner join tt.proceso p "
						+ "where u.email=?1 and p.id=?2 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						proceso },
				0, false, new Object[] {});
	}

	public void presentarDashboardEstudiante() {

		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = true;
		panelComite = false;
		panelTrabajoEscrito = false;

		String calificaciones = estudiantesTrabajoTitulacionService.traerCalificaciones(estudianteTrabajoTitulacion);
		calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);

	}

	public void presentarTutoriasEstudiante() {
		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTutorias = true;
		panelTrabajoEscrito = false;

	}

	public void seleccionarComiteEvaluador() {

		if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null
				|| estudianteTrabajoTitulacion.getOpcionTitulacion() == null) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede asignar a los evaluadores por qué el estudiante no tiene el seminario o la opción de titulación.");
		} else if (estudianteTrabajoTitulacion.getEspecialista1() != null
				&& estudianteTrabajoTitulacion.getEspecialista2() != null
				&& estudianteTrabajoTitulacion.getEspecialista3() != null
				&& estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Comite evaluador ya asignado, si desea realizar cambios comunicarse con el/la jefe/a de UMMOG.");
		} else if (!comiteDentroFecha) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede asignar comité evaluador debido a que esta fuera del rango de fechas establecidas en la hoja de ruta.");
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

			criterioBusquedaEspecialista = "";
			panelPrincipal = false;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = true;
			panelTrabajoEscrito = false;

			// }
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

	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}

	public void setValidacionArchivo(boolean validacionArchivo) {
		this.validacionArchivo = validacionArchivo;
	}

}
