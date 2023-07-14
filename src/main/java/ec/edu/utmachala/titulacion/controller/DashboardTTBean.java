package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;
import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.CertificadoEstudianteService;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsMath;;

@Controller
@Scope("session")
public class DashboardTTBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private CertificadoEstudianteService certificadoEstudianteService;

	private List<EstudianteTrabajoTitulacion> listaTT;
	private Integer ttId;
	private EstudianteTrabajoTitulacion tt;

	private List<CertificadoEstudiante> listadoCertificados;
	private List<CertificadoEstudiante> listadoCertificadosTipoEstudiante;

	private BigDecimal calificacionParteEscritaFinal;

	private BigDecimal calificacionParteOralFinal;

	private BigDecimal calificacionFinal;

	private boolean panelPrincipal;
	private boolean panelTutorias;

	public DashboardTTBean() {
	}

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelTutorias = false;
		listaTT = estudianteTrabajoTitulacionService.obtenerLista(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaTT != null && listaTT.size() == 1) {
			ttId = listaTT.get(0).getId();
			calificaciones();
			listarDependenciasNoAdeudar();
		} else {
			tt = new EstudianteTrabajoTitulacion();
			listadoCertificados = new ArrayList<CertificadoEstudiante>();
			listadoCertificadosTipoEstudiante = new ArrayList<CertificadoEstudiante>();
		}
	}

	public void activarPanelPrincipal() {
		panelPrincipal = true;
		panelTutorias = false;
	}

	public void activarPanelTutorias() {
		panelPrincipal = false;
		panelTutorias = true;
	}

	void calificaciones() {
		tt = estudianteTrabajoTitulacionService.obtenerObjeto(
				"select tt from EstudianteTrabajoTitulacion tt left join fetch tt.tutorias t where tt.id=?1",
				new Object[] { ttId }, false, new Object[] {});
		// System.out.println("datos del tt " +
		// tt.getEstudiante().getApellidoNombre());
		String calificaciones = estudianteTrabajoTitulacionService.traerCalificaciones(tt);
		calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
		calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
		calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
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

	public EstudianteTrabajoTitulacionService getEstudianteTrabajoTitulacionService() {
		return estudianteTrabajoTitulacionService;
	}

	public List<CertificadoEstudiante> getListadoCertificados() {
		return listadoCertificados;
	}

	public List<CertificadoEstudiante> getListadoCertificadosTipoEstudiante() {
		return listadoCertificadosTipoEstudiante;
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

	public boolean isPanelPrincipal() {
		return panelPrincipal;
	}

	public boolean isPanelTutorias() {
		return panelTutorias;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public void listarDependenciasNoAdeudar() {
		listadoCertificados = new ArrayList<CertificadoEstudiante>();
		listadoCertificadosTipoEstudiante = new ArrayList<CertificadoEstudiante>();
		// System.out.println("El id del estudiante ETT: " + tt.getId());
		if (tt.getNumeroActaCalificacion() != null) {

			listadoCertificadosTipoEstudiante = certificadoEstudianteService.obtenerLista(
					"select ce from CertificadoEstudiante ce inner join ce.certificado cer inner join cer.dependencia d inner join"
							+ " cer.carrera c inner join ce.estudianteTrabajoTitulacion ett where ett.id=?1 and "
							+ "d.id in ('ESTACRED', 'ESTATITU') order by d.nombre",
					new Object[] { tt.getId() }, 0, false, new Object[] {});

			listadoCertificados = certificadoEstudianteService.obtenerLista(
					"select ce from CertificadoEstudiante ce inner join ce.certificado cer inner join cer.dependencia d inner join"
							+ " cer.carrera c inner join ce.estudianteTrabajoTitulacion ett where ett.id=?1 and "
							+ "d.id not in ('ESTACRED', 'ESTATITU') order by d.nombre",
					new Object[] { tt.getId() }, 0, false, new Object[] {});

			// for (CertificadoEstudiante ce : listadoCertificados) {
			// System.out.println("Dependencia: " +
			// ce.getCertificado().getDependencia().getNombre());
			// }

			// System.out.println("Tamaño de todo la lista: " +
			// listadoCertificados.size());
			// System.out.println("Tamaño de todo la lista: " +
			// listadoCertificadosTipoEstudiante.size());
		}
	}

	public void obtenerTT() {
		if (ttId != 0)
			calificaciones();
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

	public void setEstudianteTrabajoTitulacionService(
			EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService) {
		this.estudianteTrabajoTitulacionService = estudianteTrabajoTitulacionService;
	}

	public void setListadoCertificados(List<CertificadoEstudiante> listadoCertificados) {
		this.listadoCertificados = listadoCertificados;
	}

	public void setListadoCertificadosTipoEstudiante(List<CertificadoEstudiante> listadoCertificadosTipoEstudiante) {
		this.listadoCertificadosTipoEstudiante = listadoCertificadosTipoEstudiante;
	}

	public void setListaTT(List<EstudianteTrabajoTitulacion> listaTT) {
		this.listaTT = listaTT;
	}

	public void setPanelPrincipal(boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void setPanelTutorias(boolean panelTutorias) {
		this.panelTutorias = panelTutorias;
	}

	public void setTt(EstudianteTrabajoTitulacion tt) {
		this.tt = tt;
	}

	public void setTtId(Integer ttId) {
		this.ttId = ttId;
	}

}