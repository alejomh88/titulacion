package ec.edu.utmachala.titulacion.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Asignatura;
import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.CarreraMallaProceso;
import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.MallaCurricular;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadCurricular;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CarreraMallaProcesoAgrupado;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.CarreraMallaProcesoAgrupadoService;
import ec.edu.utmachala.titulacion.service.CarreraMallaProcesoAuxService;
import ec.edu.utmachala.titulacion.service.CarreraMallaProcesoService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DocenteAsignaturaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.MallaCurricularService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.PeriodoReactivoService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UnidadCurricularService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMail;

@Controller
@Scope("session")
public class AdministrarAsignaturaBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private AsignaturaService asignaturaService;

	@Autowired
	private UnidadCurricularService unidadCurricularService;

	@Autowired
	private CarreraMallaProcesoService carreraMallaProcesoService;

	@Autowired
	private MallaCurricularService mallaCurricularService;

	@Autowired
	private CarreraMallaProcesoAuxService carreraMallaProcesoAuxService;

	@Autowired
	private CarreraMallaProcesoAgrupadoService carreraMallaProcesoAgrupadoService;

	@Autowired
	private DocenteAsignaturaService docenteAsignaturaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PeriodoReactivoService periodoReactivoService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private ProcesoService procesoService;

	private List<CarreraMallaProcesoAgrupado> listCMPA;

	private List<Usuario> listadoDocentes;
	private Usuario docente;
	private Usuario docenteSeleccionado;

	private List<CarreraMallaProceso> listCarreraMallaProceso;
	private CarreraMallaProceso carreraMallaProceso;

	// private CarreraMallaProcesoAux carreraMallaProcesoAux;
	private CarreraMallaProcesoAgrupado carreraMallaProcesoAgrupado;

	private List<MallaCurricular> listMallaCurricular;
	private MallaCurricular mallaCurricular;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int estado;

	private List<Asignatura> listAsignatura;
	private Asignatura asignatura;

	private boolean panelPrincipal;
	private boolean panelDocentes;
	private boolean panelInsertarDocentes;
	private boolean panelEliminarDocentes;

	private List<DocenteAsignatura> listDocentesAsignaturas;
	private DocenteAsignatura docenteAsignatura;

	private List<UnidadCurricular> listUnidadCurricular;

	private String criterioBusquedaDocente;

	@PostConstruct
	public void a() {

		panelPrincipal = true;
		panelDocentes = false;
		panelInsertarDocentes = false;
		panelEliminarDocentes = false;

		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();

		listCMPA = carreraMallaProcesoAgrupadoService.obtenerPorSql(
				"select distinct mc.id as id, (select string_agg(distinct cs.nombre||' - '||cs.id, ', ') from carreras cs "
						+ "inner join \"carrerasMallasProcesos\" cmps on (cmps.carrera=cs.id) inner join \"mallasCurriculares\" mcs on (mcs.id=cmps.\"mallaCurricular\") "
						+ "where mcs.id=mc.id) as carrera, mc.id as malla, (select string_agg(distinct ps.id||' ('||ps.observacion||')', ', ') "
						+ "from procesos ps inner join \"carrerasMallasProcesos\" cmps on (cmps.proceso=ps.id) inner join \"mallasCurriculares\" mcs on (mcs.id=cmps.\"mallaCurricular\") "
						+ "where mcs.id=mc.id) as proceso from \"mallasCurriculares\" mc inner join \"carrerasMallasProcesos\" cmp on (cmp.\"mallaCurricular\"=mc.id) inner join carreras c on (c.id=cmp.carrera) inner join \"permisosCarreras\" pc on (pc.carrera=c.id) inner join usuarios u on (pc.usuario=u.id) where u.email='"
						+ email
						+ "' and (mc.id>40 or mc.id=14 or mc.id=10 or mc.id=12 or mc.id=25 or mc.id=30) and mc.id<>54 order by carrera, malla;",
				CarreraMallaProcesoAgrupado.class);

		listCarreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, 0, false, new Object[0]);

		listUnidadCurricular = unidadCurricularService.obtenerLista(
				"select distinct uc from UnidadCurricular uc where uc.activo=true order by uc.id", new Object[] {}, 0,
				false, new Object[0]);
		if (listCarreras.size() == 1) {
			carrera = listCarreras.get(0).getId();
		}

		panelPrincipal = true;
		panelDocentes = false;
		panelInsertarDocentes = false;
		panelEliminarDocentes = false;
	}

	public void actualizar() {
		asignatura.setNombre(asignatura.getNombre().toUpperCase());
		if (asignatura.getNombre().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un nombre");
                else if (asignatura.getUnidadCurricular().getId().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese una unidad curricular");
		else {
			asignaturaService.actualizar(asignatura);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente la asignatura",
					"cerrar", true);
			onRowSelect();
		}
	}

	public void buscarDocente() {
		if (criterioBusquedaDocente.isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No deje el campo vacío para buscar el docente.");
		} else if (criterioBusquedaDocente.length() <= 2) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese más de dos letras para buscar al docente.");
		} else {
			listadoDocentes = usuarioService.obtenerLista(
					"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
							+ "where (u.id like ?1 or translate(lower(u.apellidoNombre), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate(lower(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) and "
							+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU' or r.id='DOTE') order by u.apellidoNombre",
					new Object[] { "%" + criterioBusquedaDocente + "%" }, 0, false, new Object[] {});
		}
	}

	public void desactivarDocenteAsignatura() {
		if (docenteAsignatura.getActivo() == null || docenteAsignatura.getActivo() == false)
			docenteAsignatura.setActivo(true);
		else
			docenteAsignatura.setActivo(false);

		docenteAsignaturaService.actualizar(docenteAsignatura);
	}

	public void eliminar() {
		if (asignatura.getActivo())
			asignatura.setActivo(false);
		else
			asignatura.setActivo(true);
		asignaturaService.actualizar(asignatura);
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
				(asignatura.getActivo() == true ? "Se activo" : "Se desactivo") + " correctamente la asignatura",
				"cerrar", true);
		onRowSelect();
	}

	public void eliminarDocente() {
		List<EstudianteExamenComplexivoPP> listadoEPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.docenteAsignatura da where da.id=?1",
				new Object[] { docenteAsignatura.getId() }, 0, false, new Object[] {});

		if (listadoEPP != null && listadoEPP.size() >= 1) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede eliminar el docente porque ha realizado reactivos, en tal caso debe desactivarlo.");

		} else {
			docenteAsignaturaService.eliminar(docenteAsignatura);
			docenteAsignatura = new DocenteAsignatura();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Docente eliminado de la asignatura satisfactoriamente.");
		}

		obtenerDocentesAsignaturas();

		panelDocentes = true;
		panelEliminarDocentes = false;

	}

	public void eliminarDocenteAsignatura() {
		panelPrincipal = false;
		panelDocentes = false;
		panelInsertarDocentes = false;
		panelEliminarDocentes = true;
	}

	public Asignatura getAsignatura() {
		return asignatura;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public CarreraMallaProceso getCarreraMallaProceso() {
		return carreraMallaProceso;
	}

	public CarreraMallaProcesoAgrupado getCarreraMallaProcesoAgrupado() {
		return carreraMallaProcesoAgrupado;
	}

	public CarreraMallaProcesoService getCarreraMallaProcesoService() {
		return carreraMallaProcesoService;
	}

	public String getCriterioBusquedaDocente() {
		return criterioBusquedaDocente;
	}

	public Usuario getDocente() {
		return docente;
	}

	public DocenteAsignatura getDocenteAsignatura() {
		return docenteAsignatura;
	}

	public Usuario getDocenteSeleccionado() {
		return docenteSeleccionado;
	}

	public int getEstado() {
		return estado;
	}

	public List<Usuario> getListadoDocentes() {
		return listadoDocentes;
	}

	public List<Asignatura> getListAsignatura() {
		return listAsignatura;
	}

	public List<CarreraMallaProceso> getListCarreraMallaProceso() {
		return listCarreraMallaProceso;
	}

	public List<Carrera> getListCarreras() {
		return listCarreras;
	}

	public List<CarreraMallaProcesoAgrupado> getListCMPA() {
		return listCMPA;
	}

	public List<DocenteAsignatura> getListDocentesAsignaturas() {
		return listDocentesAsignaturas;
	}

	public List<MallaCurricular> getListMallaCurricular() {
		return listMallaCurricular;
	}

	public List<UnidadCurricular> getListUnidadCurricular() {
		return listUnidadCurricular;
	}

	public MallaCurricular getMallaCurricular() {
		return mallaCurricular;
	}

	public void guardarDocente() {
		if (docente.getId() == "") {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No ha seleccionado un docente.");
		} else {
			try {
				docenteAsignatura = new DocenteAsignatura();
				docenteAsignatura.setDocente(docente);
				docenteAsignatura.setAsignatura(asignatura);

				carrera = Integer.parseInt(
						carreraMallaProcesoAgrupado.getCarrera().split(",")[0].split("-")[1].replace(" ", ""));
				Carrera c = carreraService.obtenerObjeto("select c from Carrera c where c.id=?1",
						new Object[] { carrera }, false, new Object[] {});
				// System.out.println(carrera);
				// System.out.println(pr.getId());
				// docenteAsignatura.setPeriodoReactivo(pr);
				docenteAsignatura.setActivo(true);
				docenteAsignaturaService.insertar(docenteAsignatura);

				List<Proceso> procesos = procesoService.obtenerLista(
						"select p from Proceso p inner join p.carreraMallaProceso cmp inner join cmp.mallaCurricular mc where mc.id=?1 order by p.fechaInicio ",
						new Object[] { carreraMallaProcesoAgrupado.getMalla() }, 0, false, new Object[] {});

				String asunto = "NOTIFICACIÓN SOBRE REACTIVOS TEÓRICOS Y PRÁCTICOS";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) docente.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que usted <strong>("
						+ docente.getId() + ") " + docente.getApellidoNombre()
						+ "</strong> ha sido asignado(a) a la asignatura de <strong>" + asignatura.getNombre()
						+ "</strong> de la carrera <strong>" + c.getNombre()
						+ "</strong> dentro de la plataforma de titulación. Dicha acción le permite a usted poder ingresar los reactivos teóricos y prácticos para el nuevo proceso "
						+ (procesos.get(procesos.size() - 1).getId() + " ("
								+ procesos.get(procesos.size() - 1).getObservacion() + ")")
						+ ".</div><br /><div><span style='color: #000000;'>&nbsp;</span></div><div>Se solicita realizar el respectivo ingreso de los reactivos teóricos y práticos a la plataforma de titulación en el ítem de menú <strong>Reactivos</strong>.</div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();

				Map<String, String> parametros = parametroService.traerMap(parametro);

				UtilsMail.envioCorreo(docente.getEmail(), asunto, detalle, listAdjunto, parametros);

				panelDocentes = true;
				panelInsertarDocentes = false;

				obtenerDocentesAsignaturas();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El docente ha sido asignado a la asignatura correctamente.");
			} catch (Exception e) {
				e.printStackTrace();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
			}
		}
	}

	public void insertar() {
		asignatura.setNombre(asignatura.getNombre().toUpperCase());
		// Asignatura asig = asignaturaService.obtenerObjeto(
		// "select a from Asignatura a inner join a.mallaCurricular mc where
		// a.nombre like ?1 and mc.id=?2 order by a.nombre",
		// new Object[] { "%" + asignatura.getNombre() + "%",
		// carreraMallaProcesoAgrupado.getMalla() }, false,
		// new Object[0]);
		if (asignatura.getNombre().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un nombre de la asignatura");
		// else if (asig != null && asig.getId() != null)
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ya
		// existe la asignatura");
		else if (asignatura.getUnidadCurricular().getId().compareTo("0") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoger la unidad curricular.");
			onRowSelect();
		} else {
			MallaCurricular mc = mallaCurricularService.obtenerObjeto(
					"select mc from MallaCurricular mc where mc.id=?1",
					new Object[] { carreraMallaProcesoAgrupado.getMalla() }, false, new Object[0]);
			asignatura.setMallaCurricular(mc);
			asignaturaService.insertar(asignatura);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto la asignatura correctamente", "cerrar",
					true);
			onRowSelect();
		}
	}

	public void insertarDocenteAsignatura() {
		panelPrincipal = false;
		panelDocentes = false;
		panelInsertarDocentes = true;
		panelEliminarDocentes = false;

		criterioBusquedaDocente = "";
		docente = new Usuario();
		buscarDocente();
	}

	public boolean isPanelDocentes() {
		return panelDocentes;
	}

	public boolean isPanelEliminarDocentes() {
		return panelEliminarDocentes;
	}

	public boolean isPanelInsertarDocentes() {
		return panelInsertarDocentes;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void limpiar() {
		try {
			asignatura = new Asignatura();
			asignatura.setUnidadCurricular(new UnidadCurricular());
			UtilsAplicacion.enviarVariableVista("abrir", true);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja una carrera con el nuevo proceso.");
		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void limpiarBean(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			System.out.println("Indica retorno de valor.");
		} else {
			System.out.println("He ingresado desde un método get.");
			// if (listCMPA != null)
			// listCMPA.clear();
			// if (listAsignatura != null)
			// listAsignatura.clear();
			// if (listCarreras != null)
			// listCarreras.clear();
			// if (listUnidadCurricular != null)
			// listUnidadCurricular.clear();
			// a();
		}
	}

	public void obtenerDocentesAsignaturas() {
		// System.out.println(asignatura.getNombre());
		listDocentesAsignaturas = docenteAsignaturaService.obtenerLista(
				"select da from DocenteAsignatura da inner join da.asignatura a inner join da.docente d where a.id=?1",
				new Object[] { asignatura.getId() }, 0, false, new Object[0]);
	}

	public void onRowSelect() {
		listAsignatura = asignaturaService.obtenerPorSql(
				"select a.* from asignaturas a inner join \"mallasCurriculares\" mc on (a.\"mallaCurricular\"=mc.id) where mc.id= "
						+ carreraMallaProcesoAgrupado.getMalla() + " order by a.nombre;",
				Asignatura.class);
	}

	public void presentarListadoDocente() {
		obtenerDocentesAsignaturas();

		panelPrincipal = false;
		panelDocentes = true;
		panelInsertarDocentes = false;
		panelEliminarDocentes = false;
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("administrarAsignatura.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void seleccionarDocente() {
		docente = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
				new Object[] { docenteSeleccionado.getId() }, false, new Object[] {});
	}

	public void setAsignatura(Asignatura asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreraMallaProceso(CarreraMallaProceso carreraMallaProceso) {
		this.carreraMallaProceso = carreraMallaProceso;
	}

	public void setCarreraMallaProcesoAgrupado(CarreraMallaProcesoAgrupado carreraMallaProcesoAgrupado) {
		this.carreraMallaProcesoAgrupado = carreraMallaProcesoAgrupado;
	}

	public void setCarreraMallaProcesoService(CarreraMallaProcesoService carreraMallaProcesoService) {
		this.carreraMallaProcesoService = carreraMallaProcesoService;
	}

	public void setCriterioBusquedaDocente(String criterioBusquedaDocente) {
		this.criterioBusquedaDocente = criterioBusquedaDocente;
	}

	public void setDocente(Usuario docente) {
		this.docente = docente;
	}

	public void setDocenteAsignatura(DocenteAsignatura docenteAsignatura) {
		this.docenteAsignatura = docenteAsignatura;
	}

	public void setDocenteSeleccionado(Usuario docenteSeleccionado) {
		this.docenteSeleccionado = docenteSeleccionado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public void setListadoDocentes(List<Usuario> listadoDocentes) {
		this.listadoDocentes = listadoDocentes;
	}

	public void setListAsignatura(List<Asignatura> listAsignatura) {
		this.listAsignatura = listAsignatura;
	}

	public void setListCarreraMallaProceso(List<CarreraMallaProceso> listCarreraMallaProceso) {
		this.listCarreraMallaProceso = listCarreraMallaProceso;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListCMPA(List<CarreraMallaProcesoAgrupado> listCMPA) {
		this.listCMPA = listCMPA;
	}
	//
	// public CarreraMallaProcesoAux getCarreraMallaProcesoAux() {
	// return carreraMallaProcesoAux;
	// }
	//
	// public void setCarreraMallaProcesoAux(CarreraMallaProcesoAux
	// carreraMallaProcesoAux) {
	// this.carreraMallaProcesoAux = carreraMallaProcesoAux;
	// }

	public void setListDocentesAsignaturas(List<DocenteAsignatura> listDocentesAsignaturas) {
		this.listDocentesAsignaturas = listDocentesAsignaturas;
	}

	public void setListMallaCurricular(List<MallaCurricular> listMallaCurricular) {
		this.listMallaCurricular = listMallaCurricular;
	}

	public void setListUnidadCurricular(List<UnidadCurricular> listUnidadCurricular) {
		this.listUnidadCurricular = listUnidadCurricular;
	}

	public void setMallaCurricular(MallaCurricular mallaCurricular) {
		this.mallaCurricular = mallaCurricular;
	}

	public void setPanelDocentes(boolean panelDocentes) {
		this.panelDocentes = panelDocentes;
	}

	public void setPanelEliminarDocentes(boolean panelEliminarDocentes) {
		this.panelEliminarDocentes = panelEliminarDocentes;
	}

	public void setPanelInsertarDocentes(boolean panelInsertarDocentes) {
		this.panelInsertarDocentes = panelInsertarDocentes;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void volverPanelDocentes() {
		panelPrincipal = false;
		panelDocentes = true;
		panelInsertarDocentes = false;
		panelEliminarDocentes = false;

		obtenerDocentesAsignaturas();
	}

	public void volverPrincipal() {
		panelPrincipal = true;
		panelDocentes = false;
		panelInsertarDocentes = false;
		panelEliminarDocentes = false;

		onRowSelect();
	}

}
