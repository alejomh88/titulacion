package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CartillaAlumno;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPTService;
import ec.edu.utmachala.titulacion.service.ExamenService;
import ec.edu.utmachala.titulacion.service.UsuarioService;

@Controller
@Scope("session")
public class ConsultarDatosComplexivoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantePeriodoService;

	@Autowired
	private ExamenService examenService;

	@Autowired
	private UsuarioService usuarioService;

	private List<CartillaAlumno> listCartillaAlumno;
	private EstudianteExamenComplexivoPP temaPractico;

	private Usuario estudiante;

	private List<Carrera> carreras;
	private Integer carrera;
	private BigDecimal calFinal;

	public BigDecimal getCalFinal() {
		return calFinal;
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

	public List<CartillaAlumno> getListCartillaAlumno() {
		return listCartillaAlumno;
	}

	public EstudianteExamenComplexivoPP getTemaPractico() {
		return temaPractico;
	}

	@PostConstruct
	public void init() {
		estudiante = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				false, new Object[] {});
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c " + "inner join c.peridosExamenes pe "
						+ "inner join pe.estudiantesPeriodos ep " + "inner join ep.estudiante e "
						+ "where e.email=?1 order by c.nombre",
				new Object[] { estudiante.getEmail() }, 0, false, new Object[] {});
		temaPractico = new EstudianteExamenComplexivoPP();
		if (carreras.size() == 1) {
			carrera = carreras.get(0).getId();
			obtenerNotaAndCasoPractico();
		}
	}

	public void obtenerNotaAndCasoPractico() {
		// if (carrera != 0) {
		// listCartillaAlumno = new ArrayList<CartillaAlumno>();
		// calFinal = new BigDecimal("0.00");
		// List<Examen> examenes = examenService.obtenerLista(
		// "select distinct e from Examen e "
		// + "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
		// + "inner join pe.carrera c inner join ep.estudiante es "
		// + "inner join es.permisos p inner join p.rol r "
		// + "where c.id=?1 and es.email=?2 and r.id='ESTU' order by e.fecha",
		// new Object[] { carrera, estudiante.getEmail() }, 0, false, new
		// Object[] {});
		// CartillaAlumno caPT = new CartillaAlumno();
		// caPT.setNombre("Teórica");
		// for (Examen e : examenes) {
		// if
		// (e.getEstudiantePeriodo().getPeriodoExamen().getTipoExamen().getId().compareToIgnoreCase("OR")
		// == 0)
		// caPT.setOrd(UtilsMath.parse(e.getCalificacion()));
		// else
		// caPT.setGra(UtilsMath.parse(e.getCalificacion()));
		// }
		// caPT.setCalFinal(caPT.getGra() != null ? caPT.getGra() :
		// caPT.getOrd());
		// listCartillaAlumno.add(caPT);
		//
		// EstudiantePeriodo ep = estudiantePeriodoService.obtenerObjeto(
		// "select ep from EstudiantePeriodo ep " + "left join fetch
		// ep.temasPracticos tp "
		// + "inner join ep.estudiante u " + "inner join ep.periodoExamen pe "
		// + "inner join pe.carrera c " + "where u.email=?1 and c.id=?2 order by
		// ep.id desc",
		// new Object[] { estudiante.getEmail(), carrera }, false, new Object[]
		// {});
		// if (ep.getTemasPracticos() == null ||
		// ep.getTemasPracticos().isEmpty()) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "No hay
		// caso práctico");
		// } else {
		// temaPractico = ep.getTemasPracticos().get(0);
		// CartillaAlumno caPPE = new CartillaAlumno();
		// caPPE.setNombre("Práctica escrita");
		// if (temaPractico.getValidarCalificacionOrdinaria() != null
		// && temaPractico.getValidarCalificacionOrdinaria())
		// caPPE.setOrd(temaPractico.getCalificacionEscritaOrdinaria());
		// if (temaPractico.getValidarCalificacionGracia() != null &&
		// temaPractico.getValidarCalificacionGracia())
		// caPPE.setGra(temaPractico.getCalificacionEscritaGracia());
		// caPPE.setCalFinal(caPPE.getGra() != null ? caPPE.getGra() :
		// caPPE.getOrd());
		// listCartillaAlumno.add(caPPE);
		// CartillaAlumno caPPO = new CartillaAlumno();
		// caPPO.setNombre("Práctica oral");
		// if (temaPractico.getValidarCalificacionOrdinaria() != null
		// && temaPractico.getValidarCalificacionOrdinaria())
		// caPPO.setOrd(temaPractico.getCalificacionOralOrdinaria());
		// if (temaPractico.getValidarCalificacionGracia() != null &&
		// temaPractico.getValidarCalificacionGracia())
		// caPPO.setGra(temaPractico.getCalificacionOralGracia());
		// caPPO.setCalFinal(caPPO.getGra() != null ? caPPO.getGra() :
		// caPPO.getOrd());
		// listCartillaAlumno.add(caPPO);
		// }
		//
		// for (CartillaAlumno ca : listCartillaAlumno) {
		// if (ca.getCalFinal() != null)
		// calFinal = calFinal.add(ca.getCalFinal());
		// }
		// calFinal = UtilsMath.redondearCalificacion(calFinal);
		// }

	}

	public void setCalFinal(BigDecimal calFinal) {
		this.calFinal = calFinal;
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

	public void setListCartillaAlumno(List<CartillaAlumno> listCartillaAlumno) {
		this.listCartillaAlumno = listCartillaAlumno;
	}

	public void setTemaPractico(EstudianteExamenComplexivoPP temaPractico) {
		this.temaPractico = temaPractico;
	}

}