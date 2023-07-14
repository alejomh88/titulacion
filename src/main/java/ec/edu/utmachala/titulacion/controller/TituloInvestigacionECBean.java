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

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;

@Controller
@Scope("session")
public class TituloInvestigacionECBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	private List<EstudianteExamenComplexivoPP> listaPP;
	private EstudianteExamenComplexivoPP pp;
	private Integer ppId;

	@PostConstruct
	public void a() {
		listaPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaPP != null && listaPP.size() == 1) {
			ppId = listaPP.get(0).getId();
			obtenerPP();
		} else
			pp = new EstudianteExamenComplexivoPP();
	}

	public void actualizar() {
		if (ppId == 0) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Seleccione una carrera e ingresé un título de la investigación");
		} else {
			if (pp.getTituloInvestigacion().trim().compareToIgnoreCase("") == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el título de la investigación");
			else {

				pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("\n", " ").trim());
				pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("  ", " ").trim());

				StringTokenizer st = new StringTokenizer(pp.getTituloInvestigacion());
				int palabras = st.countTokens();

				if (palabras > 20)
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Debe ingresar como máximo 20 palabras");
				else {

					if (pp.getTituloInvestigacion().contains("<B>") || pp.getTituloInvestigacion().contains("</B>")
							|| pp.getTituloInvestigacion().contains("<I>")
							|| pp.getTituloInvestigacion().contains("</I>")
							|| pp.getTituloInvestigacion().contains("<U>")
							|| pp.getTituloInvestigacion().contains("</U>")) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Por favor, evite ingresar las etiquetas en mayúsculas.");
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("<B>", ""));
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("</B>", ""));
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("<I>", ""));
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("</I>", ""));
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("<U>", ""));
						pp.setTituloInvestigacion(pp.getTituloInvestigacion().replaceAll("</U>", ""));

					} else {
						String[] tituloInvestigacion = pp.getTituloInvestigacion().split(" ");
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
									estudiantesExamenComplexivoPPService.actualizarSQL(
											"UPDATE \"estudiantesExamenComplexivoPP\" SET \"tituloInvestigacion\" = '"
													+ pp.getTituloInvestigacion() + "' WHERE id=" + pp.getId() + ";");
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

	public List<EstudianteExamenComplexivoPP> getListaPP() {
		return listaPP;
	}

	public EstudianteExamenComplexivoPP getPp() {
		return pp;
	}

	public Integer getPpId() {
		return ppId;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public void obtenerPP() {
		if (ppId != 0)
			pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select pp from EstudianteExamenComplexivoPP pp where pp.id=?1", new Object[] { ppId }, false,
					new Object[] {});
	}

	public void setListaPP(List<EstudianteExamenComplexivoPP> listaPP) {
		this.listaPP = listaPP;
	}

	public void setPp(EstudianteExamenComplexivoPP pp) {
		this.pp = pp;
	}

	public void setPpId(Integer ppId) {
		this.ppId = ppId;
	}

}