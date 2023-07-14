package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.UsuarioService;;

@Controller
@Scope("session")
public class DocumentosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	private Usuario usuario;

	private boolean esAutoridad = false;
	private boolean esCoordinador = false;
	private boolean esUMMOG = false;
	private boolean esTutorEC = false;
	private boolean esEstudiante = false;

	public DocumentosBean() {
	}

	@PostConstruct
	public void a() {

		usuario = usuarioService.obtenerParaLogin();
		for (GrantedAuthority ga : (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities()) {
			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0 || ga.getAuthority().compareToIgnoreCase("ANAL") == 0
					|| ga.getAuthority().compareToIgnoreCase("BIBL") == 0
					|| ga.getAuthority().compareToIgnoreCase("COTI") == 0
					|| ga.getAuthority().compareToIgnoreCase("DOEV") == 0
					|| ga.getAuthority().compareToIgnoreCase("DORE") == 0
					|| ga.getAuthority().compareToIgnoreCase("DOTE") == 0
					|| ga.getAuthority().compareToIgnoreCase("DOTU") == 0
					|| ga.getAuthority().compareToIgnoreCase("UMMO") == 0
					|| ga.getAuthority().compareToIgnoreCase("COCA") == 0)
				esAutoridad = true;
			else
				esAutoridad = false;

			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0 || ga.getAuthority().compareToIgnoreCase("COTI") == 0
					|| ga.getAuthority().compareToIgnoreCase("COCA") == 0)
				esCoordinador = true;
			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0
					|| ga.getAuthority().compareToIgnoreCase("UMMO") == 0)
				esUMMOG = true;
			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0
					|| ga.getAuthority().compareToIgnoreCase("DOTE") == 0)
				esTutorEC = true;
			if (ga.getAuthority().compareToIgnoreCase("ADMI") == 0 || ga.getAuthority().compareToIgnoreCase("ESEC") == 0
					|| ga.getAuthority().compareToIgnoreCase("ESTT") == 0)
				esEstudiante = true;
		}

	}

	public Usuario getUsuario() {
		return usuario;
	}

	public boolean isEsAutoridad() {
		return esAutoridad;
	}

	public boolean isEsCoordinador() {
		return esCoordinador;
	}

	public boolean isEsEstudiante() {
		return esEstudiante;
	}

	public boolean isEsTutorEC() {
		return esTutorEC;
	}

	public boolean isEsUMMOG() {
		return esUMMOG;
	}

	public void setEsAutoridad(boolean esAutoridad) {
		this.esAutoridad = esAutoridad;
	}

	public void setEsCoordinador(boolean esCoordinador) {
		this.esCoordinador = esCoordinador;
	}

	public void setEsEstudiante(boolean esEstudiante) {
		this.esEstudiante = esEstudiante;
	}

	public void setEsTutorEC(boolean esTutorEC) {
		this.esTutorEC = esTutorEC;
	}

	public void setEsUMMOG(boolean esUMMOG) {
		this.esUMMOG = esUMMOG;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}