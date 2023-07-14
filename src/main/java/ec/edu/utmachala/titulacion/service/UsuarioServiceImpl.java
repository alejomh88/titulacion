package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilSeguridad.generadorContrasena;
import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;
import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.redireccionar;
import static ec.edu.utmachala.titulacion.utility.UtilsMail.envioCorreo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.dao.UsuarioDao;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Permiso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Service
public class UsuarioServiceImpl extends GenericServiceImpl<Usuario, String> implements UsuarioService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioDao usuarioDao;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private EnvioCorreoService envioCorreoService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	public void actualizar(Usuario usuario) {
		usuarioDao.actualizar(usuario);
	}

	public boolean autorizacion(String login, String pass, String rol) {
		Usuario usuario = obtenerObjeto("select u from Usuario u where u.email=?1 ", new Object[] { login }, false,
				new Object[0]);
		boolean bn = false;
		if (usuario == null)
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "EL USUARIO NO EXISTE");
		else {
			if (generarClave(pass).compareTo(usuario.getPassword()) == 0) {
				for (Permiso p : usuario.getPermisos())
					if (p.getRol().getId().compareTo(rol) == 0) {
						return true;
					}
				if (!bn)
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "EL USUARIO NO ES VALIDO PARA ESTA ACCIÓN");
			} else
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "LA CLAVE NO ES VALIDA");
		}
		return false;
	}

	public void cambiarClave(String claveActual, String clave1, String clave2) {
		Usuario usuario = usuarioDao.obtenerObjeto("select u from Usuario u where u.email=?1",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName() }, false,
				new Object[] {});
		if (claveActual.length() == 0 || clave1.length() == 0 || clave2.length() == 0) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese todos los datos requeridos");
		} else if (clave1.length() < 8 || clave2.length() < 8) {
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"La nueva contraseña debe tener una longitud minima de 8 caracteres");
		} else if (!compararClave(usuario.getPassword(), generarClave(claveActual))) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La contraseña actual es incorrecta");
		} else if (!compararClave(clave1, clave2)) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Las contraseñas nuevas no coinciden");
		} else if (compararClave(clave1, usuario.getId())) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La contraseña no puede ser igual a su cédula");
		} else if (compararClave(clave1, usuario.getEmail())) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La contraseña no puede ser igual a su email");
		} else {
			usuario.setPassword(generarClave(clave1));
			actualizar(usuario);
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Cambio de password exitoso");
			redireccionar("/titulacion/views/publicas/logout.jsf");
		}
	}

	public void codigoConfirmacionCasoPractico(Usuario usuario) {
		String contrasena = generadorContrasena(15);
		usuario.setPassword(generarClave(contrasena));
		usuarioDao.actualizar(usuario);

		String asunto = "CÓDIGO DE CONFIRMACIÓN PARA OBTENCIÓN DE CASO PRÁCTICO DEL EXAMEN COMPLEXIVO Utmach 2015";
		String detalle = "Estimad@ \n\n" + usuario.getId() + " - " + usuario.getApellidoNombre() + ","
				+ "\n\nReciba un atento saludo de parte de la UTMACH - SECCIÓN DE TITULACIÓN."
				+ "\n\nEl presente correo es para notificarle que usted ha generado un código para la obtención del caso práctico."
				+ "\n\nCódigo de confirmación: " + contrasena;
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean compararClave(String clave1, String clave2) {
		return clave1.compareTo(clave2) == 0 ? true : false;
	}

	public Boolean comprobarCodigo(Usuario usuario, String codigoConfirmacion) {
		ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);
		if (usuario.getPassword().trim()
				.compareTo(shaPasswordEncoder.encodePassword(codigoConfirmacion.trim(), null)) == 0) {
			usuario.setPassword(null);
			usuario.setActivo(true);
			usuarioDao.actualizar(usuario);

			envioCorreoService.enviarCodigoActualizacionDatos(usuario, codigoConfirmacion);
			return true;
		}
		return false;
	}

	public void enviarContrasena(Usuario usuario) {
		String contrasena = generadorContrasena(15);
		usuario.setPassword(generarClave(contrasena));
		usuarioDao.actualizar(usuario);
		envioCorreoService.enviarContraseñaIngresoExamen(usuario, contrasena);

	}

	public String generarClave(String clave) {
		ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);
		return shaPasswordEncoder.encodePassword(clave, null);
	}

	public void insertar(List<Usuario> usuarios) {
		// for (Usuario u : usuarios) {
		// if (obtenerPorNumeroDocumento(u.getId()) == null)
		// usuarioDao.insertar(u);
		// else {
		// usuarioDao.actualizar(u);
		// System.out.println("ENTRE PARA ACTUALIZAR UN USUARIO");
		// }
		// }
	}

	public void insertar(Usuario usuario) {
		usuarioDao.insertar(usuario);
	}

	public void logout() {
		SecurityContextHolder.clearContext();
		System.out.println("Salió de la sesión desde Usuario Service");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("/titulacion/views/publicas/login.jsf");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Usuario obtenerParaComprobacion(String numeroDocumento, String nombre, String apellidoPaterno,
			String apellidoMaterno, Integer carrera) {
		return usuarioDao.obtenerObjeto(
				"select u from Usuario u inner join u.estudiantesPeriodos ep inner join ep.periodoExamen pe inner join pe.carrera c where u.id=?1 and lower(u.nombre)=?2 and lower(u.apellidoPaterno)=?3 and lower(u.apellidoMaterno)=?4 and c.id=?5",
				new Object[] { numeroDocumento.trim(), nombre.trim().toLowerCase(),
						apellidoPaterno.trim().toLowerCase(), apellidoMaterno.trim().toLowerCase(), carrera },
				false, new Object[] {});
	}

	public Usuario obtenerParaLogin() {
		return usuarioDao.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				false, new Object[] {});
	}

	public Usuario obtenerParaLogout() {
		return usuarioDao.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase() },
				false, new Object[] {});
	}

	public Boolean obtenerPermisoCertificado() {
		Boolean a = true;

		return a;
	}

	@SuppressWarnings("unchecked")
	public Boolean[] obtenerPermisos() {
		Boolean[] permisos = new Boolean[13];
		for (GrantedAuthority ga : (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities()) {
			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0)
				permisos[0] = true;
			if (ga.getAuthority().compareToIgnoreCase("ANAL") == 0)
				permisos[1] = true;
			if (ga.getAuthority().compareToIgnoreCase("COTI") == 0)
				permisos[2] = true;
			if (ga.getAuthority().compareToIgnoreCase("DOEV") == 0)
				permisos[3] = true;
			if (ga.getAuthority().compareToIgnoreCase("DORE") == 0)
				permisos[4] = true;
			if (ga.getAuthority().compareToIgnoreCase("DOTU") == 0)
				permisos[5] = true;
			if (ga.getAuthority().compareToIgnoreCase("ESEC") == 0)
				permisos[6] = true;
			if (ga.getAuthority().compareToIgnoreCase("ESTT") == 0)
				permisos[7] = true;
			if (ga.getAuthority().compareToIgnoreCase("UMMO") == 0)
				permisos[8] = true;
			if (ga.getAuthority().compareToIgnoreCase("BIBL") == 0)
				permisos[9] = true;
			if (ga.getAuthority().compareToIgnoreCase("DOTE") == 0)
				permisos[10] = true;
			if (ga.getAuthority().compareToIgnoreCase("CERT") == 0)
				permisos[11] = true;
			if (ga.getAuthority().compareToIgnoreCase("COCA") == 0)
				permisos[12] = true;
		}

		try {
			List<EstudianteTrabajoTitulacion> lETT = new ArrayList<EstudianteTrabajoTitulacion>();
			lETT = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e where e.email=?1",
					new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

			if (lETT.size() > 0) {
				if (lETT.size() < 2) {
					EstudianteTrabajoTitulacion ett = lETT.get(0);
					if (ett.getNumeroActaCalificacion() == null && ett.getActualizarDatos() == null)
						FacesContext.getCurrentInstance().getExternalContext().redirect("actualizarDatos.jsf");
				} else {
					EstudianteTrabajoTitulacion ett1 = lETT.get(0);
					EstudianteTrabajoTitulacion ett2 = lETT.get(1);
					if ((ett1.getNumeroActaCalificacion() == null && ett1.getActualizarDatos() == null)
							|| (ett2.getNumeroActaCalificacion() == null && ett2.getActualizarDatos() == null))
						FacesContext.getCurrentInstance().getExternalContext().redirect("actualizarDatos.jsf");
				}
			} else {
				List<EstudianteExamenComplexivoPP> lECPP = new ArrayList<EstudianteExamenComplexivoPP>();
				lECPP = estudianteExamenComplexivoPPService.obtenerLista(
						"select epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e where e.email=?1",
						new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

				if (lECPP.size() > 0) {
					if (lECPP.size() < 2) {
						EstudianteExamenComplexivoPP epp = lECPP.get(0);
						if (epp.getNumeroActaCalificacion() == null && epp.getActualizarDatos() == null)
							FacesContext.getCurrentInstance().getExternalContext().redirect("actualizarDatos.jsf");
					} else {
						EstudianteExamenComplexivoPP epp1 = lECPP.get(0);
						EstudianteExamenComplexivoPP epp2 = lECPP.get(1);
						if ((epp1.getNumeroActaCalificacion() != null && epp1.getActualizarDatos() == null)
								|| (epp2.getNumeroActaCalificacion() != null && epp2.getActualizarDatos() == null))
							FacesContext.getCurrentInstance().getExternalContext().redirect("actualizarDatos.jsf");
					}
				}
			}
		} catch (IOException e) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}

		return permisos;
	}

	public Usuario obtenerPorCedula(String cedula) {
		return usuarioDao.obtenerObjeto(
				"select u from Usuario u left join fetch u.docentesAsignaturas da where u.id=?1",
				new Object[] { cedula.trim() }, false, new Object[] {});
	}

	public Usuario obtenerPorCedulaParaCasosPracticos(String cedula) {
		return usuarioDao.obtenerObjeto(
				"select u from Usuario u left join fetch u.docentesAsignaturas da where u.id=?1",
				new Object[] { cedula.trim() }, false, new Object[] {});
	}

	public Usuario obtenerPorEmail(String email) {
		return usuarioDao.obtenerObjeto("select u from Usuario u where u.email=?1", new Object[] { email.trim() },
				false, new Object[] {});
	}
}