package ec.edu.utmachala.titulacion.service;

import java.io.File;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;

public interface EstudiantesExamenComplexivoPPService extends GenericService<EstudianteExamenComplexivoPP, Integer> {

	@Transactional
	public void actualizar(EstudianteExamenComplexivoPP temaPractico);

	@Transactional
	public void correoPrueba();

	@Transactional
	public void creacionDocumentoDrive(EstudianteExamenComplexivoPP epp);

	@Transactional
	public void enviarCorreosEstudiantesFaltantesSubirArchivos(String proceso);

	@Transactional
	public void enviarCorreosEstudiantesInconsistenciasSubirArchivos(String proceso);

	@Transactional
	public void enviarCorreoSubidaArchivoBiblioteca(String proceso, String unidadAcademica);

	@Transactional
	public void enviarCorreoSubidaArchivoUMMO(String proceso, String unidadAcademica);

	@Transactional
	public void insertar(EstudianteExamenComplexivoPP temaPractico);

	@Transactional
	public void insertarTemaPracticoEstudiante(EstudianteExamenComplexivoPP temaPractico,
			EstudianteExamenComplexivoPT estudianteExamenComplexivoPT);

	@Transactional
	public List<EstudianteExamenComplexivoPP> obtenerPorCarreraId(int carreraId);

	@Transactional
	public EstudianteExamenComplexivoPP obtenerPorId(int temaPractico);

	@Transactional(readOnly = true)
	public String traerCalificaciones(EstudianteExamenComplexivoPP epp);

	@Transactional
	public File traerEPPenExcelEPP(List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP);

	@Transactional
	public void verificarEstudianteReactivoPractico(String proceso);
}
