package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.timestamp;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.service.CarreraService;

@Controller
@Scope("session")
public class EscojerCarreraBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CarreraService carreraService;

	// @Autowired
	// private ExamenService examenService;

	private List<Carrera> listCarrera;
	private Integer carrera;
	private String email;

	public EscojerCarreraBean() {

	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getListCarrera() {
		return listCarrera;
	}

	public void iniciarExamen() {
		// if (carrera == 0) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
		// "SELECCIONE UNA CARRERA");
		// } else if
		// (!periodoExamenService.comprobarPeriodoAbiertoRendirExamen(carrera,
		// email)) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
		// "NO EXISTE UN PERIODO ABIERTO PARA RENDIR EL EXAMEN O USTED NO ESTÁ
		// ENRROLADO A UN PERIODO ACTIVO");
		// } else {
		// Examen examen = examenService.obtenerObjeto(
		// "select e from Examen e left join fetch e.preguntasExamenes pre "
		// + "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
		// + "inner join pe.carrera c inner join ep.estudiante es "
		// + "inner join es.permisos p inner join p.rol r "
		// + "where c.id=?1 and es.email=?2 and pe.fechaInicio<?3 and
		// pe.fechaCierre>=?3 and r.id='ESTU'",
		// new Object[] { carrera, email, timestamp() }, false, new Object[]
		// {});
		// if (examen == null) {
		// examen = examenService.crearExamen(email, carrera);
		// examenBean.comenzarExamen(examen, 0);
		// redireccionar("examen.jsf");
		// } else {
		// examen = examenService.obtenerObjeto(
		// "select e from Examen e left join fetch e.preguntasExamenes pre "
		// + "inner join e.estudiantePeriodo ep inner join ep.periodoExamen pe "
		// + "inner join pe.carrera c inner join ep.estudiante es "
		// + "inner join es.permisos p inner join p.rol r "
		// + "where pre.correctaIncorrecta is null and c.id=?1 and es.email=?2
		// and pe.fechaInicio<?3 and pe.fechaCierre>=?3 and r.id='ESTU'",
		// new Object[] { carrera, email, timestamp() }, false, new Object[]
		// {});
		// Boolean rf = false;
		// int fr = 0;
		// if (examen != null) {
		// for (PreguntaExamen pe : examen.getPreguntasExamenes()) {
		// if (pe.getCorrectaIncorrecta() == null) {
		// rf = true;
		// break;
		// }
		// }
		// for (PreguntaExamen pe : examen.getPreguntasExamenes())
		// if (pe.getCorrectaIncorrecta() == null)
		// fr++;
		// }
		// if (!rf) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
		// "USTED A CONTESTADO TODAS LAS PREGUNTAS");
		// } else {
		// examen.setIntento(examen.getIntento() + 1);
		// examenService.actualizar(examen);
		// if (examen.getIntento() > examen.getIntentoMaximo()) {
		// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
		// "USTED A EXECIDO EL NÚMERO MÁXIMO DE INTENTOS PARA INGRESAR AL
		// EXAMEN");
		// } else {
		// examenBean.comenzarExamen(examen, fr);
		// redireccionar("examen.jsf");
		// }
		//
		// }
		// }
		// }
	}

	@PostConstruct
	public void init() {
		email = SecurityContextHolder.getContext().getAuthentication().getName();
		carrera = 0;
		listCarrera = carreraService.obtenerLista(
				"select c from Carrera c inner join c.peridosExamenes pe " + "inner join pe.estudiantesPeriodos ep "
						+ "inner join ep.estudiante e "
						+ "where e.email=?1 and pe.fechaInicio<?2 and pe.fechaCierre>=?2 order by c.nombre",
				new Object[] { email, timestamp() }, 0, false, new Object[] {});
		if (listCarrera.size() == 1) {
			carrera = listCarrera.get(0).getId();
		}
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setListCarrera(List<Carrera> listCarrera) {
		this.listCarrera = listCarrera;
	}

}
