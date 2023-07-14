package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class ObtenerContrasenaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	private String email;

	public String getEmail() {
		return email;
	}

	public void obtenerContrasena() {
		if (email.isEmpty()) {
			presentaMensaje(FacesMessage.SEVERITY_WARN, "INGRESE SU CORREO ELECTRÓNICO");
		} else {
			Usuario usuario = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1 and u.activo=true",
					new Object[] { email.trim().toLowerCase() }, false, new Object[] {});
			if (usuario == null) {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "EL CORREO NO ESTÁ REGISTRADO");
			} else {
				usuarioService.enviarContrasena(usuario);
				presentaMensaje(FacesMessage.SEVERITY_INFO, "LA CONTRASEÑA FUE ENVIADA AL CORREO REGISTRADO");
				UtilsAplicacion.redireccionar("login.jsf");
			}
		}
	}

	public void setEmail(String email) {
		this.email = email;
	}

}