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

import ec.edu.utmachala.titulacion.dao.EstudiantesExamenComplexivoPPDao;
import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.Parametro;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.entity.Usuario;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMail;
import ec.edu.utmachala.titulacion.utility.UtilsMath;

@Service
public class EstudiantesExamenComplexivoPPServiceImpl extends GenericServiceImpl<EstudianteExamenComplexivoPP, Integer>
		implements EstudiantesExamenComplexivoPPService, Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPDao temaPracticoDao;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ParametroService parametroService;

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudianteExamenComplexivoPPService;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	public void actualizar(EstudianteExamenComplexivoPP temaPractico) {
		temaPracticoDao.actualizar(temaPractico);
	}

	public void correoPrueba() {
		try {
			String asunto = "CORREO PRUEBA";
			String detalle = "Este correo es de prueba.";
			List<File> listAdjunto = null;
			Parametro parametro = parametroService.obtener();
			Map<String, String> parametros = parametroService.traerMap(parametro);
			UtilsMail.envioCorreo("cxvega@utmachala.edu.ec", asunto, detalle, listAdjunto, parametros);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void creacionDocumentoDrive(EstudianteExamenComplexivoPP epp) {
		Proceso p = procesoService.obtenerObjeto("select p from Proceso p where p.id=?1",
				new Object[] { epp.getProceso().getId() }, false, new Object[] {});
		if (p != null && p.getFechaInicio().compareTo(new Date()) <= 0
				&& p.getFechaCierre().compareTo(new Date()) >= 0) {
			try {
				String idDocumento = EjemploDrive.creacionDocumento(epp.getUrlDrive(),
						epp.getEstudiante().getApellidoNombre() + "_" + epp.getProceso().getId(),
						epp.getProceso().getId(), "EC", epp.getEstudiante().getEmail(),
						epp.getEspecialista1().getEmail() + ";" + epp.getEspecialista2().getEmail() + ";"
								+ epp.getEspecialista3().getEmail() + ";" + epp.getEspecialistaSuplente1().getEmail());
				System.out.println("Guardar a la base de datos:" + idDocumento);
				if (idDocumento.compareTo("no") != 0) {
					usuarioService.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"urlDrive\"='"
							+ idDocumento + "' WHERE id=" + epp.getId() + ";");
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

			List<EstudianteExamenComplexivoPP> listadoEPP = estudianteExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.estudiante e inner join epp.carrera c inner join"
							+ " c.unidadAcademica ua where p.id=?1 and epp.numeroActaCalificacion is not null and epp.archivo is null",
					new Object[] { proceso }, 0, false, new Object[] {});

			System.out.println("Número del listado faltantes subidas: " + listadoEPP.size());
			if (listadoEPP != null && listadoEPP.size() >= 1) {
				for (EstudianteExamenComplexivoPP epp : listadoEPP) {
					String asunto = "URGENTE TITULACIÓN: SUBIDA DEL DOCUMENTO FINAL DE SU OPCIÓN DE TITULACIÓN";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante <br /></span></div>"
							+ "<div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que le falta "
							+ "generar el documento final de su opción de titulación, para esto debe ir a la opción de menú Estudiante --> Examen Complexivo -->"
							+ " Subir Trabajo, seguir las instrucciones indicadas en el manual de procedimiento que se indica dando clic <a title='aqu&iacute;' href='https://docs.google.com/document/d/1a-UIPjaEcwycPT1YR9RvTXCji_KPWuyzxnLJwsLCbiA/edit#heading=h.1phsvr8y5dhr' target='_blank' rel='noopener'>aqui</a></div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(epp.getEstudiante().getEmail(), asunto, detalle, listAdjunto, parametros);
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

			List<EstudianteExamenComplexivoPP> listadoEPP = estudianteExamenComplexivoPPService.obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.estudiante e inner join epp.carrera c inner join"
							+ " c.unidadAcademica ua where p.id=?1 and epp.numeroActaCalificacion is not null and epp.archivo is not null and"
							+ " epp.validarArchivo is false",
					new Object[] { proceso }, 0, false, new Object[] {});

			System.out.println("Número del listado archivos inconsistencias: " + listadoEPP.size());

			if (listadoEPP != null && listadoEPP.size() >= 1) {
				for (EstudianteExamenComplexivoPP epp : listadoEPP) {
					String asunto = "URGENTE TITULACIÓN: DOCUMENTO FINAL CON INCONSISTENCIAS DE SU OPCIÓN DE TITULACIÓN";
					String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@ estudiante <br /></span></div>"
							+ "<div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para recordarle la corrección "
							+ "de inconsistencias encontradas de su documento final cargado en la plataforma de titulación, esta validación la realiza el personal de UMMOG "
							+ "de su Unidad Académica, le sugerimos volver a generar el documento final con las correcciones respectivas en la opción de menú Estudiante --> "
							+ "Examen Complexivo --> Subir Trabajo, seguir las instrucciones indicadas en el manual de procedimiento que se indica dando clic <a title='aqu&iacute;' "
							+ "href='https://docs.google.com/document/d/1a-UIPjaEcwycPT1YR9RvTXCji_KPWuyzxnLJwsLCbiA/edit#heading=h.1phsvr8y5dhr' target='_blank' rel='noopener'>aqui.</a></div><br/>"
							+ "<div><span style='color: #000000;'>Si es que usted no corrige el documento a tiempo y lo genera nuevamente para la validación respectiva, su título de tercer nivel no podrá ser registrado en el sistema de la SENESCYT. <div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = null;
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(epp.getEstudiante().getEmail(), asunto, detalle, listAdjunto, parametros);
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

			List<EstudianteExamenComplexivoPP> listadoEPP = new ArrayList<EstudianteExamenComplexivoPP>();
			listadoEPP = obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua inner join epp.proceso p inner join epp.estudiante e where p.id=?1 and epp.archivo is not null and epp.validarArchivo is true and epp.urlBiblioteca is null and ua.id=?2 order by c.nombre, e.apellidoNombre asc",
					new Object[] { proceso, unidadAcademica }, 0, false, new Object[0]);
			System.out.println(
					"Elementos en miembrosBiblioteca de " + unidadAcademica + ": " + miembrosBiblioteca.size());
			System.out.println("Elementos en listadoEPP de " + unidadAcademica + ": " + listadoEPP.size());

			if (listadoEPP != null && listadoEPP.size() >= 1) {
				// List<Usuario> destinatarios = new ArrayList<>();
				// Usuario u = usuarioService.obtenerObjeto("select u from
				// Usuario u where u.id='0705667509'",
				// new Object[0], false, new Object[0]);
				// destinatarios.add(u);
				File fil = traerEPPenExcelEPP(listadoEPP);
				String asunto = "ARCHIVOS VALIDADOS PENDIENTE DE SUBIR AL REPOSITORIO DIGITAL";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) encargado(s) de biblioteca de la "
						+ (unidadAcademica.compareTo("UAIC") == 0 ? "Unidad Académica de Ingeniería Civil"
								: unidadAcademica.compareTo("UACA") == 0 ? "Unidad Académica de Ciencias Agropecuarias"
										: unidadAcademica.compareTo("UACQS") == 0
												? "Unidad Académica de Ciencias Químicas y de la Salud"
												: unidadAcademica.compareTo("UACE") == 0
														? "Unidad Académica de Ciencias Empresariales"
														: "Unidad Académica de Ciencias Sociales")
						+ "<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email es para notificar que se encuentran validados los archivos de la dimensión escrita por parte de los estudiantes de examen complexivo del proceso actual "
						+ proceso
						+ ". Para dicho fin, se adjunta un archivo en formato excel con el listado de dichos estudiantes para una mejor búsqueda dentro de la plataforma.</span></div><br /><div><span style='color: #000000;'>&nbsp;</span></div><div>Se solicita efectuar la respectiva subida del archivo al repositorio digital de la UTMACH.</div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
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

			List<EstudianteExamenComplexivoPP> listadoEPP = new ArrayList<EstudianteExamenComplexivoPP>();
			listadoEPP = obtenerLista(
					"select epp from EstudianteExamenComplexivoPP epp inner join epp.carrera c inner join c.unidadAcademica ua inner join epp.proceso p inner join epp.estudiante e where p.id=?1 and epp.archivo is not null and epp.validarArchivo is null and ua.id=?2 order by c.nombre, e.apellidoNombre asc",
					new Object[] { proceso, unidadAcademica }, 0, false, new Object[0]);
			System.out.println("Elementos en miembrosUmmog de " + unidadAcademica + ": " + miembrosUmmog.size());
			System.out.println("Elementos en listadoEPP de " + unidadAcademica + ": " + listadoEPP.size());

			if (listadoEPP != null && listadoEPP.size() >= 1) {
				// List<Usuario> destinatarios = new ArrayList<>();
				// Usuario u = usuarioService.obtenerObjeto("select u from
				// Usuario u where u.id='0705667509'",
				// new Object[0], false, new Object[0]);
				// destinatarios.add(u);
				File fil = traerEPPenExcelEPP(listadoEPP);
				String asunto = "ARCHIVOS SUBIDOS A LA PLATAFORMA PENDIENTES DE REVISIÓN";
				String detalle = "<div dir='ltr'><span style='color: #000000;'>Estimad@(s) analistas y jefe de UMMOG de la "
						+ (unidadAcademica.compareTo("UAIC") == 0 ? "Unidad Académica de Ingeniería Civil"
								: unidadAcademica.compareTo("UACA") == 0 ? "Unidad Académica de Ciencias Agropecuarias"
										: unidadAcademica.compareTo("UACQS") == 0
												? "Unidad Académica de Ciencias Químicas y de la Salud"
												: unidadAcademica.compareTo("UACE") == 0
														? "Unidad Académica de Ciencias Empresariales"
														: "Unidad Académica de Ciencias Sociales")
						+ "<br /></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div><span style='color: #000000;'>El presente email, es para notificar la subida de archivos de la dimensión escrita por parte de los estudiantes de examen complexivo del proceso actual "
						+ proceso
						+ " a la plataforma, de los cuales deben ser validados. Se adjunta un archivo en formato excel con el listado correspondiente, con la finalidad de efectuar una mejor búsqueda dentro de la plataforma.</span></div><div>Se solicita efectuar la respectiva validación para continuar con la siguiente fase que es la subida del mismo al repositorio digital de la UTMACH.</div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
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

	public void insertar(EstudianteExamenComplexivoPP temaPractico) {
		temaPractico.setActivo(true);
		temaPracticoDao.insertar(temaPractico);
	}

	public void insertarTemaPracticoEstudiante(EstudianteExamenComplexivoPP temaPractico,
			EstudianteExamenComplexivoPT estudianteExamenComplexivoPT) {
		temaPractico = obtenerPorId(temaPractico.getId());
		// if (temaPractico.getEstudiantePeriodo() == null) {
		// temaPractico.setFechaSeleccion(timestamp());
		// temaPractico.setEstudiantePeriodo(estudiantePeriodo);
		// temaPracticoDao.actualizar(temaPractico);
		// // envioCorreoService.enviarCasoPractico(
		// // estudiantePeriodo.getEstudiante(), temaPractico);
		// }
	}

	public List<EstudianteExamenComplexivoPP> obtenerPorCarreraId(int carreraId) {
		return temaPracticoDao.obtenerLista(
				"select tp from TemaPractico tp " + "inner join tp.docenteAsignatura da "
						+ "inner join da.asignatura a " + "inner join a.mallaCurricular mc "
						+ "inner join mc.carrera c " + "where c.id=?1 and tp.estudiantePeriodo is null",
				new Object[] { carreraId }, 0, false, new Object[] {});
	}

	public EstudianteExamenComplexivoPP obtenerPorId(int id) {
		return temaPracticoDao.obtenerObjeto("select pp from EstudianteExamenComplexivoPP pp where pp.id=?1",
				new Object[] { id }, false, new Object[] {});
	}

	public File reporteNoAdeudar(List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {

		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Listado de estudiantes examen complexivo");

			// Crear encabezados
			Row encabezado = sheet.createRow(0);
			encabezado.createCell(0).setCellValue("Carrera");
			encabezado.createCell(1).setCellValue("Cedula Estudiante");
			encabezado.createCell(2).setCellValue("Apellidos y Nombres del Estudiante");

			// Crear encabezados
			Row columnas = sheet.createRow(0);
			columnas.createCell(0).setCellValue("Carrera");
			columnas.createCell(1).setCellValue("Cedula Estudiante");
			columnas.createCell(2).setCellValue("Apellidos y Nombres del Estudiante");
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
			for (EstudianteExamenComplexivoPP epp : listadoEstudiantesExamenComplexivoPP) {
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
					new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesExamenComplexivo.xls"));
			workbook.write(out);
			out.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
		return new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesExamenComplexivo.xls");
	}

	public String traerCalificaciones(EstudianteExamenComplexivoPP epp) {
		BigDecimal notaTeorica = epp.getNotaTeorica() == null ? new BigDecimal(0) : epp.getNotaTeorica();
		BigDecimal notaEscritaFinal = new BigDecimal(0);
		BigDecimal notaOralFinal = new BigDecimal(0);
		BigDecimal notaFinal = new BigDecimal(0);
		if (epp.getEspecialistaSuplantadoE() != null
				&& epp.getEspecialista1().getId().compareTo(epp.getEspecialistaSuplantadoE().getId()) == 0) {
			if (epp.getEs1() != null && epp.getEe2() != null && epp.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(epp.getEs1().add(epp.getEe2()).add(epp.getEe3()), 3);
		} else if (epp.getEspecialistaSuplantadoE() != null
				&& epp.getEspecialista2().getId().compareTo(epp.getEspecialistaSuplantadoE().getId()) == 0) {
			if (epp.getEe1() != null && epp.getEs1() != null && epp.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(epp.getEe1().add(epp.getEs1()).add(epp.getEe3()), 3);
		} else if (epp.getEspecialistaSuplantadoE() != null
				&& epp.getEspecialista3().getId().compareTo(epp.getEspecialistaSuplantadoE().getId()) == 0) {
			if (epp.getEe1() != null && epp.getEe2() != null && epp.getEs1() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(epp.getEe1().add(epp.getEe2()).add(epp.getEs1()), 3);
		} else {
			if (epp.getEe1() != null && epp.getEe2() != null && epp.getEe3() != null)
				notaEscritaFinal = UtilsMath.divideCalificaciones(epp.getEe1().add(epp.getEe2()).add(epp.getEe3()), 3);
		}

		if (epp.getFechaSustentacionGracia() == null) {
			if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista1().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOos1() != null && epp.getOoe2() != null && epp.getOoe3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOos1().add(epp.getOoe2()).add(epp.getOoe3()),
							3);
				}
			} else if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista2().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOoe1() != null && epp.getOos1() != null && epp.getOoe3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOoe1().add(epp.getOos1()).add(epp.getOoe3()),
							3);
				}
			} else if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista3().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOoe1() != null && epp.getOoe2() != null && epp.getOos1() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOoe1().add(epp.getOoe2()).add(epp.getOos1()),
							3);
				}
			} else {
				if (epp.getOoe1() != null && epp.getOoe2() != null && epp.getOoe3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOoe1().add(epp.getOoe2()).add(epp.getOoe3()),
							3);
				}
			}
		} else {
			if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista1().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOgs1() != null && epp.getOge2() != null && epp.getOge3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOgs1().add(epp.getOge2()).add(epp.getOge3()),
							3);
				}
			} else if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista2().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOge1() != null && epp.getOgs1() != null && epp.getOge3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOge1().add(epp.getOgs1()).add(epp.getOge3()),
							3);
				}
			} else if (epp.getEspecialistaSuplantadoO() != null
					&& epp.getEspecialista3().getId().compareTo(epp.getEspecialistaSuplantadoO().getId()) == 0) {
				if (epp.getOge1() != null && epp.getOge2() != null && epp.getOgs1() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOge1().add(epp.getOge2()).add(epp.getOgs1()),
							3);
				}
			} else {
				if (epp.getOge1() != null && epp.getOge2() != null && epp.getOge3() != null) {
					notaOralFinal = UtilsMath.divideCalificaciones(epp.getOge1().add(epp.getOge2()).add(epp.getOge3()),
							3);
				}
			}
		}

		notaFinal = UtilsMath.redondearCalificacion(notaTeorica.add(notaEscritaFinal).add(notaOralFinal));
		System.out.println(
				"En el service: " + notaTeorica + "-" + notaEscritaFinal + "-" + notaOralFinal + "-" + notaFinal);
		return notaTeorica + "-" + notaEscritaFinal + "-" + notaOralFinal + "-" + notaFinal;
	}

	public File traerEPPenExcelEPP(List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {

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
			for (EstudianteExamenComplexivoPP epp : listadoEstudiantesExamenComplexivoPP) {
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
					new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesExamenComplexivo.xls"));
			workbook.write(out);
			out.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
		return new File(UtilsArchivos.getRutaReporteExcel() + "listadoEstudiantesExamenComplexivo.xls");
	}

	public void verificarEstudianteReactivoPractico(String proceso) {
		List<Usuario> estudiantesPendientes = usuarioService.obtenerPorSql(
				"select distinct e1.* from \"estudiantesExamenComplexivoPT\" ept1 inner join examenes ex1 on (ex1.\"estudianteExamenComplexivoPT\"=ept1.id) inner join \"preguntasExamenes\" pre1 on (pre1.examen=ex1.id) inner join	usuarios e1 on (ept1.estudiante=e1.id) inner join procesos p1 on (ept1.proceso=p1.id) inner join \"tiposExamenes\" te1 on (ex1.\"tipoExamen\"=te1.id) inner join carreras c1 on (ept1.carrera=c1.id) where p1.id='"
						+ proceso
						+ "' and (te1.id='OR' or te1.id='GR') and (select count(pe2.*) from \"preguntasExamenes\" pe2 inner join examenes ex2 on (pe2.examen=ex2.id)inner join \"estudiantesExamenComplexivoPT\" ept2 on(ex2.\"estudianteExamenComplexivoPT\"=ept2.id)inner join procesos p2 on (ept2.proceso=p2.id)inner join preguntas pre2 on (pe2.pregunta=pre2.id) inner join \"tiposExamenes\" te2 on (ex2.\"tipoExamen\"=te2.id) where ept2.id=ept1.id and	p2.id=p1.id and	te2.id=te1.id and pe2.respuesta=pre2.\"sostiApantisi\") >= 20 and not exists (select 1 from \"estudiantesExamenComplexivoPP\" epp1 where epp1.estudiante = e1.id)",
				Usuario.class);
		System.out.println("Los estudiantes que faltan son: ");
		try {
			if (estudiantesPendientes != null || estudiantesPendientes.size() >= 1) {
				for (Usuario u : estudiantesPendientes) {
					System.out.println("Estudiantes: " + u.getApellidoNombre());

					String asunto = "URGENTE TITULACIÓN, SELECCIÓN DE REACTIVOS PRÁCTICOS";
					String detalle = "<div dir='ltr'><span style='color: #000000;'> Estimad@ estudiante en proceso de titulación, el presente email es para informar que, como usted ha aprobado el examen complexivo - parte teórica (prueba por computadora), esta pendiente la elección del reactivo práctico para que pueda continuar con su proceso de titulación, de no hacerlo usted podría quedar fuera del proceso. Recuerde que en la plataforma de titulación, opción de menú Estudiante --> Examen Complexivo --> Reactivo Práctico, a continuación se le presentará un listado de los reactivos prácticos disponibles para que usted elija.</span></div><br /><div><span style='color: #000000;'>&nbsp;</span></div><br/><div dir='ltr'><span style='color: #000000;'>Esperando contar con su colaboraci&oacute;n se despide<span style='font-size: small;'><strong><br /></strong></span></span></div><div dir='ltr'><span style='color: #000000;'>&nbsp;</span></div><div dir='ltr'><span style='color: #000000;'><img src='https://ci4.googleusercontent.com/proxy/Wj0OkVGh9fJ8PoFAmkEEGaL-sjCkuc1N7BqU8-SFeyFYcwJF3YYfePkm2H72-i0kX6E_kDS3LdWgY0N5f0eaDmPTEdAm2EeR7zFr7wfuHN7ss4k36Xk60qyB9FUxCqnnvJIbG8zYWFPkIg=s0-d-e1-ft#http://www.utmachala.edu.ec/portalwp/wp-content/uploads/2015/08/LOGO_OUT-300x300.png' alt='' width='96' height='96' /><br /></span></div><div dir='ltr'>&nbsp;</div><div dir='ltr'><strong><span style='color: #000000; font-size: small;'>Direcci&oacute;n Acad&eacute;mica - <a href='http://www.utmachala.edu.ec/portal/public/general/articulo/hl/es/item/456' target='_blank'>Secci&oacute;n de Titulaci&oacute;n</a></span></strong><div><div><div><strong><span style='color: #000000;'><a href='http://www.utmachala.edu.ec/' target='_blank'>www.utmachala.edu.ec</a></span></strong></div></div></div><div><div><div><div><span style='color: #000000; font-size: small;'>Machala, Ecuador</span></div><div><span style='color: #000000; font-size: small;'>&nbsp;</span></div></div><div><p><span style='font-size: small;'><em>No imprima &eacute;ste correo a menos que sea estrictamente necesario.</em></span></p><p><strong><span lang='ES'>&nbsp;</span></strong></p><p><span style='color: #0000ff; font-family: arial, helvetica, sans-serif; font-size: xx-small;'><em><strong><span lang='ES'>AVISO DE CONFIDENCIALIDAD:</span></strong><span lang='ES'>&nbsp;'La informaci&oacute;n contenida en este e-mail es confidencial y solo puede ser utilizada por la persona natural o jur&iacute;dica a la cual est&aacute; dirigido. En el evento, de que el receptor no sea la persona autorizada; cualquier retenci&oacute;n, difusi&oacute;n, distribuci&oacute;n o copia de &eacute;ste mensaje es &nbsp;prohibida y sancionada por la ley'. 'Si Usted recibi&oacute; este mensaje por error, notifique al Administrador o a quien le envi&oacute; inmediatamente, elim&iacute;nelo sin hacer copias.&nbsp;</span>Las opiniones que contenga este mensaje son exclusivas de su autor y no necesariamente representan la opini&oacute;n oficial de la UNIVERSIDAD T&Eacute;CNICA DE MACHALA'</em></span></p></div></div></div>";
					List<File> listAdjunto = new ArrayList<File>();
					Parametro parametro = parametroService.obtener();
					Map<String, String> parametros = parametroService.traerMap(parametro);
					UtilsMail.envioCorreo(u.getEmail(), asunto, detalle, listAdjunto, parametros);

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}