package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.primefaces.event.CloseEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.EstudianteBibliotecaService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class BibliotecaBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudianteBibliotecaService estudianteBibliotecaService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private ParametroService parametroService;

	private Integer carrera;

	private List<Carrera> carreras;

	private String criterioBusquedaEstudiante;
	private String criterioBusqueda;

	private Usuario estudiante;
	private List<EstudianteBiblioteca> listadoEstudiantesBiblioteca;
	private List<EstudianteBiblioteca> listadoEstudiantesBibliotecaEstadistica;
	private List<EstudianteBiblioteca> listadoEstudiantesBuscados;

	private boolean panelPrincipal;
	private boolean panelTrabajoEscrito;

	private String proceso;

	private List<Proceso> procesos;

	private EstudianteBiblioteca estudianteBiblioteca;

	private String urlDocumento;

	private FileInputStream fileInputStream;
	private InputStream stream;
	private StreamedContent documento;

	private String modalidadTitulacion;
	private String numeroPaginas;
	private InputStream archivo;

	private boolean esTrabajoTitulacion;
	private String estudianteTrabajoTitulacion2;
	private String tutor;

	private String modalidad;
	private String mostrar;

	private Integer numArchivosSubidos;
	private Integer numArchivosNoSubidos;
	private Integer numArchivosSinValidaroInconsistenciaUmmog;
	private Integer totalEstudiantes;

	private String tituloInvestigacion;
	private String infoEstudiante;
	private String citaDocumento;

	private String apellidoNombreE1;
	private String cedulaPasaporteE1;
	private String emailE1;
	private String telefonoE1;

	private String apellidoNombreE2;
	private String cedulaPasaporteE2;
	private String emailE2;
	private String telefonoE2;

	private boolean checkBusqueda;

	@PostConstruct
	public void a() {
		urlDocumento = "";
		panelPrincipal = true;
		panelTrabajoEscrito = false;

		esTrabajoTitulacion = false;

		proceso = "N";
		numArchivosNoSubidos = 0;
		numArchivosSubidos = 0;
		numArchivosSinValidaroInconsistenciaUmmog = 0;
		totalEstudiantes = 0;

		checkBusqueda = false;

		estudianteBiblioteca = new EstudianteBiblioteca();
	}

	public void actualizar() {

		urlDocumento = urlDocumento.trim();
		if (urlDocumento == null || urlDocumento == "") {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Debe ingresar el ruta del link del DSPACE.");
		} else if (urlDocumento.length() < 10) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese una ruta.");
		} else if (!urlDocumento.contains("http://repositorio.utmachala.edu.ec/handle")) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese correctamente la ruta del repositorio.");
		} else {
			try {
				if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0) {

					estudianteTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"urlBiblioteca\" = '"
									+ urlDocumento + "' WHERE id = "
									+ Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) + ";");
					enviarCorreoURLDSPACE();

				} else if (estudianteBiblioteca.getId().split("-")[0].compareTo("E") == 0) {
					estudianteExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"urlBiblioteca\" = '"
									+ urlDocumento + "' WHERE id = "
									+ Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) + ";");
					enviarCorreoURLDSPACE();
				}
			} catch (Exception e) {
				e.printStackTrace();
				presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
			}
			buscar();
			obtenerEstadistica();
			panelPrincipal = true;
			panelTrabajoEscrito = false;
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha guardado correctamente.");
		}
	}

	public void buscar() {

		if (mostrar.compareTo("N") == 0) {
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una opción de mostrar.");
		} else {

			if (listadoEstudiantesBiblioteca != null) {
				listadoEstudiantesBiblioteca.clear();
			}

			String procesados = "";
			String porProcesar = "";
			String pendientes = "";
			System.out.println("contenido de mostrar: " + mostrar);
			if (mostrar.compareTo("procesados") == 0) {
				procesados = " and ett.\"validarArchivo\" is " + true + " and ett.\"urlBiblioteca\" is not null ";
			} else if (mostrar.compareTo("porProcesar") == 0) {
				porProcesar = " and ett.\"validarArchivo\" is " + true + " and ett.\"urlBiblioteca\" is null ";
			} else if (mostrar.compareTo("pendientes") == 0) {
				pendientes = " and ett.\"validarArchivo\" is not " + true + " ";
			}

			if (criterioBusquedaEstudiante.isEmpty()) {
				if (modalidad.compareTo("M") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ " and ett.estudiante = u.id and ett.carrera=c.id order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("T") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id and ot.id<>4 order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("E") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.proceso='"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera="
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.carrera=c.id order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("A") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ot.id=4 and ett.carrera=c.id order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else {
					presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione todos los criterios de búsqueda.");
				}
			} else {
				if (modalidad.compareTo("M") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.\"opcionTitulacion\"=ot.id and ett.estudiante = u.id and ett.carrera=c.id union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ " and ett.estudiante = u.id and ett.carrera=c.id and (u.id like '%"
									+ criterioBusquedaEstudiante.trim()
									+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
									+ " like translate('%" + criterioBusquedaEstudiante.trim().toUpperCase()
									+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("T") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id and ot.id<>4 and (u.id like '%"
									+ criterioBusquedaEstudiante.trim()
									+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
									+ " like translate('%" + criterioBusquedaEstudiante.trim().toUpperCase()
									+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("E") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.proceso='"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera="
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.carrera=c.id and (u.id like '%"
									+ criterioBusquedaEstudiante.trim()
									+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
									+ " like translate('%" + criterioBusquedaEstudiante.trim().toUpperCase()
									+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu'))order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else if (modalidad.compareTo("A") == 0) {
					listadoEstudiantesBiblioteca = estudianteBibliotecaService.obtenerPorSql(
							"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
									+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = "
									+ carrera + procesados + porProcesar + pendientes
									+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id and ot.id=4 and (u.id like '%"
									+ criterioBusquedaEstudiante.trim()
									+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
									+ " like translate('%" + criterioBusquedaEstudiante.trim().toUpperCase()
									+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
							EstudianteBiblioteca.class);
				} else {
					presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione todos los criterios de búsqueda.");
				}
			}
		}
	}

	public void buscarEstudiante() {
		if (!criterioBusqueda.isEmpty()) {
			listadoEstudiantesBuscados = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.\"numeroActaCalificacion\" is not null and ett.\"opcionTitulacion\"=ot.id and ett.estudiante = u.id and ett.carrera=c.id and (u.id like '%"
							+ criterioBusqueda.trim()
							+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate('%" + criterioBusqueda.trim().toUpperCase()
							+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and (u.id like '%"
							+ criterioBusqueda.trim()
							+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate('%" + criterioBusqueda.trim().toUpperCase()
							+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese un número de cédula o apellido del estudiante a buscar.");

	}

	public String capitalizeNombresPersonas(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			} else if (Character.isWhitespace(chars[i])) {
				// You can add other chars here
				found = false;
			}
		}
		return String.valueOf(chars);
	}

	public String capitalizeString(String string) {
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			}
		}
		return String.valueOf(chars);
	}

	public void cerrarModal(CloseEvent event) {
		checkBusqueda = false;
	}

	public void descargarArchivo() {

		if (estudianteBiblioteca.getArchivo() != null) {
			try {
				System.out.println("Ingreso al descargar archivo.");
				fileInputStream = new FileInputStream(estudianteBiblioteca.getArchivo());
				if (fileInputStream.read() == -1)
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "El archivo se encuentra dañado.");
				else {
					stream = new FileInputStream(estudianteBiblioteca.getArchivo());
					documento = new DefaultStreamedContent(stream, "application/pdf",
							estudianteBiblioteca.getId() + "_" + estudianteBiblioteca.getApellidoNombre() + ".pdf");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "El estudiante aún no sube el documento a la plataforma.");
		}

	}

	public void descargarTapaPasta() {
		try {
			Report report = new Report();
			report.setFormat("PDF");
			report.setPath(UtilsReport.serverPathReport);
			if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0)
				report.setName("TT_TapaPasta");
			else
				report.setName("EC_TapaPasta");
			report.addParameter("proceso", estudianteBiblioteca.getProceso());
			report.addParameter("idEstudiante", estudianteBiblioteca.getEstudiante());
			report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));
			documento = UtilsReport.ejecutarReporte(report);

			// Image i = JasperPrintManager.printPageToImage((InputStream)
			// UtilsReport.ejecutarReporte(report).getStream(),
			// 0, 1);
			// documento = (StreamedContent) ImageIO.createImageInputStream(i);

		} catch (Exception ex) {
			System.out.println(ex.getClass());
		}
	}

	@Async
	public void enviarCorreoURLDSPACE() {
		if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0) {
			EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett where ett.id=?1",
					new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
					new Object[] {});
			EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.id!=?2",
					new Object[] { ett.getSeminarioTrabajoTitulacion().getId(), ett.getEstudiante().getId() }, false,
					new Object[] {});
			if (ett2 != null) {
				estudianteTrabajoTitulacionService
						.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"urlBiblioteca\" = '"
								+ urlDocumento + "' WHERE id = " + ett2.getId() + ";");
			}

			if (estudianteBiblioteca.getUrlBiblioteca() == null)
				if (ett2 == null) {
					String destinatarios[] = new String[1];
					destinatarios[0] = ett.getEstudiante().getEmail();
					String asunto = "PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo del trabajo escrito del proceso de titulación opción trabajo de titulación, ya se encuentra publicado en el repositorio digital de la Universidad Técnica de Machala, cuyo enlace al mismo es el siguiente: <a href='"
							+ urlDocumento + "' target=_blank>" + urlDocumento
							+ "</a>.</span></div><br /><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					try {
						UtilsMail.envioCorreo(destinatarios, asunto, detalle, listAdjunto, parametros);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					String destinatarios[] = new String[2];
					destinatarios[0] = ett.getEstudiante().getEmail();
					destinatarios[1] = ett2.getEstudiante().getEmail();
					String asunto = "PUBLICACIÓN DE REPOSITORIO DIGITAL";
					String detalle = "<div dir='ltr'><span style='color:#000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo del trabajo escrito del proceso de titulación opción trabajo de titulación, ya se encuentra publicado en el repositorio digital de la Universidad Técnica de Machala, cuyo enlace al mismo es el siguiente: <a href='"
							+ urlDocumento + "' target=_blank>" + urlDocumento
							+ "</a>.</span></div><br /><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					try {
						UtilsMail.envioCorreo(destinatarios, asunto, detalle, listAdjunto, parametros);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		} else if (estudianteBiblioteca.getId().split("-")[0].compareTo("E") == 0) {

			if (estudianteBiblioteca.getUrlBiblioteca() == null) {

				String destinatarios[] = new String[1];
				destinatarios[0] = estudianteBiblioteca.getEmail();
				String asunto = "PUBLICACIÓN DE REPOSITORIO DIGITAL";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que su archivo del trabajo escrito del proceso de titulación opción examen complexivo, ya se encuentra publicado en el repositorio digital de la Universidad Técnica de Machala, cuyo enlace al mismo es el siguiente: <a href='"
						+ urlDocumento + "' target=_blank>" + urlDocumento
						+ "</a>.</span></div><br /><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);

				try {
					UtilsMail.envioCorreo(destinatarios, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void enviarDatos() {

		modalidad = estudianteBiblioteca.getId().split("-")[0];
		obtenerProcesos();
		proceso = estudianteBiblioteca.getProceso();
		obtenerCarreras();
		carrera = estudianteBiblioteca.getCarrera();
		obtenerEstadistica();

		if (estudianteBiblioteca.getValidarArchivo() == true && estudianteBiblioteca.getUrlBiblioteca() != null)
			mostrar = "procesados";
		else if (estudianteBiblioteca.getValidarArchivo() == true && estudianteBiblioteca.getUrlBiblioteca() == null)
			mostrar = "porProcesar";
		else if (estudianteBiblioteca.getValidadoBiblioteca() != true)
			mostrar = "pendientes";

		criterioBusquedaEstudiante = estudianteBiblioteca.getEstudiante();

		buscar();

	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
		panelTrabajoEscrito = false;
	}

	public String getApellidoNombreE1() {
		return apellidoNombreE1;
	}

	public String getApellidoNombreE2() {
		return apellidoNombreE2;
	}

	public InputStream getArchivo() {
		return archivo;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public String getCedulaPasaporteE1() {
		return cedulaPasaporteE1;
	}

	public String getCedulaPasaporteE2() {
		return cedulaPasaporteE2;
	}

	public String getCitaDocumento() {
		return citaDocumento;
	}

	public String getCriterioBusqueda() {
		return criterioBusqueda;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public String getEmailE1() {
		return emailE1;
	}

	public String getEmailE2() {
		return emailE2;
	}

	public boolean getEsTrabajoTitulacion() {
		return esTrabajoTitulacion;
	}

	public Usuario getEstudiante() {
		return estudiante;
	}

	public EstudianteBiblioteca getEstudianteBiblioteca() {
		return estudianteBiblioteca;
	}

	public String getEstudianteTrabajoTitulacion2() {
		return estudianteTrabajoTitulacion2;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public String getInfoEstudiante() {
		return infoEstudiante;
	}

	public List<EstudianteBiblioteca> getListadoEstudiantesBiblioteca() {
		return listadoEstudiantesBiblioteca;
	}

	public List<EstudianteBiblioteca> getListadoEstudiantesBibliotecaEstadistica() {
		return listadoEstudiantesBibliotecaEstadistica;
	}

	public List<EstudianteBiblioteca> getListadoEstudiantesBuscados() {
		return listadoEstudiantesBuscados;
	}

	public String getModalidad() {
		return modalidad;
	}

	public String getModalidadTitulacion() {
		return modalidadTitulacion;
	}

	public String getMostrar() {
		return mostrar;
	}

	public Integer getNumArchivosNoSubidos() {
		return numArchivosNoSubidos;
	}

	public Integer getNumArchivosSinValidaroInconsistenciaUmmog() {
		return numArchivosSinValidaroInconsistenciaUmmog;
	}

	public Integer getNumArchivosSubidos() {
		return numArchivosSubidos;
	}

	public String getNumeroPaginas() {
		return numeroPaginas;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public InputStream getStream() {
		return stream;
	}

	public String getTelefonoE1() {
		return telefonoE1;
	}

	public String getTelefonoE2() {
		return telefonoE2;
	}

	public String getTituloInvestigacion() {
		return tituloInvestigacion;
	}

	public Integer getTotalEstudiantes() {
		return totalEstudiantes;
	}

	public String getTutor() {
		return tutor;
	}

	public String getUrlDocumento() {
		return urlDocumento;
	}

	public boolean isCheckBusqueda() {
		return checkBusqueda;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPanelTrabajoEscrito() {
		return panelTrabajoEscrito;
	}

	public void obtenerCarreras() {
		if (carreras != null) {
			carreras.clear();
		}
		if (modalidad.compareTo("M") == 0) {
			carreras = carreraService.obtenerPorSql(
					"select distinct c.* as nombre from exetasi.carreras c inner join exetasi.\"estudiantesExamenComplexivoPP\" epp on (epp.carrera=c.id) where epp.proceso='"
							+ proceso
							+ "' union select distinct c.* as nombre from exetasi.carreras c inner join exetasi.\"estudiantesTrabajosTitulacion\" ett on (ett.carrera=c.id) where ett.proceso='"
							+ proceso + "' order by nombre;",
					Carrera.class);
		} else if (modalidad.compareTo("T") == 0) {
			carreras = carreraService.obtenerLista(
					"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u inner join c.estudiantesTrabajosTitulaciones ett "
							+ "inner join ett.proceso p inner join ett.opcionTitulacion ot where u.email=?1 and p.id=?2 and ot.id<>4 order by c.nombre",
					new Object[] {
							SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
							proceso },
					0, false, new Object[] {});
		} else if (modalidad.compareTo("E") == 0) {
			carreras = carreraService.obtenerLista(
					"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u inner join c.estudiantesExamenComplexivoPP epp inner join epp.proceso p where u.email=?1 and p.id=?2 order by c.nombre",
					new Object[] {
							SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
							proceso },
					0, false, new Object[] {});
		} else if (modalidad.compareTo("A") == 0) {
			carreras = carreraService.obtenerLista(
					"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u inner join c.estudiantesTrabajosTitulaciones ett "
							+ "inner join ett.proceso p inner join ett.opcionTitulacion ot where u.email=?1 and p.id=?2 and ot.id=4 order by c.nombre",
					new Object[] {
							SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
							proceso },
					0, false, new Object[] {});
		}
	}

	public void obtenerEstadistica() {
		if (modalidad.compareTo("M") == 0) {
			listadoEstudiantesBibliotecaEstadistica = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
							+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = " + carrera
							+ " and ett.\"opcionTitulacion\"=ot.id and ett.estudiante = u.id and ett.carrera=c.id union select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, carreras c where ett.proceso = '"
							+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = " + carrera
							+ " and ett.carrera=c.id and ett.estudiante = u.id order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else if (modalidad.compareTo("T") == 0) {
			listadoEstudiantesBibliotecaEstadistica = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\",ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
							+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = " + carrera
							+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id and ot.id<>4 order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else if (modalidad.compareTo("E") == 0) {
			listadoEstudiantesBibliotecaEstadistica = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'E-'||epp.id as id, 'Examen Complexivo' as modalidad, epp.carrera as carrera, c.nombre as \"carreraNombre\", epp.estudiante as estudiante, epp.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, epp.archivo as archivo, epp.\"validarArchivo\" as \"validarArchivo\", epp.resumen as resumen, epp.abstract1 as abstract1, epp.\"palabrasClaves\" as \"palabrasClaves\", epp.\"tituloInvestigacion\" as \"tituloInvestigacion\", epp.\"urlBiblioteca\" as \"urlBiblioteca\", epp.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" epp, usuarios u, carreras c where epp.proceso='"
							+ proceso + "' and epp.\"numeroActaCalificacion\" is not null and epp.carrera=" + carrera
							+ " and epp.estudiante = u.id and epp.carrera=c.id order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else if (modalidad.compareTo("A") == 0) {
			listadoEstudiantesBibliotecaEstadistica = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'T-'||ett.id as id, 'Trabajo Titulacion' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\",ett.estudiante as estudiante, ett.proceso as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", ot.nombre as opcion from \"estudiantesTrabajosTitulacion\" ett, usuarios u, carreras c, \"opcionesTitulacion\" ot where ett.proceso = '"
							+ proceso + "' and ett.\"numeroActaCalificacion\" is not null and ett.carrera = " + carrera
							+ "and ett.estudiante = u.id and ett.\"opcionTitulacion\"=ot.id and ett.carrera=c.id and ot.id=4 order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		}
		if (listadoEstudiantesBibliotecaEstadistica != null || listadoEstudiantesBibliotecaEstadistica.size() != 0) {
			System.out.println("numero de elementos: " + listadoEstudiantesBibliotecaEstadistica.size());
			totalEstudiantes = 0;
			numArchivosSubidos = 0;
			numArchivosNoSubidos = 0;
			numArchivosSinValidaroInconsistenciaUmmog = 0;
			totalEstudiantes = listadoEstudiantesBibliotecaEstadistica.size();
			for (int i = 0; i < listadoEstudiantesBibliotecaEstadistica.size(); i++) {
				if (listadoEstudiantesBibliotecaEstadistica.get(i).getValidarArchivo() == null) {
					numArchivosSinValidaroInconsistenciaUmmog++;
				} else if (listadoEstudiantesBibliotecaEstadistica.get(i).getValidarArchivo() == false) {
					numArchivosSinValidaroInconsistenciaUmmog++;
				} else if (listadoEstudiantesBibliotecaEstadistica.get(i).getValidarArchivo() == true
						&& listadoEstudiantesBibliotecaEstadistica.get(i).getUrlBiblioteca() == null)
					numArchivosNoSubidos++;
				else if (listadoEstudiantesBibliotecaEstadistica.get(i).getUrlBiblioteca().contains("http:"))
					numArchivosSubidos++;
				else
					numArchivosNoSubidos++;
			}
		}
	}

	public void obtenerProcesos() {
		if (modalidad.compareTo("T") == 0 || modalidad.compareTo("E") == 0 || modalidad.compareTo("M") == 0
				|| modalidad.compareTo("A") == 0) {
			procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
					false, new Object[] {});
			if (proceso.compareTo("N") != 0) { // para que al cambiar de
												// modalidad se cargue el
												// combobox, por ello se da un
												// valor al Seleccione modalidad
												// como el de N, o el numero
												// cero en carreras
				obtenerCarreras();
				carrera = 0;
				mostrar = "N";
				totalEstudiantes = 0;
				numArchivosSubidos = 0;
				numArchivosNoSubidos = 0;
				numArchivosSinValidaroInconsistenciaUmmog = 0;
				if (listadoEstudiantesBiblioteca != null) {
					listadoEstudiantesBiblioteca.clear();
				}

			}
		} else if (modalidad.compareTo("N") == 0) {
			procesos.clear();
		}
	}

	public void presentarPanelDocumento() {
		try {
			urlDocumento = estudianteBiblioteca.getUrlBiblioteca();
			String urlArchivo = estudianteBiblioteca.getArchivo();
			File f = new File(urlArchivo);
			if (urlArchivo == null) {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "El estudiante aún no ha subido el documento.");
			} else if (f.exists()) {
				if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0) {
					esTrabajoTitulacion = true;
					System.out.println("Entro en el si: " + esTrabajoTitulacion);
					EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett where ett.id=?1",
							new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
							new Object[] {});
					EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.id!=?2",
							new Object[] { ett.getSeminarioTrabajoTitulacion().getId(), ett.getEstudiante().getId() },
							false, new Object[] {});
					apellidoNombreE1 = capitalizeNombresPersonas(ett.getEstudiante().getApellidoNombre());
					cedulaPasaporteE1 = ett.getEstudiante().getId();
					emailE1 = ett.getEstudiante().getEmail();
					telefonoE1 = ett.getEstudiante().getTelefono();

					infoEstudiante = "Apellidos y Nombres: "
							+ capitalizeNombresPersonas(ett.getEstudiante().getApellidoNombre()) + " |||| C.I.: "
							+ ett.getEstudiante().getId() + " ||||  Email: " + ett.getEstudiante().getEmail()
							+ " |||| Teléfono: " + ett.getEstudiante().getTelefono();
					tutor = capitalizeNombresPersonas(
							ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getApellidoNombre()
									+ " - ("
									+ ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getId()
									+ ") - ")
							+ ett.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente().getEmail();
					tituloInvestigacion = capitalizeString(ett.getTituloInvestigacion());
					estudianteTrabajoTitulacion2 = ett2 == null ? null
							: "Apellidos y Nombres: "
									+ capitalizeNombresPersonas(ett2.getEstudiante().getApellidoNombre())
									+ " |||| C.I.: " + ett2.getEstudiante().getId() + " |||| Email: "
									+ ett2.getEstudiante().getEmail() + " |||| Teléfono: "
									+ ett2.getEstudiante().getTelefono();
					if (ett2 != null) {
						apellidoNombreE2 = capitalizeNombresPersonas(ett2.getEstudiante().getApellidoNombre());
						cedulaPasaporteE2 = ett2.getEstudiante().getId();
						emailE2 = ett2.getEstudiante().getEmail();
						telefonoE2 = ett2.getEstudiante().getTelefono();
					}
				} else {
					esTrabajoTitulacion = false;
					EstudianteExamenComplexivoPP epp = estudianteExamenComplexivoPPService.obtenerObjeto(
							"select epp from EstudianteExamenComplexivoPP epp where epp.id=?1",
							new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
							new Object[] {});
					infoEstudiante = capitalizeNombresPersonas(
							estudianteBiblioteca.getApellidoNombre() + " - (" + estudianteBiblioteca.getEstudiante())
							+ ") - " + estudianteBiblioteca.getEmail() + " - " + estudianteBiblioteca.getTelefono();
					tituloInvestigacion = capitalizeString(estudianteBiblioteca.getTituloInvestigacion());
					System.out.println("Entro en el no: " + esTrabajoTitulacion);
					tutor = capitalizeNombresPersonas(epp.getTutor().getApellidoNombre()) + " - ("
							+ epp.getTutor().getId() + ") - " + epp.getTutor().getEmail();
					estudianteTrabajoTitulacion2 = null;
					apellidoNombreE1 = capitalizeNombresPersonas(epp.getEstudiante().getApellidoNombre());
					cedulaPasaporteE1 = epp.getEstudiante().getId();
					emailE1 = epp.getEstudiante().getEmail();
					telefonoE1 = epp.getEstudiante().getTelefono();
				}
				panelPrincipal = false;
				panelTrabajoEscrito = true;

				modalidadTitulacion = estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0
						? "Trabajo de titulación"
						: "Examen Complexivo";
				System.out.println(estudianteBiblioteca.getId().split("-")[0]);

				InputStream inSt = new FileInputStream(estudianteBiblioteca.getArchivo());
				PDDocument pdArchivo;
				pdArchivo = PDDocument.load(inSt);
				numeroPaginas = String.valueOf(pdArchivo.getNumberOfPages());
				pdArchivo.close();
				citaDocumento = estudianteBibliotecaService.obtenerCita(estudianteBiblioteca) + " " + numeroPaginas
						+ " p.";

			} else {
				presentaMensaje(FacesMessage.SEVERITY_INFO, "La ruta del archivo se encuentra incorrecta.");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "La ruta del archivo se encuentra incorrecta.");
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor del tipo: " + e.getClass());
		}

	}

	public void setApellidoNombreE1(String apellidoNombreE1) {
		this.apellidoNombreE1 = apellidoNombreE1;
	}

	public void setApellidoNombreE2(String apellidoNombreE2) {
		this.apellidoNombreE2 = apellidoNombreE2;
	}

	public void setArchivo(InputStream archivo) {
		this.archivo = archivo;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCedulaPasaporteE1(String cedulaPasaporteE1) {
		this.cedulaPasaporteE1 = cedulaPasaporteE1;
	}

	public void setCedulaPasaporteE2(String cedulaPasaporteE2) {
		this.cedulaPasaporteE2 = cedulaPasaporteE2;
	}

	public void setCheckBusqueda(boolean checkBusqueda) {
		this.checkBusqueda = checkBusqueda;
	}

	public void setCitaDocumento(String citaDocumento) {
		this.citaDocumento = citaDocumento;
	}

	public void setCriterioBusqueda(String criterioBusqueda) {
		this.criterioBusqueda = criterioBusqueda;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setEmailE1(String emailE1) {
		this.emailE1 = emailE1;
	}

	public void setEmailE2(String emailE2) {
		this.emailE2 = emailE2;
	}

	public void setEsTrabajoTitulacion(boolean esTrabajoTitulacion) {
		this.esTrabajoTitulacion = esTrabajoTitulacion;
	}

	public void setEstudiante(Usuario estudiante) {
		this.estudiante = estudiante;
	}

	public void setEstudianteBiblioteca(EstudianteBiblioteca estudianteBiblioteca) {
		this.estudianteBiblioteca = estudianteBiblioteca;
	}

	public void setEstudianteTrabajoTitulacion2(String estudianteTrabajoTitulacion2) {
		this.estudianteTrabajoTitulacion2 = estudianteTrabajoTitulacion2;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

	public void setInfoEstudiante(String infoEstudiante) {
		this.infoEstudiante = infoEstudiante;
	}

	public void setListadoEstudiantesBiblioteca(List<EstudianteBiblioteca> listadoEstudiantesBiblioteca) {
		this.listadoEstudiantesBiblioteca = listadoEstudiantesBiblioteca;
	}

	public void setListadoEstudiantesBibliotecaEstadistica(
			List<EstudianteBiblioteca> listadoEstudiantesBibliotecaEstadistica) {
		this.listadoEstudiantesBibliotecaEstadistica = listadoEstudiantesBibliotecaEstadistica;
	}

	public void setListadoEstudiantesBuscados(List<EstudianteBiblioteca> listadoEstudiantesBuscados) {
		this.listadoEstudiantesBuscados = listadoEstudiantesBuscados;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}

	public void setModalidadTitulacion(String modalidadTitulacion) {
		this.modalidadTitulacion = modalidadTitulacion;
	}

	public void setMostrar(String mostrar) {
		this.mostrar = mostrar;
	}

	public void setNumArchivosNoSubidos(Integer numArchivosNoSubidos) {
		this.numArchivosNoSubidos = numArchivosNoSubidos;
	}

	public void setNumArchivosSinValidaroInconsistenciaUmmog(Integer numArchivosSinValidaroInconsistenciaUmmog) {
		this.numArchivosSinValidaroInconsistenciaUmmog = numArchivosSinValidaroInconsistenciaUmmog;
	}

	public void setNumArchivosSubidos(Integer numArchivosSubidos) {
		this.numArchivosSubidos = numArchivosSubidos;
	}

	public void setNumeroPaginas(String numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPanelTrabajoEscrito(boolean panelTrabajoEscrito) {
		this.panelTrabajoEscrito = panelTrabajoEscrito;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public void setTelefonoE1(String telefonoE1) {
		this.telefonoE1 = telefonoE1;
	}

	public void setTelefonoE2(String telefonoE2) {
		this.telefonoE2 = telefonoE2;
	}

	public void setTituloInvestigacion(String tituloInvestigacion) {
		this.tituloInvestigacion = tituloInvestigacion;
	}

	public void setTotalEstudiantes(Integer totalEstudiantes) {
		this.totalEstudiantes = totalEstudiantes;
	}

	public void setTutor(String tutor) {
		this.tutor = tutor;
	}

	public void setUrlDocumento(String urlDocumento) {
		this.urlDocumento = urlDocumento;
	}

	public void validarDocumento() {
		try {
			if (estudianteBiblioteca.getId().split("-")[0].compareTo("T") == 0) {

				estudianteTrabajoTitulacionService.actualizarSQL(
						"UPDATE \"estudiantesTrabajosTitulacion\" SET \"validadoBiblioteca\" = 'true' WHERE id = "
								+ Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) + ";");
				EstudianteTrabajoTitulacion ett = estudianteTrabajoTitulacionService.obtenerObjeto(
						"select ett from EstudianteTrabajoTitulacion ett where ett.id=?1",
						new Object[] { Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) }, false,
						new Object[] {});
				EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
						"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.id!=?2",
						new Object[] { ett.getSeminarioTrabajoTitulacion().getId(), ett.getEstudiante().getId() },
						false, new Object[] {});
				if (ett2 != null) {
					estudianteTrabajoTitulacionService.actualizarSQL(
							"UPDATE \"estudiantesTrabajosTitulacion\" SET \"validadoBiblioteca\" = 'true' WHERE id = "
									+ ett2.getId() + ";");
				}

			} else if (estudianteBiblioteca.getId().split("-")[0].compareTo("E") == 0) {
				estudianteExamenComplexivoPPService.actualizarSQL(
						"UPDATE \"estudiantesExamenComplexivoPP\" SET \"validadoBiblioteca\" = 'true' WHERE id = "
								+ Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]) + ";");
			}
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
		buscar();
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Se ha validado el documento.");
	}

}