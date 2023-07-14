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
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.TutoriaEC;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DetalleEntidadGenericaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.GrupoService;
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
public class GestionCoordinadorECBean implements Serializable {
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

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<FechaProceso> listadoFechasProcesos;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private boolean panelPrincipal;
	private boolean panelSeleccionar;
	private boolean panelDashboard;
	private boolean panelComite;
	private boolean panelTutorias;
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

		esUmmog = UtilSeguridad.obtenerRol("UMMO");

		comiteDentroFecha = false;
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
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or e.apellidoNombre like upper(?3)) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[] { "getTutorias" });

		if (listadoEstudiantesExamenComplexivoPP != null && listadoEstudiantesExamenComplexivoPP.size() >= 1) {

			listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='COMEVA' and fp.activo='true' order by fp.id asc",
					new Object[] { listadoEstudiantesExamenComplexivoPP.get(0).getProceso().getId() }, 0, false,
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
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='COMEVA' and fp.activo='true' order by fp.id asc",
							new Object[] { listadoEstudiantesExamenComplexivoPP.get(0).getProceso().getId(),
									listadoEstudiantesExamenComplexivoPP.get(0).getCarrera().getId() },
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
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='COMEVA' and fp.activo='true' order by fp.id asc",
						new Object[] { listadoEstudiantesExamenComplexivoPP.get(0).getProceso().getId(),
								listadoEstudiantesExamenComplexivoPP.get(0).getCarrera().getId() },
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

	public void eliminarNotaEscrita1() {
		estudianteExamenComplexivoPP.setEe1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscrita2() {
		estudianteExamenComplexivoPP.setEe2(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscrita3() {
		estudianteExamenComplexivoPP.setEe3(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaEscritaSuplente1() {
		estudianteExamenComplexivoPP.setEs1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia1() {
		estudianteExamenComplexivoPP.setOge1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia2() {
		estudianteExamenComplexivoPP.setOge2(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGracia3() {
		estudianteExamenComplexivoPP.setOge3(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralGraciaSuplente1() {
		estudianteExamenComplexivoPP.setOgs1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario1() {
		estudianteExamenComplexivoPP.setOoe1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario2() {
		estudianteExamenComplexivoPP.setOoe2(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinario3() {
		estudianteExamenComplexivoPP.setOoe3(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void eliminarNotaOralOrdinarioSuplente1() {
		estudianteExamenComplexivoPP.setOos1(null);
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
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

	public Usuario getTutor() {
		return tutor;
	}

	public String getUrlDocumento() {
		return urlDocumento;
	}

	public void guardarComite() {
		try {
			if (tutor.getId() == null || especialista1.getId() == null || especialista2.getId() == null
					|| especialista3.getId() == null || especialistaSuplente1.getId() == null)
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Falta de seleccionar el tutor o algún especialista del comité evaluador");
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
				panelPrincipal = true;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;

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
							+ tutor.getApellidoNombre() + " - (" + tutor.getId() + ")<br /><b>Especialista 1: </b> "
							+ especialista1.getApellidoNombre() + " - (" + especialista1.getId()
							+ ")<br /><b>Especialista 2: </b> " + especialista2.getApellidoNombre() + " - ("
							+ especialista2.getId() + ") <br /><b>Especialista 3: </b> "
							+ especialista3.getApellidoNombre() + " - (" + especialista3.getId()
							+ ")<br /><b>Especialista Suplente: </b> " + especialistaSuplente1.getApellidoNombre()
							+ " - (" + especialistaSuplente1.getId()
							+ ") <br />En la plataforma podrá encontrar dicha información, específicamente en <b>Dashboard estudiante</b>. Los especialistas podrán observar a quienes evaluarán en el menú Evaluador y se escoje examen complexivo o trabajo de titulación</span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
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
						if (estudianteExamenComplexivoPP.getEe1() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(especialista1);
							estudianteExamenComplexivoPP.setActivarSuplenteO(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista1.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteExamenComplexivoPP.getEspecialista2().getId())) {
						if (estudianteExamenComplexivoPP.getEe2() == null) {
							estudianteExamenComplexivoPP.setEspecialistaSuplantadoO(especialista2);
							estudianteExamenComplexivoPP.setActivarSuplenteO(true);
						} else
							presentaMensaje(FacesMessage.SEVERITY_INFO, "El especialista "
									+ especialista2.getApellidoNombre()
									+ ", no puede ser reemplazado por que ya ha asignadado una calificación del trabajo escrito del exámen complexivo.");
					} else if (especialistaSuplantadoInterfazO
							.equals(estudianteExamenComplexivoPP.getEspecialista3().getId())) {
						if (estudianteExamenComplexivoPP.getEe3() == null) {
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
				presentaMensaje(FacesMessage.SEVERITY_INFO, "El comité evaluador fue asignado de manera correcta");
				buscar();

				try {
					System.out.println("entro en el crear archivo.");
					estudiantesExamenComplexivoPPService.creacionDocumentoDrive(estudianteExamenComplexivoPP);
				} catch (Exception e) {
					e.printStackTrace();
					presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
				}

				panelPrincipal = true;
				panelSeleccionar = false;
				panelDashboard = false;
				panelComite = false;

			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Erorr en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}
	}

	public void habilitarSuplenteE() {
		System.out.println("Entro");
	}

	public void habilitarSuplenteO() {
		System.out.println("Entro");
	}

	public void imprimir(TutoriaEC tutoEC) {
		TutoriaEC tutoriaEC;
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

	public void insertarFecha() {
		System.out.println(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria());
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Fecha de sustentación ordinaria guardada correctamente");
	}

	public void insertarFechaGracia() {
		System.out.println(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria());
		estudiantesExamenComplexivoPPService.actualizar(estudianteExamenComplexivoPP);
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Fecha de sustentación de gracia guardada correctamente");
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

	public void presentarTutoriasEstudiante() {
		panelPrincipal = false;
		panelSeleccionar = false;
		panelDashboard = false;
		panelComite = false;
		panelTutorias = true;
		panelTrabajoEscrito = false;
	}

	public void seleccionarComiteEvaluador() {

		if (estudianteExamenComplexivoPP.getTutor() != null && estudianteExamenComplexivoPP.getEspecialista1() != null
				&& estudianteExamenComplexivoPP.getEspecialista2() != null
				&& estudianteExamenComplexivoPP.getEspecialista3() != null
				&& estudianteExamenComplexivoPP.getEspecialistaSuplente1() != null) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Comite evaluador ya asignado, si desea realizar cambios comunicarse con el/la jefe/a de UMMOG.");
		} else if (!comiteDentroFecha) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede asignar comité evaluador debido a que esta fuera del rango de fechas establecidas en la hoja de ruta.");
		} else {
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
						new Object[] { estudianteExamenComplexivoPP.getEspecialista1().getId() }, false,
						new Object[] {});
			}
			if (estudianteExamenComplexivoPP.getEspecialista2() != null) {
				especialista2 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteExamenComplexivoPP.getEspecialista2().getId() }, false,
						new Object[] {});
			}
			if (estudianteExamenComplexivoPP.getEspecialista3() != null) {
				especialista3 = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
						new Object[] { estudianteExamenComplexivoPP.getEspecialista3().getId() }, false,
						new Object[] {});
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

			criterioBusquedaEspecialista = "";
			buscarEspecialista();

			boolTutor = true;
			boolEspecialista1 = false;
			boolEspecialista2 = false;
			boolEspecialista3 = false;
			boolEspecialistaSuplente1 = false;
			panelPrincipal = false;
			panelSeleccionar = false;
			panelDashboard = false;
			panelComite = true;
		}
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

	public void setTutor(Usuario tutor) {
		this.tutor = tutor;
	}

	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}

	public void setValidacionArchivo(boolean validacionArchivo) {
		this.validacionArchivo = validacionArchivo;
	}

}
