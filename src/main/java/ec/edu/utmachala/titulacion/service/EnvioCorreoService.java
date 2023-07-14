package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.entity.Usuario;

public interface EnvioCorreoService {
	@Transactional
	public void enviarCodigoActualizacionDatos(Usuario usuario, String contrasena);

	@Transactional
	public void enviarContraseñaIngresoExamen(Usuario usuario, String contrasena);

	@Transactional
	public void enviarDatosComplexivo(String email, List<Examen> examenes, EstudianteExamenComplexivoPP temaPractico);

	@Transactional
	public void enviarDatosTrabajoTitulacion(String email, EstudianteTrabajoTitulacion estudianteTrabajoTitulacion);

	@Transactional
	public void enviarMensajeConfirmacion(Usuario usuario, String codigoConfirmacion);

	@Transactional
	public void enviarNotificiaEstudiante(String email);
}