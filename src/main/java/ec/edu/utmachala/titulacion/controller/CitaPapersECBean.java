package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class CitaPapersECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private Boolean panelPrincipal;
	private Boolean panelCitas;

	private Integer paginaActual;

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelCitas = false;

		listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.carrera c "
						+ "inner join epp.estudiante e inner join epp.especialista2 e2 inner join epp.especialistaSuplente1 es1 where (p.fechaInicio<=?1 and p.fechaCierre>=?1) "
						+ "and (e2.email=?2 or es1.email=?2) order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);
	}

	public void actualizar() {

		if ((estudianteExamenComplexivoPP.getCitas1() != null && estudianteExamenComplexivoPP.getCitas1() < 0)) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar un número negativo.");
		} else {
			estudiantesExamenComplexivoPPService.actualizarSQL(
					"UPDATE \"estudiantesExamenComplexivoPP\" SET citas1=" + estudianteExamenComplexivoPP.getCitas1()
							+ ", \"observacionesCitas\" = '" + estudianteExamenComplexivoPP.getObservacionesCitas()
							+ "' WHERE id=" + estudianteExamenComplexivoPP.getId() + ";");
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La información fue actualizada de manera correcta");

			panelPrincipal = true;
			panelCitas = false;
			a();
		}
	}

	public void establecerPanelPrincipal() {
		panelPrincipal = true;
		panelCitas = false;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public EstudiantesExamenComplexivoPPService getEstudiantesExamenComplexivoPPService() {
		return estudiantesExamenComplexivoPPService;
	}

	public List<EstudianteExamenComplexivoPP> getListadoEstudiantesExamenComplexivoPP() {
		return listadoEstudiantesExamenComplexivoPP;
	}

	public Integer getPaginaActual() {
		return paginaActual;
	}

	public Boolean getPanelCitas() {
		return panelCitas;
	}

	public Boolean getPanelPrincipal() {
		return panelPrincipal;
	}

	public void insertarCitas() {
		if (estudianteExamenComplexivoPP.getAprobado() == null) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el número de CITAS PAPERS debido que el estudiante no se encuentra aprobado por el tutor.");
		} else if (!verificarDentroFecha()) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el número de CITAS PAPERS debido que exedió la fecha determinada en el cronograma del proceso.");
		} else {
			panelPrincipal = false;
			panelCitas = true;
		}
	}

	public void onPageChange(PageEvent event) {
		System.out.println("Pagina: " + ((DataTable) event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudiantesExamenComplexivoPPService(
			EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService) {
		this.estudiantesExamenComplexivoPPService = estudiantesExamenComplexivoPPService;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setPaginaActual(Integer paginaActual) {
		this.paginaActual = paginaActual;
	}

	public void setPanelCitas(Boolean panelCitas) {
		this.panelCitas = panelCitas;
	}

	public void setPanelPrincipal(Boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public boolean verificarDentroFecha() {
		boolean dentroFecha = false;

		System.out.println("proceso estudiante: " + estudianteExamenComplexivoPP.getProceso().getId());

		try {
			List<FechaProceso> listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo is true and fp.carrera is null order by fp.id asc",
					new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[] {});

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {

				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

					dentroFecha = true;

				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
									estudianteExamenComplexivoPP.getCarrera().getId() },
							0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

							dentroFecha = true;

						}
					}
				}
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
								estudianteExamenComplexivoPP.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

						dentroFecha = true;

					}
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("No existe registro de fecha para esta actividad.");
			npe.printStackTrace();

		}

		return dentroFecha;
	}

}
