package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.ArrayList;
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
import ec.edu.utmachala.titulacion.service.EnvioCorreoService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class FechasyComitesTemasPracticosBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPPService temaPracticoService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EnvioCorreoService envioCorreoService;

	private List<Carrera> listCarreras;
	private Integer carrera;
	private int consultaABC;
	private int consultaOrdGra;

	private List<EstudianteExamenComplexivoPP> listTemaPracticos;
	private EstudianteExamenComplexivoPP temaPractico;
	private List<Usuario> listEspecialista;

	private String criterioBusquedaEspecialista1;
	private String criterioBusquedaEspecialista2;
	private String criterioBusquedaEspecialista3;
	private Usuario especialista1;
	private Usuario especialista2;
	private Usuario especialista3;

	private String consultaBuscarEspecialista = "select distinct u from Usuario u inner join u.permisos p inner join p.rol r where (u.id like ?1 or lower(u.apellidoPaterno) like ?1 or lower(u.apellidoMaterno) like ?1 or lower(u.nombre) like ?1) and r.id='DOCE' order by u.apellidoPaterno, u.apellidoMaterno, u.nombre";
	private String consultaObtenerEspecialista = "select u from Usuario u where u.id=?1";
	private String consultaAI = "and (e.apellidoPaterno like 'A%' or e.apellidoPaterno like 'B%' or e.apellidoPaterno like 'C%' or e.apellidoPaterno like 'D%' or e.apellidoPaterno like 'E%' or e.apellidoPaterno like 'F%' or e.apellidoPaterno like 'G%' or e.apellidoPaterno like 'H%' or e.apellidoPaterno like 'I%') ";
	private String consultaJQ = "and (e.apellidoPaterno like 'J%' or e.apellidoPaterno like 'K%' or e.apellidoPaterno like 'L%' or e.apellidoPaterno like 'M%' or e.apellidoPaterno like 'N%' or e.apellidoPaterno like 'Ñ%' or e.apellidoPaterno like 'O%' or e.apellidoPaterno like 'P%' or e.apellidoPaterno like 'Q%') ";
	private String consultaRZ = "and (e.apellidoPaterno like 'R%' or e.apellidoPaterno like 'S%' or e.apellidoPaterno like 'T%' or e.apellidoPaterno like 'U%' or e.apellidoPaterno like 'V%' or e.apellidoPaterno like 'W%' or e.apellidoPaterno like 'X%' or e.apellidoPaterno like 'Y%' or e.apellidoPaterno like 'Z%') ";

	public void actualizar() {
		this.temaPracticoService.actualizar(this.temaPractico);
	}

	public void buscar() {
		if (consultaOrdGra == 1) {
			this.listTemaPracticos = this.temaPracticoService.obtenerLista(
					"select tp from TemaPractico tp inner join tp.estudiantePeriodo ep inner join ep.estudiante e inner join ep.periodoExamen pe inner join pe.proceso p inner join pe.carrera c where c.id=?1 and tp.estudiantePeriodo is not null and p.fechaInicio<=?2 and p.fechaCierre>?2 "
							+ (this.consultaABC == 2 ? this.consultaJQ
									: this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
							+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
					new Object[] { this.carrera, UtilsDate.timestamp() }, Integer.valueOf(0), false, new Object[0]);
		} else if (consultaOrdGra == 2) {
			this.listTemaPracticos = this.temaPracticoService.obtenerLista("select tp from TemaPractico tp "
					+ "inner join tp.estudiantePeriodo ep " + "inner join ep.estudiante e "
					+ "inner join ep.examenes ex " + "inner join ep.periodoExamen pe "
					+ "inner join pe.proceso p inner join pe.carrera c "
					+ "where c.id=?1 and tp.validarCalificacionOrdinaria=true and tp.estudiantePeriodo is not null and p.fechaInicio<=?2 and p.fechaCierre>?2 and ex.calificacion>=20 and (ex.calificacion+tp.calificacionEscritaOrdinaria+tp.calificacionOralOrdinaria)<69.5 "
					+ (this.consultaABC == 2 ? this.consultaJQ
							: this.consultaABC == 1 ? this.consultaAI : this.consultaRZ)
					+ "order by e.apellidoPaterno, e.apellidoMaterno, e.nombre",
					new Object[] { this.carrera, UtilsDate.timestamp() }, Integer.valueOf(0), false, new Object[0]);
		}
	}

	public void buscarEspecialista1() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista1.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void buscarEspecialista2() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista2.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void buscarEspecialista3() {
		this.listEspecialista = this.usuarioService.obtenerLista(this.consultaBuscarEspecialista,
				new Object[] { "%" + this.criterioBusquedaEspecialista3.toLowerCase() + "%" }, Integer.valueOf(0),
				false, new Object[0]);
	}

	public void cargarEspecialista1() {
		if ((this.temaPractico.getEspecialista2() != null) && (this.temaPractico.getEspecialista2().getId()
				.compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((this.temaPractico.getEspecialista3() != null) && (this.temaPractico.getEspecialista3().getId()
				.compareToIgnoreCase(this.especialista1.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			this.temaPractico
					.setEspecialista1((Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { this.especialista1.getId() }, false, new Object[0]));
			this.especialista1 = new Usuario();
		}
	}

	public void cargarEspecialista2() {
		if ((this.temaPractico.getEspecialista1() != null) && (this.temaPractico.getEspecialista1().getId()
				.compareToIgnoreCase(this.especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((this.temaPractico.getEspecialista3() != null) && (this.temaPractico.getEspecialista3().getId()
				.compareToIgnoreCase(this.especialista2.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			this.temaPractico
					.setEspecialista2((Usuario) this.usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
							new Object[] { this.especialista2.getId() }, false, new Object[0]));
			this.especialista2 = new Usuario();
		}
	}

	public void cargarEspecialista3() {
		if ((this.temaPractico.getEspecialista1() != null) && (this.temaPractico.getEspecialista1().getId()
				.compareToIgnoreCase(this.especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else if ((this.temaPractico.getEspecialista2() != null) && (this.temaPractico.getEspecialista2().getId()
				.compareToIgnoreCase(this.especialista3.getId()) == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El especialista ya existe, ingrese uno diferente");
		} else {
			temaPractico.setEspecialista3(usuarioService.obtenerObjeto(this.consultaObtenerEspecialista,
					new Object[] { this.especialista3.getId() }, false, new Object[0]));
			especialista3 = new Usuario();
		}
	}

	public void enviarCorreo() {
		// Usuario estudiante =
		// this.temaPractico.getEstudiantePeriodo().getEstudiante();
		// this.envioCorreoService.enviarNotificiaEstudiante(estudiante.getEmail());
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public int getConsultaABC() {
		return this.consultaABC;
	}

	public int getConsultaOrdGra() {
		return consultaOrdGra;
	}

	public String getCriterioBusquedaEspecialista1() {
		return this.criterioBusquedaEspecialista1;
	}

	public String getCriterioBusquedaEspecialista2() {
		return this.criterioBusquedaEspecialista2;
	}

	public String getCriterioBusquedaEspecialista3() {
		return this.criterioBusquedaEspecialista3;
	}

	public Usuario getEspecialista1() {
		return this.especialista1;
	}

	public Usuario getEspecialista2() {
		return this.especialista2;
	}

	public Usuario getEspecialista3() {
		return this.especialista3;
	}

	public List<Carrera> getListCarreras() {
		return this.listCarreras;
	}

	public List<Usuario> getListEspecialista() {
		return this.listEspecialista;
	}

	public List<EstudianteExamenComplexivoPP> getListTemaPracticos() {
		return this.listTemaPracticos;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return this.temaPractico;
	}

	@PostConstruct
	public void init() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase();
		this.listCarreras = this.carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u where u.email=?1 order by c.nombre",
				new Object[] { email }, Integer.valueOf(0), false, new Object[0]);

		if (this.listCarreras.size() == 1) {
			this.carrera = ((Carrera) this.listCarreras.get(0)).getId();
		}
	}

	public void insertarComiteEvaluador() {
		if (this.temaPractico.getFechaSustentacionOrdinaria() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la fecha de exposición");
		} else if ((this.temaPractico.getEspecialista1() == null)
				|| (this.temaPractico.getEspecialista1().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 1");
		} else if ((this.temaPractico.getEspecialista2() == null)
				|| (this.temaPractico.getEspecialista2().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 2");
		} else if ((this.temaPractico.getEspecialista3() == null)
				|| (this.temaPractico.getEspecialista3().getId().compareToIgnoreCase("") == 0)) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el especialista 3");
		} else {
			temaPracticoService.actualizar(this.temaPractico);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Inserto correctamente el comite", "cerrar",
					true);
		}
	}

	public void insertarFechaGracia() {
		if (this.temaPractico.getFechaSustentacionGracia() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese la fecha de sustentación de gracia");
		} else {
			temaPracticoService.actualizar(this.temaPractico);
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Inserto correctamente la fecha de sustentación de gracia", "cerrar", true);
		}
	}

	public void limpiar() {
	}

	public void limpiarObjetosBusquedaEspecialista() {
		this.listEspecialista = new ArrayList<Usuario>();
		this.criterioBusquedaEspecialista1 = "";
		this.criterioBusquedaEspecialista2 = "";
		this.criterioBusquedaEspecialista3 = "";
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setConsultaABC(int consultaABC) {
		this.consultaABC = consultaABC;
	}

	public void setConsultaOrdGra(int consultaOrdGra) {
		this.consultaOrdGra = consultaOrdGra;
	}

	public void setCriterioBusquedaEspecialista1(String criterioBusquedaEspecialista1) {
		this.criterioBusquedaEspecialista1 = criterioBusquedaEspecialista1;
	}

	public void setCriterioBusquedaEspecialista2(String criterioBusquedaEspecialista2) {
		this.criterioBusquedaEspecialista2 = criterioBusquedaEspecialista2;
	}

	public void setCriterioBusquedaEspecialista3(String criterioBusquedaEspecialista3) {
		this.criterioBusquedaEspecialista3 = criterioBusquedaEspecialista3;
	}

	public void setEspecialista1(Usuario especialista1) {
		this.especialista1 = especialista1;
	}

	public void setEspecialista2(Usuario especialista2) {
		this.especialista2 = especialista2;
	}

	public void setEspecialista3(Usuario especialista3) {
		this.especialista3 = especialista3;
	}

	public void setListCarreras(List<Carrera> listCarreras) {
		this.listCarreras = listCarreras;
	}

	public void setListEspecialista(List<Usuario> listEspecialista) {
		this.listEspecialista = listEspecialista;
	}

	public void setListTemaPracticos(List<EstudianteExamenComplexivoPP> listTemaPracticos) {
		this.listTemaPracticos = listTemaPracticos;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}
