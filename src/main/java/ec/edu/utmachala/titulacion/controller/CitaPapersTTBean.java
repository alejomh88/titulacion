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

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class CitaPapersTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private Boolean panelPrincipal;
	private Boolean panelCitas;

	private Integer paginaActual;
	private Integer citas1;

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelCitas = false;

		// listadoEstudiantesTrabajoTitulacion =
		// estudianteTrabajoTitulacionService.obtenerLista(
		// "select ett from EstudianteTrabajoTitulacion ett inner join
		// ett.proceso p inner join ett.carrera c "
		// + "inner join ett.estudiante e inner join ett.especialista2 e2 where
		// p.id=?1 and e2.email=?2 order by c.nombre, e.apellidoNombre",
		// new Object[] { "PT-010216",
		// SecurityContextHolder.getContext().getAuthentication().getName() },
		// 0,
		// false, new Object[0]);
		listadoEstudiantesTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
						+ "inner join ett.estudiante e inner join ett.especialista2 e2 inner join ett.opcionTitulacion ot inner join ett.especialistaSuplente1 es1 "
						+ "where (p.fechaInicio<=?1 and p.fechaCierre>=?1) and (e2.email=?2 or es1.email=?2) order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);
	}

	public void actualizar() {
		if (estudianteTrabajoTitulacion.getCitas1() != null && estudianteTrabajoTitulacion.getCitas1() < 0) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "No puede ingresar un porcentaje menor a cero o mayor a cien");
		} else {
			estudianteTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET citas1="
					+ citas1 + ", \"observacionesCitas\"='" + estudianteTrabajoTitulacion.getObservacionesCitas()
					+ "' WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");

			EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
					new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
							estudianteTrabajoTitulacion.getEstudiante().getEmail() },
					false, new Object[] {});

			if (ett2 != null && ett2.getId() != null) {
				estudianteTrabajoTitulacionService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET citas1="
						+ citas1 + ", \"observacionesCitas\"='" + estudianteTrabajoTitulacion.getObservacionesCitas()
						+ "' WHERE id=" + ett2.getId() + ";");
			}
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

	public Integer getCitas1() {
		return citas1;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<EstudianteTrabajoTitulacion> getListadoEstudiantesTrabajoTitulacion() {
		return listadoEstudiantesTrabajoTitulacion;
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
		if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El estudiante no tiene asignado un tema de investigación por parte del tutor");
		} else if (estudianteTrabajoTitulacion.getAprobado() == null) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el número de CITAS PAPERS debido que el estudiante no se encuentra aprobado por el tutor.");
		} else if (estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre()
				.compareToIgnoreCase("ENSAYOS O ÁRTICULOS ACADÉMICOS") == 0) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No es necesario cargar el número de CITAS PAPERS ya que es un Artículo Académico publicado.");
		} else if (!verificarDentroFecha()) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el número de CITAS PAPERS debido que exedió la fecha determinada en el cronograma del proceso.");
		} else {
			panelPrincipal = false;
			panelCitas = true;
			citas1 = estudianteTrabajoTitulacion.getCitas1();
		}

	}

	public void onPageChange(PageEvent event) {
		// System.out.println("Pagina: " + ((DataTable)
		// event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setCitas1(Integer citas1) {
		this.citas1 = citas1;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListadoEstudiantesTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajoTitulacion) {
		this.listadoEstudiantesTrabajoTitulacion = listadoEstudiantesTrabajoTitulacion;
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

		System.out.println("proceso estudiante: " + estudianteTrabajoTitulacion.getProceso().getId());

		try {
			List<FechaProceso> listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo is true and fp.carrera is null order by fp.id asc",
					new Object[] { estudianteTrabajoTitulacion.getProceso().getId() }, 0, false, new Object[] {});

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {

				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

					dentroFecha = true;

				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
									estudianteTrabajoTitulacion.getCarrera().getId() },
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
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
								estudianteTrabajoTitulacion.getCarrera().getId() },
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
