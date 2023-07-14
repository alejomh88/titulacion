package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;

@Service
public class EstudianteBibliotecaServiceImpl extends GenericServiceImpl<EstudianteBiblioteca, Integer>
		implements EstudianteBibliotecaService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	public String obtenerCita(EstudianteBiblioteca estudianteBiblioteca) {
		String cita = "";

		String[] apellidoNombre = estudianteBiblioteca.getApellidoNombre().toLowerCase().split(" ");

		if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0) {
			System.out.println("codigo T: " + estudianteBiblioteca.getId().split("-")[1]);
			EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett where ett.id=?1",
					new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
					new Object[] {});
			EstudianteTrabajoTitulacion ett2 = new EstudianteTrabajoTitulacion();

			ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.id!=?2",
					new Object[] { ett.getSeminarioTrabajoTitulacion().getId(), ett.getEstudiante().getId() }, false,
					new Object[0]);

			for (int i = 0; i < apellidoNombre.length; i++) {
				if (i == 0)
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + apellidoNombre[i].substring(1) + " ";
				else if (i == 1)
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + apellidoNombre[i].substring(1) + ", ";
				else
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + ".";
			}

			if (ett2 != null && ett2.getId() != null) {

				String[] apellidoNombre2 = ett2.getEstudiante().getApellidoNombre().toLowerCase().split(" ");

				for (int i = 0; i < apellidoNombre2.length; i++) {
					if (i == 0)
						cita += ", " + Character.toUpperCase(apellidoNombre2[i].charAt(0))
								+ apellidoNombre2[i].substring(1) + " ";
					if (i == 1)
						cita += Character.toUpperCase(apellidoNombre2[i].charAt(0)) + apellidoNombre2[i].substring(1)
								+ ", ";
					else
						cita += Character.toUpperCase(apellidoNombre2[i].charAt(0)) + ".";
				}
			}

			cita += " (" + ett.getFechaSustentacion().toString().split("-")[0] + ")";

			cita += " " + Character.toUpperCase(ett.getTituloInvestigacion().charAt(0))
					+ ett.getTituloInvestigacion().toLowerCase().substring(1);

			cita += " (trabajo de titulación).";

			cita += " UTMACH, ";

			String[] unidadAcademica = ett.getCarrera().getUnidadAcademica().getNombre().toLowerCase().split(" ");

			for (int i = 0; i < unidadAcademica.length; i++)
				if (i == (unidadAcademica.length - 1))
					cita += Character.toUpperCase(unidadAcademica[i].charAt(0)) + unidadAcademica[i].substring(1)
							+ ", ";
				else if (i == 2)
					cita += unidadAcademica[i] + " ";
				else
					cita += Character.toUpperCase(unidadAcademica[i].charAt(0)) + unidadAcademica[i].substring(1) + " ";

			cita += "Machala, Ecuador.";

		} else if (estudianteBiblioteca.getId().split("-")[0].compareTo("E") == 0) {
			EstudianteExamenComplexivoPP epp = new EstudianteExamenComplexivoPP();

			epp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select epp from EstudianteExamenComplexivoPP epp where epp.id=?1",
					new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
					new Object[0]);

			for (int i = 0; i < apellidoNombre.length; i++) {
				if (i == 0)
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + apellidoNombre[i].substring(1) + " ";
				else if (i == 1)
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + apellidoNombre[i].substring(1) + ", ";
				else
					cita += Character.toUpperCase(apellidoNombre[i].charAt(0)) + ".";
			}

			if (epp.getFechaSustentacionGracia() == null)
				cita += " (" + epp.getFechaSustentacionOrdinaria().toString().split("-")[0] + ")";
			else
				cita += " (" + epp.getFechaSustentacionGracia().toString().split("-")[0] + ")";

			cita += " " + Character.toUpperCase(epp.getTituloInvestigacion().charAt(0))
					+ epp.getTituloInvestigacion().toLowerCase().substring(1);

			cita += " (examen complexivo).";

			cita += " UTMACH, ";

			String[] unidadAcademica = epp.getCarrera().getUnidadAcademica().getNombre().toLowerCase().split(" ");

			for (int i = 0; i < unidadAcademica.length; i++)
				if (i == (unidadAcademica.length - 1))
					cita += Character.toUpperCase(unidadAcademica[i].charAt(0)) + unidadAcademica[i].substring(1)
							+ ", ";
				else if (i == 2)
					cita += unidadAcademica[i] + " ";
				else
					cita += Character.toUpperCase(unidadAcademica[i].charAt(0)) + unidadAcademica[i].substring(1) + " ";

			cita += "Machala, Ecuador.";

		}
		return cita;
	}

}