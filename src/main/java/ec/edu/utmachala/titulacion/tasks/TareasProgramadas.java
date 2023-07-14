package ec.edu.utmachala.titulacion.tasks;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.UnidadAcademica;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UnidadAcademicaService;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Component
public class TareasProgramadas {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private UnidadAcademicaService unidadAcademicaService;

	@Scheduled(cron = "00 18 16 * * MON-FRI")
	public void envioEstudiantesFaltanEscogerReactivosPracticos() {

		Proceso p = procesoService.obtenerObjeto(
				"select p from Proceso p where p.fechaInicio<=?1 and p.fechaCierre>=?1",
				new Object[] { UtilsDate.timestamp() }, false, new Object[] {});

		if (p != null) {
			estudianteExamenComplexivoPPService.verificarEstudianteReactivoPractico(p.getId());
		}
	}

	@Scheduled(cron = "00 30 22 * * MON-FRI")
	public void envioPrueba() {
		estudianteExamenComplexivoPPService.correoPrueba();
		System.out.println("ingreso al segundo método");
	}

	@Scheduled(cron = "00 20 09,12,14,16 * * MON-FRI")
	public void enviosCorreosEstudiantesAprobadosSinSubirArchivosoInconsistencias() {
		List<Proceso> procesos = procesoService.obtenerLista(
				"select p from Proceso p where p.fechaInicio<=?1 and p.fechaCierre>=?1 order by p.fechaInicio",
				new Object[] { UtilsDate.timestamp() }, 0, false, new Object[] {});

		System.out.println("Número de procesos: " + procesos.size());

		Proceso p = procesos.get(0);

		System.out.println("Proceso seleccionado: " + p.getId());

		if (p != null) {

			estudianteExamenComplexivoPPService.enviarCorreosEstudiantesFaltantesSubirArchivos(p.getId());
			estudianteExamenComplexivoPPService.enviarCorreosEstudiantesInconsistenciasSubirArchivos(p.getId());
			estudianteTrabajoTitulacionService.enviarCorreosEstudiantesFaltantesSubirArchivos(p.getId());
			estudianteTrabajoTitulacionService.enviarCorreosEstudiantesInconsistenciasSubirArchivos(p.getId());

		}
	}

	@Scheduled(cron = "00 00 11 * * MON-FRI")
	public void enviosCorreosUMMOGBiblioteca() {

		List<Proceso> procesos = procesoService.obtenerLista(
				"select p from Proceso p where p.fechaInicio<=?1 and p.fechaCierre>=?1 order by p.fechaInicio",
				new Object[] { UtilsDate.timestamp() }, 0, false, new Object[] {});

		System.out.println("Número de procesos: " + procesos.size());

		Proceso p = procesos.get(0);

		System.out.println("Proceso seleccionado: " + p.getId());

		if (p != null) {

			List<UnidadAcademica> unidadesAcademicas = unidadAcademicaService.obtenerUnidades();
			for (UnidadAcademica ua : unidadesAcademicas) {
				estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO(p.getId(), ua.getId());
				estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoBiblioteca(p.getId(), ua.getId());
				estudianteTrabajoTitulacionService.enviarCorreoSubidaArchivoUMMO(p.getId(), ua.getId());
				estudianteTrabajoTitulacionService.enviarCorreoSubidaArchivoBiblioteca(p.getId(), ua.getId());
			}
		}
	}
}
