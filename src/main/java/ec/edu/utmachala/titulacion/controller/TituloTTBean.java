package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.ProcesoService;

@Controller
@Scope("session")
public class TituloTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteTrabajoTitulacion> listadoEstudianteTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private String criterioBusquedaEstudiante;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "where u.email=?1 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				0, false, new Object[] {});
	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			listadoEstudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[0]);
		else
			listadoEstudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
							+ "inner join ett.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or e.apellidoNombre like upper(?3)) order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[0]);
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<EstudianteTrabajoTitulacion> getListadoEstudianteTrabajoTitulacion() {
		return listadoEstudianteTrabajoTitulacion;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public void limpiar() {
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListadoEstudianteTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudianteTrabajoTitulacion) {
		this.listadoEstudianteTrabajoTitulacion = listadoEstudianteTrabajoTitulacion;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}
}
