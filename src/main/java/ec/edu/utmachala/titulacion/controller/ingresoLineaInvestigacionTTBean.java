package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacion;
import ec.edu.utmachala.titulacion.entity.LineaInvestigacionCarrera;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Seminario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.LineaInvestigacionCarreraService;
import ec.edu.utmachala.titulacion.service.LineaInvestigacionService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.SeminarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class ingresoLineaInvestigacionTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private LineaInvestigacionService lineaInvestigacionService;

	@Autowired
	private LineaInvestigacionCarreraService lineaInvestigacionCarreraService;

	@Autowired
	private SeminarioService seminarioService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private LineaInvestigacion lineaInvestigacion;

	private List<LineaInvestigacionCarrera> listadoListasInvestigacionCarrera;
	private LineaInvestigacionCarrera lineaInvestigacionCarrera;

	private boolean panelPrincipal;
	private boolean panelInsertarLineaInvestigacion;
	private boolean panelEditarLineaInvestigacion;
	private boolean panelEliminarLineaInvestigacion;

	private String criterioBusquedaLinea;

	private boolean esUmmog;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		panelPrincipal = true;
		panelInsertarLineaInvestigacion = false;
		panelEditarLineaInvestigacion = false;
		panelEliminarLineaInvestigacion = false;
		lineaInvestigacion = new LineaInvestigacion();

		esUmmog = UtilSeguridad.obtenerRol("UMMO");
	}

	public void actualizarLinea() {
		if (lineaInvestigacion.getNombre().compareTo("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Debe indicar el nombre de la línea de investigación.");
		} else {
			try {

				lineaInvestigacion.setProceso(proceso);
				lineaInvestigacion.setNombre(lineaInvestigacion.getNombre().toUpperCase());
				lineaInvestigacionService.actualizar(lineaInvestigacion);

				LineaInvestigacionCarrera lic = lineaInvestigacionCarreraService.obtenerObjeto(
						"select lic from LineaInvestigacionCarrera lic inner join lic.lineaInvestigacion li where li.id=?1",
						new Object[] { lineaInvestigacion.getId() }, false, new Object[] {});

				Carrera ca = carreraService.obtenerObjeto("select ca from Carrera ca where ca.id=?1",
						new Object[] { carrera }, false, new Object[] {});

				lic.setCarrera(ca);
				lic.setLineaInvestigacion(lineaInvestigacion);
				lic.setFechaResolucion(lineaInvestigacion.getFechaResolucion());
				lic.setResolucion(lineaInvestigacion.getResolucion());

				lineaInvestigacionCarreraService.actualizar(lic);

				panelPrincipal = true;
				panelEditarLineaInvestigacion = false;
				buscar();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Línea de investigación editada correctamente.");

			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
				e.printStackTrace();
			}
		}
	}

	public void buscar() {
		System.out.println("Carrera seleccionada: " + criterioBusquedaLinea);
		if (criterioBusquedaLinea.isEmpty())
			listadoListasInvestigacionCarrera = lineaInvestigacionCarreraService.obtenerLista(
					"select lic from LineaInvestigacionCarrera lic inner join lic.carrera ca inner join lic.lineaInvestigacion li "
							+ "where ca.id=?1 and li.proceso=?2 order by li.nombre",
					new Object[] { carrera, proceso }, 0, false, new Object[] {});
		else
			listadoListasInvestigacionCarrera = lineaInvestigacionCarreraService.obtenerLista(
					"select lic from LineaInvestigacionCarrera lic inner join lic.lineaInvestigacion li inner join lic.carrera ca "
							+ "where ca.id=?1 and li.proceso=?2 and li.nombre like upper(?3) order by li.nombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaLinea.trim() + "%" }, 0, false,
					new Object[] {});

	}

	public void eliminarLineaInvestigacion() {
		if (esUmmog) {
			panelPrincipal = false;
			panelEliminarLineaInvestigacion = true;
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Usted no tiene permisos para eliminar líneas de investigación.");
		}
	}

	public void eliminarLineaInvestigacionAccion() {
		List<Seminario> ls = new ArrayList<Seminario>();
		ls = seminarioService.obtenerLista(
				"select s from Seminario s inner join s.proceso p inner join s.lineaInvestigacionCarrera lic where lic.id=?1 and p.id=?2",
				new Object[] { lineaInvestigacionCarrera.getId(), proceso }, 0, false, new Object[] {});
		if (ls.size() == 0) {
			lineaInvestigacion = lineaInvestigacionCarrera.getLineaInvestigacion();
			lineaInvestigacionCarreraService.eliminar(lineaInvestigacionCarrera);
			lineaInvestigacionService.eliminar(lineaInvestigacion);
			panelPrincipal = true;
			panelEliminarLineaInvestigacion = false;

			lineaInvestigacion = new LineaInvestigacion();
			lineaInvestigacionCarrera = new LineaInvestigacionCarrera();

			buscar();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Se ha eliminado la línea de investigación de forma satisfactoria.");
		} else {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede eliminar la línea de investigación porque tiene asignado seminario(s)");
		}
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCriterioBusquedaLinea() {
		return criterioBusquedaLinea;
	}

	public LineaInvestigacion getLineaInvestigacion() {
		return lineaInvestigacion;
	}

	public LineaInvestigacionCarrera getLineaInvestigacionCarrera() {
		return lineaInvestigacionCarrera;
	}

	public List<LineaInvestigacionCarrera> getListadoListasInvestigacionCarrera() {
		return listadoListasInvestigacionCarrera;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public void insertarLinea() {
		if (lineaInvestigacion.getNombre().compareTo("") == 0 || lineaInvestigacion.getNombre().compareTo(" ") == 0
				|| lineaInvestigacion.getNombre().compareTo("   ") == 0
				|| lineaInvestigacion.getNombre().trim().length() == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Debe indicar el nombre de la línea de investigación.");
		} else {
			try {

				lineaInvestigacion.setProceso(proceso);
				lineaInvestigacion.setNombre(lineaInvestigacion.getNombre().toUpperCase());
				lineaInvestigacionService.insertar(lineaInvestigacion);
				lineaInvestigacion = lineaInvestigacionService.obtenerObjeto(
						"select li from LineaInvestigacion li where li.nombre=?1 and li.proceso=?2",
						new Object[] { lineaInvestigacion.getNombre().toUpperCase(), proceso }, false, new Object[] {});

				lineaInvestigacionCarrera = new LineaInvestigacionCarrera();

				Carrera ca = carreraService.obtenerObjeto("select ca from Carrera ca where ca.id=?1",
						new Object[] { carrera }, false, new Object[] {});
				lineaInvestigacionCarrera.setCarrera(ca);
				lineaInvestigacionCarrera.setLineaInvestigacion(lineaInvestigacion);
				lineaInvestigacionCarrera.setResolucion(lineaInvestigacion.getResolucion());
				lineaInvestigacionCarrera.setFechaResolucion(lineaInvestigacion.getFechaResolucion());

				lineaInvestigacionCarreraService.insertar(lineaInvestigacionCarrera);

				panelPrincipal = true;
				panelInsertarLineaInvestigacion = false;
				buscar();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Línea de investigación ingresada correctamente.");

			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
				e.printStackTrace();
			}
		}
	}

	public void insertarLineaInvestigacionPanel() {
		panelPrincipal = false;
		panelInsertarLineaInvestigacion = true;

		lineaInvestigacion = new LineaInvestigacion();
	}

	public boolean isEsUmmog() {
		return esUmmog;
	}

	public boolean isPanelEditarLineaInvestigacion() {
		return panelEditarLineaInvestigacion;
	}

	public boolean isPanelEliminarLineaInvestigacion() {
		return panelEliminarLineaInvestigacion;
	}

	public boolean isPanelInsertarLineaInvestigacion() {
		return panelInsertarLineaInvestigacion;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			System.out.println("Indica retorno de valor.");
		} else {
			System.out.println("He ingresado desde un método get.");
			if (procesos != null)
				procesos.clear();
			if (carreras != null)
				carreras.clear();
			criterioBusquedaLinea = "";
			if (listadoListasInvestigacionCarrera != null)
				listadoListasInvestigacionCarrera.clear();
			a();
		}
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

	public void presentarEditarLineaInvestigacion() {
		panelPrincipal = false;
		panelInsertarLineaInvestigacion = false;
		panelEditarLineaInvestigacion = true;

		lineaInvestigacion = lineaInvestigacionService.obtenerObjeto(
				"select li from LineaInvestigacion li where li.id=?1",
				new Object[] { lineaInvestigacionCarrera.getLineaInvestigacion().getId() }, false, new Object[] {});
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCriterioBusquedaLinea(String criterioBusquedaLinea) {
		this.criterioBusquedaLinea = criterioBusquedaLinea;
	}

	public void setEsUmmog(boolean esUmmog) {
		this.esUmmog = esUmmog;
	}

	public void setLineaInvestigacion(LineaInvestigacion lineaInvestigacion) {
		this.lineaInvestigacion = lineaInvestigacion;
	}

	public void setLineaInvestigacionCarrera(LineaInvestigacionCarrera lineaInvestigacionCarrera) {
		this.lineaInvestigacionCarrera = lineaInvestigacionCarrera;
	}

	public void setListadoListasInvestigacionCarrera(
			List<LineaInvestigacionCarrera> listadoListasInvestigacionCarrera) {
		this.listadoListasInvestigacionCarrera = listadoListasInvestigacionCarrera;
	}

	public void setPanelEditarLineaInvestigacion(boolean panelEditarLineaInvestigacion) {
		this.panelEditarLineaInvestigacion = panelEditarLineaInvestigacion;
	}

	public void setPanelEliminarLineaInvestigacion(boolean panelEliminarLineaInvestigacion) {
		this.panelEliminarLineaInvestigacion = panelEliminarLineaInvestigacion;
	}

	public void setPanelInsertarLineaInvestigacion(boolean panelInsertarLineaInvestigacion) {
		this.panelInsertarLineaInvestigacion = panelInsertarLineaInvestigacion;
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

	public void volverPrincipal() {
		panelPrincipal = true;
		panelInsertarLineaInvestigacion = false;
		panelEditarLineaInvestigacion = false;
		panelEliminarLineaInvestigacion = false;
		buscar();
	}

}
