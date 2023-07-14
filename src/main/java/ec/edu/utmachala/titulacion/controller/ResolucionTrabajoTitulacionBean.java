package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class ResolucionTrabajoTitulacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;

	private List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulaciones;
	private List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulacionesSeleccionados;
	private String resolucion;
	private Date fechaResolucion;

	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void buscar() {
		// listEstudiantesTrabajosTitulaciones =
		// estudianteTrabajoTitulacionService.obtenerLista(
		// "select ett from EstudianteTrabajoTitulacion ett inner join
		// ett.estudiante e "
		// + "inner join ett.carrera c inner join ett.trabajoTitulacion tt "
		// + "inner join tt.seminario s inner join s.proceso p "
		// + "where c.id=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2 "
		// + "and ett.fechaSustentacion is not null and ett.especialista1 is not
		// null and ett.especialista2 is not null and ett.especialista3 is not
		// null "
		// + (this.consultaABC == 2 ? this.consultaJQ
		// : this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
		// + "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
		// new Object[] { this.carrera, UtilsDate.timestamp() },
		// Integer.valueOf(0), false, new Object[0]);

		// traer sin proceso todos
		listEstudiantesTrabajosTitulaciones = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e "
						+ "inner join ett.carrera c where c.id=?1 "
						+ "and ett.fechaSustentacion is not null and ett.especialista1 is not null and ett.especialista2 is not null and ett.especialista3 is not null "
						+ (this.consultaABC == 2 ? this.consultaJQ
								: this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
						+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
				new Object[] { this.carrera }, 0, false, new Object[0]);
	}

	public void comprobar() {
		if (listEstudiantesTrabajosTitulacionesSeleccionados.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Selccione un registro para continuar", "abrir",
					false);
		else
			UtilsAplicacion.enviarVariableVista("abrir", true);
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public int getConsultaABC() {
		return this.consultaABC;
	}

	public Date getFechaResolucion() {
		return fechaResolucion;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<EstudianteTrabajoTitulacion> getListEstudiantesTrabajosTitulaciones() {
		return listEstudiantesTrabajosTitulaciones;
	}

	public List<EstudianteTrabajoTitulacion> getListEstudiantesTrabajosTitulacionesSeleccionados() {
		return listEstudiantesTrabajosTitulacionesSeleccionados;
	}

	public String getResolucion() {
		return resolucion;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		this.listCarreras = this.carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email },

				Integer.valueOf(0), false, new Object[0]);
		if (this.listCarreras.size() == 1) {
			this.carrera = ((Carrera) this.listCarreras.get(0)).getId();
		}
	}

	public void insertarResolucion() {
		if (fechaResolucion == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la fecha de resolución", "cerrar",
					false);
		} else if (resolucion == null || resolucion.compareToIgnoreCase("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el número de resolucion", "cerrar",
					false);
		} else {
			for (EstudianteTrabajoTitulacion ett : listEstudiantesTrabajosTitulacionesSeleccionados) {
				ett.setFechaResolucion(fechaResolucion);
				ett.setResolucion(resolucion);
				estudianteTrabajoTitulacionService.actualizar(ett);
			}
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente", "cerrar", true);
			listEstudiantesTrabajosTitulaciones.removeAll(listEstudiantesTrabajosTitulacionesSeleccionados);
			listEstudiantesTrabajosTitulacionesSeleccionados = new ArrayList<EstudianteTrabajoTitulacion>();
		}
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setConsultaABC(int consultaABC) {
		this.consultaABC = consultaABC;
	}

	public void setFechaResolucion(Date fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListEstudiantesTrabajosTitulaciones(
			List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulaciones) {
		this.listEstudiantesTrabajosTitulaciones = listEstudiantesTrabajosTitulaciones;
	}

	public void setListEstudiantesTrabajosTitulacionesSeleccionados(
			List<EstudianteTrabajoTitulacion> listEstudiantesTrabajosTitulacionesSeleccionados) {
		this.listEstudiantesTrabajosTitulacionesSeleccionados = listEstudiantesTrabajosTitulacionesSeleccionados;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}

}
