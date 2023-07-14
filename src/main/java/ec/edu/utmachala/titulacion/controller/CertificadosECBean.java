package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.event.CloseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.Certificado;
import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;
import ec.edu.utmachala.titulacion.entity.Dependencia;
import ec.edu.utmachala.titulacion.entity.EstudianteBiblioteca;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.CertificadoEstudianteService;
import ec.edu.utmachala.titulacion.service.CertificadoService;
import ec.edu.utmachala.titulacion.service.DependenciaService;
import ec.edu.utmachala.titulacion.service.EstudianteBibliotecaService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMail;

@Controller
@Scope("session")
public class CertificadosECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private CertificadoService certificadoService;

	@Autowired
	private CertificadoEstudianteService certificadoEstudianteService;

	@Autowired
	private DependenciaService dependenciaService;

	@Autowired
	private EstudianteBibliotecaService estudianteBibliotecaService;

	private List<Proceso> procesos;
	private String proceso;

	private List<Proceso> procesosNoAdeudar;
	private String procesoNoAdeudar;

	private List<Carrera> carreras;
	private Integer carrera;

	private List<Carrera> carrerasNoAdeudar;
	private Integer carreraNoAdeudar;

	private List<Dependencia> dependencias;
	private String dependencia;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private List<Certificado> certificados;
	private List<CertificadoEstudiante> certificadosEstudiantes;

	private List<CertificadoEstudiante> certificadosEstudiantesNoAdeudar;

	private List<EstudianteBiblioteca> listadoEstudiantesBuscados;

	private EstudianteBiblioteca estudianteBiblioteca;

	private boolean panelPrincipal;

	private String criterioBusquedaEspecialista;
	private String criterioBusquedaEstudiante;

	private EstudianteExamenComplexivoPP estudianteEPPSeleccionado;

	private BigDecimal calificacionParteTeoricaFinal;
	private BigDecimal calificacionParteEscritaFinal;
	private BigDecimal calificacionParteOralFinal;
	private BigDecimal calificacionFinal;

	private Date fechaParteTeorica;
	private Grupo grupoExamen;

	private boolean checkBusqueda;

	private boolean habilitarBotonNoAdeudar;

	private String textoNoAdeudar;

	private String criterioBusqueda;

	@PostConstruct
	public void a() {
		procesos = procesoService.obtenerLista(
				"select p from Proceso p where p.fechaInicio>='2016-02-01' order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		procesosNoAdeudar = procesoService.obtenerLista(
				"select p from Proceso p where p.fechaInicio>='2016-02-01' order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});

		panelPrincipal = true;

		checkBusqueda = false;

		habilitarBotonNoAdeudar = true;
	}

	public void actualizarNoAdeudar() {
		for (CertificadoEstudiante ce : certificadosEstudiantesNoAdeudar)
			if (ce.getAdeuda() == null) {
				ce.setAdeuda(false);
				certificadoEstudianteService.actualizar(ce);
			}

		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Se han actualizado los registros correctamente.");

		obtenerCertificadosNoAdeudar();

	}

	public void buscar() {
		if (criterioBusquedaEstudiante.isEmpty())
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join fetch epp.carrera c inner join epp.estudiante e "
							+ "where c.id=?1 and p.id=?2 and epp.numeroActaCalificacion is not null order by e.apellidoNombre",
					new Object[] { carrera, proceso }, 0, false, new Object[0]);
		else
			listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join fetch epp.carrera c "
							+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or translate(e.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate(upper(?3), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) "
							+ "and epp.numeroActaCalificacion is not null order by e.apellidoNombre",
					new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
					new Object[0]);

		certificados = certificadoService.obtenerLista(
				"select c from Certificado c inner join c.carrera car inner join c.dependencia d where car.id=?1 order by d.nombre",
				new Object[] { carrera }, 0, false, new Object[] {});

		for (Certificado c : certificados)
			System.out.println("Certificado: " + c.getId() + ", de dependencia: " + c.getDependencia().getNombre());

	}

	public void buscarEstudiante() {
		if (!criterioBusqueda.isEmpty()) {
			listadoEstudiantesBuscados = estudianteBibliotecaService.obtenerPorSql(
					"select distinct 'E-'||ett.id as id, 'Examen Complexivo' as modalidad, ett.carrera as carrera, c.nombre as \"carreraNombre\", ett.estudiante as estudiante, p.id||' | '||p.observacion as proceso, u.\"apellidoNombre\" as \"apellidoNombre\", u.email as email, u.telefono as telefono, ett.archivo as archivo, ett.\"validarArchivo\" as \"validarArchivo\", ett.resumen as resumen, ett.abstract1 as abstract1, ett.\"palabrasClaves\" as \"palabrasClaves\", ett.\"tituloInvestigacion\" as \"tituloInvestigacion\", ett.\"urlBiblioteca\" as \"urlBiblioteca\", ett.\"validadoBiblioteca\" as \"validadoBiblioteca\", 'EXAMEN COMPLEXIVO' as opcion from \"estudiantesExamenComplexivoPP\" ett, usuarios u, usuarios u1, carreras c, procesos p, \"certificadosEstudiantes\" ce, \"permisosCarreras\" pc where ett.\"numeroActaCalificacion\" is not null and ett.estudiante = u.id and ett.carrera=c.id and ett.proceso=p.id and ce.epp=ett.id and pc.carrera=c.id and pc.usuario=u1.id and u1.email = '"
							+ UtilSeguridad.obtenerUsuario() + "' and (u.id like '%" + criterioBusqueda.trim()
							+ "%' or translate(u.\"apellidoNombre\", 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
							+ " like translate('%" + criterioBusqueda.trim().toUpperCase()
							+ "%', 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) order by \"apellidoNombre\" asc;",
					EstudianteBiblioteca.class);
		} else
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese un número de cédula o apellido del estudiante a buscar.");

	}

	public void cambio() {
	}

	public void cambioCarrera() {
		if (listadoEstudiantesExamenComplexivoPP != null)
			listadoEstudiantesExamenComplexivoPP.clear();
		if (certificadosEstudiantes != null)
			certificadosEstudiantes.clear();
		criterioBusquedaEstudiante = "";
	}

	public void cerrarModal(CloseEvent event) {
		checkBusqueda = false;
	}

	public void enviarDatos() {
		System.out.println(estudianteBiblioteca.getProceso());
		proceso = estudianteBiblioteca.getProceso().trim().split(" ")[0];
		obtenerCarreras();
		carrera = estudianteBiblioteca.getCarrera();
		Integer idEPP = Integer.parseInt(estudianteBiblioteca.getId().split("-")[1]);
		criterioBusquedaEstudiante = estudianteBiblioteca.getEstudiante();

		listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join fetch epp.carrera c "
						+ "inner join epp.estudiante e where c.id=?1 and p.id=?2 and (e.id like ?3 or translate(e.apellidoNombre, 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')"
						+ " like translate(upper(?3), 'ÁÀÉÈÍÌÓÒÚÙáàéèíìóòúù', 'AAEEIIOOUUaaeeiioouu')) "
						+ "and epp.numeroActaCalificacion is not null order by e.apellidoNombre",
				new Object[] { carrera, proceso, "%" + criterioBusquedaEstudiante.trim() + "%" }, 0, false,
				new Object[0]);
	}

	public void establecerPanelPrincipal() {
		buscar();
		panelPrincipal = true;
	}

	public BigDecimal getCalificacionFinal() {
		return calificacionFinal;
	}

	public BigDecimal getCalificacionParteEscritaFinal() {
		return calificacionParteEscritaFinal;
	}

	public BigDecimal getCalificacionParteOralFinal() {
		return calificacionParteOralFinal;
	}

	public BigDecimal getCalificacionParteTeoricaFinal() {
		return calificacionParteTeoricaFinal;
	}

	public Integer getCarrera() {
		return this.carrera;
	}

	public Integer getCarreraNoAdeudar() {
		return carreraNoAdeudar;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public List<Carrera> getCarrerasNoAdeudar() {
		return carrerasNoAdeudar;
	}

	public List<Certificado> getCertificados() {
		return certificados;
	}

	public List<CertificadoEstudiante> getCertificadosEstudiantes() {
		return certificadosEstudiantes;
	}

	public List<CertificadoEstudiante> getCertificadosEstudiantesNoAdeudar() {
		return certificadosEstudiantesNoAdeudar;
	}

	public String getCriterioBusqueda() {
		return criterioBusqueda;
	}

	public String getCriterioBusquedaEspecialista() {
		return criterioBusquedaEspecialista;
	}

	public String getCriterioBusquedaEstudiante() {
		return criterioBusquedaEstudiante;
	}

	public String getDependencia() {
		return dependencia;
	}

	public List<Dependencia> getDependencias() {
		return dependencias;
	}

	public EstudianteBiblioteca getEstudianteBiblioteca() {
		return estudianteBiblioteca;
	}

	public EstudianteExamenComplexivoPP getEstudianteEPPSeleccionado() {
		return estudianteEPPSeleccionado;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public Date getFechaParteTeorica() {
		return fechaParteTeorica;
	}

	public Grupo getGrupoExamen() {
		return grupoExamen;
	}

	public List<EstudianteBiblioteca> getListadoEstudiantesBuscados() {
		return listadoEstudiantesBuscados;
	}

	public List<EstudianteExamenComplexivoPP> getListadoEstudiantesExamenComplexivoPP() {
		return listadoEstudiantesExamenComplexivoPP;
	}

	public String getProceso() {
		return proceso;
	}

	public String getProcesoNoAdeudar() {
		return procesoNoAdeudar;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public List<Proceso> getProcesosNoAdeudar() {
		return procesosNoAdeudar;
	}

	public String getTextoNoAdeudar() {
		return textoNoAdeudar;
	}

	public void guardarAdeudar(CertificadoEstudiante certificadoEstudianteSeleccionado) {

		certificadoEstudianteService.actualizarSQL("UPDATE \"certificadosEstudiantes\" SET adeuda="
				+ certificadoEstudianteSeleccionado.getAdeuda() + ", \"descripcionAdeuda\"="
				+ (certificadoEstudianteSeleccionado.getDescripcionAdeuda() == null
						? certificadoEstudianteSeleccionado.getDescripcionAdeuda()
						: certificadoEstudianteSeleccionado.getDescripcionAdeuda().length() <= 2 ? null
								: "'" + certificadoEstudianteSeleccionado.getDescripcionAdeuda() + "'")
				+ " WHERE id=" + certificadoEstudianteSeleccionado.getId());

		if (certificadoEstudianteSeleccionado.getAdeuda()) {

			if (certificadoEstudianteSeleccionado.getCertificado().getDependencia().getId()
					.compareTo("ESTATITU") == 0) {

				List<Dependencia> dependencias = dependenciaService.obtenerPorSql(
						"select distinct d.* from dependencias d inner join certificados cer on (cer.dependencia=d.id) inner join carreras c on (cer.carrera=c.id) where c.id="
								+ carrera + " and d.nombre not like 'ESTA%' order by d.nombre asc",
						Dependencia.class);
				String dependenciasCarreras = "";
				for (Dependencia d : dependencias) {
					dependenciasCarreras += d.getNombre() + "<br/>";
				}

				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE PAGO DE 2DO O MÁS TÍTULOS (ESTUDIANTE PROFESIONAL)";
				String detalle = "<div><span>Le comunicamos que una vez realizada la respectiva revisi&oacute;n por el departamento de estad&iacute;stica se ha detectado que usted debe cancelar la especie del t&iacute;tulo por obtener su segundo t&iacute;tulo o m&aacute;s en una nueva carrera en la Universidad T&eacute;cnica de Machala (Art. 58 DEL REGLAMENTO DE TASAS DE LA UNIVERSIDAD T&Eacute;CNICA DE MACHALA).</span></div><br/><div>Valor a cancelar TTU-03 ($ 100,00)</div><br /><div>Nombre del banco: Banco de Machala<br />Tipo de cuenta: Corriente<br />Titular de cuenta: Universidad T&eacute;cnica de Machala<br />N&uacute;mero de cuenta: 1010120671<br />C&oacute;digo: 130127<br /><br />Le recordamos que en los predios universitarios existe una agencia del Banco de Machala para mayor comodidad.<br />Una vez que cancele en el banco debe entregar la papeleta (original y copia) en el Departamento de Tesorer&iacute;a ubicado en el edificio de Administraci&oacute;n Central.<br /><br />2. Debe cancelar el costo por concepto de cr&eacute;ditos de certificados de NO ADEUDAR en el Departamento de Tesorer&iacute;a (US$ 1,50 c/cr&eacute;dito) de las siguientes dependencias:<br /></span><span class='gmail-string' dir='auto'><br />"
						+ dependenciasCarreras
						+ "<br /><br />Presente una copia del comprobante emitido en Tesorer&iacute;a en cada una de las dependencias antes mencionadas.<br /><br />3. El recibo que le den en Tesorer&iacute;a (punto 1) debe ser entregado (original y copia) junto con los certificados de NO ADEUDAR (punto 2) en la oficina de la UMMOG de forma inmediata para que pueda continuar con su proceso de titulaci&oacute;n.</span></span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (certificadoEstudianteSeleccionado.getCertificado().getDependencia().getId()
					.compareTo("ESTACRED") == 0) {
				List<Dependencia> dependencias = dependenciaService.obtenerPorSql(
						"select distinct d.* from dependencias d inner join certificados cer on (cer.dependencia=d.id) inner join carreras c on (cer.carrera=c.id) where c.id="
								+ carrera + " and d.nombre not like 'ESTA%' order by d.nombre asc",
						Dependencia.class);
				String dependenciasCarreras = "";
				for (Dependencia d : dependencias) {
					dependenciasCarreras += d.getNombre() + "<br/>";
				}

				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE DE MÁS DE 30% DE LOS CRÉDITOS O ASIGNATURAS REPROBADAS (ESTUDIANTE IRREGULAR)";
				String detalle = "<div><span>Le comunicamos que una vez realizada la respectiva revisión por el departamento de estadística se ha detectado que usted debe cancelar la especie del título por reprobar en términos acumulativos, el treinta por ciento de materias o créditos de su malla curricular vigente (Art. 13 DEL REGLAMENTO DE RÉGIMEN ACADÉMICO DE LA UNIVERSIDAD TÉCNICA DE MACHALA).<br /><div>Valor a cancelar TTU-01 ($ 50,00)</div><br />Tipo de cuenta: Corriente<br />Titular de cuenta: Universidad T&eacute;cnica de Machala<br />N&uacute;mero de cuenta: 1010120671<br />C&oacute;digo: 130127<br /><br />Le recordamos que en los predios universitarios existe una agencia del Banco de Machala para mayor comodidad.<br />Una vez que cancele en el banco debe entregar la papeleta (original y copia) en el Departamento de Tesorer&iacute;a ubicado en el edificio de Administraci&oacute;n Central.<br /><br />2. Debe cancelar el costo por concepto de cr&eacute;ditos de certificados de NO ADEUDAR en el Departamento de Tesorer&iacute;a (US$ 1,50 c/cr&eacute;dito) de las siguientes dependencias:<br /></span><span class='gmail-string' dir='auto'><br />"
						+ dependenciasCarreras
						+ "<br /><br />Presente una copia del comprobante emitido en Tesorer&iacute;a en cada una de las dependencias antes mencionadas.<br /><br />3. El recibo que le den en Tesorer&iacute;a (punto 1) debe ser entregado (original y copia) junto con los certificados de NO ADEUDAR (punto 2) en la oficina de la UMMOG de forma inmediata para que pueda continuar con su proceso de titulaci&oacute;n.</span></span></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE CERTIFICADOS DE NO ADEUDAR";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Mediante el presente email se le notifica que usted presenta una deuda con la dependencia: "
						+ certificadoEstudianteSeleccionado.getCertificado().getDependencia().getNombre()
						+ ".</span></div><br /><div>A continuación se describe lo adeudado: </div><br/><div>"
						+ certificadoEstudianteSeleccionado.getDescripcionAdeuda()
						+ "</div><br/><div>Ante lo expuesto, solicitamos acercarse a la dependencia indicada y solucionar el inconveniente de forma inmediata, con ello usted continuaría normalmente dentro del proceso de titulación. </div><br/><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Registro guardado.");
		onRowSelect();
	}

	public void guardarDevolvio(CertificadoEstudiante certificadoEstudianteSeleccionado) {

		certificadoEstudianteService.actualizarSQL("UPDATE \"certificadosEstudiantes\" SET devolvio="
				+ certificadoEstudianteSeleccionado.getDevolvio() + ", \"descripcionDevolvio\"="
				+ (certificadoEstudianteSeleccionado.getDescripcionDevolvio() == null
						? certificadoEstudianteSeleccionado.getDescripcionDevolvio()
						: certificadoEstudianteSeleccionado.getDescripcionDevolvio().length() <= 2 ? null
								: "'" + certificadoEstudianteSeleccionado.getDescripcionDevolvio() + "'")
				+ " WHERE id=" + certificadoEstudianteSeleccionado.getId());

		if (certificadoEstudianteSeleccionado.getDevolvio() && certificadoEstudianteSeleccionado.getAdeuda()) {
			if (certificadoEstudianteSeleccionado.getCertificado().getDependencia().getId()
					.compareTo("ESTATITU") == 0) {

				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE PAGO DE 2DO O MÁS TÍTULOS (ESTUDIANTE PROFESIONAL)";
				String detalle = "<div><span>Le comunicamos que se ha registrado el pago de su 2do o más título en la plataforma de titulación. Por lo que puede continuar con normalidad su proceso de titulación.</span></div><br/><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (certificadoEstudianteSeleccionado.getCertificado().getDependencia().getId()
					.compareTo("ESTACRED") == 0) {

				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE DE MÁS DE 30% DE LOS CRÉDITOS O ASIGNATURAS REPROBADAS (ESTUDIANTE IRREGULAR)";
				String detalle = "<div><span>Le comunicamos que se ha registrado el pago de reprobación del 30% de la malla curricular en la plataforma de titulación. Por lo que puede continuar con normalidad su proceso de titulación.<br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				String destinatario = certificadoEstudianteSeleccionado.getEstudianteExamenComplexivoPP()
						.getEstudiante().getEmail();
				String asunto = "NOTIFICACIÓN SOBRE CERTIFICADOS DE NO ADEUDAR";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que usted ha realizado el pago o devolución de un objeto en la dependencia: "
						+ certificadoEstudianteSeleccionado.getCertificado().getDependencia().getNombre()
						+ ".</span></div><br /><div>Con los siguientes datos: </div><br/><div>"
						+ certificadoEstudianteSeleccionado.getDescripcionDevolvio()
						+ "</div><br/><div>Ante lo expuesto, se informa que puede continuar normalmente con el proceso de titulación.</div><br/><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br />";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				detalle += parametro.getPieEmail();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		presentaMensaje(FacesMessage.SEVERITY_INFO, "Registro guardado.");
		onRowSelect();
	}

	public void insertarFecha() {
		try {
			if (estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria().equals(null)
					|| estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria().equals("")
					|| estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria().equals(null)
					|| estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria().equals("")) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"No deje el campo vacío de fecha o lugar de sustentación. Por favor ingrese los campos requeridos.");
			} else {

				DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				estudiantesExamenComplexivoPPService
						.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"fechaSustentacionOrdinaria\"='"
								+ hourdateFormat.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
								+ "', \"lugarSustentacionOrdinaria\"='"
								+ estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria() + "' WHERE id="
								+ estudianteExamenComplexivoPP.getId() + ";");

				DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
				DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
				String[] destinatarios = { estudianteExamenComplexivoPP.getEstudiante().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista1().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista2().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista3().getEmail(),
						estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail() };

				String asunto = "ASIGNACIÓN FECHA Y LUGAR DE SUSTENTACIÓN DEL ESTUDIANTE "
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ " DE LA MODALIDAD EXAMEN COMPLEXIVO.";
				String detalle = "<div dir='ltr'><span style='color:#00000; font-size:16px;'>El presente email es para informar que la fecha de sustentación del estudiante <strong>"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> a las <strong>"
						+ formatoHora.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> en el lugar <strong>"
						+ estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria()
						+ "</strong>. <br /> <br /><div><div><br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br/> <br /><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Fecha y lugar de sustentación ordinaria guardada correctamente");
				buscar();

			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Por favor determine una fecha de sustentación ordinaria antes de guardarla.");
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void insertarFechaGracia() {
		try {
			if (estudianteExamenComplexivoPP.getFechaSustentacionGracia().equals(null)
					|| estudianteExamenComplexivoPP.getLugarSustentacionGracia().equals("")) {
				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Por favor ingrese una fecha de sustentación de gracia antes de guardar.");
			} else {
				DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

				estudiantesExamenComplexivoPPService
						.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"fechaSustentacionGracia\"='"
								+ hourdateFormat.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
								+ "', \"lugarSustentacionGracia\"='"
								+ estudianteExamenComplexivoPP.getLugarSustentacionGracia() + "' WHERE id="
								+ estudianteExamenComplexivoPP.getId() + ";");

				DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
				DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
				String[] destinatarios = { estudianteExamenComplexivoPP.getEstudiante().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista1().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista2().getEmail(),
						estudianteExamenComplexivoPP.getEspecialista3().getEmail(),
						estudianteExamenComplexivoPP.getEspecialistaSuplente1().getEmail() };
				String asunto = "ASIGNACIÓN FECHA DE SUSTENTACIÓN GRACIA DEL ESTUDIANTE  "
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ " DE LA MODALIDAD EXAMEN COMPLEXIVO.";
				String detalle = "<div dir='ltr'><span style='color:#000000; font-size:16px;'>El presente email es para informar que la fecha de sustentación de gracia del estudiante <strong>"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
						+ "</strong> a las <strong>"
						+ formatoHora.format(estudianteExamenComplexivoPP.getFechaSustentacionGracia())
						+ "</strong> en el lugar <strong>" + estudianteExamenComplexivoPP.getLugarSustentacionGracia()
						+ "</strong>. <br /> <br /><div><div> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br/> <p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

				presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Fecha y lugar de sustentación de gracia guardada correctamente");
				buscar();
			}
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Por favor determine una fecha de sustentación antes de guardarla.");
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}

	}

	public boolean isCheckBusqueda() {
		return checkBusqueda;
	}

	public boolean isHabilitarBotonNoAdeudar() {
		return habilitarBotonNoAdeudar;
	}

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public void obtenerCarreras() {
		carreras = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "inner join c.estudiantesExamenComplexivoPP ec inner join ec.proceso p "
						+ "where u.email=?1 and p.id=?2 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						proceso },
				0, false, new Object[] {});
		if (listadoEstudiantesExamenComplexivoPP != null)
			listadoEstudiantesExamenComplexivoPP.clear();
		if (certificadosEstudiantes != null)
			certificadosEstudiantes.clear();
		criterioBusquedaEstudiante = "";

	}

	public void obtenerCarrerasNoAdeudar() {
		carrerasNoAdeudar = carreraService.obtenerLista(
				"select distinct c from Carrera c inner join c.permisosCarreras pc inner join pc.usuario u "
						+ "inner join c.estudiantesExamenComplexivoPP ec inner join ec.proceso p "
						+ "where u.email=?1 and p.id=?2 order by c.nombre",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName().trim().toLowerCase(),
						procesoNoAdeudar },
				0, false, new Object[] {});
		textoNoAdeudar = "";
		habilitarBotonNoAdeudar = true;

	}

	public void obtenerCertificadosNoAdeudar() {
		try {
			certificadosEstudiantesNoAdeudar = certificadoEstudianteService.obtenerLista(
					"select distinct ce from CertificadoEstudiante ce inner join ce.certificado cer "
							+ "inner join cer.dependencia d inner join cer.carrera c "
							+ "inner join ce.estudianteExamenComplexivoPP epp inner join epp.proceso p "
							+ "where d.id=?1 and c.id=?2 and p.id=?3",
					new Object[] { dependencia, carreraNoAdeudar, procesoNoAdeudar }, 0, false, new Object[] {});

			int certificadosEstudiantesNulos = 0;
			int certificadosEstudiantesFalse = 0;
			int certificadosEstudiantesTrue = 0;

			for (CertificadoEstudiante ce : certificadosEstudiantesNoAdeudar) {
				if (ce.getAdeuda() == null) {
					certificadosEstudiantesNulos++;
				} else if (ce.getAdeuda() == false) {
					certificadosEstudiantesFalse++;
				} else if (ce.getAdeuda() == true) {
					certificadosEstudiantesTrue++;
				}
			}

			habilitarBotonNoAdeudar = (certificadosEstudiantesNulos > 0 ? false : true);

			textoNoAdeudar = "En la carrera y dependencia seleccionada existen: <br /> " + certificadosEstudiantesNulos
					+ " estudiantes no tienen registros de deuda o préstamo. <br /> " + certificadosEstudiantesFalse
					+ " estudiantes tienen registro de no adudar o no tienen préstamos.<br /> "
					+ certificadosEstudiantesTrue
					+ " estudiantes que tienen registro de si adeudan o tienen préstamos.";

		} catch (Exception ex) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en: " + ex.getClass());
		}
	}

	public void obtenerDependencias() {
		dependencias = dependenciaService.obtenerLista(
				"select d from Dependencia d inner join d.certificados cer inner join cer.carrera c "
						+ "inner join d.permisoscertificados percert inner join percert.usuario u where c.id=?1 "
						+ "and u.email=?2 order by d.nombre asc",
				new Object[] { carreraNoAdeudar, UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});
		textoNoAdeudar = "";
		habilitarBotonNoAdeudar = true;
	}

	public void onRowSelect() {

		certificadosEstudiantes = certificadoEstudianteService
				.obtenerPorSql("select distinct ce.* from carreras c inner join"
						+ " \"estudiantesExamenComplexivoPP\" epp on (epp.carrera=c.id) inner join"
						+ " usuarios e on (epp.estudiante=e.id) inner join"
						+ " procesos p on (epp.proceso=p.id) inner join"
						+ "	\"certificadosEstudiantes\" ce on (ce.epp=epp.id) inner join"
						+ " certificados cer on (ce.certificado=cer.id) inner join"
						+ " dependencias d on (cer.dependencia=d.id) inner join"
						+ " \"permisosCertificados\" pc on (pc.dependencia=d.id) inner join"
						+ " usuarios u on (pc.usuario=u.id) where p.id='" + proceso + "' and c.id=" + carrera
						+ " and u.email = '" + UtilSeguridad.obtenerUsuario() + "' and epp.id="
						+ estudianteEPPSeleccionado.getId() + " order by ce.id;", CertificadoEstudiante.class);

//		for (CertificadoEstudiante ce : certificadosEstudiantes) {
//			System.out.println("Certificados de los estudiantes: " + ce.getId());
//			System.out.println("certificado: " + ce.getCertificado());
//			System.out.println("depe: " + ce.getCertificado().getDependencia().getNombre());
//		}
	}

	public void presentaTexto(int nulos, int falsos, int verdaderos) {

	}

	public void setCalificacionFinal(BigDecimal calificacionFinal) {
		this.calificacionFinal = calificacionFinal;
	}

	public void setCalificacionParteEscritaFinal(BigDecimal calificacionParteEscritaFinal) {
		this.calificacionParteEscritaFinal = calificacionParteEscritaFinal;
	}

	public void setCalificacionParteOralFinal(BigDecimal calificacionParteOralFinal) {
		this.calificacionParteOralFinal = calificacionParteOralFinal;
	}

	public void setCalificacionParteTeoricaFinal(BigDecimal calificacionParteTeoricaFinal) {
		this.calificacionParteTeoricaFinal = calificacionParteTeoricaFinal;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreraNoAdeudar(Integer carreraNoAdeudar) {
		this.carreraNoAdeudar = carreraNoAdeudar;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setCarrerasNoAdeudar(List<Carrera> carrerasNoAdeudar) {
		this.carrerasNoAdeudar = carrerasNoAdeudar;
	}

	public void setCertificados(List<Certificado> certificados) {
		this.certificados = certificados;
	}

	public void setCertificadosEstudiantes(List<CertificadoEstudiante> certificadosEstudiantes) {
		this.certificadosEstudiantes = certificadosEstudiantes;
	}

	public void setCertificadosEstudiantesNoAdeudar(List<CertificadoEstudiante> certificadosEstudiantesNoAdeudar) {
		this.certificadosEstudiantesNoAdeudar = certificadosEstudiantesNoAdeudar;
	}

	public void setCheckBusqueda(boolean checkBusqueda) {
		this.checkBusqueda = checkBusqueda;
	}

	public void setCriterioBusqueda(String criterioBusqueda) {
		this.criterioBusqueda = criterioBusqueda;
	}

	public void setCriterioBusquedaEspecialista(String criterioBusquedaEspecialista) {
		this.criterioBusquedaEspecialista = criterioBusquedaEspecialista;
	}

	public void setCriterioBusquedaEstudiante(String criterioBusquedaEstudiante) {
		this.criterioBusquedaEstudiante = criterioBusquedaEstudiante;
	}

	public void setDependencia(String dependencia) {
		this.dependencia = dependencia;
	}

	public void setDependencias(List<Dependencia> dependencias) {
		this.dependencias = dependencias;
	}

	public void setEstudianteBiblioteca(EstudianteBiblioteca estudianteBiblioteca) {
		this.estudianteBiblioteca = estudianteBiblioteca;
	}

	public void setEstudianteEPPSeleccionado(EstudianteExamenComplexivoPP estudianteEPPSeleccionado) {
		this.estudianteEPPSeleccionado = estudianteEPPSeleccionado;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setFechaParteTeorica(Date fechaParteTeorica) {
		this.fechaParteTeorica = fechaParteTeorica;
	}

	public void setGrupoExamen(Grupo grupoExamen) {
		this.grupoExamen = grupoExamen;
	}

	public void setHabilitarBotonNoAdeudar(boolean habilitarBotonNoAdeudar) {
		this.habilitarBotonNoAdeudar = habilitarBotonNoAdeudar;
	}

	public void setListadoEstudiantesBuscados(List<EstudianteBiblioteca> listadoEstudiantesBuscados) {
		this.listadoEstudiantesBuscados = listadoEstudiantesBuscados;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesoNoAdeudar(String procesoNoAdeudar) {
		this.procesoNoAdeudar = procesoNoAdeudar;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

	public void setProcesosNoAdeudar(List<Proceso> procesosNoAdeudar) {
		this.procesosNoAdeudar = procesosNoAdeudar;
	}

	public void setTextoNoAdeudar(String textoNoAdeudar) {
		this.textoNoAdeudar = textoNoAdeudar;
	}

}
