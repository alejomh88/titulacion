package ec.edu.utmachala.titulacion.service;

import java.io.File;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;

public interface EstudianteTrabajoTitulacionService extends GenericService<EstudianteTrabajoTitulacion, Integer> {
	@Transactional
	public void actualizar(EstudianteTrabajoTitulacion asignatura);

	@Transactional
	public void creacionDocumentoDrive(EstudianteTrabajoTitulacion ett1, EstudianteTrabajoTitulacion ett2);

	@Transactional
	public void enviarCorreosEstudiantesFaltantesSubirArchivos(String proceso);

	@Transactional
	public void enviarCorreosEstudiantesInconsistenciasSubirArchivos(String proceso);

	@Transactional
	public void enviarCorreoSubidaArchivoBiblioteca(String proceso, String unidadAcademica);

	@Transactional
	public void enviarCorreoSubidaArchivoUMMO(String proceso, String unidadAcademica);

	@Transactional
	public void insertar(EstudianteTrabajoTitulacion asignatura);

	@Transactional
	public void insertar(List<EstudianteTrabajoTitulacion> asignaturas);

	@Transactional(readOnly = true)
	public String traerCalificaciones(EstudianteTrabajoTitulacion ett);

	@Transactional
	public File traerEPPenExcelETT(List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajosTitulacion);

}