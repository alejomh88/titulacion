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
import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Seminario;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.LineaInvestigacionCarreraService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SeminarioService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class AdministrarSeminarioBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private LineaInvestigacionCarreraService lineaInvestigacionCarreraService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private SeminarioService seminarioService;

	private List<Carrera> listCarreras;
	private Integer carrera;

	private List<LineaInvestigacionCarrera> listLineaInvestigacionCarrera;
	private int lineaInvestigacionCarrera;

	private List<Proceso> listProceso;
	private String proceso;

	private List<Seminario> listSeminario;
	private Seminario seminario;

	private List<Usuario> listEspecialista;
	private String criterioBusquedaEspecialista;
	private Usuario especialista;
	private String consultaBuscarEspecialista = "select distinct u from Usuario u inner join u.permisos p inner join p.rol r where (u.id like ?1 or lower(u.apellidoPaterno) like ?1 or lower(u.apellidoMaterno) like ?1 or lower(u.nombre) like ?1) and r.id='DOCE' order by u.apellidoPaterno, u.apellidoMaterno, u.nombre";
	private String consultaObtenerEspecialista = "select u from Usuario u where u.id=?1";

	public void actualizar() {
		if (seminario.getNombre().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar espacios en blanco");
		else {
			seminarioService.actualizar(seminario);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente el seminario",
					"cerrar", true);
		}
	}

	public void buscar() {
		listSeminario = seminarioService.obtenerLista(
				"select distinct s from Seminario s left join fetch s.docentesSeminarios ds inner join s.proceso p inner join s.lineaInvestigacionCarrera lic where p.id=?1 and lic.id=?2 order by s.nombre",
				new Object[] { proceso, lineaInvestigacionCarrera }, 0, false, new Object[0]);
	}

	public void buscarEspecialista() {
		listEspecialista = usuarioService.obtenerLista(consultaBuscarEspecialista,
				new Object[] { "%" + criterioBusquedaEspecialista.toLowerCase() + "%" }, 0, false, new Object[0]);
	}

	public void buscarLineaInvestigacionCarrera() {
		listLineaInvestigacionCarrera = lineaInvestigacionCarreraService.obtenerLista(
				"select lic from LineaInvestigacionCarrera lic inner join lic.carrera c where c.id=?1",
				new Object[] { carrera }, 0, false, new Object[0]);
	}

	// public void cambiarLI() {
	// lineaInvestigacionCarrera = lineaInvestigacionCarrera;
	// }
	//
	// public void cambiarP() {
	// proceso = proceso;
	// }

	public void cargarEspecialista() {
		DocenteSeminario ds = new DocenteSeminario();
		ds.setDocente(usuarioService.obtenerObjeto(consultaObtenerEspecialista, new Object[] { especialista.getId() },
				false, new Object[0]));

		// seminario.addDocenteSeminario(ds);
		seminario.getDocentesSeminarios().add(ds);
		ds.setSeminario(seminario);

		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Eliminó correctamente el docente");
		especialista = new Usuario();
	}

	public void eliminarDocente(DocenteSeminario docenteSeminario) {
		seminario.getDocentesSeminarios().remove(docenteSeminario);
		docenteSeminario.setSeminario(null);
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Eliminó correctamente el docente");
	}

	public void eliminarRP() {
		// if (temaPractico.getActivo())
		// temaPractico.setActivo(false);
		// else
		// temaPractico.setActivo(true);
		// temaPracticoService.actualizar(temaPractico);
		buscar();
	}

	public Integer getCarrera() {
		return carrera;
	}

	public String getCriterioBusquedaEspecialista() {
		return criterioBusquedaEspecialista;
	}

	public Usuario getEspecialista() {
		return especialista;
	}

	public int getLineaInvestigacionCarrera() {
		return lineaInvestigacionCarrera;
	}

	public List<Carrera> getListCarreras() {
		return listCarreras;
	}

	public List<Usuario> getListEspecialista() {
		return listEspecialista;
	}

	public List<LineaInvestigacionCarrera> getListLineaInvestigacionCarrera() {
		return listLineaInvestigacionCarrera;
	}

	public List<Proceso> getListProceso() {
		return listProceso;
	}

	public List<Seminario> getListSeminario() {
		return listSeminario;
	}

	public String getProceso() {
		return proceso;
	}

	public Seminario getSeminario() {
		return seminario;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCarreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, 0, false, new Object[0]);
		listProceso = procesoService.obtenerLista("select p from Proceso p order by p.id", new Object[] {}, 0, false,
				new Object[0]);
		if (listCarreras.size() == 1) {
			carrera = listCarreras.get(0).getId();
		}
	}

	public void insertar() {
		if (seminario.getNombre().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar espacios en blanco");
		else {
			seminarioService.insertar(seminario);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente el seminario", "cerrar",
					true);
		}
		buscar();
	}

	public void limpiar() {
		if (lineaInvestigacionCarrera != 0 && proceso != null && proceso.compareTo("") != 0
				&& proceso.compareToIgnoreCase("0") != 0) {
			seminario = new Seminario();
			seminario.setLineaInvestigacionCarrera(lineaInvestigacionCarreraService.obtenerObjeto(
					"select lic from LineaInvestigacionCarrera lic where lic.id=?1",
					new Object[] { lineaInvestigacionCarrera }, false, new Object[0]));
			seminario.setProceso(procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
					new Object[] { proceso }, false, new Object[0]));
			seminario.setDocentesSeminarios(new ArrayList<DocenteSeminario>());
			UtilsAplicacion.enviarVariableVista("cerrar", true);
		}
	}

	public void limpiarObjetosBusquedaEspecialista() {
		this.listEspecialista = new ArrayList<Usuario>();
		this.criterioBusquedaEspecialista = "";
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCriterioBusquedaEspecialista(String criterioBusquedaEspecialista) {
		this.criterioBusquedaEspecialista = criterioBusquedaEspecialista;
	}

	public void setEspecialista(Usuario especialista) {
		this.especialista = especialista;
	}

	public void setLineaInvestigacionCarrera(int lineaInvestigacionCarrera) {
		this.lineaInvestigacionCarrera = lineaInvestigacionCarrera;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

	public void setListLineaInvestigacionCarrera(List<LineaInvestigacionCarrera> listLineaInvestigacionCarrera) {
		this.listLineaInvestigacionCarrera = listLineaInvestigacionCarrera;
	}

	public void setListProceso(List<Proceso> listProceso) {
		this.listProceso = listProceso;
	}

	public void setListSeminario(List<Seminario> listSeminario) {
		this.listSeminario = listSeminario;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setSeminario(Seminario seminario) {
		this.seminario = seminario;
	}
}
