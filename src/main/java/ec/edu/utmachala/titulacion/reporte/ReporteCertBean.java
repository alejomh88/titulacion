package ec.edu.utmachala.titulacion.reporte;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.Carrera;
import ec.edu.utmachala.titulacion.entity.Proceso;
import ec.edu.utmachala.titulacion.service.CarreraService;
import ec.edu.utmachala.titulacion.service.ProcesoService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class ReporteCertBean {

	@Autowired
	private ProcesoService procesoService;

	@Autowired
	private CarreraService carreraService;

	private String formatoReporte;

	private List<Proceso> procesos;
	private String proceso;

	private List<Carrera> carreras;
	private Integer carrera;

	private String actaGraduacion;

	private StreamedContent documento;

	@PostConstruct
	public void a() {
		carrera = 0;
		procesos = procesoService.obtenerLista("select p from Proceso p order by p.fechaInicio", new Object[] {}, 0,
				false, new Object[] {});
	}

	public void cargarDatosReporte() {

	}

	public String getActaGraduacion() {
		return actaGraduacion;
	}

	public Integer getCarrera() {
		return carrera;
	}

	public List<Carrera> getCarreras() {
		return carreras;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public String getFormatoReporte() {
		return formatoReporte;
	}

	public String getProceso() {
		return proceso;
	}

	public List<Proceso> getProcesos() {
		return procesos;
	}

	public void obtenerCarreras() {
		carrera = 0;
		carreras = carreraService.obtenerPorSql("select distinct c.* from \"estudiantesTrabajosTitulacion\" eet "
				+ "inner join \"permisosCarreras\" pc on (pc.carrera=eet.carrera) inner join usuarios u on (u.id=pc.usuario) "
				+ " inner join carreras c on (c.id=pc.carrera) where proceso='" + proceso + "' and u.email='"
				+ UtilSeguridad.obtenerUsuario() + "' "
				+ "union select distinct c.* from \"estudiantesExamenComplexivoPP\" eet "
				+ "inner join \"permisosCarreras\" pc on (pc.carrera=eet.carrera) inner join usuarios u on (u.id=pc.usuario) "
				+ "inner join carreras c on (c.id=pc.carrera) where proceso='" + proceso + "' and u.email='"
				+ UtilSeguridad.obtenerUsuario() + "' " + "order by id", Carrera.class);
	}

	public void reporteExamenComplexivo() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {

			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_DependenciaNoAdeudar");
				report.addParameter("proceso", proceso);
				report.addParameter("usuario", UtilSeguridad.obtenerUsuario());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_DependenciaNoAdeudarCarrera");
				report.addParameter("proceso", proceso);
				report.addParameter("usuario", UtilSeguridad.obtenerUsuario());
				report.addParameter("carrera", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reporteTrabajoTitulacion() {
		if (formatoReporte == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el formato");
		} else if (proceso == null) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione el proceso");
		} else if (carrera == 0) {

			try {
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_DependenciaNoAdeudar");
				report.addParameter("proceso", proceso);
				report.addParameter("usuario", UtilSeguridad.obtenerUsuario());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			try {
				System.out.println("Ingreso a carreras, eligiendo la " + carrera);
				Report report = new Report();
				report.setFormat(formatoReporte);
				report.setPath(UtilsReport.serverPathReport);
				report.setName("TT_DependenciaNoAdeudarCarrera");
				report.addParameter("proceso", proceso);
				report.addParameter("usuario", UtilSeguridad.obtenerUsuario());
				report.addParameter("carrera", carrera.toString());

				documento = UtilsReport.ejecutarReporte(report);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setActaGraduacion(String actaGraduacion) {
		this.actaGraduacion = actaGraduacion;
	}

	public void setCarrera(Integer carrera) {
		this.carrera = carrera;
	}

	public void setCarreras(List<Carrera> carreras) {
		this.carreras = carreras;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
	}

	public void setFormatoReporte(String formatoReporte) {
		this.formatoReporte = formatoReporte;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public void setProcesos(List<Proceso> procesos) {
		this.procesos = procesos;
	}

}
