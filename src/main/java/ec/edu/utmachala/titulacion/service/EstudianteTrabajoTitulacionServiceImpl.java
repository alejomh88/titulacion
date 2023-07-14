package ec.edu.utmachala.titulacion.service;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import ec.edu.utmachala.titulacion.dao.EstudianteTrabajoTitulacionDao;
import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Service
public class EstudianteTrabajoTitulacionServiceImpl extends GenericServiceImpl<EstudianteTrabajoTitulacion, Integer>
		implements EstudianteTrabajoTitulacionService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionDao estudianteTrabajoTitulacionDao;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private ProcesoService procesoService;

	public void actualizar(EstudianteTrabajoTitulacion asignatura) {
		estudianteTrabajoTitulacionDao.actualizar(asignatura);
	}

	public void creacionDocumentoDrive(EstudianteTrabajoTitulacion ett1, EstudianteTrabajoTitulacion ett2) {
		Proceso p = procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
				new Object[] { ett1.getProceso().getId() }, false, new Object[] {});
		if (p != null && p.getFechaInicio().compareTo(new Date()) <= 0
				&& p.getFechaCierre().compareTo(new Date()) >= 0) {
			try {
				String idsEstudiantes = (ett2 == null ? ett1.getEstudiante().getApellidoNombre()
						: ett1.getEstudiante().getApellidoNombre() + "_" + ett2.getEstudiante().getApellidoNombre());

				String emailsEstudiantes = (ett2 == null ? ett1.getEstudiante().getEmail()
						: ett1.getEstudiante().getEmail() + ";" + ett2.getEstudiante().getEmail());

				String idDocumento = EjemploDrive.creacionDocumento(ett1.getUrlDrive(),
						idsEstudiantes + "_" + ett1.getProceso().getId(), ett1.getProceso().getId(), "TT",
						emailsEstudiantes,
						ett1.getEspecialista1().getEmail() + ";" + ett1.getEspecialista2().getEmail() + ";"
								+ ett1.getEspecialista3().getEmail() + ";"
								+ ett1.getEspecialistaSuplente1().getEmail());

				System.out.println("Guardar a la base de datos:" + idDocumento);

				if (idDocumento.compareTo("no") != 0) {
					usuarioService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"urlDrive\"='"
							+ idDocumento + "' WHERE id=" + ett1.getId() + ";");
					if (ett2 != null) {
						usuarioService.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"urlDrive\"='"
								+ idDocumento + "' WHERE id=" + ett2.getId() + ";");
					}
				}
			} catch (GoogleJsonResponseException gjsone) {
				gjsone.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void enviarCorreosEstudiantesFaltantesSubirArchivos(String proceso) {
		try {

			List<EstudianteTrabajoTitulacion> listadoETT = this.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.estudiante e inner join ett.carrera c inner join c.unidadAcademica ua"
							+ " where p.id=?1 and ett.numeroActaCalificacion is not null and ett.archivo is null",
					new Object[] { proceso }, 0, false, new Object[] {});

			System.out.println("Número estudiantes sin archivo ett: " + listadoETT.size());

			if (listadoETT != null && listadoETT.size() >= 1) {
				for (EstudianteTrabajoTitulacion ett : listadoETT) {
					String asunto = "URGENTE TITULACIÓN: SUBIDA DEL DOCUMENTO FINAL DE SU OPCIÓN DE TITULACIÓN";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante <br /></span></div>"
							+ "<div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que le falta "
							+ "generar el documento final de su opción de titulación, para esto debe ir a la opción de menú Estudiante --> Examen Complexivo -->"
							+ " Subir Trabajo, seguir las instrucciones indicadas en el manual de procedimiento que se indica dando clic <a title='aqu&iacute;' href='https://docs.google.com/document/d/1a-UIPjaEcwycPT1YR9RvTXCji_KPWuyzxnLJwsLCbiA/edit#heading=h.1phsvr8y5dhr' target='_blank' rel='noopener'>aqui</a></div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(ett.getEstudiante().getEmail(), asunto, detalle, listAdjunto, parametros);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void enviarCorreosEstudiantesInconsistenciasSubirArchivos(String proceso) {
		try {

			List<EstudianteTrabajoTitulacion> listadoETT = this.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.estudiante e inner join ett.carrera c inner join c.unidadAcademica ua"
							+ " where p.id=?1 and ett.numeroActaCalificacion is not null and ett.archivo is not null and ett.validarArchivo is false)",
					new Object[] { proceso }, 0, false, new Object[] {});

			System.out.println("Número estudiantes con archivo ett y con inconsistencias: " + listadoETT.size());

			if (listadoETT != null && listadoETT.size() >= 1) {
				for (EstudianteTrabajoTitulacion ett : listadoETT) {
					String asunto = "URGENTE TITULACIÓN: DOCUMENTO FINAL CON INCONSISTENCIAS DE SU OPCIÓN DE TITULACIÓN";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante <br /></span></div>"
							+ "<div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para recordarle la corrección "
							+ "de inconsistencias encontradas de su documento final cargado en la plataforma de titulación, esta validación la realiza el personal de UMMOG "
							+ "de su Unidad Académica, le sugerimos volver a generar el documento final con las correcciones respectivas en la opción de menú Estudiante --> "
							+ "Trabajo Titulación --> Subir Trabajo, seguir las instrucciones indicadas en el manual de procedimiento que se indica dando clic <a title='aqu&iacute;' "
							+ "href='https://docs.google.com/document/d/1a-UIPjaEcwycPT1YR9RvTXCji_KPWuyzxnLJwsLCbiA/edit#heading=h.1phsvr8y5dhr' target='_blank' rel='noopener'>aqui.</a></div><br/>"
							+ "<div><span style='color: #000000;'>Si es que usted no corrige el documento a tiempo y lo genera nuevamente para la validación respetiva, su título de tercer nivel no podrá ser registrado en el sistema de la SENESCYT.</span></div><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(ett.getEstudiante().getEmail(), asunto, detalle, listAdjunto, parametros);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void enviarCorreoSubidaArchivoBiblioteca(String proceso, String unidadAcademica) {
		try {
			List<Usuario> miembrosBiblioteca = new ArrayList<Usuario>();
			miembrosBiblioteca = usuarioService.obtenerLista(
					"select distinct u from Usuario u inner join u.permisosCarreras pc inner join u.permisos p inner join p.rol r inner join pc.carrera c inner join c.unidadAcademica ua where ua.id=?1 and r.id='BIBL' and u.email <> 'faconza@utmachala.edu.ec' and u.email <> 'jbenites@utmachala.edu.ec' and u.email <> 'paulo@adrianconza.com' and u.activo is true",
					new Object[] { unidadAcademica }, 0, false, new Object[0]);

			List<String> miembrosBibliotecas = new ArrayList<String>();
			for (Usuario u : miembrosBiblioteca)
				miembrosBibliotecas.add(u.getEmail());

			List<EstudianteTrabajoTitulacion> listadoETT = new ArrayList<EstudianteTrabajoTitulacion>();
			listadoETT = this.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.proceso p inner join ett.estudiante e where p.id=?1 and ett.archivo is not null and ett.validarArchivo is true and ett.urlBiblioteca is null and ua.id=?2 order by c.nombre, e.apellidoNombre asc",
					new Object[] { proceso, unidadAcademica }, 0, false, new Object[0]);
			System.out.println(
					"Elementos en miembrosBiblioteca de " + unidadAcademica + ": " + miembrosBiblioteca.size());
			System.out.println("Elementos en listadoEPP de " + unidadAcademica + ": " + listadoETT.size());

			if (listadoETT != null && listadoETT.size() >= 1) {
				// List<Usuario> destinatarios = new ArrayList<>();
				// Usuario u = usuarioService.obtenerObjeto("select u from
				// Usuario u where u.id='0705667509'",
				// new Object[0], false, new Object[0]);
				// destinatarios.add(u);
				File fil = traerEPPenExcelETT(listadoETT);
				String asunto = "ARCHIVOS VALIDADOS PENDIENTE DE SUBIR AL REPOSITORIO DIGITAL";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) encargado(s) de biblioteca de la "
						+ (unidadAcademica.compareTo("UAIC") == 0 ? "Unidad Académica de Ingeniería Civil"
								: unidadAcademica.compareTo("UACA") == 0 ? "Unidad Académica de Ciencias Agropecuarias"
										: unidadAcademica.compareTo("UACQS") == 0
												? "Unidad Académica de Ciencias Químicas y de la Salud"
												: unidadAcademica.compareTo("UACE") == 0
														? "Unidad Académica de Ciencias Empresariales"
														: "Unidad Académica de Ciencias Sociales")
						+ "<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que se encuentran validados los archivos de la dimensión escrita de los estudiantes en la modalidad trabajo de titulación del proceso actual "
						+ proceso
						+ ". Por tal motivo se adjunta un archivo de excel con el listado de dichos estudiantes para una mejor búsqueda dentro de la plataforma.</div><br /><div><span style='color: #000000;'>&nbsp;</span></div><div>Se solicita efectuar la respectiva subida del archivo al repositorio digital de la UTMACH.</div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = new ArrayList<File>();
				listAdjunto.add(fil);
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoArchivoUMMOG(miembrosBibliotecas, asunto, detalle, listAdjunto, parametros);
			}
		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void enviarCorreoSubidaArchivoUMMO(String proceso, String unidadAcademica) {
		try {
			List<Usuario> miembrosUmmog = new ArrayList<Usuario>();
			miembrosUmmog = usuarioService.obtenerLista(
					"select distinct u from Usuario u inner join u.permisosCarreras pc inner join u.permisos p inner join p.rol r inner join pc.carrera c inner join c.unidadAcademica ua where ua.id=?1 and (r.id='ANAL' or r.id='UMMO') and u.email <> 'faconza@utmachala.edu.ec' and u.email <> 'jbenites@utmachala.edu.ec' and u.email <> 'paulo@adrianconza.com' and u.activo is true",
					new Object[] { unidadAcademica }, 0, false, new Object[0]);

			List<String> miembrosUMMOGS = new ArrayList<String>();
			for (Usuario u : miembrosUmmog)
				miembrosUMMOGS.add(u.getEmail());

			List<EstudianteTrabajoTitulacion> listadoETT = new ArrayList<EstudianteTrabajoTitulacion>();
			listadoETT = this.obtenerLista(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.carrera c inner join c.unidadAcademica ua inner join ett.proceso p inner join ett.estudiante e where p.id=?1 and ett.archivo is not null and ett.validarArchivo is null and ua.id=?2 order by c.nombre, e.apellidoNombre asc",
					new Object[] { proceso, unidadAcademica }, 0, false, new Object[0]);
			System.out.println("Elementos en miembrosUmmog de " + unidadAcademica + ": " + miembrosUmmog.size());
			System.out.println("Elementos en listadoEPP de " + unidadAcademica + ": " + listadoETT.size());

			if (listadoETT != null && listadoETT.size() >= 1) {
				// List<Usuario> destinatarios = new ArrayList<>();
				// Usuario u = usuarioService.obtenerObjeto("select u from
				// Usuario u where u.id='0705667509'",
				// new Object[0], false, new Object[0]);
				// destinatarios.add(u);
				File fil = traerEPPenExcelETT(listadoETT);
				String asunto = "ARCHIVOS SUBIDOS A LA PLATAFORMA PENDIENTES DE REVISIÓN";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) analistas y jefe de UMMOG de la "
						+ (unidadAcademica.compareTo("UAIC") == 0 ? "Unidad Académica de Ingeniería Civil"
								: unidadAcademica.compareTo("UACA") == 0 ? "Unidad Académica de Ciencias Agropecuarias"
										: unidadAcademica.compareTo("UACQS") == 0
												? "Unidad Académica de Ciencias Químicas y de la Salud"
												: unidadAcademica.compareTo("UACE") == 0
														? "Unidad Académica de Ciencias Empresariales"
														: "Unidad Académica de Ciencias Sociales")
						+ "<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar la subida de archivos de la dimensión escrita por parte de los estudiantes de la modalidad trabajo de titulación del proceso actual "
						+ proceso
						+ " a la plataforma que deben ser validados. Por tal motivo se adjunta un archivo en formato excel con el listado para una mejor búsqueda dentro de la plataforma de titulación.</div><br /><div><span style='color: #000000;'>&nbsp;</span></div><div>Se solicita efectuar la respectiva validación para continuar con la siguiente fase que es la subida del mismo al repositorio digital de la UTMACH.</div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
				List<File> listAdjunto = new ArrayList<File>();
				listAdjunto.add(fil);
				Parametro parametro = parametroService.obtener();
				Map<String, String> parametros = parametroService.traerMap(parametro);
				UtilsMail.envioCorreoArchivoUMMOG(miembrosUMMOGS, asunto, detalle, listAdjunto, parametros);
			}
		} catch (Exception e) {
			e.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void insertar(EstudianteTrabajoTitulacion asignatura) {
		estudianteTrabajoTitulacionDao.insertar(asignatura);
	}

	public void insertar(List<EstudianteTrabajoTitulacion> asignaturas) {
		for (EstudianteTrabajoTitulacion asignatura : asignaturas) {
			estudianteTrabajoTitulacionDao.insertar(asignatura);
		}
	}

	public String traerCalificaciones(EstudianteTrabajoTitulacion ett) {

		BigDecimal notaEscritaFinal = new BigDecimal(0);
		BigDecimal notaOralFinal = new BigDecimal(0);
		BigDecimal notaFinal = new BigDecimal(0);

		if (ett.getEspecialistaSuplantadoE() != null
				&& ett.getEspecialista1().getId().compareTo(ett.getEspecialistaSuplantadoE().getId()) == 0) {
			if (ett.getEs1() != null && ett.getEe2() != null && ett.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(ett.getEs1().add(ett.getEe2()).add(ett.getEe3()), 3);
		} else if (ett.getEspecialistaSuplantadoE() != null
				&& ett.getEspecialista2().getId().compareTo(ett.getEspecialistaSuplantadoE().getId()) == 0) {
			if (ett.getEe1() != null && ett.getEs1() != null && ett.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(ett.getEe1().add(ett.getEs1()).add(ett.getEe3()), 3);
		} else if (ett.getEspecialistaSuplantadoE() != null
				&& ett.getEspecialista3().getId().compareTo(ett.getEspecialistaSuplantadoE().getId()) == 0) {
			if (ett.getEe1() != null && ett.getEe2() != null && ett.getEs1() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(ett.getEe1().add(ett.getEe2()).add(ett.getEs1()), 3);
		} else {
			if (ett.getEe1() != null && ett.getEe2() != null && ett.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(ett.getEe1().add(ett.getEe2()).add(ett.getEe3()), 3);
		}

		if (ett.getEspecialistaSuplantadoO() != null
				&& ett.getEspecialista1().getId().compareTo(ett.getEspecialistaSuplantadoO().getId()) == 0) {
			if (ett.getOs1() != null && ett.getOe2() != null && ett.getOe3() != null) {
				notaOralFinal = UtilsMath.divideCalificaciones(ett.getOs1().add(ett.getOe2()).add(ett.getOe3()), 3);
			}
		} else if (ett.getEspecialistaSuplantadoO() != null
				&& ett.getEspecialista2().getId().compareTo(ett.getEspecialistaSuplantadoO().getId()) == 0) {
			if (ett.getOe1() != null && ett.getOs1() != null && ett.getOe3() != null) {
				notaOralFinal = UtilsMath.divideCalificaciones(ett.getOe1().add(ett.getOs1()).add(ett.getOe3()), 3);
			}
		} else if (ett.getEspecialistaSuplantadoO() != null
				&& ett.getEspecialista3().getId().compareTo(ett.getEspecialistaSuplantadoO().getId()) == 0) {
			if (ett.getOe1() != null && ett.getOe2() != null && ett.getOs1() != null) {
				notaOralFinal = UtilsMath.divideCalificaciones(ett.getOe1().add(ett.getOe2()).add(ett.getOs1()), 3);
			}
		} else {
			if (ett.getOe1() != null && ett.getOe2() != null && ett.getOe3() != null) {
				notaOralFinal = UtilsMath.divideCalificaciones(ett.getOe1().add(ett.getOe2()).add(ett.getOe3()), 3);
			}
		}

		System.out.println("En el service: " + notaEscritaFinal + "-" + notaOralFinal + "-" + notaFinal);
		notaFinal = UtilsMath.redondearCalificacion(notaEscritaFinal.add(notaOralFinal));
		System.out.println("En el service: " + notaEscritaFinal + "-" + notaOralFinal + "-" + notaFinal);
		return notaEscritaFinal + "-" + notaOralFinal + "-" + notaFinal;
	}

	public File traerEPPenExcelETT(List<EstudianteTrabajoTitulacion> listadoEstudiantesTrabajosTitulacion) {

		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Listado de estudiantes examen complexivo");

			// Crear encabezados
			Row encabezado = sheet.createRow(0);
			encabezado.createCell(0).setCellValue("Carrera");
			encabezado.createCell(1).setCellValue("Cedula Estudiante");
			encabezado.createCell(2).setCellValue("Apellidos y Nombres del Estudiante");
			for (int i = 0; i < 3; i++) {
				CellStyle estiloEncabezado = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBold(true);
				font.setFontName(HSSFFont.FONT_ARIAL);
				font.setFontHeightInPoints((short) 11);
				estiloEncabezado.setFont(font);
				encabezado.getCell(i).setCellStyle(estiloEncabezado);
			}

			// LLenar los campos del excel con datos de lista EPP
			int f = 1;
			for (EstudianteTrabajoTitulacion epp : listadoEstudiantesTrabajosTitulacion) {
				Row fila = sheet.createRow(f);
				// Columna carrera
				Cell celdaCarrera = fila.createCell(0);
				celdaCarrera.setCellValue(epp.getCarrera().getNombre());
				// Columna Cedula Estudiante
				Cell celdaCedulaEstudiante = fila.createCell(1);
				celdaCedulaEstudiante.setCellValue(epp.getEstudiante().getId());
				// Columna ApellidoNombre Estudiante
				Cell celdaInfoEstudiante = fila.createCell(2);
				celdaInfoEstudiante.setCellValue(epp.getEstudiante().getApellidoNombre());
				f++;
			}

			// Ajustar las columnas al ancho del contenido.
			for (int j = 0; j < 3; j++) {
				sheet.autoSizeColumn(j);
			}

			// Guardar el archivo de excel
			FileOutputStream out = new FileOutputStream(
					new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesTrabajosTitulacion.xls"));
			workbook.write(out);
			out.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
		return new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesTrabajosTitulacion.xls");
	}

}