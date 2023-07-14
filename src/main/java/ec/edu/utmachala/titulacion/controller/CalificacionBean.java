package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.redireccionar;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.service.ExamenService;

@Controller
@Scope("session")
public class CalificacionBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ExamenService examenService;

	private String calificacion;
	private int examenId;

	public CalificacionBean() {
		if (examenId == 0)
			redireccionar("escojerCarrera.jsf");
	}

	public String getCalificacion() {
		if (examenId == 0)
			redireccionar("escojerCarrera.jsf");
		return calificacion;
	}

	public int getExamenId() {
		return examenId;
	}

	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}

	public void setExamenId(int examenId) {
		this.examenId = examenId;
	}

	public void verCalificacion() {
		if (examenId == 0)
			redireccionar("escojerCarrera.jsf");
		// if (examenService.validarVerCalificacionExamen(examenId)) {
		// calificacion = "Tiene preguntas por responder, utilice el segundo
		// intento que tiene.";
		// } else {
		Examen e = examenService.obtenerCalificacionExamen(examenId);
		// calificacion = String.valueOf(e.getCalificacion()) + "/" +
		// e.getPreguntasExamenes().size() + " puntos";
		// }
	}

}
