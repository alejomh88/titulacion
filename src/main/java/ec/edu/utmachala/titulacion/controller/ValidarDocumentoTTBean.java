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
public class ValidarDocumentoTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajosTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private Boolean panelPrincipal;
	private Boolean panelValidar;

	private Integer paginaActual;

	@PostConstruct
	public void a() {

		listadoEstudiantesTrabajosTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c inner join ett.opcionTitulacion ot "
						+ "inner join ett.estudiante e inner join ett.especialista3 e3 inner join ett.especialistaSuplente1 es1 where (p.fechaInicio<=?1 and p.fechaCierre>=?1) "
						+ "and (e3.email=?2 or es1.email=?2) order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);

		panelPrincipal = true;
		panelValidar = false;
	}

	public void actualizar() {

		estudianteTrabajoTitulacion.setValidarDocumentoE3(true);

		estudianteTrabajoTitulacionService
				.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarDocumentoE3\"="
						+ estudianteTrabajoTitulacion.getValidarDocumentoE3() + " WHERE id="
						+ estudianteTrabajoTitulacion.getId() + ";");

		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha validado de forma correcta.");
		a();
		panelPrincipal = true;
		panelValidar = false;

	}

	public void establecerPanelPrincipal() {
		a();
		panelPrincipal = true;
		panelValidar = false;

	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<EstudianteTrabajoTitulacion> getListadoEstudiantesTrabajosTitulacion() {
		return listadoEstudiantesTrabajosTitulacion;
	}

	public Integer getPaginaActual() {
		return paginaActual;
	}

	public Boolean getPanelPrincipal() {
		return panelPrincipal;
	}

	public Boolean getPanelValidar() {
		return panelValidar;
	}

	public void onPageChange(PageEvent event) {
		System.out.println("Pagina: " + ((DataTable) event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListadoEstudiantesTrabajosTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajosTitulacion) {
		this.listadoEstudiantesTrabajosTitulacion = listadoEstudiantesTrabajosTitulacion;
	}

	public void setPaginaActual(Integer paginaActual) {
		this.paginaActual = paginaActual;
	}

	public void setPanelPrincipal(Boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setpanelValidar(Boolean panelValidar) {
		this.panelValidar = panelValidar;
	}

	public void setPanelValidar(Boolean panelValidar) {
		this.panelValidar = panelValidar;
	}

	public void validarDocumento() {

		try {

			if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null) {
				a();
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El estudiante no tiene asignado un tema de investigación por parte del tutor");
			} else if (estudianteTrabajoTitulacion.getAprobado() == null) {
				a();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No puede registrar la VALIDACIÓN debido que el estudiante no se encuentra aprobado por el tutor.");
			} else if (estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre()
					.compareToIgnoreCase("ENSAYOS O ÁRTICULOS ACADÉMICOS") == 0) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No es necesario validar la estructura del documento ya que es un Artículo Académico publicado.");
			} else if (!verificarDentroFecha()) {
				a();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No puede registrar la VALIDACIÓN debido que exedió la fecha determinada en el cronograma del proceso.");
			} else {
				panelPrincipal = false;
				panelValidar = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

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
