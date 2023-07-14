package ec.edu.utmachala.titulacion.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.SeminarioTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.ParametroService;
import ec.edu.utmachala.titulacion.service.SeminarioTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.UsuarioService;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;;

@Controller
@Scope("session")
public class ActualizarDatosBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private ReporteService reporteService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private SeminarioTrabajoTitulacionService seminarioTrabajoTitulacionService;

	private Usuario usuario;

	private Boolean renderUrkund;

	private boolean adminFranklin;
	private boolean adminCarlos;

	private boolean activarMensajeActualizarDatos;

	public ActualizarDatosBean() {
	}

	@PostConstruct
	public void a() {
		usuario = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
				new Object[] { SecurityContextHolder.getContext().getAuthentication().getName() }, false,
				new Object[] {});

		renderUrkund = false;
		if (UtilSeguridad.obtenerRol("DOEV") || UtilSeguridad.obtenerRol("DORE") || UtilSeguridad.obtenerRol("DOTU"))
			renderUrkund = true;

		adminFranklin = UtilSeguridad.obtenerUsuario().compareTo("faconza@utmachala.edu.ec") == 0
				|| UtilSeguridad.obtenerUsuario().compareTo("cxvega@utmachala.edu.ec") == 0;

		adminCarlos = UtilSeguridad.obtenerUsuario().compareTo("cxvega@utmachala.edu.ec") == 0;

		activarMensajeActualizarDatos = false;

		try {
			List<EstudianteTrabajoTitulacion> lETT = new ArrayList<EstudianteTrabajoTitulacion>();
			lETT = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e where e.email=?1",
					new Object[] { usuario.getEmail() }, 0, false, new Object[] {});

			if (lETT.size() > 0) {
				if (lETT.size() < 2) {
					EstudianteTrabajoTitulacion ett = lETT.get(0);
					if (ett.getNumeroActaCalificacion() == null && ett.getActualizarDatos() == null)
						activarMensajeActualizarDatos = true;
				} else {
					EstudianteTrabajoTitulacion ett1 = lETT.get(0);
					EstudianteTrabajoTitulacion ett2 = lETT.get(1);
					if ((ett1.getNumeroActaCalificacion() == null && ett1.getActualizarDatos() == null)
							|| (ett2.getNumeroActaCalificacion() == null && ett2.getActualizarDatos() == null))
						activarMensajeActualizarDatos = true;
				}
			} else {
				List<EstudianteExamenComplexivoPP> lECPP = new ArrayList<EstudianteExamenComplexivoPP>();
				lECPP = estudianteExamenComplexivoPPService.obtenerLista(
						"select epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e where e.email=?1",
						new Object[] { usuario.getEmail() }, 0, false, new Object[] {});

				if (lECPP.size() > 0) {
					if (lECPP.size() < 2) {
						EstudianteExamenComplexivoPP epp = lECPP.get(0);
						if (epp.getNumeroActaCalificacion() == null && epp.getActualizarDatos() == null)
							activarMensajeActualizarDatos = true;
					} else {
						EstudianteExamenComplexivoPP epp1 = lECPP.get(0);
						EstudianteExamenComplexivoPP epp2 = lECPP.get(1);
						if ((epp1.getNumeroActaCalificacion() == null && epp1.getActualizarDatos() == null)
								|| (epp2.getNumeroActaCalificacion() == null && epp2.getActualizarDatos() == null))
							activarMensajeActualizarDatos = true;
					}
				}
			}
		} catch (Exception e) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}
	}

	public void actualizar() {
		usuario.setApellidoNombre(usuario.getApellidoNombre().trim().toUpperCase());
		usuario.setEmail(usuario.getEmail().trim().toLowerCase());
		if (usuario.getUsuarioUrkund() != null)
			usuario.setUsuarioUrkund(usuario.getUsuarioUrkund().trim().toLowerCase());
		usuario.setTelefono(usuario.getTelefono().trim());

		if (usuario.getApellidoNombre() == null)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese sus apellidos y nombres");
		else {
			Usuario usuarioEmail = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1 and u.id!=?2",
					new Object[] { usuario.getEmail(), usuario.getId() }, false, new Object[] {});
			Usuario usuarioCelular = usuarioService.obtenerObjeto(
					"select u from Usuario u where u.telefono=?1 and u.id!=?2",
					new Object[] { usuario.getTelefono(), usuario.getId() }, false, new Object[] {});
			if (usuarioEmail != null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El correo electrónicónico ya existe, ingrese uno diferente");
			} else if (usuarioCelular != null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"El número de celular ya existe, ingrese uno diferente");
			} else {
				usuarioService.actualizar(usuario);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Los datos fueron actualizados");
			}

			try {
				List<EstudianteTrabajoTitulacion> lETT = new ArrayList<EstudianteTrabajoTitulacion>();
				lETT = estudianteTrabajoTitulacionService.obtenerLista(
						"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e where e.email=?1",
						new Object[] { usuario.getEmail() }, 0, false, new Object[] {});

				if (lETT.size() > 0) {
					if (lETT.size() < 2) {
						EstudianteTrabajoTitulacion ett = lETT.get(0);
						if (ett.getNumeroActaCalificacion() == null && ett.getActualizarDatos() == null)
							estudianteTrabajoTitulacionService.actualizarSQL(
									"UPDATE \"estudiantesTrabajosTitulacion\" SET \"actualizarDatos\" = true WHERE id="
											+ ett.getId() + ";");
					} else {
						EstudianteTrabajoTitulacion ett1 = lETT.get(0);
						EstudianteTrabajoTitulacion ett2 = lETT.get(1);
						if ((ett1.getNumeroActaCalificacion() != null && ett1.getActualizarDatos() == null)
								|| (ett2.getNumeroActaCalificacion() != null && ett2.getActualizarDatos() == null))
							estudianteTrabajoTitulacionService.actualizarSQL(
									"UPDATE \"estudiantesTrabajosTitulacion\" SET \"actualizarDatos\" = true WHERE id="
											+ ett1.getId() + ";");
						estudianteTrabajoTitulacionService.actualizarSQL(
								"UPDATE \"estudiantesTrabajosTitulacion\" SET \"actualizarDatos\" = true WHERE id="
										+ ett2.getId() + ";");
					}
				} else {
					List<EstudianteExamenComplexivoPP> lECPP = new ArrayList<EstudianteExamenComplexivoPP>();
					lECPP = estudianteExamenComplexivoPPService.obtenerLista(
							"select epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e where e.email=?1",
							new Object[] { usuario.getEmail() }, 0, false, new Object[] {});

					if (lECPP.size() > 0) {
						if (lECPP.size() < 2) {
							EstudianteExamenComplexivoPP epp = lECPP.get(0);
							if (epp.getActualizarDatos() == null && epp.getActualizarDatos() == null)
								estudianteExamenComplexivoPPService.actualizarSQL(
										"UPDATE \"estudiantesExamenComplexivoPP\" SET \"actualizarDatos\" = true WHERE id="
												+ epp.getId() + ";");
						} else {
							EstudianteExamenComplexivoPP epp1 = lECPP.get(0);
							EstudianteExamenComplexivoPP epp2 = lECPP.get(1);
							if ((epp1.getNumeroActaCalificacion() != null && epp1.getActualizarDatos() == null)
									|| (epp2.getNumeroActaCalificacion() != null && epp2.getActualizarDatos() == null))
								estudianteExamenComplexivoPPService.actualizarSQL(
										"UPDATE \"estudiantesExamenComplexivoPP\" SET \"actualizarDatos\" = true WHERE id="
												+ epp1.getId() + ";");
							estudianteExamenComplexivoPPService.actualizarSQL(
									"UPDATE \"estudiantesExamenComplexivoPP\" SET \"actualizarDatos\" = true WHERE id="
											+ epp2.getId() + ";");
						}
					}
				}
			} catch (Exception e) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Error en el servidor de tipo: " + e.getClass());
				e.printStackTrace();
			}
		}
	}

	public void crearDocumentoDrive() {
		// List<EstudianteExamenComplexivoPP> estudiantesECPP =
		// estudianteExamenComplexivoPPService
		// .obtenerLista(
		// "select epp from EstudianteExamenComplexivoPP epp inner join
		// epp.especialista1 e1 inner join epp.especialista2 e2"
		// + " inner join epp.especialista3 e3 inner join
		// epp.especialistaSuplente1 es1 inner join epp.tutor tu"
		// + " inner join epp.proceso p where p.id='PT-010517' and (tu.id is not
		// null or e1 is not null or e2 is not null"
		// + " or e3 is not null) and epp.urlDrive is null",
		// new Object[] {}, 0, false, new Object[] {});
		//
		// List<EstudianteTrabajoTitulacion> estudiantesTT =
		// estudianteTrabajoTitulacionService.obtenerLista(
		// "select epp from EstudianteTrabajoTitulacion epp inner join
		// epp.especialista1 e1 inner join epp.especialista2 e2"
		// + " inner join epp.especialista3 e3 inner join
		// epp.especialistaSuplente1 es1 inner join epp.proceso p"
		// + " where p.id='PT-010517' and (e1 is not null or e2 is not null or
		// e3 is not null) and epp.urlDrive is null",
		// new Object[] {}, 0, false, new Object[] {});

		List<SeminarioTrabajoTitulacion> seminariosTrabajosTitulacion = seminarioTrabajoTitulacionService.obtenerLista(
				"select distinct stt from SeminarioTrabajoTitulacion stt inner join fetch stt.estudiantesTrabajosTitulacion ett inner join ett.proceso p inner join"
						+ " ett.especialista1 e1 inner join ett.especialista2 e2 inner join ett.especialista3 e3 inner join ett.especialistaSuplente1 es1"
						+ " where p.id='PT-010517' and (e1.id is not null or e2.id is not null or e3.id is not null or es1.id is not null) and ett.urlDrive is null",
				new Object[] {}, 0, false, new Object[] {});

		// System.out.println("El número de estudiantes con comité evauluador
		// epp: "
		// + (estudiantesECPP == null ? "Lista vacía" : "" +
		// estudiantesECPP.size()));
		//
		// System.out.println("El número de estudiantes con comité evauluador
		// ett: "
		// + (estudiantesTT == null ? "Lista vacía" : "" +
		// estudiantesTT.size()));
		//
		// System.out.println("El número de estudiantes con comité evaluador ett
		// y stt: "
		// + (estudiantesTT == null ? "Lista vacía" : "" +
		// seminariosTrabajosTitulacion.size()));
		//
		// for (EstudianteExamenComplexivoPP epp : estudiantesECPP) {
		// estudianteExamenComplexivoPPService.creacionDocumentoDrive(epp);
		// }

		System.out.println("Elementos encontrados: " + seminariosTrabajosTitulacion.size());

		for (SeminarioTrabajoTitulacion stt : seminariosTrabajosTitulacion) {
			if (stt.getEstudiantesTrabajosTitulacion().size() == 2) {
				System.out.println(
						"(2)El estudiante es: " + stt.getEstudiantesTrabajosTitulacion().get(0).getEstudiante().getId()
								+ ", en el seminario: " + stt.getId());
				System.out.println(
						"(2)El estudiante es: " + stt.getEstudiantesTrabajosTitulacion().get(1).getEstudiante().getId()
								+ ", en el seminario: " + stt.getId());
				// estudianteTrabajoTitulacionService.creacionDocumentoDrive(stt.getEstudiantesTrabajosTitulacion().get(0),
				// stt.getEstudiantesTrabajosTitulacion().get(1));
			} else if (stt.getEstudiantesTrabajosTitulacion().size() == 1) {
				System.out.println(
						"(1)El estudiante es: " + stt.getEstudiantesTrabajosTitulacion().get(0).getEstudiante().getId()
								+ ", en el seminario: " + stt.getId());
				// estudianteTrabajoTitulacionService.creacionDocumentoDrive(stt.getEstudiantesTrabajosTitulacion().get(0),
				// null);
			}
		}
	}

	public void enviarInfoCorreosCambiados() throws IOException {
		System.out.println("Ingresó al método pruebaCambioCorreos");
		File f = new File(UtilsArchivos.getRutaRaiz() + "/ListadoParticipantes1Correos.csv");
		int contador = 0;
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while (null != line) {
			String[] fields = line.split(";");

			Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { fields[0] }, false, new Object[] {});
			if (!u.getEmail().contains("@utmachala.edu")) {
				String destinatario = u.getEmail();
				String asunto = "IMPORTANTE TITULACIÓN: CAMBIO CORREOS CONVENCIONALES POR CORREOS INSTITUCIONALES ";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que a partir de este momento el correo electrónico por el cual usted va a ingresar a la plataforma de titulación y recibir información sobre el proceso de titulación será con su correo institucional.</span></div><br /><div>El correo electrónico que estaba utilizando: "
						+ u.getEmail() + ", se ha cambiado al institucional: " + fields[1]
						+ ". La contraseña de acceso a la plataforma de titulación (<a href='http://titulacion.utmachala.edu.ec' target='_blank'>http://titulacion.utmachala.edu.ec</a>) sigue siendo la misma. Es decir que en la plataforma de titulación debe ingresar su correo institucional y la misma contraseña de acceso.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br /> <font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "El correo erróneo es: ");
					e.printStackTrace();
				}
				System.out.println("Cambio de correo convencional: " + u.getEmail()
						+ ", el nuevo correo institucional: " + fields[0]);
				contador++;
			}

			line = br.readLine();
		}

		br.close();
		//////////////////////////////////////////////////////////////////////////

		File f1 = new File(UtilsArchivos.getRutaRaiz() + "/ListadoParticipantes2.csv");

		BufferedReader br1 = new BufferedReader(new FileReader(f1));
		String line1 = br1.readLine();
		while (null != line1) {
			String[] fields = line1.split(";");

			Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { fields[0] }, false, new Object[] {});
			if (!u.getEmail().contains("@utmachala.edu")) {
				String destinatario = u.getEmail();
				String asunto = "IMPORTANTE TITULACIÓN: CAMBIO CORREOS CONVENCIONALES POR CORREOS INSTITUCIONALES ";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que a partir de este momento el correo electrónico por el cual usted va a ingresar a la plataforma de titulación y recibir información sobre el proceso de titulación será con su correo institucional.</span></div><br /><div>El correo electrónico que estaba utilizando: "
						+ u.getEmail() + ", se ha cambiado al institucional: " + fields[2]
						+ ". La contraseña de acceso a la plataforma de titulación (<a href='http://titulacion.utmachala.edu.ec' target='_blank'>http://titulacion.utmachala.edu.ec</a>) sigue siendo la misma. Es decir que en la plataforma de titulación debe ingresar su correo institucional y la misma contraseña de acceso.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "El correo erróneo es: ");
					e.printStackTrace();
				}
				System.out.println("Cambio de correo convencional: " + u.getEmail()
						+ ", el nuevo correo institucional: " + fields[2]);
				contador++;
			}

			line1 = br1.readLine();
		}
		br1.close();
		System.out.println("Números de cambios realizados: " + contador);
	}

	public void envioCorreoFechasLugarEC() {
		try {
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP = new ArrayList<EstudianteExamenComplexivoPP>();
			listadoEstudiantesExamenComplexivoPP = estudianteExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua inner join epp.especialista1 e1 inner join "
							+ "epp.especialista2 inner join epp.especialista3 inner join epp.especialistaSuplente1 es1 where epp.proceso='PT-011018' and epp.fechaSustentacionOrdinaria is not null "
							+ "and epp.lugarSustentacionOrdinaria is not null and ua.id='UACE'",
					new Object[0], 0, false, new Object[0]);

			System.out.println("Total estudiantes: " + listadoEstudiantesExamenComplexivoPP.size());

			for (EstudianteExamenComplexivoPP estudianteExamenComplexivoPP : listadoEstudiantesExamenComplexivoPP) {

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
				String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la fecha de sustentación del estudiante <strong>"
						+ estudianteExamenComplexivoPP.getEstudiante().getApellidoNombre()
						+ "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> a las <strong>"
						+ formatoHora.format(estudianteExamenComplexivoPP.getFechaSustentacionOrdinaria())
						+ "</strong> en el lugar <strong>"
						+ estudianteExamenComplexivoPP.getLugarSustentacionOrdinaria()
						+ "</strong>. <font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos ni bebidas al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p>"
						+ "<br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**IMPORTANTE</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se indica que este es el correo oficial del proceso de titulación para informar las sustentaciones de los estudiantes. </span></strong></p> <br /><div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

			}
			listadoEstudiantesExamenComplexivoPP.size();
			System.out.println("El número de estudiantes que tienen fecha y lugar de sustentación ordinaria: "
					+ listadoEstudiantesExamenComplexivoPP.size());
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El número de estudiantes que tienen fecha y lugar de sustentación ordinaria: "
							+ listadoEstudiantesExamenComplexivoPP.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void envioCorreoFechasLugarTT() {
		try {
			List<EstudianteTrabajoTitulacion> listadoEstudiantesTT = new ArrayList<EstudianteTrabajoTitulacion>();
			listadoEstudiantesTT = estudianteTrabajoTitulacionService.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.especialista1 e1 inner join"
							+ " ett.especialista2 inner join ett.especialista3 inner join ett.especialistaSuplente1 es1"
							+ " where ett.proceso='PT-011017' and ett.fechaSustentacion is not null and ett.lugarSustentacion is not null and ua.id='UACE'",
					new Object[0], 0, false, new Object[0]);

			System.out.println("Estudiantes con fechas y lugar de sustentacion: " + listadoEstudiantesTT.size());

			for (EstudianteTrabajoTitulacion ett : listadoEstudiantesTT) {

				DateFormat formatoHora = new SimpleDateFormat("hh:mm a");
				DateFormat formatoFecha = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es"));
				String[] destinatarios = { ett.getEstudiante().getEmail(), ett.getEspecialista1().getEmail(),
						ett.getEspecialista2().getEmail(), ett.getEspecialista3().getEmail(),
						ett.getEspecialistaSuplente1().getEmail() };

				String asunto = "ASIGNACIÓN FECHA Y LUGAR DE SUSTENTACIÓN DEL ESTUDIANTE "
						+ ett.getEstudiante().getApellidoNombre() + " DE LA MODALIDAD TRABAJO TITULACIÓN.";
				String detalle = "<div dir='ltr'><span style='color:#000000;'>El presente email es para informar que la fecha de sustentación del estudiante <strong>"
						+ ett.getEstudiante().getApellidoNombre() + "</strong> ha sido determinada para el <strong>"
						+ formatoFecha.format(ett.getFechaSustentacion()) + "</strong> a las <strong>"
						+ formatoHora.format(ett.getFechaSustentacion()) + "</strong> en el lugar <strong>"
						+ ett.getLugarSustentacion()
						+ "</strong>. <br /> <h1 style='text-align: justify;'><span style='font-size:16px;'>**RECORDATORIO</span></h1><p style='text-align: justify;'><strong><span style='font-size:16px;'>Se le recuerda que est&aacute; terminantemente prohibido el ingreso de alimentos ni bebidas al lugar de sustentaci&oacute;n, de hacer caso omiso a este recordatorio cualquiera de los especialistas del comite o personal de UMMOG podr&aacute; suspender dicho acto.</span></strong></p> <br /> <font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /> <div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color:#0000ff; font-family: arial, helvetica, sans-serif;font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento,de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoEstudiantesComiteEvaluador(destinatarios, asunto, detalle, listAdjunto,
						parametros);

			}
			listadoEstudiantesTT.size();
			System.out.println("El número de estudiantes que tienen fecha y lugar de sustentación ordinaria: "
					+ listadoEstudiantesTT.size());
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El número de estudiantes que tienen fecha y lugar de sustentación ordinaria: "
							+ listadoEstudiantesTT.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean getRenderUrkund() {
		return renderUrkund;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public boolean isActivarMensajeActualizarDatos() {
		return activarMensajeActualizarDatos;
	}

	public boolean isAdminCarlos() {
		return adminCarlos;
	}

	public boolean isAdminFranklin() {
		return adminFranklin;
	}

	public void probarDocumentoDrive() {
		try {
			System.out.println("El secutiry context obtener usuario: " + UtilSeguridad.obtenerUsuario());
			Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.email=?1",
					new Object[] { UtilSeguridad.obtenerUsuario() }, false, new Object[] {});
			EjemploDrive.probarExportacionDocumento("1PaFigPHBFwy0TJJpeEHlYw5lr9voO9yxn1sWjpqUfwc", "PT-010517", "EC");
			EjemploDrive.probarExportacionDocumento("13J-D1EKwmmDkoikt5wgUAUPam-VW5bGlIOGqljk5tLY", "PT-010517", "EC");

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void probarDrive() {
		try {
			EjemploDrive.probar();
			// DriveServiceTitulacion.ejecutar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void probarDriveEPP() {
		EstudianteExamenComplexivoPP epp = estudianteExamenComplexivoPPService.obtenerObjeto(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.estudiante e inner join epp.especialista1 e1 inner join epp.especialista2 e2 inner join "
						+ "epp.especialista3 e3 inner join epp.especialistaSuplente1 es1 inner join epp.proceso p inner join epp.carrera c where p.id=?1 and e.id=?2 and c.id=?3",
				new Object[] { "PT-010517", "0705667509", 57 }, false, new Object[] {});
		if (epp != null) {
			// epp.setEspecialistaSuplente1(usuarioService.obtenerObjeto("select
			// u from Usuario u where u.email=?1",
			// new Object[] { "gleon@utmachala.edu.ec" }, false, new Object[]
			// {}));
			System.out.println("Los datos son: " + epp.getEstudiante().getEmail() + "\ne1: "
					+ epp.getEspecialista1().getId() + "\ne2: " + epp.getEspecialista2().getId() + "\ne3: "
					+ epp.getEspecialista3().getId() + "\nes1: " + epp.getEspecialistaSuplente1().getId());
			estudianteExamenComplexivoPPService.creacionDocumentoDrive(epp);
		} else {
			System.out.println("No existe el estudiante en la consulta");
		}

	}

	public void probarExcel() {

		estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO("PT-031016", "UAIC");
		estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO("PT-031016", "UACS");
		estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO("PT-031016", "UACE");
		estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO("PT-031016", "UACA");
		estudianteExamenComplexivoPPService.enviarCorreoSubidaArchivoUMMO("PT-031016", "UACQS");
	}

	public void pruebaCambioCorreos() throws IOException {
		System.out.println("Ingresó al método pruebaCambioCorreos");
		File f = new File(UtilsArchivos.getRutaRaiz() + "/ListadoParticipantes1.csv");
		int contador = 0;
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line = br.readLine();
		while (null != line) {
			String[] fields = line.split(";");

			Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { fields[0] }, false, new Object[] {});
			if (!u.getEmail().contains("@utmachala.edu")) {
				String destinatario = u.getEmail();
				String asunto = "IMPORTANTE TITULACIÓN: CAMBIO CORREOS CONVENCIONALES POR CORREOS INSTITUCIONALES ";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que a partir de este momento el correo electrónico por el cual usted va a ingresar a la plataforma de titulación y recibir información sobre el proceso de titulación será con su correo institucional.</span></div><br /><div>El correo electrónico que estaba utilizando: "
						+ u.getEmail() + ", se ha cambiado al institucional: " + fields[1]
						+ ". La contraseña de acceso a la plataforma de titulación (<a href='http://titulacion.utmachala.edu.ec' target='_blank'>http://titulacion.utmachala.edu.ec</a>) sigue siendo la misma. Es decir que en la plataforma de titulación debe ingresar su correo institucional y la misma contraseña de acceso.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><br /><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "El correo erróneo es: ");
					e.printStackTrace();
				}
				System.out.println("Cambio de correo convencional: " + u.getEmail()
						+ ", el nuevo correo institucional: " + fields[0]);
				u.setEmail(fields[1]);
				usuarioService.actualizar(u);
				contador++;
			}

			line = br.readLine();
		}

		br.close();
		//////////////////////////////////////////////////////////////////////////

		File f1 = new File(UtilsArchivos.getRutaRaiz() + "/ListadoParticipantes2.csv");

		BufferedReader br1 = new BufferedReader(new FileReader(f1));
		String line1 = br1.readLine();
		while (null != line1) {
			String[] fields = line1.split(";");

			Usuario u = usuarioService.obtenerObjeto("select u from Usuario u where u.id=?1",
					new Object[] { fields[0] }, false, new Object[] {});
			if (!u.getEmail().contains("@utmachala.edu")) {
				String destinatario = u.getEmail();
				String asunto = "IMPORTANTE TITULACIÓN: CAMBIO CORREOS CONVENCIONALES POR CORREOS INSTITUCIONALES";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) estudiante.<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>Se le notifica que a partir de este momento el correo electrónico por el cual usted va a ingresar a la plataforma de titulación y recibir información sobre el proceso de titulación será con su correo institucional.</span></div><br /><div>El correo electrónico que estaba utilizando: "
						+ u.getEmail() + ", se ha cambiado al institucional: " + fields[2]
						+ ". La contraseña de acceso a la plataforma de titulación (<a href='http://titulacion.utmachala.edu.ec' target='_blank'>http://titulacion.utmachala.edu.ec</a>) sigue siendo la misma. Es decir que en la plataforma de titulación debe ingresar su correo institucional y la misma contraseña de acceso.</div><br/<div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div></div><br/><font face='Cambria, serif'>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</font> <br /><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario..</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = null;
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				try {
					UtilsMail.envioCorreo(destinatario, asunto, detalle, listAdjunto, parametros);
				} catch (Exception e) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "El correo erróneo es: ");
					e.printStackTrace();
				}
				System.out.println("Cambio de correo convencional: " + u.getEmail()
						+ ", el nuevo correo institucional: " + fields[2]);
				u.setEmail(fields[2]);
				usuarioService.actualizar(u);
				contador++;
			}

			line1 = br1.readLine();
		}
		br1.close();
		System.out.println("Números de cambios realizados: " + contador);
	}

	public void setActivarMensajeActualizarDatos(boolean activarMensajeActualizarDatos) {
		this.activarMensajeActualizarDatos = activarMensajeActualizarDatos;
	}

	public void setAdminCarlos(boolean adminCarlos) {
		this.adminCarlos = adminCarlos;
	}

	public void setAdminFranklin(boolean adminFranklin) {
		this.adminFranklin = adminFranklin;
	}

	public void setRenderUrkund(Boolean renderUrkund) {
		this.renderUrkund = renderUrkund;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void sustentacionesCSVFCE() throws FileNotFoundException, ParseException {
		System.out.println("La ruta raíz es: " + UtilsArchivos.getRutaRaiz());
		File f = new File(UtilsArchivos.getRutaRaiz() + "/listadoSustentacionesTentativasFCE_ExamenComplexivo_.csv");

		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		try {
			line = br.readLine();
			int contadorSustentacionesRegistradas = 0;
			while (null != line) {
				String[] fields = line.split(";");
				EstudianteExamenComplexivoPP epp = estudianteExamenComplexivoPPService.obtenerObjeto(
						"select epp from EstudianteExamenComplexivoPP epp inner join "
								+ "epp.proceso p inner join epp.carrera c inner join epp.estudiante e where p.id='PT-011018' and epp.aprobado is true and e.id=?1",
						new Object[] { fields[5] }, false, new Object[] {});
				if (epp != null) {

					int contadorCalificaciones = 0;
					if (epp.getEe1() != null)
						contadorCalificaciones++;
					if (epp.getEe2() != null)
						contadorCalificaciones++;
					if (epp.getEe3() != null)
						contadorCalificaciones++;
					if (epp.getEs1() != null)
						contadorCalificaciones++;

					if (contadorCalificaciones >= 3) {
						String calificaciones = estudianteExamenComplexivoPPService.traerCalificaciones(epp);
						BigDecimal calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[3]);
						if (calificacionFinal.compareTo(new BigDecimal("39.5")) >= 0) {
							System.out.println("Carrera " + fields[4] + ", Estudiante: " + fields[5]
									+ ", estudiante apellidos y nombres: " + fields[4]);
							epp.setLugarSustentacionOrdinaria(fields[7]);
							Date fecha = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(fields[6]);
							epp.setFechaSustentacionOrdinaria(fecha);
							estudianteExamenComplexivoPPService.actualizar(epp);
							contadorSustentacionesRegistradas++;
						}

					}
				}

				line = br.readLine();
			}
			System.out.println("Sustentaciones registradas: " + contadorSustentacionesRegistradas);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}