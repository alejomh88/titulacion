package ec.edu.utmachala.titulacion.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Examen;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.utility.UtilsDate;
import ec.edu.utmachala.titulacion.utility.UtilsMail;

@Service
public class EnvioCorreoServiceImpl implements EnvioCorreoService, Serializable {
	private static final long serialVersionUID = 1L;
	@Autowired
	private ParametroService parametroService;
	@Autowired
	private UsuarioService usuarioService;

	public void enviarCodigoActualizacionDatos(Usuario usuario, String contrasena) {
		String asunto = "CODIGO DE CONFIRMACIÓN DE DATOS PARA EXAMEN COMPLEXIVO UTMACH 2015";

		String detalle = "Estimad@ \n\n" + usuario.getId() + " - " + usuario.getApellidoNombre() + ","
				+ "\n\nReciba un atento saludo de parte de la UTMACH - SECCIÓN DE TITULACIÓN."
				+ "\n\nEl presente correo es para notificarle que usted ha actualizado su correo electrónico y número de teléfono celular, "
				+ "para el posterior envío de la contraseña para el acceso al Examen Complexivo - Parte Teórica."
				+ "\n\nRecuerde que el Examen estó previsto para los dias 12 y 13 de Septiembre del 2015."
				+ "\n\nCopie el siguiente código de confirmaci�n en la ventana de actualización de datos: "
				+ contrasena;
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarContraseñaIngresoExamen(Usuario usuario, String contrasena) {
		String asunto = "CONTRASEÑA TEMPORAL PARA EL INGRESO A LA PLATAFORMA DE TITULACIÓN Utmach 2015";

		String detalle = "Estimad@ \n\n" + usuario.getId() + " - " + usuario.getApellidoNombre() + ","
				+ "\n\nReciba un atento saludo de parte de la Utmach - Sección de Titulación."
				+ "\n\nContraseña temporal: " + contrasena;
		detalle = detalle
				+ "<div><p style='font-size:13px; margin: 0;'><strong>Dirección Académica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Sección de Titulación</a></strong></p><p style='font-size:13px; margin: 0;'><strong><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></strong></p><p style='font-size:13px; margin: 0;'>Machala, Ecuador</p><p></p><p style='font-size:14px; margin: 0;'><strong>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</strong><p><p style='font-size:13px; color:green'><em>No imprima éste correo a menos que sea estrictamente necesario.</em></p><p></p><p style='font-size:12px; color:#0000ff; line-height: 1.5em;' ><em><strong>AVISO DE CONFIDENCIALIDAD:</strong>\"La información contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jurídica a la cual está dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retención, difusión, distribución o copia de éste mensaje es prohibida y sancionada por la ley\". \"Si Usted recibió este mensaje por error, notifique al Administrador o a quien le envió inmediatamente, elimínelo sin hacer copias. Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opinión oficial de la UNIVERSIDAD TÉCNICA DE MACHALA\"</em></p></div>";
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarDatosComplexivo(String email, List<Examen> examenes, EstudianteExamenComplexivoPP temaPractico) {
		Usuario usuario = (Usuario) this.usuarioService.obtenerObjeto(
				"select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { email.trim().toLowerCase() }, false, new Object[0]);
		Examen e1 = (Examen) examenes.get(0);
		Examen e2 = null;
		if (examenes.size() > 1) {
			e2 = (Examen) examenes.get(1);
		}
		String asunto = "DATOS DEL EXAMEN COMPLEXIVO Utmach 2015";

		String detalle = "<div style='box-shadow: rgba(0, 0, 0, 0.7) 5px 5px 20px; padding: 22px;'><figure style='text-align: center;'><img src='http://www.utmachala.edu.ec/archivos/siutmach/imagenes/logo_utm.png' alt='LOGO UNIVERSITARIO' style='margin-left: 40%;'></figure><h1 style='font-size: 22px; color: #0D9BED; text-align: center;'>Reciba un atento saludo de parte de la Utmach - Sección de Titulación.</h1><p>Estimad@,</p><p>Cédula/Pasaporte: "
				+ usuario.getId() + "</p><p>Nombres: " + (usuario.getApellidoNombre()) + "</p><p>PROCESO: "
				+ e1.getEstudianteExamenComplexivoPT().getProceso().getId() + "</p>"
				+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>PARTE TEÓRICA</h2><p>";

		detalle = detalle + parteExamen(e1);
		if (e2 != null) {
			detalle = detalle + parteExamen(e2);
		}
		detalle = detalle + "</p>";

		detalle = detalle
				+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>PARTE PRÁCTICA</h2><p>Docente que generó el reactivo: "
				+ temaPractico.getDocenteAsignatura().getDocente().getApellidoNombre() + "</p>";
		if (temaPractico.getTituloInvestigacion() != null)
			detalle = detalle + "<p>Título del Trabajo de Investigación: " + temaPractico.getTituloInvestigacion()
					+ "</p>";
		detalle = detalle + "<p>Caso de Investigación: " + temaPractico.getCasoInvestigacion() + "</p>";
		if ((temaPractico.getArchivoAdjunto() != null) && (temaPractico.getArchivoAdjunto().compareTo("") != 0)
				&& (temaPractico.getArchivoAdjunto().compareToIgnoreCase("null") != 0)) {
			detalle = detalle + "<p>Archivo: <a href=" + temaPractico.getArchivoAdjunto() + ">"
					+ temaPractico.getArchivoAdjunto() + "</a></p>";
		}
		Usuario p1 = temaPractico.getEspecialista1();
		Usuario p2 = temaPractico.getEspecialista2();
		Usuario p3 = temaPractico.getEspecialista3();
		if ((p1 != null) && (p2 != null) && (p3 != null)) {
			// String nmb1 = (p1.getApellidoPaterno() == null ? "" :
			// p1.getApellidoPaterno()) + " "
			// + (p1.getApellidoMaterno() == null ? "" :
			// p1.getApellidoMaterno()) + " " + p1.getNombre();
			// String nmb2 = (p2.getApellidoPaterno() == null ? "" :
			// p2.getApellidoPaterno()) + " "
			// + (p2.getApellidoMaterno() == null ? "" :
			// p2.getApellidoMaterno()) + " " + p2.getNombre();
			// String nmb3 = (p3.getApellidoPaterno() == null ? "" :
			// p3.getApellidoPaterno()) + " "
			// + (p3.getApellidoMaterno() == null ? "" :
			// p3.getApellidoMaterno()) + " " + p3.getNombre();

			String nmb1 = null;
			String nmb2 = null;
			String nmb3 = null;

			detalle = detalle
					+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>FECHA DE SUSTENTACIÓN</h2><p style='text-align: center;'>"
					+ UtilsDate.fechaFormatoString(temaPractico.getFechaSustentacionOrdinaria(),
							"EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm")
					+ "</p>"
					+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>COMITÉ EVALUADOR</h2><p style='text-align: center;'>"
					+ nmb1 + "</p><p style='text-align: center;'>" + nmb2 + "</p><p style='text-align: center;'>" + nmb3
					+ "</p>";
		}
		detalle = detalle + "</div>";
		detalle = detalle
				+ "<div><p style='font-size:13px; margin: 0;'><strong>Dirección Académica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Sección de Titulación</a></strong></p><p style='font-size:13px; margin: 0;'><strong><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></strong></p><p style='font-size:13px; margin: 0;'>Machala, Ecuador</p><p></p><p style='font-size:14px; margin: 0;'><strong>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</strong><p><p style='font-size:13px; color:green'><em>No imprima éste correo a menos que sea estrictamente necesario.</em></p><p></p><p style='font-size:12px; color:#0000ff; line-height: 1.5em;' ><em><strong>AVISO DE CONFIDENCIALIDAD:</strong>\"La información contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jurídica a la cual está dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retención, difusión, distribución o copia de éste mensaje es prohibida y sancionada por la ley\". \"Si Usted recibió este mensaje por error, notifique al Administrador o a quien le envió inmediatamente, elimínelo sin hacer copias. Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opinión oficial de la UNIVERSIDAD TÉCNICA DE MACHALA\"</em></p></div>";
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarDatosTrabajoTitulacion(String email, EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		Usuario usuario = (Usuario) this.usuarioService.obtenerObjeto(
				"select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { email.trim().toLowerCase() }, false, new Object[0]);

		String asunto = "DATOS DEL TRABAJO DE TITULACIÓN Utmach 2015";

		String detalle = "<div style='box-shadow: rgba(0, 0, 0, 0.7) 5px 5px 20px; padding: 22px;'><figure style='text-align: center;'><img src='http://www.utmachala.edu.ec/archivos/siutmach/imagenes/logo_utm.png' alt='LOGO UNIVERSITARIO' style='margin-left: 40%;'></figure><h1 style='font-size: 22px; color: #0D9BED; text-align: center;'>Reciba un atento saludo de parte de la Utmach - Sección de Titulación.</h1><p>Estimad@,</p><p>Cédula/Pasaporte: "
				+ usuario.getId() + "</p><p>Nombres: " + (usuario.getApellidoNombre()) + "</p>"
				+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>TRABAJO DE TITULACIÓN</h2>";

		Usuario p1 = estudianteTrabajoTitulacion.getEspecialista1();
		Usuario p2 = estudianteTrabajoTitulacion.getEspecialista2();
		Usuario p3 = estudianteTrabajoTitulacion.getEspecialista3();
		Usuario ps1 = estudianteTrabajoTitulacion.getEspecialistaSuplente1();
		Usuario ps2 = estudianteTrabajoTitulacion.getEspecialistaSuplente2();
		if ((p1 != null) && (p2 != null) && (p3 != null) && (ps1 != null) && (ps2 != null)) {
			// String nmb1 = (p1.getApellidoPaterno() == null ? "" :
			// p1.getApellidoPaterno()) + " "
			// + (p1.getApellidoMaterno() == null ? "" :
			// p1.getApellidoMaterno()) + " " + p1.getNombre();
			// String nmb2 = (p2.getApellidoPaterno() == null ? "" :
			// p2.getApellidoPaterno()) + " "
			// + (p2.getApellidoMaterno() == null ? "" :
			// p2.getApellidoMaterno()) + " " + p2.getNombre();
			// String nmb3 = (p3.getApellidoPaterno() == null ? "" :
			// p3.getApellidoPaterno()) + " "
			// + (p3.getApellidoMaterno() == null ? "" :
			// p3.getApellidoMaterno()) + " " + p3.getNombre();
			// String nmb4 = (ps1.getApellidoPaterno() == null ? "" :
			// ps1.getApellidoPaterno()) + " "
			// + (ps1.getApellidoMaterno() == null ? "" :
			// ps1.getApellidoMaterno()) + " " + ps1.getNombre();
			// String nmb5 = (ps2.getApellidoPaterno() == null ? "" :
			// ps2.getApellidoPaterno()) + " "
			// + (ps2.getApellidoMaterno() == null ? "" :
			// ps2.getApellidoMaterno()) + " " + ps2.getNombre();
			String nmb1 = null;
			String nmb2 = null;
			String nmb3 = null;
			String nmb4 = null;
			String nmb5 = null;
			detalle = detalle
					+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>FECHA DE SUSTENTACIÓN</h2><p style='text-align: center;'>"
					+ UtilsDate.fechaFormatoString(estudianteTrabajoTitulacion.getFechaSustentacion(),
							"EEEE dd 'de' MMMM 'de' yyyy 'a las' HH:mm")
					+ "</p>"
					+ "<h2 style='font-size: 18px; color: #0D9BED; text-align: center;'>COMITÉ EVALUADOR</h2><p style='text-align: center;'>Especialista 1: "
					+ nmb1 + "</p><p style='text-align: center;'>Especialista 2: " + nmb2
					+ "</p><p style='text-align: center;'>Especialista 3: " + nmb3
					+ "</p><p style='text-align: center;'>Especialista Suplente 1: " + nmb4
					+ "</p><p style='text-align: center;'>Especialista Suplente 2: " + nmb5 + "</p>";
		}
		detalle = detalle + "</div>";
		detalle = detalle
				+ "<div><p style='font-size:13px; margin: 0;'><strong>Dirección Académica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Sección de Titulación</a></strong></p><p style='font-size:13px; margin: 0;'><strong><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></strong></p><p style='font-size:13px; margin: 0;'>Machala, Ecuador</p><p></p><p style='font-size:14px; margin: 0;'><strong>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</strong><p><p style='font-size:13px; color:green'><em>No imprima éste correo a menos que sea estrictamente necesario.</em></p><p></p><p style='font-size:12px; color:#0000ff; line-height: 1.5em;' ><em><strong>AVISO DE CONFIDENCIALIDAD:</strong>\"La información contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jurídica a la cual está dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retención, difusión, distribución o copia de éste mensaje es prohibida y sancionada por la ley\". \"Si Usted recibió este mensaje por error, notifique al Administrador o a quien le envió inmediatamente, elimínelo sin hacer copias. Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opinión oficial de la UNIVERSIDAD TÉCNICA DE MACHALA\"</em></p></div>";
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarMensajeConfirmacion(Usuario usuario, String codigoConfirmacion) {
		String asunto = "VALIDACIÓN EXITOSA DE DATOS PARA EXAMEN COMPLEXIVO UTMACH 2015";

		String detalle = "Estimad@ \n\n" + usuario.getId() + " - " + usuario.getApellidoNombre() + ","
				+ "\n\nReciba un atento saludo de parte de la UTMACH - SECCIÓN DE TITULACIÓN."
				+ "\n\nEl presente correo es para notificarle que su cuenta esta actualizada y activa, para rendir "
				+ "el Examen Complexivo - Parte Teórica."
				+ "\n\nRecuerde que el Examen estó previsto para los dias 12 y 13 de Septiembre del 2015.";
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void enviarNotificiaEstudiante(String email) {
		Usuario usuario = (Usuario) this.usuarioService.obtenerObjeto(
				"select u from Usuario u where u.email=?1 and u.activo=true",
				new Object[] { email.trim().toLowerCase() }, false, new Object[0]);

		String asunto = "Utmach - Sección de Titulación";

		String detalle = "<div style='box-shadow: rgba(0, 0, 0, 0.7) 5px 5px 20px; padding: 22px;'><figure style='text-align: center;'><img src='http://www.utmachala.edu.ec/archivos/siutmach/imagenes/logo_utm.png' alt='LOGO UNIVERSITARIO' style='margin-left: 40%;'></figure><h1 style='font-size: 22px; color: #0D9BED; text-align: center;'>Reciba un atento saludo de parte de la Utmach - Sección de Titulación.</h1><p>Estimad@ "
				+ (usuario.getApellidoNombre()) + ".</p>" + "<p>Usted tiene nuevos datos a consultar.</p>"
				+ "<p>Para revisarlos, ingrese en titulacion.utmachala.edu.ec - <em><strong>Opción Consultar Datos</strong></em> usuando su usuario y contraseña.</p></div>";
		detalle = detalle
				+ "<div><p style='font-size:13px; margin: 0;'><strong>Dirección Académica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Sección de Titulación</a></strong></p><p style='font-size:13px; margin: 0;'><strong><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></strong></p><p style='font-size:13px; margin: 0;'>Machala, Ecuador</p><p></p><p style='font-size:14px; margin: 0;'><strong>Este mensaje es enviado como una notificación automática, por favor no responda a esta dirección de correo.</strong><p><p style='font-size:13px; color:green'><em>No imprima éste correo a menos que sea estrictamente necesario.</em></p><p></p><p style='font-size:12px; color:#0000ff; line-height: 1.5em;' ><em><strong>AVISO DE CONFIDENCIALIDAD:</strong>\"La información contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jurídica a la cual está dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retención, difusión, distribución o copia de éste mensaje es prohibida y sancionada por la ley\". \"Si Usted recibió este mensaje por error, notifique al Administrador o a quien le envió inmediatamente, elimínelo sin hacer copias. Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opinión oficial de la UNIVERSIDAD TÉCNICA DE MACHALA\"</em></p></div>";
		try {
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo(usuario.getEmail(), asunto, detalle, null, parametros);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String parteExamen(Examen examen) {
		// String detalle = "Tipo: "
		// +
		// examen.getEstudianteExamenComplexivoPT().getExamenes().get(0).getGrupo().getTipoExamen().getNombre()
		// + " Fecha: " + UtilsDate.fechaFormatoString(examen.getFecha(),
		// "dd/MM/yyyy HH:mm:ss") + " Nota: "
		// + examen.getId() + "/50";
		// if (examen.getId().intValue() >= 20) {
		// detalle = detalle
		// .concat("<p>FELICITACIONES!!!</p><p>Usted a aprobado la parte Teórica
		// del Examen Complexivo</p>");
		// }
		// return detalle;
		return "";
	}
}
