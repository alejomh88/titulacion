package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class EscogerEstudianteExamenComplexivoPPBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private UsuarioService usuarioService;

	private List<Carrera> carreras;
	private Integer carrera;
	private Carrera carreraSeleccionada;

	private List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private boolean panelPrincipal;
	private boolean panelSeleccion;

	@PostConstruct
	public void a() {
		carreras = carreraService.obtenerLista(
				"select ca from Carrera ca inner join ca.permisosCarreras pc inner join pc.usuario u where u.email=?1",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName() }, 0, false,
				new Object[] {});

		panelPrincipal = true;
		panelSeleccion = false;
	}

	public void escoger() {
		try {
			Usuario tutor = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
					new Object[] { SecurityContextHolder.getContext().getAuthentication().getName() }, false,
					new Object[] {});
			estudianteExamenComplexivoPP.setTutor(tutor);
			estudianteExamenComplexivoService.actualizar(estudianteExamenComplexivoPP);
			panelPrincipal = true;
			panelSeleccion = false;
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Estudiante escogido correctamente.");
		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor del tipo: " + e.getClass());
		}

	}

	public void establecerPanelPrincipal() {
		onRowSelect();
		panelPrincipal = true;
		panelSeleccion = false;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public Carrera getCarreraSeleccionada() {
		return carreraSeleccionada;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public List<EstudianteExamenComplexivoPP> getEstudiantesExamenComplexivoPP() {
		return estudiantesExamenComplexivoPP;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean ispanelSeleccion() {
		return panelSeleccion;
	}

	public void onRowSelect() {
		estudiantesExamenComplexivoPP = estudianteExamenComplexivoService.obtenerLista(
				"select distinct epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e inner join epp.proceso p inner join epp.carrera ca where ca.id=?1 and p.id='PT-031016' and e.id is not null",
				new Object[] { carreraSeleccionada.getId() }, 0, false, new Object[0]);
	}

	public void presentarPanelEscoger() {
		panelPrincipal = false;
		panelSeleccion = true;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCarreraSeleccionada(Carrera carreraSeleccionada) {
		this.carreraSeleccionada = carreraSeleccionada;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudiantesExamenComplexivoPP(List<EstudianteExamenComplexivoPP> estudiantesExamenComplexivoPP) {
		this.estudiantesExamenComplexivoPP = estudiantesExamenComplexivoPP;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setpanelSeleccion(boolean panelSeleccion) {
		this.panelSeleccion = panelSeleccion;
	}

}
