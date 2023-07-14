package ec.edu.utmachala.titulacion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.service.UsuarioService;

@Controller
@Scope("session")
public class CambiarClaveBean {

	@Autowired
	private UsuarioService usuarioService;

	private String claveActual;
	private String claveNueva1;
	private String claveNueva2;

	public CambiarClaveBean() {
	}

	public void cambiarClave() {
		usuarioService.cambiarClave(claveActual, claveNueva1, claveNueva2);
	}

	public String getClaveActual() {
		return claveActual;
	}

	public String getClaveNueva1() {
		return claveNueva1;
	}

	public String getClaveNueva2() {
		return claveNueva2;
	}

	public void setClaveActual(String claveActual) {
		this.claveActual = claveActual;
	}

	public void setClaveNueva1(String claveNueva1) {
		this.claveNueva1 = claveNueva1;
	}

	public void setClaveNueva2(String claveNueva2) {
		this.claveNueva2 = claveNueva2;
	}
}
