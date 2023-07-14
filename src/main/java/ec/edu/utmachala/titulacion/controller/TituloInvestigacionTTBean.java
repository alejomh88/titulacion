package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class TituloInvestigacionTTBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	private List<EstudianteTrabajoTitulacion> listaTT;
	private Integer ttId;
	private EstudianteTrabajoTitulacion tt;

	@PostConstruct
	public void a() {
		listaTT = estudianteTrabajoTitulacionService.obtenerLista(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaTT != null && listaTT.size() == 1) {
			ttId = listaTT.get(0).getId();
			obtenerTT();
		} else if (listaTT != null && listaTT.size() > 1) {
			ttId = 0;
			obtenerTT();
		} else
			tt = new EstudianteTrabajoTitulacion();
	}

	public void actualizar() {
		if (tt.getSeminarioTrabajoTitulacion() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
		} else if (ttId == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Seleccione una carrera e ingresé un título de la investigación");
		} else {
			if (tt.getTituloInvestigacion().trim().compareToIgnoreCase("") == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el título de la investigación");
			else {

				tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("\n", " ").trim());
				tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("  ", " ").trim());

				StringTokenizer st = new StringTokenizer(tt.getTituloInvestigacion());
				int palabras = st.countTokens();

				if (palabras > 20)
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Debe ingresar como máximo 20 palabras");
				else {
					EstudianteTrabajoTitulacion tta = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e where tt.tituloInvestigacion=?1 and e.email!=?2",
							new Object[] { tt.getTituloInvestigacion(), UtilSeguridad.obtenerUsuario() }, false,
							new Object[] {});

					if (tta != null && tta.getId() != null) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Este título de investigación ya fué registrado por otro estudiante");
					} else {

						if (tt.getTituloInvestigacion().contains("<B>") || tt.getTituloInvestigacion().contains("</B>")
								|| tt.getTituloInvestigacion().contains("<I>")
								|| tt.getTituloInvestigacion().contains("</I>")
								|| tt.getTituloInvestigacion().contains("<U>")
								|| tt.getTituloInvestigacion().contains("</U>")) {
							UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
									"Por favor, evite ingresar las etiquetas en mayúsculas.");
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("<B>", ""));
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("</B>", ""));
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("<I>", ""));
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("</I>", ""));
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("<U>", ""));
							tt.setTituloInvestigacion(tt.getTituloInvestigacion().replaceAll("</U>", ""));

						} else {
							String[] tituloInvestigacion = tt.getTituloInvestigacion().split(" ");
							int abrirNegrita = 0;
							int cerrarNegrita = 0;
							int abrirCursiva = 0;
							int cerrarCursiva = 0;
							int abrirSubrayado = 0;
							int cerrarSubrayado = 0;

							for (int i = 0; i < tituloInvestigacion.length; i++) {
								if (tituloInvestigacion[i].contains("<b>"))
									abrirNegrita++;
								if (tituloInvestigacion[i].contains("</b>"))
									cerrarNegrita++;
								if (tituloInvestigacion[i].contains("<i>"))
									abrirCursiva++;
								if (tituloInvestigacion[i].contains("</i>"))
									cerrarCursiva++;
								if (tituloInvestigacion[i].contains("<u>"))
									abrirSubrayado++;
								if (tituloInvestigacion[i].contains("</u>"))
									cerrarSubrayado++;
							}

							if (abrirNegrita == cerrarNegrita) {
								if (abrirCursiva == cerrarCursiva) {
									if (abrirSubrayado == cerrarSubrayado) {
										EstudianteTrabajoTitulacion ett2 = new EstudianteTrabajoTitulacion();
										ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
												"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
												new Object[] { tt.getSeminarioTrabajoTitulacion().getId(),
														UtilSeguridad.obtenerUsuario() },
												false, new Object[] {});
										if (ett2 != null && ett2.getId() != null) {
											ett2.setTituloInvestigacion(tt.getTituloInvestigacion());
											// estudianteTrabajoTitulacionService.actualizar(ett2);
											estudianteTrabajoTitulacionService.actualizarSQL(
													"UPDATE \"estudiantesTrabajosTitulacion\" SET \"tituloInvestigacion\" = '"
															+ tt.getTituloInvestigacion() + "' WHERE id=" + ett2.getId()
															+ ";");
										}
										// estudianteTrabajoTitulacionService.actualizar(tt);
										estudianteTrabajoTitulacionService.actualizarSQL(
												"UPDATE \"estudiantesTrabajosTitulacion\" SET \"tituloInvestigacion\" = '"
														+ tt.getTituloInvestigacion() + "' WHERE id=" + tt.getId()
														+ ";");
										UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
												"Actualización exitosa");
										a();
									} else
										UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
												"Si quiere dar estilo de subrayado, verifique que tiene las etiquetas de subrayado abierto(s) y cerrado(s) respectivamente.");
								} else
									UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
											"Si quiere dar estilo de cursiva, verifique que tiene las etiquetas de cursiva abierta(s) y cerrada(s) respectivamente.");
							} else
								UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
										"Si quiere dar estilo de negrita, verifique que tiene las etiquetas de negrita abierta(s) y cerrada(s) respectivamente.");
						}
					}
				}
			}
		}
	}

	public List<EstudianteTrabajoTitulacion> getListaTT() {
		return listaTT;
	}

	public EstudianteTrabajoTitulacion getTt() {
		return tt;
	}

	public Integer getTtId() {
		return ttId;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public void obtenerTT() {
		if (ttId == 0)
			tt = new EstudianteTrabajoTitulacion();
		else
			tt = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt where tt.id=?1", new Object[] { ttId }, false,
					new Object[] {});
	}

	public void setListaTT(List<EstudianteTrabajoTitulacion> listaTT) {
		this.listaTT = listaTT;
	}

	public void setTt(EstudianteTrabajoTitulacion tt) {
		this.tt = tt;
	}

	public void setTtId(Integer ttId) {
		this.ttId = ttId;
	}

}