package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Rol;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.RolService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class HomeBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private RolService rolService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private ScheduleModel eventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	private String infoProceso1;

	private Boolean mostrarCalendario;

	private Boolean esEstudiante;

	private String descripcion;

	@PostConstruct
	public void a() throws Exception {
//		leerArchivo();

		mostrarCalendario = false;
		esEstudiante = false;

		List<Rol> roles = rolService.obtenerLista(
				"select r from Rol r inner join r.permisos p inner join p.usuario u where u.email = ?1 order by r.id",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		int contador = 0;

		for (Rol r : roles) {
			if (r.getId().compareToIgnoreCase("ADMI") == 0) {
				contador = 100;
				break;
			} else if (r.getId().compareToIgnoreCase("ESTT") == 0 || r.getId().compareToIgnoreCase("ESEC") == 0) {
				contador = 1;
				break;
			}
		}

		if (contador == 1) {
			EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.proceso p where e.email = ?1 and p.fechaInicio<= ?2 and p.fechaCierre >= ?2",
					new Object[] { UtilSeguridad.obtenerUsuario(), UtilsDate.timestamp() }, false, new Object[] {});

			EstudianteExamenComplexivoPP epp = estudianteExamenComplexivoPPService.obtenerObjeto(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.estudiante e where e.email = ?1 and p.fechaInicio <= ?2 and p.fechaCierre >= ?2 ",
					new Object[] { UtilSeguridad.obtenerUsuario(), UtilsDate.timestamp() }, false, new Object[] {});
			if (ett != null && epp != null) {
				mostrarCalendario = true;
				esEstudiante = true;
				eventModel = new DefaultScheduleModel();
				List<FechaProceso> fechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.carrera is null",
						new Object[] { ett.getProceso().getId() }, 0, false, new Object[] {});
				for (FechaProceso fp : fechasProcesos) {
					eventModel.addEvent(new DefaultScheduleEvent(
							fp.getNombre() + (fp.getModalidad().compareToIgnoreCase("TT") == 0 ? " (Trabajo Titulación)"
									: " (Examen Complexivo)"),
							fp.getFechaInicio(), fp.getFechaFinal(), (Object) fp.getDescripcion()));
					System.out.println("Fase del evento encontrado: " + fp.getFase() + ", con su fecha inicio: "
							+ fp.getFechaInicio() + ", y su fecha final: " + fp.getFechaFinal());
				}
			} else if (ett != null) {
				mostrarCalendario = true;
				esEstudiante = true;
				eventModel = new DefaultScheduleModel();
				List<FechaProceso> fechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.carrera is null and fp.modalidad='TT'",
						new Object[] { ett.getProceso().getId() }, 0, false, new Object[] {});
				for (FechaProceso fp : fechasProcesos) {
					eventModel.addEvent(new DefaultScheduleEvent(fp.getNombre() + " (Trabajo Titulación)",
							fp.getFechaInicio(), fp.getFechaFinal(), (Object) fp.getDescripcion()));
					System.out.println("Fase del evento encontrado: " + fp.getNombre() + ", con su fecha inicio: "
							+ fp.getFechaInicio() + ", y su fecha final: " + fp.getFechaFinal());
				}
			} else if (epp != null) {
				mostrarCalendario = true;
				esEstudiante = true;
				eventModel = new DefaultScheduleModel();
				List<FechaProceso> fechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.carrera is null and modalidad='EC'",
						new Object[] { epp.getProceso().getId() }, 0, false, new Object[] {});
				for (FechaProceso fp : fechasProcesos) {
					eventModel.addEvent(new DefaultScheduleEvent(fp.getNombre() + " (Examen Complexivo)",
							fp.getFechaInicio(), fp.getFechaFinal(), (Object) fp.getDescripcion()));
					System.out.println("Fase del evento encontrado: " + fp.getFase() + ", con su fecha inicio: "
							+ fp.getFechaInicio() + ", y su fecha final: " + fp.getFechaFinal());
				}
			}

		} else {
			mostrarCalendario = true;
			eventModel = new DefaultScheduleModel();

			List<Proceso> procesos = procesoService.obtenerLista(
					"select p from Proceso p where p.fechaInicio <= ?1 and p.fechaCierre >= ?1 order by p.fechaInicio",
					new Object[] { UtilsDate.timestamp() }, 0, false, new Object[] {});

			if (procesos.size() == 1) {
				System.out.println("Existe un proceso");
				infoProceso1 = procesos.get(0).getObservacion() + " (" + procesos.get(0).getId() + ")";
			} else if (procesos.size() == 2) {
				System.out.println("Existen dos procesos");
			} else {

			}
			// System.out.println("El proceso encontrado es: " +
			// procesos.get(0).getId());

			if (procesos != null && procesos.size() != 0) {

				List<FechaProceso> fechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.carrera is null",
						new Object[] { procesos.get(0).getId() }, 0, false, new Object[] {});
				for (FechaProceso fp : fechasProcesos) {
					eventModel.addEvent(new DefaultScheduleEvent(
							fp.getNombre() + (fp.getModalidad().compareToIgnoreCase("TT") == 0 ? " (Trabajo Titulación)"
									: " (Examen Complexivo)"),
							fp.getFechaInicio(), fp.getFechaFinal(), (Object) fp.getDescripcion()));
					System.out.println("Fase del evento encontrado: " + fp.getNombre() + ", con su fecha inicio: "
							+ fp.getFechaInicio() + ", y su fecha final: " + fp.getFechaFinal());
				}
			}
		}
		esEstudiante = false; // Ya no se visualizará el mensaje a los
								// estudiantes sobre el documento de google
								// drive.
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Boolean getEsEstudiante() {
		return esEstudiante;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public String getInfoProceso1() {
		return infoProceso1;
	}

	public Boolean getMostrarCalendario() {
		return mostrarCalendario;
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
		descripcion = (event.getData().toString().isEmpty() ? "No existe descripción de la actividad."
				: event.getData().toString());

	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setEsEstudiante(Boolean esEstudiante) {
		this.esEstudiante = esEstudiante;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public void setInfoProceso1(String infoProceso1) {
		this.infoProceso1 = infoProceso1;
	}

	public void setMostrarCalendario(Boolean mostrarCalendario) {
		this.mostrarCalendario = mostrarCalendario;
	}

}