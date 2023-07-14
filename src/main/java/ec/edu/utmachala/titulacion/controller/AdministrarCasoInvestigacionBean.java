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
import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.TrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.DocenteSeminarioService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.SeminarioTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.TrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class AdministrarCasoInvestigacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private TrabajoTitulacionService trabajoTitulacionService;

	@Autowired
	private DocenteSeminarioService docenteSeminarioService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private SeminarioTrabajoTitulacionService seminarioTrabajoTitulacionService;

	private List<TrabajoTitulacion> trabajosTitulacion;
	private TrabajoTitulacion trabajoTitulacion;

	private List<DocenteSeminario> docentesSeminarios;
	private DocenteSeminario docenteSeminarioSeleccionado;

	private boolean disableBtnCasoInvestigacion;
	private boolean panelPrincipal;
	private boolean panelInsertar;
	private boolean panelEditar;
	private boolean panelEliminar;

	@PostConstruct
	public void a() {
		docentesSeminarios = docenteSeminarioService.obtenerLista(
				"select ds from DocenteSeminario ds inner join ds.docente d "
						+ "inner join ds.seminario s inner join s.proceso p where d.email=?1 and p.fechaInicio<=?2 and p.fechaCierre>?2",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						UtilsDate.timestamp() },
				0, false, new Object[0]);

		disableBtnCasoInvestigacion = true;
		panelPrincipal = true;
		panelInsertar = false;
		panelEditar = false;
		panelEliminar = false;
	}

	public void editar() {
		trabajoTitulacion.setCasoInvestigacion(trabajoTitulacion.getCasoInvestigacion().trim().toUpperCase());

		if (trabajoTitulacion.getCasoInvestigacion().compareToIgnoreCase("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un tema de investigación");
		} else {
			TrabajoTitulacion ttb = trabajoTitulacionService.obtenerObjeto(
					"select tt from TrabajoTitulacion tt where tt.casoInvestigacion=?1 and tt.id!=?2",
					new Object[] { trabajoTitulacion.getCasoInvestigacion(), trabajoTitulacion.getId() }, false,
					new Object[0]);
			if (ttb != null && ttb.getId() != null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Este tema ya fue investigado anteriormente");
			} else {
				trabajoTitulacionService.actualizar(trabajoTitulacion);

				establecerPanelPrincipal();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Los datos se guardaron correctamente");
			}
		}
	}

	public void eliminar() {
		List<EstudianteTrabajoTitulacion> listETT = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt "
						+ "inner join stt.trabajoTitulacion tt where tt.id=?1",
				new Object[] { trabajoTitulacion.getId() }, 0, false, new Object[0]);

		if (!listETT.isEmpty())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No se puede eliminar el tema de investigación, por que ya está asignado a un estudiante");
		else {
			trabajoTitulacionService.eliminar(trabajoTitulacion);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Los datos se eliminaron correctamente");
			establecerPanelPrincipal();
		}
	}

	public void establecerPanelPrincipal() {
		onRowSelect();
		panelPrincipal = true;
		panelInsertar = false;
		panelEditar = false;
		panelEliminar = false;
	}

	public DocenteSeminario getDocenteSeminarioSeleccionado() {
		return docenteSeminarioSeleccionado;
	}

	public List<DocenteSeminario> getDocentesSeminarios() {
		return docentesSeminarios;
	}

	public List<TrabajoTitulacion> getTrabajosTitulacion() {
		return trabajosTitulacion;
	}

	public TrabajoTitulacion getTrabajoTitulacion() {
		return trabajoTitulacion;
	}

	public void insertar() {
		trabajoTitulacion.setCasoInvestigacion(trabajoTitulacion.getCasoInvestigacion().trim().toUpperCase());

		if (trabajoTitulacion.getCasoInvestigacion().compareToIgnoreCase("") == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese un tema de investigación");
		} else {
			TrabajoTitulacion ttb = trabajoTitulacionService.obtenerObjeto(
					"select tt from TrabajoTitulacion tt where tt.casoInvestigacion=?1",
					new Object[] { trabajoTitulacion.getCasoInvestigacion() }, false, new Object[0]);
			if (ttb != null && ttb.getId() != null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Este tema ya fue investigado anteriormente");
			} else {
				// SeminarioTrabajoTitulacion stt = new
				// SeminarioTrabajoTitulacion();
				// stt.setSeminario(docenteSeminarioSeleccionado.getSeminario());
				// stt.setTrabajoTitulacion(trabajoTitulacion);
				// stt.setDocenteSeminario(docenteSeminarioSeleccionado);
				//
				// trabajoTitulacion.setSeminariosTrabajosTitulacion(new
				// ArrayList<SeminarioTrabajoTitulacion>());
				// trabajoTitulacion.getSeminariosTrabajosTitulacion().add(stt);
				// trabajoTitulacionService.insertar(trabajoTitulacion);
				trabajoTitulacionService.insertar(trabajoTitulacion);

				SeminarioTrabajoTitulacion stt = new SeminarioTrabajoTitulacion();
				stt.setSeminario(docenteSeminarioSeleccionado.getSeminario());
				stt.setTrabajoTitulacion(trabajoTitulacion);
				stt.setDocenteSeminario(docenteSeminarioSeleccionado);
				seminarioTrabajoTitulacionService.insertar(stt);

				establecerPanelPrincipal();
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Los datos se guardaron correctamente");
			}
		}
	}

	public boolean isDisableBtnCasoInvestigacion() {
		return disableBtnCasoInvestigacion;
	}

	public boolean isPanelEditar() {
		return panelEditar;
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
		disableBtnCasoInvestigacion = false;
		trabajosTitulacion = trabajoTitulacionService.obtenerLista(
				"select tt from TrabajoTitulacion tt inner join tt.seminariosTrabajosTitulacion stt "
						+ "inner join stt.docenteSeminario ds where ds.id=?1 order by tt.casoInvestigacion",
				new Object[] { docenteSeminarioSeleccionado.getId() }, 0, false, new Object[0]);
	}

	public void prepararEditar() {
		panelPrincipal = false;
		panelEditar = true;
	}

	public void prepararEliminar() {
		panelPrincipal = false;
		panelEliminar = true;
	}

	public void prepararInsertar() {
		trabajoTitulacion = new TrabajoTitulacion();
		panelPrincipal = false;
		panelInsertar = true;
	}

	public void setDisableBtnCasoInvestigacion(boolean disableBtnCasoInvestigacion) {
		this.disableBtnCasoInvestigacion = disableBtnCasoInvestigacion;
	}

	public void setDocenteSeminarioSeleccionado(DocenteSeminario docenteSeminarioSeleccionado) {
		this.docenteSeminarioSeleccionado = docenteSeminarioSeleccionado;
	}

	public void setDocentesSeminarios(List<DocenteSeminario> docentesSeminarios) {
		this.docentesSeminarios = docentesSeminarios;
	}

	public void setPanelEditar(boolean panelEditar) {
		this.panelEditar = panelEditar;
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

	public void setTrabajosTitulacion(List<TrabajoTitulacion> trabajosTitulacion) {
		this.trabajosTitulacion = trabajosTitulacion;
	}

	public void setTrabajoTitulacion(TrabajoTitulacion trabajoTitulacion) {
		this.trabajoTitulacion = trabajoTitulacion;
	}

}
