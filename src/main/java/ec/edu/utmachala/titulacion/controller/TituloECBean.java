package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ProcesoService;

@Controller
@Scope("session")
public class TituloECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

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
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.carrera c "
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[0]);
		else
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.carrera c "
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or e.apellidoNombre like upper(?3)) order by e.apellidoNombre",
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

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
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

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

}
