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
public class ResumenECBean implements Serializable {

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
		if (ppId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Seleccione una carrera e ingresé el resumen de la investigación y las palabras claves");
		else if (pp.getResumen().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el resumen de la investigación");
		else if (pp.getAbstract1().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Ingrese el abstract de la investigación");
		else if (pp.getPalabrasClaves().trim().compareToIgnoreCase("") == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Ingrese las palabras claves de la investigación");
		else {
			pp.setResumen(pp.getResumen().replaceAll("\n", " ").trim());
			pp.setResumen(pp.getResumen().replaceAll("  ", " ").trim());

			pp.setAbstract1(pp.getAbstract1().replaceAll("\n", " ").trim());
			pp.setAbstract1(pp.getAbstract1().replaceAll("  ", " ").trim());

			pp.setPalabrasClaves(pp.getPalabrasClaves().replaceAll("\n", " ").trim());
			pp.setPalabrasClaves(pp.getPalabrasClaves().replaceAll("  ", " ").trim());
			pp.setPalabrasClaves(pp.getPalabrasClaves().toUpperCase());

			StringTokenizer st = new StringTokenizer(pp.getResumen());
			int palabras = st.countTokens();

			if (palabras > 250)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Debe ingresar hasta 250 palabras");
			else {
				estudiantesExamenComplexivoPPService.actualizar(pp);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Actualización exitosa");
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