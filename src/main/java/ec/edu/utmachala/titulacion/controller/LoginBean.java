package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.UsuarioService;

@Controller
@Scope("session")
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	private Usuario usuario;
	private Boolean[] permisos;
	private Boolean permisoCertificados;

	@PostConstruct
	public void a() {
		usuario = usuarioService.obtenerParaLogin();
		permisos = usuarioService.obtenerPermisos();
	}

	public Boolean[] getPermisos() {
		return permisos;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void logout() {
		usuarioService.logout();
	}

	public void setPermisos(Boolean[] permisos) {
		this.permisos = permisos;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}