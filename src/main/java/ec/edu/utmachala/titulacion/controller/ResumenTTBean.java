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
public class ResumenTTBean implements Serializable {

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
		} else
			tt = new EstudianteTrabajoTitulacion();
	}

	public void actualizar() {
		if (tt.getSeminarioTrabajoTitulacion() == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
		} else if (ttId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Seleccione una carrera e ingresé el resumen de la investigación y las palabras claves");
		else if (tt.getResumen().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el resumen de la investigación");
		else if (tt.getAbstract1().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el abstract de la investigación");
		else if (tt.getPalabrasClaves().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese las palabras claves de la investigación");
		else {
			tt.setResumen(tt.getResumen().replaceAll("\n", " ").trim());
			tt.setResumen(tt.getResumen().replaceAll("  ", " ").trim());

			tt.setAbstract1(tt.getAbstract1().replaceAll("\n", " ").trim());
			tt.setAbstract1(tt.getAbstract1().replaceAll("  ", " ").trim());

			tt.setPalabrasClaves(tt.getPalabrasClaves().replaceAll("\n", " ").trim());
			tt.setPalabrasClaves(tt.getPalabrasClaves().replaceAll("  ", " ").trim());
			tt.setPalabrasClaves(tt.getPalabrasClaves().toUpperCase());

			StringTokenizer st = new StringTokenizer(tt.getResumen());
			int palabras = st.countTokens();

			if (tt.getOpcionTitulacion().getId() == 4) {// verificar si es
														// opción artículo
														// científico
				guardarResumenAbstractKeyWords();
			} else {
				if (palabras < 500 || palabras > 1000)
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Debe ingresar desde 500 hasta 1000 palabras");
				else {
					guardarResumenAbstractKeyWords();
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

	public void guardarResumenAbstractKeyWords() {
		EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
				new Object[] { tt.getSeminarioTrabajoTitulacion().getId(), UtilSeguridad.obtenerUsuario() }, false,
				new Object[] {});
		if (ett2 != null && ett2.getId() != null) {
			ett2.setPalabrasClaves(tt.getPalabrasClaves());
			ett2.setResumen(tt.getResumen());
			ett2.setAbstract1(tt.getAbstract1());
			estudianteTrabajoTitulacionService.actualizar(ett2);
		}
		estudianteTrabajoTitulacionService.actualizar(tt);
		UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualización exitosa");
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
		if (ttId != 0)
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