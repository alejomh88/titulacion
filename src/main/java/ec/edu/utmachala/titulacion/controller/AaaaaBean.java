package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.PreguntaExamen;
import ec.edu.utmachala.titulacion.service.PreguntaExamenService;;

@Controller
@Scope("session")
public class AaaaaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private PreguntaExamenService preguntaExamenService;

	public AaaaaBean() {
	}

	public void ejecutar() {
		List<PreguntaExamen> preguntasExamenes = preguntaExamenService.obtenerLista(
				"select pe from PreguntaExamen pe "
						+ "inner join pe.examen e inner join e.estudianteExamenComplexivoPT ept "
						+ "inner join ept.proceso p inner join pe.pregunta pre "
						+ "where p.id='PT-010216A' and pre.activo=false and pe.respuesta!=pre.sostiApantisi",
				new Object[] {}, 0, false, new Object[] {});

		System.out.println("Número de registros: " + preguntasExamenes.size());

		for (PreguntaExamen pe : preguntasExamenes) {
			// System.out.println(pe.getId());
			pe.setRespuesta(pe.getPregunta().getSostiApantisi());
			preguntaExamenService.actualizar(pe);
		}
	}

}