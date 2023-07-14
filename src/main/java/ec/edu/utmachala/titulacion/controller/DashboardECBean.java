package ec.edu.utmachala.titulacion.controller;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.CertificadoEstudiante;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPT;
import ec.edu.utmachala.titulacion.entity.Grupo;
import ec.edu.utmachala.titulacion.service.CertificadoEstudianteService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPTService;
import ec.edu.utmachala.titulacion.service.GrupoService;
import ec.edu.utmachala.titulacion.service.NotaBigDecimalService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsMath;;

@Controller
@Scope("session")
public class DashboardECBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private EstudiantesExamenComplexivoPTService estudiantesExamenComplexivoPTService;

	@Autowired
	private NotaBigDecimalService notaBigDecimalService;

	@Autowired
	private GrupoService grupoService;

	@Autowired
	private CertificadoEstudianteService certificadoEstudianteService;

	private List<EstudianteExamenComplexivoPT> listaPT;
	private Integer ptId;

	private List<CertificadoEstudiante> listadoCertificados;
	private List<CertificadoEstudiante> listadoCertificadosTipoEstudiante;

	private EstudianteExamenComplexivoPP pp;
	private EstudianteExamenComplexivoPT pt;

	private BigDecimal calificacionParteTeoricaOdinaria;
	private BigDecimal calificacionParteTeoricaGracia;
	private BigDecimal calificacionParteTeoricaFinal;

	private BigDecimal calificacionParteEscritaFinal;

	private BigDecimal calificacionParteOralOdinaria;
	private BigDecimal calificacionParteOralGracia;
	private BigDecimal calificacionParteOralFinal;

	private BigDecimal calificacionFinal;

	private Date fechaParteTeorica;
	private Grupo grupoExamen;

	public DashboardECBean() {
	}

	@PostConstruct
	public void a() {
		listaPT = estudiantesExamenComplexivoPTService.obtenerLista(
				"select pt from EstudianteExamenComplexivoPT pt inner join pt.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaPT != null && listaPT.size() == 1) {
			ptId = listaPT.get(0).getId();
			calificaciones();
			obtenerFechaParteTeorica();
			listarDependenciasNoAdeudar();
		} else {
			pp = new EstudianteExamenComplexivoPP();
			listadoCertificados = new ArrayList<CertificadoEstudiante>();
			listadoCertificadosTipoEstudiante = new ArrayList<CertificadoEstudiante>();
		}
	}

	void calificaciones() {

		pt = estudiantesExamenComplexivoPTService.obtenerObjeto(
				"select pt from EstudianteExamenComplexivoPT pt where pt.id=?1", new Object[] { ptId }, false,
				new Object[] {});

		pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
						+ "inner join pp.carrera c inner join pp.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
				new Object[] { UtilSeguridad.obtenerUsuario(), pt.getCarrera().getId(), pt.getProceso().getId() },
				false, new Object[] {});

		try {
			String calificaciones = estudiantesExamenComplexivoPPService.traerCalificaciones(pp);
			calificacionParteTeoricaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[0]);
			calificacionParteEscritaFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[1]);
			calificacionParteOralFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[2]);
			calificacionFinal = UtilsMath.newBigDecimal(calificaciones.split("-")[3]);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}

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

	public BigDecimal getCalificacionParteOralGracia() {
		return calificacionParteOralGracia;
	}

	public BigDecimal getCalificacionParteOralOdinaria() {
		return calificacionParteOralOdinaria;
	}

	public BigDecimal getCalificacionParteTeoricaFinal() {
		return calificacionParteTeoricaFinal;
	}

	public BigDecimal getCalificacionParteTeoricaGracia() {
		return calificacionParteTeoricaGracia;
	}

	public BigDecimal getCalificacionParteTeoricaOdinaria() {
		return calificacionParteTeoricaOdinaria;
	}

	public Date getFechaParteTeorica() {
		return fechaParteTeorica;
	}

	public Grupo getGrupoExamen() {
		return grupoExamen;
	}

	public List<CertificadoEstudiante> getListadoCertificados() {
		return listadoCertificados;
	}

	public List<CertificadoEstudiante> getListadoCertificadosTipoEstudiante() {
		return listadoCertificadosTipoEstudiante;
	}

	public List<EstudianteExamenComplexivoPT> getListaPT() {
		return listaPT;
	}

	public EstudianteExamenComplexivoPP getPp() {
		return pp;
	}

	public EstudianteExamenComplexivoPT getPt() {
		return pt;
	}

	public Integer getPtId() {
		return ptId;
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
		try {
			listadoCertificados = new ArrayList<CertificadoEstudiante>();
			listadoCertificadosTipoEstudiante = new ArrayList<CertificadoEstudiante>();
			// System.out.println("El id del estudiante EPP: " + pp.getId());
			if (pp.getNumeroActaCalificacion() != null) {

				listadoCertificadosTipoEstudiante = certificadoEstudianteService.obtenerLista(
						"select ce from CertificadoEstudiante ce inner join ce.certificado cer inner join cer.dependencia d inner join"
								+ " cer.carrera c inner join ce.estudianteExamenComplexivoPP epp where epp.id=?1 and "
								+ "d.id in ('ESTACRED', 'ESTATITU') order by d.nombre",
						new Object[] { pp.getId() }, 0, false, new Object[] {});

				listadoCertificados = certificadoEstudianteService.obtenerLista(
						"select ce from CertificadoEstudiante ce inner join ce.certificado cer inner join cer.dependencia d inner join"
								+ " cer.carrera c inner join ce.estudianteExamenComplexivoPP epp where epp.id=?1 and "
								+ "d.id not in ('ESTACRED', 'ESTATITU') order by d.nombre",
						new Object[] { pp.getId() }, 0, false, new Object[] {});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void obtenerFechaParteTeorica() {
		try {
			grupoExamen = grupoService.obtenerObjeto(
					"select gr from Grupo gr inner join gr.examen ex inner join ex.estudianteExamenComplexivoPT ept inner join ept.estudiante e where e.id=?1",
					new Object[] { pp.getEstudiante().getId() }, false, new Object[] {});
			fechaParteTeorica = grupoExamen.getFechaInicio();
			// System.out.println("Entro a la fecha de párte teorica: " +
			// fechaParteTeorica);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void obtenerPP() {
		if (ptId != 0)
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

	public void setCalificacionParteOralGracia(BigDecimal calificacionParteOralGracia) {
		this.calificacionParteOralGracia = calificacionParteOralGracia;
	}

	public void setCalificacionParteOralOdinaria(BigDecimal calificacionParteOralOdinaria) {
		this.calificacionParteOralOdinaria = calificacionParteOralOdinaria;
	}

	public void setCalificacionParteTeoricaFinal(BigDecimal calificacionParteTeoricaFinal) {
		this.calificacionParteTeoricaFinal = calificacionParteTeoricaFinal;
	}

	public void setCalificacionParteTeoricaGracia(BigDecimal calificacionParteTeoricaGracia) {
		this.calificacionParteTeoricaGracia = calificacionParteTeoricaGracia;
	}

	public void setCalificacionParteTeoricaOdinaria(BigDecimal calificacionParteTeoricaOdinaria) {
		this.calificacionParteTeoricaOdinaria = calificacionParteTeoricaOdinaria;
	}

	public void setFechaParteTeorica(Date fechaParteTeorica) {
		this.fechaParteTeorica = fechaParteTeorica;
	}

	public void setGrupoExamen(Grupo grupoExamen) {
		this.grupoExamen = grupoExamen;
	}

	public void setListadoCertificados(List<CertificadoEstudiante> listadoCertificados) {
		this.listadoCertificados = listadoCertificados;
	}

	public void setListadoCertificadosTipoEstudiante(List<CertificadoEstudiante> listadoCertificadosTipoEstudiante) {
		this.listadoCertificadosTipoEstudiante = listadoCertificadosTipoEstudiante;
	}

	public void setListaPT(List<EstudianteExamenComplexivoPT> listaPT) {
		this.listaPT = listaPT;
	}

	public void setPp(EstudianteExamenComplexivoPP pp) {
		this.pp = pp;
	}

	public void setPt(EstudianteExamenComplexivoPT pt) {
		this.pt = pt;
	}

	public void setPtId(Integer ptId) {
		this.ptId = ptId;
	}

}