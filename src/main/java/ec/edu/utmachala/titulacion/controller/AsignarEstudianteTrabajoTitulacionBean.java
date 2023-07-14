package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.DocenteSeminario;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.OpcionTitulacion;
import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.DocenteSeminarioService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.OpcionTitulacionService;
import ec.edu.utmachala.titulacion.service.SeminarioTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class AsignarEstudianteTrabajoTitulacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SeminarioTrabajoTitulacionService seminarioTrabajoTitulacionService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private DocenteSeminarioService docenteSeminarioService;

	@Autowired
	private OpcionTitulacionService opcionTitulacionService;

	private List<DocenteSeminario> docentesSeminarios;
	private DocenteSeminario docenteSeminarioSeleccionado;

	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionSeleccionado;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacionSeleccionado;

	private List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion;
	private SeminarioTrabajoTitulacion seminarioTrabajoTitulacion;

	private List<OpcionTitulacion> opcionesTitulacion;
	private OpcionTitulacion opcionTitulacion;

	private boolean disableBtnInsertarEstudiante;
	private boolean panelPrincipal;
	private boolean panelInsertar;
	private boolean panelEliminar;

	@PostConstruct
	public void a() {
		docentesSeminarios = docenteSeminarioService.obtenerLista(
				"select ds from DocenteSeminario ds inner join ds.docente d "
						+ "inner join ds.seminario s inner join s.proceso p where d.email=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName(),
						UtilsDate.timestamp() },
				0, false, new Object[0]);

		opcionTitulacion = new OpcionTitulacion();
		opcionesTitulacion = opcionTitulacionService.obtenerLista(
				"select ot from OpcionTitulacion ot order by ot.nombre", new Object[] {}, 0, false, new Object[] {});

		disableBtnInsertarEstudiante = true;
		panelPrincipal = true;
	}

	public void eliminar() {
		if (estudianteTrabajoTitulacion.getTutorias() == null || !estudianteTrabajoTitulacion.getTutorias().isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede desvincular al estudiante, ya que posee tutorías");
		else {
			estudianteTrabajoTitulacion.setSeminarioTrabajoTitulacion(null);
			estudianteTrabajoTitulacion.setOpcionTitulacion(null);
			estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacion);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El estudiante fue desvinculado de este seminario correctamente");
			establecerPanelPrincipal();
		}
	}

	public void establecerPanelPrincipal() {
		onRowSelect();
		panelPrincipal = true;
		panelInsertar = false;
		// panelEditar = false;
		panelEliminar = false;
	}

	public DocenteSeminario getDocenteSeminarioSeleccionado() {
		return docenteSeminarioSeleccionado;
	}

	public List<DocenteSeminario> getDocentesSeminarios() {
		return docentesSeminarios;
	}

	public List<EstudianteTrabajoTitulacion> getEstudiantesTrabajosTitulacion() {
		return estudiantesTrabajosTitulacion;
	}

	public List<EstudianteTrabajoTitulacion> getEstudiantesTrabajosTitulacionSeleccionado() {
		return estudiantesTrabajosTitulacionSeleccionado;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacionSeleccionado() {
		return estudianteTrabajoTitulacionSeleccionado;
	}

	public List<OpcionTitulacion> getOpcionesTitulacion() {
		return opcionesTitulacion;
	}

	public OpcionTitulacion getOpcionTitulacion() {
		return opcionTitulacion;
	}

	public List<SeminarioTrabajoTitulacion> getSeminariosTrabajosTitulacion() {
		return seminariosTrabajosTitulacion;
	}

	public SeminarioTrabajoTitulacion getSeminarioTrabajoTitulacion() {
		return seminarioTrabajoTitulacion;
	}

	public void insertar() {
		if (estudianteTrabajoTitulacionSeleccionado == null || estudianteTrabajoTitulacionSeleccionado.getId() == null
				|| estudianteTrabajoTitulacionSeleccionado.getId() == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja un estudiante");
		else if (seminarioTrabajoTitulacion.getId() == null || seminarioTrabajoTitulacion.getId() == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja un tema de investigación");
		else if (opcionTitulacion.getId() == null || opcionTitulacion.getId() == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja una opción de trabajo de titulación");
		else {
			List<EstudianteTrabajoTitulacion> estudiantesTrabajos = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett "
							+ "inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1",
					new Object[] { seminarioTrabajoTitulacion.getId() }, 0, false, new Object[] {});

			if (estudiantesTrabajos == null || estudiantesTrabajos.size() >= 2)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Este tema de investigación ya ha sido asignado como máximo a dos estudiantes");
			else {
				estudianteTrabajoTitulacionSeleccionado.setSeminarioTrabajoTitulacion(seminarioTrabajoTitulacion);
				estudianteTrabajoTitulacionSeleccionado.setOpcionTitulacion(opcionTitulacion);
				estudianteTrabajoTitulacionService.actualizar(estudianteTrabajoTitulacionSeleccionado);

				establecerPanelPrincipal();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Los datos se guardaron correctamente");
			}
		}
	}

	public boolean isDisableBtnInsertarEstudiante() {
		return disableBtnInsertarEstudiante;
	}

	public boolean isPanelEliminar() {
		return panelEliminar;
	}

	public boolean isPanelInsertar() {
		return panelInsertar;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void onRowSelect() {
		disableBtnInsertarEstudiante = false;

		estudiantesTrabajosTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
				"select distinct ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e "
						+ "inner join ett.seminarioTrabajoTitulacion stt inner join stt.seminario s "
						+ "inner join stt.trabajoTitulacion tt inner join ett.opcionTitulacion ot "
						+ "left join fetch ett.tutorias where s.id=?1",
				new Object[] { docenteSeminarioSeleccionado.getSeminario().getId() }, 0, false, new Object[0]);
	}

	public void onRowSelectEstudiante() {

	}

	public void prepararEliminar() {
		panelPrincipal = false;
		panelEliminar = true;
	}

	public void prepararInsertar() {
		estudianteTrabajoTitulacionSeleccionado = new EstudianteTrabajoTitulacion();
		seminarioTrabajoTitulacion = new SeminarioTrabajoTitulacion();
		opcionTitulacion = new OpcionTitulacion();
		panelPrincipal = false;
		panelInsertar = true;

		estudiantesTrabajosTitulacionSeleccionado = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e "
						+ "inner join ett.carrera c inner join ett.proceso p "
						+ "where c.id=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2 and ett.seminarioTrabajoTitulacion is null and ett.opcionTitulacion is null order by e.apellidoNombre",
				new Object[] {
						docenteSeminarioSeleccionado.getSeminario().getLineaInvestigacionCarrera().getCarrera().getId(),
						UtilsDate.timestamp() },
				0, false, new Object[0]);

		seminariosTrabajosTitulacion = seminarioTrabajoTitulacionService.obtenerLista(
				"select stt from SeminarioTrabajoTitulacion stt inner join stt.trabajoTitulacion tt "
						+ "inner join stt.seminario s inner join stt.docenteSeminario ds inner join ds.docente d where s.id=?1 and d.email=?2 order by tt.casoInvestigacion",
				new Object[] { docenteSeminarioSeleccionado.getSeminario().getId(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[] {});
	}

	public void setDisableBtnInsertarEstudiante(boolean disableBtnInsertarEstudiante) {
		this.disableBtnInsertarEstudiante = disableBtnInsertarEstudiante;
	}

	public void setDocenteSeminarioSeleccionado(DocenteSeminario docenteSeminarioSeleccionado) {
		this.docenteSeminarioSeleccionado = docenteSeminarioSeleccionado;
	}

	public void setDocentesSeminarios(List<DocenteSeminario> docentesSeminarios) {
		this.docentesSeminarios = docentesSeminarios;
	}

	public void setEstudiantesTrabajosTitulacion(List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacion) {
		this.estudiantesTrabajosTitulacion = estudiantesTrabajosTitulacion;
	}

	public void setEstudiantesTrabajosTitulacionSeleccionado(
			List<EstudianteTrabajoTitulacion> estudiantesTrabajosTitulacionSeleccionado) {
		this.estudiantesTrabajosTitulacionSeleccionado = estudiantesTrabajosTitulacionSeleccionado;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setEstudianteTrabajoTitulacionSeleccionado(
			EstudianteTrabajoTitulacion estudianteTrabajoTitulacionSeleccionado) {
		this.estudianteTrabajoTitulacionSeleccionado = estudianteTrabajoTitulacionSeleccionado;
	}

	public void setOpcionesTitulacion(List<OpcionTitulacion> opcionesTitulacion) {
		this.opcionesTitulacion = opcionesTitulacion;
	}

	public void setOpcionTitulacion(OpcionTitulacion opcionTitulacion) {
		this.opcionTitulacion = opcionTitulacion;
	}

	public void setPanelEliminar(boolean panelEliminar) {
		this.panelEliminar = panelEliminar;
	}

	public void setPanelInsertar(boolean panelInsertar) {
		this.panelInsertar = panelInsertar;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setSeminariosTrabajosTitulacion(List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion) {
		this.seminariosTrabajosTitulacion = seminariosTrabajosTitulacion;
	}

	public void setSeminarioTrabajoTitulacion(SeminarioTrabajoTitulacion seminarioTrabajoTitulacion) {
		this.seminarioTrabajoTitulacion = seminarioTrabajoTitulacion;
	}

}
