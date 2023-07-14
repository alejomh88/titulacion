package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Permiso;
import ec.edu.utmachala.titulacion.entity.Rol;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.RolService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;

@Controller
@Scope("session")
public class DocenteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RolService rolService;

	private List<Usuario> listDocente;
	private String criterioBusquedaDocente;
	private Usuario docente;

	private boolean boolPanelPrincipal;
	private boolean boolPanelEditar;
	private boolean boolPanelInsertar;

	public DocenteBean() {
	}

	@PostConstruct
	public void a() {
		boolPanelPrincipal = true;
		boolPanelInsertar = false;
		boolPanelEditar = false;
	}

	public void actualizar() {
		if (docente.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese al menos un apellido");
		else if (docente.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nombre");
		else if (docente.getEmail().trim().isEmpty())
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su dirección de correo electrónico");
		else if (docente.getTelefono().trim().isEmpty() || docente.getTelefono().trim().length() != 10)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su número personal de celular");

		else {
			docente.setApellidoNombre(docente.getApellidoNombre().trim().toUpperCase());
			docente.setEmail(docente.getEmail().trim().toLowerCase());
			docente.setTelefono(docente.getTelefono().trim());

			usuarioService.actualizar(docente);

			boolPanelPrincipal = true;
			boolPanelInsertar = false;
			boolPanelEditar = false;

			presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente los datos del docente", "cerrar",
					true);
		}
	}

	public void buscar() {
		listDocente = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where (u.id like ?1 or translate(u.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') like "
						+ "translate(upper(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) "
						+ "and (r.id='DOTU' or r.id='DORE' or r.id='DOEV')) order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaDocente.trim() + "%" }, 0, false, new Object[0]);
	}

	public String getCriterioBusquedaDocente() {
		return criterioBusquedaDocente;
	}

	public Usuario getDocente() {
		return docente;
	}

	public List<Usuario> getListDocente() {
		return listDocente;
	}

	public void insertar() {
		if (docente.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese al menos un apellido");
		else if (docente.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nombre");
		else if (docente.getEmail().trim().isEmpty())
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su dirección de correo electrónico");
		else if (docente.getTelefono().trim().isEmpty() || docente.getTelefono().trim().length() != 10)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su número personal de celular");
		else {
			Usuario u = usuarioService.obtenerObjeto(
					"select u from Usuario u left join fetch u.docentesAsignaturas da where u.id=?1",
					new Object[] { docente.getId().trim() }, false, new Object[] {});
			if (u != null && u.getId().compareToIgnoreCase("") != 0) {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "El docente ya existe");
			} else {
				docente.setApellidoNombre(docente.getApellidoNombre().trim().toUpperCase());
				docente.setEmail(docente.getEmail().trim().toLowerCase());
				docente.setTelefono(docente.getTelefono().trim());
				docente.setActivo(true);

				Rol rol = rolService.obtenerPorNombre("DOEV");
				Rol rol1 = rolService.obtenerPorNombre("DOTU");
				Rol rol2 = rolService.obtenerPorNombre("DORE");
				Rol rol3 = rolService.obtenerPorNombre("DOTE");
				Permiso permiso = new Permiso();
				Permiso permiso1 = new Permiso();
				Permiso permiso2 = new Permiso();
				Permiso permiso3 = new Permiso();
				permiso.setRol(rol);
				permiso1.setRol(rol1);
				permiso2.setRol(rol2);
				permiso3.setRol(rol3);
				docente.setPermisos(new ArrayList<Permiso>());
				docente.getPermisos().add(permiso);
				docente.getPermisos().add(permiso1);
				docente.getPermisos().add(permiso2);
				docente.getPermisos().add(permiso3);
				permiso.setUsuario(docente);
				permiso1.setUsuario(docente);
				permiso2.setUsuario(docente);
				permiso3.setUsuario(docente);

				docente.setPassword(UtilSeguridad.generarClave(docente.getId()));

				usuarioService.insertar(docente);

				boolPanelPrincipal = true;
				boolPanelInsertar = false;
				boolPanelEditar = false;

				presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingresó correctamente los datos del docente");
			}
		}
	}

	public boolean isBoolPanelEditar() {
		return boolPanelEditar;
	}

	public boolean isBoolPanelInsertar() {
		return boolPanelInsertar;
	}

	public boolean isBoolPanelPrincipal() {
		return boolPanelPrincipal;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
			if (listDocente != null)
				listDocente.clear();
			criterioBusquedaDocente = "";
		}
	}

	public void prepararEditar() {
		boolPanelPrincipal = false;
		boolPanelInsertar = false;
		boolPanelEditar = true;
	}

	public void prepararInsertar() {
		boolPanelPrincipal = false;
		boolPanelInsertar = true;
		boolPanelEditar = false;
		docente = new Usuario();
	}

	public void regresar() {
		boolPanelPrincipal = true;
		boolPanelInsertar = false;
		boolPanelEditar = false;
	}

	public void setBoolPanelEditar(boolean boolPanelEditar) {
		this.boolPanelEditar = boolPanelEditar;
	}

	public void setBoolPanelInsertar(boolean boolPanelInsertar) {
		this.boolPanelInsertar = boolPanelInsertar;
	}

	public void setBoolPanelPrincipal(boolean boolPanelPrincipal) {
		this.boolPanelPrincipal = boolPanelPrincipal;
	}

	public void setCriterioBusquedaDocente(String criterioBusquedaDocente) {
		this.criterioBusquedaDocente = criterioBusquedaDocente;
	}

	public void setDocente(Usuario docente) {
		this.docente = docente;
	}

	public void setListDocente(List<Usuario> listDocente) {
		this.listDocente = listDocente;
	}

}