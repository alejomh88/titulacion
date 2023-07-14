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
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EnvioCorreoService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class FechasyComitesTrabajoTitulacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EnvioCorreoService envioCorreoService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;

	private List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulaciones;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;
	private List<Usuario> listEspecialista;

	private String criterioBusquedaEspecialista1;
	private String criterioBusquedaEspecialista2;
	private String criterioBusquedaEspecialista3;
	private String criterioBusquedaEspecialistaSuplente1;
	private String criterioBusquedaEspecialistaSuplente2;
	private Usuario especialista1;
	private Usuario especialista2;
	private Usuario especialista3;
	private Usuario especialistaSuplente1;
	private Usuario especialistaSuplente2;

	private String consultaBuscarEspecialista = "select distinct u from Usuario u inner join u.permisos p inner join p.rol r where (u.id like ?1 or lower(u.apellidoPaterno) like ?1 or lower(u.apellidoMaterno) like ?1 or lower(u.nombre) like ?1) and r.id='DOCE' order by u.apellidoPaterno, u.apellidoMaterno, u.nombre";
	private String consultaObtenerEspecialista = "select u from Usuario u where u.id=?1";
	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void actualizar() {
		this.estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
	}

	public void buscar() {
		// this.listEstudiantesTrabajosTitulaciones =
		// estudianteTrabajoTitulacionService.obtenerLista(
		// "select ett from EstudianteTrabajoTitulacion ett inner join
		// ett.estudiante e "
		// + "inner join ett.carrera c inner join ett.trabajoTitulacion tt "
		// + "inner join tt.seminario s inner join s.proceso p "
		// + "where c.id=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2 and
		// ett.aprobado=true "
		// + (this.consultaABC == 2 ? this.consultaJQ
		// : this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
		// + "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
		// new Object[] { this.carrera, UtilsDate.timestamp() },
		// Integer.valueOf(0), false, new Object[0]);

		// traer sin proceso todos
		listEstudiantesTrabajosTitulaciones = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e "
						+ "inner join ett.carrera c where c.id=?1 "
						+ (this.consultaABC == 2 ? this.consultaJQ
								: this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
						+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
				new Object[] { this.carrera }, 0, false, new Object[0]);
	}

	public void buscarEspecialista1() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista1.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void buscarEspecialista2() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista2.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void buscarEspecialista3() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista3.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void buscarEspecialistaSuplente1() {
		listEspecialista = usuarioService.obtenerLista(consultaBuscarEspecialista,
				new Object[] { "%" + criterioBusquedaEspecialistaSuplente1.toLowerCase() + "%" }, 0, false,
				new Object[0]);
	}

	public void buscarEspecialistaSuplente2() {
		listEspecialista = usuarioService.obtenerLista(consultaBuscarEspecialista,
				new Object[] { "%" + criterioBusquedaEspecialistaSuplente2.toLowerCase() + "%" }, 0, false,
				new Object[0]);
	}

	public void cargarEspecialista1() {
		if ((estudianteTrabajoTitulacion.getEspecialista2() != null) && (estudianteTrabajoTitulacion.getEspecialista2()
				.getId().compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista3() != null) && (estudianteTrabajoTitulacion
				.getEspecialista3().getId().compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente1().getId().compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente2() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente2().getId().compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			estudianteTrabajoTitulacion
					.setEspecialista1((Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { this.especialista1.getId() }, false, new Object[0]));
			this.especialista1 = new Usuario();
		}
	}

	public void cargarEspecialista2() {
		if ((estudianteTrabajoTitulacion.getEspecialista1() != null) && (estudianteTrabajoTitulacion.getEspecialista1()
				.getId().compareToIgnoreCase(this.especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista3() != null) && (estudianteTrabajoTitulacion
				.getEspecialista3().getId().compareToIgnoreCase(this.especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente1().getId().compareToIgnoreCase(especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente2() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente2().getId().compareToIgnoreCase(especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			estudianteTrabajoTitulacion
					.setEspecialista2((Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { this.especialista2.getId() }, false, new Object[0]));
			this.especialista2 = new Usuario();
		}
	}

	public void cargarEspecialista3() {
		if ((estudianteTrabajoTitulacion.getEspecialista1() != null) && (estudianteTrabajoTitulacion.getEspecialista1()
				.getId().compareToIgnoreCase(this.especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista2() != null) && (estudianteTrabajoTitulacion
				.getEspecialista2().getId().compareToIgnoreCase(this.especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente1().getId().compareToIgnoreCase(especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente2() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente2().getId().compareToIgnoreCase(especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			estudianteTrabajoTitulacion.setEspecialista3(usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
					new Object[] { this.especialista3.getId() }, false, new Object[0]));
			especialista3 = new Usuario();
		}
	}

	public void cargarEspecialistaSuplente1() {
		if ((estudianteTrabajoTitulacion.getEspecialista1() != null) && (estudianteTrabajoTitulacion.getEspecialista1()
				.getId().compareToIgnoreCase(especialistaSuplente1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista2() != null) && (estudianteTrabajoTitulacion
				.getEspecialista2().getId().compareToIgnoreCase(especialistaSuplente1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista3() != null) && (estudianteTrabajoTitulacion
				.getEspecialista3().getId().compareToIgnoreCase(especialistaSuplente1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente2() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente2().getId().compareToIgnoreCase(especialistaSuplente1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			estudianteTrabajoTitulacion.setEspecialistaSuplente1(
					(Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { this.especialistaSuplente1.getId() }, false, new Object[0]));
			especialistaSuplente1 = new Usuario();
		}
	}

	public void cargarEspecialistaSuplente2() {
		if ((estudianteTrabajoTitulacion.getEspecialista1() != null) && (estudianteTrabajoTitulacion.getEspecialista1()
				.getId().compareToIgnoreCase(especialistaSuplente2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista2() != null) && (estudianteTrabajoTitulacion
				.getEspecialista2().getId().compareToIgnoreCase(especialistaSuplente2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialista3() != null) && (estudianteTrabajoTitulacion
				.getEspecialista3().getId().compareToIgnoreCase(especialistaSuplente2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente1() != null) && (estudianteTrabajoTitulacion
				.getEspecialistaSuplente1().getId().compareToIgnoreCase(especialistaSuplente2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			estudianteTrabajoTitulacion.setEspecialistaSuplente2(
					(Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { especialistaSuplente2.getId() }, false, new Object[0]));
			especialistaSuplente2 = new Usuario();
		}
	}

	public void enviarCorreo() {
		Usuario estudiante = estudianteTrabajoTitulacion.getEstudiante();
		envioCorreoService.enviarDatosTrabajoTitulacion(estudiante.getEmail(), estudianteTrabajoTitulacion);
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public int getConsultaABC() {
		return this.consultaABC;
	}

	public String getCriterioBusquedaEspecialista1() {
		return this.criterioBusquedaEspecialista1;
	}

	public String getCriterioBusquedaEspecialista2() {
		return this.criterioBusquedaEspecialista2;
	}

	public String getCriterioBusquedaEspecialista3() {
		return this.criterioBusquedaEspecialista3;
	}

	public String getCriterioBusquedaEspecialistaSuplente1() {
		return criterioBusquedaEspecialistaSuplente1;
	}

	public String getCriterioBusquedaEspecialistaSuplente2() {
		return criterioBusquedaEspecialistaSuplente2;
	}

	public Usuario getEspecialista1() {
		return this.especialista1;
	}

	public Usuario getEspecialista2() {
		return this.especialista2;
	}

	public Usuario getEspecialista3() {
		return this.especialista3;
	}

	public Usuario getEspecialistaSuplente1() {
		return especialistaSuplente1;
	}

	public Usuario getEspecialistaSuplente2() {
		return especialistaSuplente2;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<Usuario> getListEspecialista() {
		return this.listEspecialista;
	}

	public List<EstudianteTrabajoTitulacion> getListEstudiantesTrabajosTitulaciones() {
		return listEstudiantesTrabajosTitulaciones;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		this.listCarreras = this.carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, Integer.valueOf(0), false, new Object[0]);

		if (this.listCarreras.size() == 1) {
			this.carrera = ((Carrera) this.listCarreras.get(0)).getId();
		}
	}

	public void insertarComiteEvaluador() {
		if ((estudianteTrabajoTitulacion.getEspecialista1() == null)
				|| (estudianteTrabajoTitulacion.getEspecialista1().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 1");
		} else if ((estudianteTrabajoTitulacion.getEspecialista2() == null)
				|| (estudianteTrabajoTitulacion.getEspecialista2().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 2");
		} else if ((estudianteTrabajoTitulacion.getEspecialista3() == null)
				|| (estudianteTrabajoTitulacion.getEspecialista3().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 3");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente1() == null)
				|| (estudianteTrabajoTitulacion.getEspecialistaSuplente1().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista suplente 1");
		} else if ((estudianteTrabajoTitulacion.getEspecialistaSuplente2() == null)
				|| (estudianteTrabajoTitulacion.getEspecialistaSuplente2().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista suplente 2");
		} else {
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente el comite", "cerrar",
					true);
		}
	}

	public void insertarFecha() {
		if (estudianteTrabajoTitulacion.getFechaSustentacion() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la fecha de exposición");
		} else {
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto la fecha de sustentación", "cerrar",
					true);
		}
	}

	public void limpiar() {
	}

	public void limpiarObjetosBusquedaEspecialista() {
		this.listEspecialista = new ArrayList<Usuario>();
		this.criterioBusquedaEspecialista1 = "";
		this.criterioBusquedaEspecialista2 = "";
		this.criterioBusquedaEspecialista3 = "";
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setConsultaABC(int consultaABC) {
		this.consultaABC = consultaABC;
	}

	public void setCriterioBusquedaEspecialista1(String criterioBusquedaEspecialista1) {
		this.criterioBusquedaEspecialista1 = criterioBusquedaEspecialista1;
	}

	public void setCriterioBusquedaEspecialista2(String criterioBusquedaEspecialista2) {
		this.criterioBusquedaEspecialista2 = criterioBusquedaEspecialista2;
	}

	public void setCriterioBusquedaEspecialista3(String criterioBusquedaEspecialista3) {
		this.criterioBusquedaEspecialista3 = criterioBusquedaEspecialista3;
	}

	public void setCriterioBusquedaEspecialistaSuplente1(String criterioBusquedaEspecialistaSuplente1) {
		this.criterioBusquedaEspecialistaSuplente1 = criterioBusquedaEspecialistaSuplente1;
	}

	public void setCriterioBusquedaEspecialistaSuplente2(String criterioBusquedaEspecialistaSuplente2) {
		this.criterioBusquedaEspecialistaSuplente2 = criterioBusquedaEspecialistaSuplente2;
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

	public void setEspecialistaSuplente1(Usuario especialistaSuplente1) {
		this.especialistaSuplente1 = especialistaSuplente1;
	}

	public void setEspecialistaSuplente2(Usuario especialistaSuplente2) {
		this.especialistaSuplente2 = especialistaSuplente2;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

	public void setListEstudiantesTrabajosTitulaciones(
			List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulaciones) {
		this.listEstudiantesTrabajosTitulaciones = listEstudiantesTrabajosTitulaciones;
	}

}
