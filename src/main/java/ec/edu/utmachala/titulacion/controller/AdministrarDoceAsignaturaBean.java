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

import ec.edu.utmachala.titulacion.entity.Asignatura;
import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.DocenteAsignatura;
import ec.edu.utmachala.titulacion.entity.PeriodoReactivo;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.AsignaturaService;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.DocenteAsignaturaService;
import ec.edu.utmachala.titulacion.service.PeriodoReactivoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class AdministrarDoceAsignaturaBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private AsignaturaService asignaturaService;

	@Autowired
	private DocenteAsignaturaService docenteAsignaturaService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PeriodoReactivoService periodoReactivoService;

	private List<Carrera> listCarreras;
	private Integer carrera;

	private List<Asignatura> listAsignatura;
	private int asignatura;

	private List<DocenteAsignatura> listDocenteAsignatura;
	private DocenteAsignatura docenteAsignatura;

	private List<Usuario> listEspecialista;
	private String criterioBusquedaEspecialista;
	private Usuario especialista;
	private String consultaBuscarEspecialista = "select distinct u from Usuario u inner join u.permisos p inner join p.rol r where (u.id like ?1 or upper(u.apellidoNombre) like ?1) and r.id='DORE' order by u.apellidoNombre";
	private String consultaObtenerEspecialista = "select u from Usuario u where u.id=?1";

	@PostConstruct
	public void a() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		listCarreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, 0, false, new Object[0]);
		if (listCarreras.size() == 1) {
			carrera = listCarreras.get(0).getId();
			buscarAsignaturas();
		}
	}

	public void actualizar() {
		PeriodoReactivo pr = periodoReactivoService.obtenerObjeto(
				"select pr from PeriodoReactivo pr inner join pr.carrera c where c.id=?1 and pr.fechaInicio<=?2 and pr.fechaCierre>?2",
				new Object[] { carrera, UtilsDate.timestamp() }, false, new Object[0]);
		if (docenteAsignatura.getDocente().getId().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un docente");
		if (pr == null || pr.getId() == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No hay un periodo abierto", "cerrar", true);
		else {
			docenteAsignatura.setAsignatura(asignaturaService.obtenerObjeto(
					"select a from Asignatura a left join fetch a.docentesAsignaturas da where a.id=?1",
					new Object[] { asignatura }, false, new Object[0]));
			docenteAsignatura.setPeriodoReactivo(pr);
			docenteAsignaturaService.actualizar(docenteAsignatura);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente el docente", "cerrar",
					true);
			buscar();
		}
	}

	public void buscar() {
		listDocenteAsignatura = docenteAsignaturaService.obtenerLista(
				"select distinct da from DocenteAsignatura da left join fetch da.temasPracticos tp inner join da.asignatura a where a.id=?1",
				new Object[] { asignatura }, 0, false, new Object[0]);
	}

	public void buscarAsignaturas() {
		listAsignatura = asignaturaService.obtenerLista(
				"select a from Asignatura a inner join a.mallaCurricular mc inner join mc.carrera c where c.id=?1 and a.activo=true order by a.nombre",
				new Object[] { carrera }, 0, false, new Object[0]);
	}

	public void buscarEspecialista() {
		listEspecialista = usuarioService.obtenerLista(consultaBuscarEspecialista,
				new Object[] { "%" + criterioBusquedaEspecialista.toUpperCase() + "%" }, 0, false, new Object[0]);
	}

	public void cargarEspecialista() {
		docenteAsignatura.setDocente(usuarioService.obtenerObjeto(consultaObtenerEspecialista,
				new Object[] { especialista.getId() }, false, new Object[0]));
		especialista = new Usuario();
	}

	public void eliminar() {
		if (docenteAsignatura.getTemasPracticos() == null || docenteAsignatura.getTemasPracticos().isEmpty())
			docenteAsignaturaService.eliminar(docenteAsignatura);
		else {
			if (docenteAsignatura.getActivo())
				docenteAsignatura.setActivo(false);
			else
				docenteAsignatura.setActivo(true);
			docenteAsignaturaService.actualizar(docenteAsignatura);
		}
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Eliminó correctamente el docente", "cerrar", true);
		buscar();
	}

	public int getAsignatura() {
		return asignatura;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public String getCriterioBusquedaEspecialista() {
		return criterioBusquedaEspecialista;
	}

	public DocenteAsignatura getDocenteAsignatura() {
		return docenteAsignatura;
	}

	public Usuario getEspecialista() {
		return especialista;
	}

	public List<Asignatura> getListAsignatura() {
		return listAsignatura;
	}

	public List<Carrera> getListCarreras() {
		return listCarreras;
	}

	public List<DocenteAsignatura> getListDocenteAsignatura() {
		return listDocenteAsignatura;
	}

	public List<Usuario> getListEspecialista() {
		return listEspecialista;
	}

	public void insertar() {
		PeriodoReactivo pr = periodoReactivoService.obtenerObjeto(
				"select pr from PeriodoReactivo pr inner join pr.carrera c where c.id=?1 and pr.fechaInicio<=?2 and pr.fechaCierre>?2",
				new Object[] { carrera, UtilsDate.timestamp() }, false, new Object[0]);
		if (docenteAsignatura.getDocente().getId().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un docente");
		if (pr == null || pr.getId() == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No hay un periodo abierto", "cerrar", true);
		else {
			docenteAsignatura.setAsignatura(asignaturaService.obtenerObjeto(
					"select a from Asignatura a left join fetch a.docentesAsignaturas da where a.id=?1",
					new Object[] { asignatura }, false, new Object[0]));
			docenteAsignatura.setPeriodoReactivo(pr);
			docenteAsignaturaService.insertar(docenteAsignatura);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Añadio correctamente el docente", "cerrar",
					true);
			buscar();
		}
	}

	public void limpiar() {
		if (asignatura != 0) {
			docenteAsignatura = new DocenteAsignatura();
			UtilsAplicacion.enviarVariableVista("cerrar", true);
		}
	}

	public void limpiarObjetosBusquedaEspecialista() {
		this.listEspecialista = new ArrayList<Usuario>();
		this.criterioBusquedaEspecialista = "";
	}

	public void setAsignatura(int asignatura) {
		this.asignatura = asignatura;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCriterioBusquedaEspecialista(String criterioBusquedaEspecialista) {
		this.criterioBusquedaEspecialista = criterioBusquedaEspecialista;
	}

	public void setDocenteAsignatura(DocenteAsignatura docenteAsignatura) {
		this.docenteAsignatura = docenteAsignatura;
	}

	public void setEspecialista(Usuario especialista) {
		this.especialista = especialista;
	}

	public void setListAsignatura(List<Asignatura> listAsignatura) {
		this.listAsignatura = listAsignatura;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListDocenteAsignatura(List<DocenteAsignatura> listDocenteAsignatura) {
		this.listDocenteAsignatura = listDocenteAsignatura;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

}
