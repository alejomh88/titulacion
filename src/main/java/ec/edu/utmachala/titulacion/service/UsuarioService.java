package ec.edu.utmachala.titulacion.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ec.edu.utmachala.titulacion.entity.Usuario;

public interface UsuarioService extends GenericService<Usuario, String> {
	@Transactional
	public void actualizar(Usuario usuario);

	@Transactional
	public boolean autorizacion(String login, String pass, String rol);

	@Transactional
	public void cambiarClave(String claveActual, String clave1, String clave2);

	@Transactional
	public void codigoConfirmacionCasoPractico(Usuario usuario);

	@Transactional
	public Boolean comprobarCodigo(Usuario usuario, String codigoConfirmacion);

	@Transactional
	public void enviarContrasena(Usuario usuario);

	@Transactional
	public void insertar(List<Usuario> usuarios);

	@Transactional
	public void insertar(Usuario usuario);

	@Transactional(readOnly = true)
	public void logout();

	@Transactional(readOnly = true)
	public Usuario obtenerParaComprobacion(String numeroDocumento, String nombre, String apellidoPaterno,
			String apellidoMaterno, Integer carrera);

	@Transactional
	public Usuario obtenerParaLogin();

	@Transactional
	public Usuario obtenerParaLogout();

	@Transactional(readOnly = true)
	public Boolean obtenerPermisoCertificado();

	@Transactional(readOnly = true)
	public Boolean[] obtenerPermisos();

	@Transactional(readOnly = true)
	public Usuario obtenerPorCedula(String cedula);

	@Transactional(readOnly = true)
	public Usuario obtenerPorCedulaParaCasosPracticos(String cedula);

	@Transactional(readOnly = true)
	public Usuario obtenerPorEmail(String email);

}