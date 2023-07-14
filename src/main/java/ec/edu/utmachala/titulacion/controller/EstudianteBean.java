package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.Permiso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.EstudianteBibliotecaService;
import ec.edu.utmachala.titulacion.service.PermisoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;

@Controller
@Scope("session")
public class EstudianteBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private PermisoService permisoService;

	@Autowired
	private EstudianteBibliotecaService estudianteBibliotecaService;

	private List<Usuario> estudiantes;
	private String criterioBusquedaEstudiante;
	private Usuario estudiante;

	private boolean boolPanelPrincipal;
	private boolean boolPanelEditar;

	private boolean checkBusqueda;

	private String criterioBusqueda;

	private List<EstudianteBiblioteca> listadoEstudiantesBuscados;

	private boolean permisoUMMOGAnalista;

	public EstudianteBean() {
	}

	@PostConstruct
	public void a() {
		boolPanelPrincipal = true;
		boolPanelEditar = false;

		checkBusqueda = false;

		List<Permiso> permisos = permisoService.obtenerLista(
				"select p from Permiso p inner join p.rol r inner join p.usuario u where u.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});
		int contador = 0;
		for (Permiso p : permisos)
			if (p.getRol().getId().compareToIgnoreCase("UMMO") == 0
					|| p.getRol().getId().compareToIgnoreCase("ANAL") == 0)
				contador++;

		permisoUMMOGAnalista = (contador >= 1 ? true : false);
	}

	public void actualizar() {
		if (estudiante.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese al menos un apellido");
		else if (estudiante.getApellidoNombre().compareToIgnoreCase("") == 0)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el nombre");
		else if (estudiante.getEmail().trim().isEmpty())
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su dirección de correo electrónico");
		else if (estudiante.getTelefono().trim().isEmpty() || estudiante.getTelefono().trim().length() != 10)
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese su número personal de celular");

		else {
			estudiante.setApellidoNombre(estudiante.getApellidoNombre().trim().toUpperCase());
			estudiante.setEmail(estudiante.getEmail().trim().toLowerCase());
			estudiante.setTelefono(estudiante.getTelefono().trim());

			usuarioService.actualizar(estudiante);

			boolPanelPrincipal = true;
			boolPanelEditar = false;

			presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualizó correctamente los datos del docente", "cerrar",
					true);
		}
	}

	public void buscar() {
		estudiantes = usuarioService.obtenerLista(
				"select distinct u from Usuario u inner join u.permisos p inner join p.rol r "
						+ "where ((u.id like ?1 or translate(u.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') "
						+ "like translate(upper(?1), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu'))) "
						+ "and (r.id='ESEC' or r.id='ESTT')) order by u.apellidoNombre",
				new Object[] { "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false, new Object[0]);
	}

	public void buscarEstudiante() {
		if (!criterioBusqueda.isEmpty()) {
			listadoEstudiantesBuscados = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, p.id||' ('||p.observacion||')' as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot, procesos p where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and ett.\"opcionTitulacion\"=ot.id and ett.proceso = p.id and (u.id like '%"
							+ criterioBusqueda.trim()
							+ "%' or translate(u.\"apellidoNombre\",'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') like translate('%"
							+ criterioBusqueda.trim().toUpperCase()
							+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, p.id||' ('||p.observacion||')' as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'Examen Complexivo' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c, procesos p where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and ett.proceso=p.id and (u.id like '%"
							+ criterioBusqueda.trim()
							+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu') like translate('%"
							+ criterioBusqueda.trim().toUpperCase()
							+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else {
			if (!listadoEstudiantesBuscados.isEmpty())
				listadoEstudiantesBuscados.clear();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese un número de cédula o apellido del estudiante a buscar.");
		}
	}

	public String getCriterioBusqueda() {
		return criterioBusqueda;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public Usuario getEstudiante() {
		return estudiante;
	}

	public List<Usuario> getEstudiantes() {
		return estudiantes;
	}

	public List<EstudianteBiblioteca> getListadoEstudiantesBuscados() {
		return listadoEstudiantesBuscados;
	}

	public boolean isBoolPanelEditar() {
		return boolPanelEditar;
	}

	public boolean isBoolPanelPrincipal() {
		return boolPanelPrincipal;
	}

	public boolean isCheckBusqueda() {
		return checkBusqueda;
	}

	public boolean isPermisoUMMOGAnalista() {
		return permisoUMMOGAnalista;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			if (estudiantes != null)
				estudiantes.clear();
			a();
			criterioBusquedaEstudiante = "";
			criterioBusqueda = "";
			if (listadoEstudiantesBuscados != null)
				listadoEstudiantesBuscados.clear();
		}
	}

	public void prepararEditar() {
		boolPanelPrincipal = false;
		boolPanelEditar = true;
	}

	public void regresar() {
		boolPanelPrincipal = true;
		boolPanelEditar = false;
	}

	public void setBoolPanelEditar(boolean boolPanelEditar) {
		this.boolPanelEditar = boolPanelEditar;
	}

	public void setBoolPanelPrincipal(boolean boolPanelPrincipal) {
		this.boolPanelPrincipal = boolPanelPrincipal;
	}

	public void setCheckBusqueda(boolean checkBusqueda) {
		this.checkBusqueda = checkBusqueda;
	}

	public void setCriterioBusqueda(String criterioBusqueda) {
		this.criterioBusqueda = criterioBusqueda;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setEstudiante(Usuario estudiante) {
		this.estudiante = estudiante;
	}

	public void setEstudiantes(List<Usuario> estudiantes) {
		this.estudiantes = estudiantes;
	}

	public void setListadoEstudiantesBuscados(List<EstudianteBiblioteca> listadoEstudiantesBuscados) {
		this.listadoEstudiantesBuscados = listadoEstudiantesBuscados;
	}

	public void setPermisoUMMOGAnalista(boolean permisoUMMOGAnalista) {
		this.permisoUMMOGAnalista = permisoUMMOGAnalista;
	}

}