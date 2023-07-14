package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;

@Controller
@Scope("session")
public class ComiteEvaluadorTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private UsuarioService usuarioService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private boolean panelPrincipal;
	private boolean panelSeleccionar;
	private List<Usuario> especialistas;

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

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "where u.email=?1 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				0, false, new Object[] {});

		panelPrincipal = true;
		panelSeleccionar = false;
		especialista1 = new Usuario();
		especialista2 = new Usuario();
		especialista3 = new Usuario();
		especialistaSuplente1 = new Usuario();

		boolEspecialista1 = true;
	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			listadoEstudiantesTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[0]);
		else
			listadoEstudiantesTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or e.apellidoNombre like upper(?3)) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[0]);
	}

	public void buscarEspecialista() {
		especialistas = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where (u.id=?1 or lower(u.apellidoNombre) like lower(?1)) and "
						+ "(r.id='DOEV' or  r.id='DORE' or r.id='DOTU') order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaEspecialista + "%" }, 0, false, new Object[] {});
	}

	public void establecerPanelPrincipal() {
		panelPrincipal = true;
		panelSeleccionar = false;
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

	public Usuario getEspecialistaSuplente1() {
		return especialistaSuplente1;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
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

	public void guardar() {
		if (especialista1.getId() == null || especialista2.getId() == null || especialista3.getId() == null)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Falta de seleccionar un especialista del comité evaluador");
		else if ((especialista1.getId().compareTo(especialista2.getId()) == 0
				|| especialista1.getId().compareTo(especialista3.getId()) == 0
				|| especialista1.getId().compareTo(especialistaSuplente1.getId()) == 0)
				|| (especialista2.getId().compareTo(especialista3.getId()) == 0
						|| especialista2.getId().compareTo(especialistaSuplente1.getId()) == 0)
				|| (especialista3.getId().compareTo(especialistaSuplente1.getId()) == 0)) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Algún especialista está considerado mas de una vez");
		} else {
			panelPrincipal = true;
			panelSeleccionar = false;

			estudianteTrabajoTitulacion.setEspecialista1(especialista1);
			estudianteTrabajoTitulacion.setEspecialista2(especialista2);
			estudianteTrabajoTitulacion.setEspecialista3(especialista3);
			estudianteTrabajoTitulacion.setEspecialistaSuplente1(especialistaSuplente1);

			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			presentaMensaje(FacesMessage.SEVERITY_INFO, "El comité evaluador fue asignado de manera correcta");
		}
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

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPanelSeleccionar() {
		return panelSeleccionar;
	}

	public void seleccionarComiteEvaluador() {
		if (UtilSeguridad.obtenerRol("COTI") || UtilSeguridad.obtenerRol("COCA")
				&& (UtilSeguridad.obtenerUsuario().compareTo("faconza@utmachala.edu.ec") != 0
						&& UtilSeguridad.obtenerUsuario().compareTo("jbenites@utmachala.edu.ec") != 0
						&& UtilSeguridad.obtenerUsuario().compareTo("aduran@utmachala.edu.ec") != 0))
			presentaMensaje(FacesMessage.SEVERITY_INFO, "No tiene permisos para acceder a esta opción");
		else {
			especialista1 = new Usuario();
			especialista2 = new Usuario();
			especialista3 = new Usuario();
			especialistaSuplente1 = new Usuario();

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

			panelPrincipal = false;
			panelSeleccionar = true;
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

	public void setEspecialistaSuplente1(Usuario especialistaSuplente1) {
		this.especialistaSuplente1 = especialistaSuplente1;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListadoEstudiantesTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion) {
		this.listadoEstudiantesTrabajoTitulacion = listadoEstudiantesTrabajoTitulacion;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPanelSeleccionar(boolean panelSeleccionar) {
		this.panelSeleccionar = panelSeleccionar;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

}
