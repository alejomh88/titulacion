package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.DocenteSeminario;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacion;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Seminario;
import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DocenteSeminarioService;
import ec.edu.utmachala.titulacion.service.LineaInvestigacionCarreraService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SeminarioService;
import ec.edu.utmachala.titulacion.service.SeminarioTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class GestionSeminarioTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private LineaInvestigacionCarreraService lineaInvestigacionCarreraService;

	@Autowired
	private SeminarioService seminarioService;

	@Autowired
	private SeminarioTrabajoTitulacionService seminarioTrabajoTitulacionService;

	@Autowired
	private DocenteSeminarioService docenteSeminarioService;

	@Autowired
	private UsuarioService usuarioService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private LineaInvestigacion lineaInvestigacion;

	private List<Seminario> listadoSeminarios;
	private Seminario seminario;

	private List<LineaInvestigacionCarrera> listadoLineasInvestigacionCarreras;
	private LineaInvestigacionCarrera lineaInvestigacionCarrera;
	private String lic;

	private List<DocenteSeminario> listadoDocenteSeminario;
	private DocenteSeminario docenteSeminario;

	private List<Usuario> listadoDocentes;
	private Usuario docente;
	private Usuario docenteSeleccionado;

	private boolean panelPrincipal;
	private boolean panelInsertarSeminario;
	private boolean panelEditarSeminario;
	private boolean panelEliminarSeminario;
	private boolean panelPresentarDocentes;
	private boolean panelInsertarDocenteSeminario;
	private boolean panelEliminarDocenteSeminario;

	private String criterioBusquedaSeminario;
	private String criterioBusquedaDocente;

	private boolean esUmmog;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		panelPrincipal = true;
		panelInsertarSeminario = false;
		panelEditarSeminario = false;
		panelEliminarSeminario = false;
		panelPresentarDocentes = false;
		panelInsertarDocenteSeminario = false;
		panelEliminarDocenteSeminario = false;
		lineaInvestigacion = new LineaInvestigacion();

		esUmmog = UtilSeguridad.obtenerRol("UMMO");
	}

	public void actualizarSeminario() {

		if (lic.compareTo("N") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una línea de investigación.");
		} else if (seminario.getNombre().compareTo("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Debe ingresar el nombre del seminario.");
		} else {
			try {
				lineaInvestigacionCarrera = lineaInvestigacionCarreraService.obtenerObjeto(
						"select lic from LineaInvestigacionCarrera lic where lic.id=?1",
						new Object[] { Integer.parseInt(lic) }, false, new Object[] {});

				seminario.setLineaInvestigacionCarrera(lineaInvestigacionCarrera);
				seminario.setNombre(seminario.getNombre().toUpperCase());
				seminarioService.actualizar(seminario);

				panelPrincipal = true;
				panelEditarSeminario = false;
				buscar();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El seminario se ha actualizado correctamente.");

			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
				e.printStackTrace();
			}
		}
	}

	public void buscar() {
		System.out.println("Carrera seleccionada: " + carrera + " - proceso: " + proceso);
		if (criterioBusquedaSeminario.isEmpty())
			listadoSeminarios = seminarioService.obtenerLista(
					"select s from Seminario s inner join s.proceso p inner join s.lineaInvestigacionCarrera lic inner join lic.carrera ca "
							+ "where ca.id=?1 and p.id=?2 order by s.nombre",
					new Object[] { carrera, proceso }, 0, false, new Object[] {});
		else
			listadoSeminarios = seminarioService.obtenerLista(
					"select s from Seminario s inner join s.proceso p inner join s.lineaInvestigacionCarrera lic inner join lic.carrera ca "
							+ "where ca.id=?1 and p.id=?2 and s.nombre like upper(?3) order by s.nombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaSeminario.trim() + "%" }, 0, false,
					new Object[] {});
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
							+ "where (u.id=?1 or translate(lower(u.apellidoNombre), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate(lower(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) and "
							+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU') order by u.apellidoNombre",
					new Object[] { "%" + criterioBusquedaDocente + "%" }, 0, false, new Object[] {});
		}
	}

	public void eliminarDocenteSeminario() {
		if (esUmmog) {
			panelPresentarDocentes = false;
			panelEliminarDocenteSeminario = true;

		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No tiene permisos para eliminar el docente, únicamente lo puede hacer el jefe de UMMOG .");
		}
	}

	public void eliminarDocenteSeminarioAccion() {
		System.out.println("Docente seminario id: " + docenteSeminario.getId());
		List<SeminarioTrabajoTitulacion> lSTT = new ArrayList<SeminarioTrabajoTitulacion>();
		lSTT = seminarioTrabajoTitulacionService.obtenerLista(
				"select stt from SeminarioTrabajoTitulacion stt inner join stt.docenteSeminario ds where ds.id=?1",
				new Object[] { docenteSeminario.getId() }, 0, false, new Object[] {});
		if (lSTT.size() == 0) {
			docenteSeminarioService.eliminar(docenteSeminario);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Docente eliminado satisfactoriamente.");

			listadoDocenteSeminario = docenteSeminarioService.obtenerLista(
					"select ds from DocenteSeminario ds inner join ds.seminario s where s.id=?1",
					new Object[] { seminario.getId() }, 0, false, new Object[] {});

			panelPresentarDocentes = true;
			panelEliminarDocenteSeminario = false;
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede eliminar, ya que el docente tiene el tema de investigación definido.");
		}
	}

	public void eliminarLineaInvestigacion() {
		if (esUmmog) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingreso a eliminar linea investigación.");
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Usted no tiene permisos para eliminar líneas de investigación.");
		}
	}

	public void eliminarSeminario() {
		if (esUmmog) {
			panelPrincipal = false;
			panelEliminarSeminario = true;
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No tiene permisos para eliminar el seminario, únicamente el jefe de UMMOG lo puede hacer.");
		}
	}

	public void eliminarSeminarioAccion() {

		List<DocenteSeminario> lds = new ArrayList<DocenteSeminario>();
		lds = docenteSeminarioService.obtenerLista(
				"select ds from DocenteSeminario ds inner join ds.seminario s where s.id=?1",
				new Object[] { seminario.getId() }, 0, false, new Object[] {});

		if (lds.size() == 0) {
			seminarioService.eliminar(seminario);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Se ha eliminado el seminario satisfactoriamente.");
			panelPrincipal = true;
			panelEliminarSeminario = false;
			buscar();
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede eliminar el seminario ya que tiene asigando docentes.");
		}
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCriterioBusquedaDocente() {
		return criterioBusquedaDocente;
	}

	public String getCriterioBusquedaSeminario() {
		return criterioBusquedaSeminario;
	}

	public Usuario getDocente() {
		return docente;
	}

	public Usuario getDocenteSeleccionado() {
		return docenteSeleccionado;
	}

	public DocenteSeminario getDocenteSeminario() {
		return docenteSeminario;
	}

	public String getLic() {
		return lic;
	}

	public LineaInvestigacion getLineaInvestigacion() {
		return lineaInvestigacion;
	}

	public LineaInvestigacionCarrera getLineaInvestigacionCarrera() {
		return lineaInvestigacionCarrera;
	}

	public List<Usuario> getListadoDocentes() {
		return listadoDocentes;
	}

	public List<DocenteSeminario> getListadoDocenteSeminario() {
		return listadoDocenteSeminario;
	}

	public List<LineaInvestigacionCarrera> getListadoLineasInvestigacionCarreras() {
		return listadoLineasInvestigacionCarreras;
	}

	public List<Seminario> getListadoSeminarios() {
		return listadoSeminarios;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public Seminario getSeminario() {
		return seminario;
	}

	public void guardarDocente() {
		if (docente == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No ha seleccionado un docente.");
		} else {
			docenteSeminario = new DocenteSeminario();
			docenteSeminario.setSeminario(seminario);
			docenteSeminario.setDocente(docente);
			docenteSeminarioService.insertar(docenteSeminario);

			panelPresentarDocentes = true;
			panelInsertarDocenteSeminario = false;

			listadoDocenteSeminario = docenteSeminarioService.obtenerLista(
					"select ds from DocenteSeminario ds inner join ds.seminario s where s.id=?1",
					new Object[] { seminario.getId() }, 0, false, new Object[] {});

			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El docente ha sido asignado al seminario correctamente.");
		}
	}

	public void insertarDocenteSeminarioPanel() {
		panelPresentarDocentes = false;
		panelInsertarDocenteSeminario = true;
		criterioBusquedaDocente = "";
		docente = new Usuario();
		buscarDocente();
	}

	public void insertarSeminario() {
		if (lic.compareTo("N") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una línea de investigación.");
		} else if (seminario.getNombre().compareTo("") == 0 || seminario.getNombre().compareTo(" ") == 0
				|| seminario.getNombre().compareTo("  ") == 0 || seminario.getNombre().compareTo("   ") == 0
				|| seminario.getNombre().trim().length() == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Debe ingresar el nombre del seminario.");
		} else {
			try {
				lineaInvestigacionCarrera = lineaInvestigacionCarreraService.obtenerObjeto(
						"select lic from LineaInvestigacionCarrera lic where lic.id=?1",
						new Object[] { Integer.parseInt(lic) }, false, new Object[] {});

				Proceso p = procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
						new Object[] { proceso }, false, new Object[] {});

				seminario.setProceso(p);
				seminario.setLineaInvestigacionCarrera(lineaInvestigacionCarrera);
				seminario.setNombre(seminario.getNombre().toUpperCase());
				seminarioService.insertar(seminario);

				panelPrincipal = true;
				panelInsertarSeminario = false;
				buscar();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "El seminario se ingresado correctamente.");

			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
				e.printStackTrace();
			}
		}
	}

	public void insertarSeminarioPanel() {
		panelPrincipal = false;
		panelInsertarSeminario = true;

		listadoLineasInvestigacionCarreras = lineaInvestigacionCarreraService.obtenerLista(
				"select lic from LineaInvestigacionCarrera lic inner join lic.carrera ca inner join lic.lineaInvestigacion li where ca.id=?1 and li.proceso=?2",
				new Object[] { carrera, proceso }, 0, false, new Object[] {});

		seminario = new Seminario();
	}

	public boolean isEsUmmog() {
		return esUmmog;
	}

	public boolean isPanelEditarSeminario() {
		return panelEditarSeminario;
	}

	public boolean isPanelEliminarDocenteSeminario() {
		return panelEliminarDocenteSeminario;
	}

	public boolean isPanelEliminarSeminario() {
		return panelEliminarSeminario;
	}

	public boolean isPanelInsertarDocenteSeminario() {
		return panelInsertarDocenteSeminario;
	}

	public boolean isPanelInsertarSeminario() {
		return panelInsertarSeminario;
	}

	public boolean isPanelPresentarDocentes() {
		return panelPresentarDocentes;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void obtenerCarreras() {
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u inner join c.lineasInvestigacionesCarreras lic where u.email=?1 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				0, false, new Object[] {});
	}

	public void presentarDocentes() {
		listadoDocenteSeminario = docenteSeminarioService.obtenerLista(
				"select ds from DocenteSeminario ds inner join ds.seminario s where s.id=?1",
				new Object[] { seminario.getId() }, 0, false, new Object[] {});

		panelPrincipal = false;
		panelPresentarDocentes = true;
	}

	public void presentarEditarSeminario() {
		panelPrincipal = false;
		panelInsertarSeminario = false;
		panelEditarSeminario = true;

		listadoLineasInvestigacionCarreras = lineaInvestigacionCarreraService.obtenerLista(
				"select lic from LineaInvestigacionCarrera lic inner join lic.carrera ca inner join lic.lineaInvestigacion li where ca.id=?1 and li.proceso=?2",
				new Object[] { carrera, proceso }, 0, false, new Object[] {});

		lic = String.valueOf(seminario.getLineaInvestigacionCarrera().getId());
	}

	public void seleccionarDocente() {
		docente = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
				new Object[] { docenteSeleccionado.getId() }, false, new Object[] {});
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCriterioBusquedaDocente(String criterioBusquedaDocente) {
		this.criterioBusquedaDocente = criterioBusquedaDocente;
	}

	public void setCriterioBusquedaSeminario(String criterioBusquedaSeminario) {
		this.criterioBusquedaSeminario = criterioBusquedaSeminario;
	}

	public void setDocente(Usuario docente) {
		this.docente = docente;
	}

	public void setDocenteSeleccionado(Usuario docenteSeleccionado) {
		this.docenteSeleccionado = docenteSeleccionado;
	}

	public void setDocenteSeminario(DocenteSeminario docenteSeminario) {
		this.docenteSeminario = docenteSeminario;
	}

	public void setEsUmmog(boolean esUmmog) {
		this.esUmmog = esUmmog;
	}

	public void setLic(String lic) {
		this.lic = lic;
	}

	public void setLineaInvestigacion(LineaInvestigacion lineaInvestigacion) {
		this.lineaInvestigacion = lineaInvestigacion;
	}

	public void setLineaInvestigacionCarrera(LineaInvestigacionCarrera lineaInvestigacionCarrera) {
		this.lineaInvestigacionCarrera = lineaInvestigacionCarrera;
	}

	public void setListadoDocentes(List<Usuario> listadoDocentes) {
		this.listadoDocentes = listadoDocentes;
	}

	public void setListadoDocenteSeminario(List<DocenteSeminario> listadoDocenteSeminario) {
		this.listadoDocenteSeminario = listadoDocenteSeminario;
	}

	public void setListadoLineasInvestigacionCarreras(
			List<LineaInvestigacionCarrera> listadoLineasInvestigacionCarreras) {
		this.listadoLineasInvestigacionCarreras = listadoLineasInvestigacionCarreras;
	}

	public void setListadoSeminarios(List<Seminario> listadoSeminarios) {
		this.listadoSeminarios = listadoSeminarios;
	}

	public void setPanelEditarSeminario(boolean panelEditarSeminario) {
		this.panelEditarSeminario = panelEditarSeminario;
	}

	public void setPanelEliminarDocenteSeminario(boolean panelEliminarDocenteSeminario) {
		this.panelEliminarDocenteSeminario = panelEliminarDocenteSeminario;
	}

	public void setPanelEliminarSeminario(boolean panelEliminarSeminario) {
		this.panelEliminarSeminario = panelEliminarSeminario;
	}

	public void setPanelInsertarDocenteSeminario(boolean panelInsertarDocenteSeminario) {
		this.panelInsertarDocenteSeminario = panelInsertarDocenteSeminario;
	}

	public void setPanelInsertarSeminario(boolean panelInsertarSeminario) {
		this.panelInsertarSeminario = panelInsertarSeminario;
	}

	public void setPanelPresentarDocentes(boolean panelPresentarDocentes) {
		this.panelPresentarDocentes = panelPresentarDocentes;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setSeminario(Seminario seminario) {
		this.seminario = seminario;
	}

	public void volverPresentarDocentes() {
		panelPresentarDocentes = true;
		panelInsertarDocenteSeminario = false;
		panelEliminarDocenteSeminario = false;

		listadoDocenteSeminario = docenteSeminarioService.obtenerLista(
				"select ds from DocenteSeminario ds inner join ds.seminario s where s.id=?1",
				new Object[] { seminario.getId() }, 0, false, new Object[] {});
	}

	public void volverPrincipal() {
		panelPrincipal = true;
		panelInsertarSeminario = false;
		panelEditarSeminario = false;
		panelEliminarSeminario = false;
		panelPresentarDocentes = false;
		buscar();
	}

}
