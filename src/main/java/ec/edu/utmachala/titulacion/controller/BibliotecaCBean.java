package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
public class BibliotecaCBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	private EstudianteExamenComplexivoPP temaPractico;
	private Usuario estudiante;

	private List<Carrera> carreras;
	private Integer carrera;

	public void actualizar() {
		if (carrera == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Escoja una carrera");
		else if (temaPractico.getNumPaginas() == null || temaPractico.getNumPaginas() == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el número de páginas");
		else if (temaPractico.getResumen() == null || temaPractico.getResumen().compareTo("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el resumen");
		else {
			StringTokenizer st = new StringTokenizer(temaPractico.getResumen());
			int palabras = st.countTokens();
			if (palabras < 250 || palabras > 300)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Debe ingresar de 250 a 300 palabras");
			else {
				temaPracticoService.actualizar(temaPractico);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Inserto correctamente el registro para biblioteca");
			}
		}
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public Usuario getEstudiante() {
		return estudiante;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return temaPractico;
	}

	@PostConstruct
	public void init() {
		estudiante = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				false, new Object[] {});
		// carreras = carreraService.obtenerLista(
		// "select distinct c from Carrera c inner join c.peridosExamenes pe "
		// + "inner join pe.estudiantesPeriodos ep inner join ep.estudiante e "
		// + "where e.email=?1 order by c.nombre",
		// new Object[] { estudiante.getEmail() }, 0, false, new Object[] {})
		carreras = new ArrayList<Carrera>();
		temaPractico = new EstudianteExamenComplexivoPP();
		if (carreras.size() == 1) {
			carrera = carreras.get(0).getId();
			obtenerTemaPractico();
		}
	}

	public void obtenerTemaPractico() {
		if (carrera != 0) {
			temaPractico = temaPracticoService.obtenerObjeto(
					"select tp from TemaPractico tp inner join tp.estudiantePeriodo ep "
							+ "inner join ep.estudiante e inner join ep.periodoExamen pe "
							+ "inner join pe.carrera c where e.id=?1 and c.id=?2",
					new Object[] { estudiante.getId(), carrera }, false, new Object[] {});
			if (temaPractico == null || temaPractico.getId() == null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No hay caso práctico");
			}
		}
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setEstudiante(Usuario estudiante) {
		this.estudiante = estudiante;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}