package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.entityAux.CartillaAlumno;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Controller
@Scope("session")
public class ConsultarDatosTitulacionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private UsuarioService usuarioService;

	private List<CartillaAlumno> listCartillaAlumno;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;
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

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public List<CartillaAlumno> getListCartillaAlumno() {
		return listCartillaAlumno;
	}

	@PostConstruct
	public void init() {
		estudiante = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				false, new Object[] {});
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.estudiantesTrabajosTitulaciones ett "
						+ "inner join ett.estudiante e where e.email=?1 order by c.nombre",
				new Object[] { estudiante.getEmail() }, 0, false, new Object[] {});
		estudianteTrabajoTitulacion = new EstudianteTrabajoTitulacion();
		if (carreras.size() == 1) {
			carrera = carreras.get(0).getId();
			obtenerTrabajoTitulacion();
		}
	}

	public void obtenerTrabajoTitulacion() {
		if (carrera != 0) {
			listCartillaAlumno = new ArrayList<CartillaAlumno>();
			calFinal = new BigDecimal("0.00");
			estudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett " + "inner join ett.carrera c "
							+ "inner join ett.estudiante e " + "left join fetch ett.tutorias t "
							+ "where c.id=?1 and e.id=?2 order by t.fecha",
					new Object[] { carrera, estudiante.getId() }, false, new Object[0]);
			CartillaAlumno caPPE = new CartillaAlumno();
			CartillaAlumno caPPO = new CartillaAlumno();
			caPPE.setNombre("Práctica escrita");
			caPPO.setNombre("Práctica oral");
			if (estudianteTrabajoTitulacion.getValidarCalificacion() != null
					&& estudianteTrabajoTitulacion.getValidarCalificacion()) {
				caPPE.setOrd(estudianteTrabajoTitulacion.getCalificacionEscrita());
				caPPO.setOrd(estudianteTrabajoTitulacion.getCalificacionOral());
				caPPE.setCalFinal(caPPE.getOrd());
				caPPO.setCalFinal(caPPO.getOrd());
				listCartillaAlumno.add(caPPE);
				listCartillaAlumno.add(caPPO);
			}
			for (CartillaAlumno ca : listCartillaAlumno) {
				if (ca.getCalFinal() != null)
					calFinal = calFinal.add(ca.getCalFinal());
			}
			calFinal = UtilsMath.redondearCalificacion(calFinal);
		}
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

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setListCartillaAlumno(List<CartillaAlumno> listCartillaAlumno) {
		this.listCartillaAlumno = listCartillaAlumno;
	}

}