package ec.edu.utmachala.titulacion.reporte;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entityAux.Caratula;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class ReporteEstudianteECBean {

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private ReporteService reporteService;

	private List<EstudianteExamenComplexivoPP> listaPP;
	private EstudianteExamenComplexivoPP pp;
	private Integer ppId;
	private BigDecimal calificacionFinal;

	private StreamedContent documento;
	private InputStream stream = null;

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

	public void cesionPublicacion() {
		try {
			if (ppId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
			} else if (pp.getTituloInvestigacion() == null
					|| pp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No ha generado el título del reactivo práctico");
			} else {
				Report report = new Report();
				report.setFormat("PDF");
				report.setPath(UtilsReport.serverPathReport);
				report.setName("EC_CesionPublicacion");
				report.addParameter("ID", ppId.toString());
				documento = UtilsReport.ejecutarReporte(report);
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Descargado con éxito.");
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public boolean comprobarCitasBibliograficas() {
		boolean a1 = false;
		boolean a2 = false;
		if (pp.getCitas1() != null && pp.getCitas1() >= 10)
			a1 = true;

		if (pp.getCitas2() != null && pp.getCitas2() >= 10)
			a2 = true;

		return (a1 || a2) ? true : false;
	}

	public boolean comprobarCoincidencia() {
		boolean a1 = false;
		boolean a2 = false;
		if (pp.getAntiplagio() != null && pp.getAntiplagio().compareTo(new BigDecimal("10")) <= 0)
			a1 = true;

		if (pp.getAntiplagio2() != null && pp.getAntiplagio2().compareTo(new BigDecimal("10")) <= 0)
			a2 = true;

		return (a1 || a2) ? true : false;
	}

	public void entregaRecepcion() {
		try {
			if (ppId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {

				List<Caratula> list = new ArrayList<Caratula>();
				pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
						"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
								+ "inner join fetch pp.carrera c inner join pp.proceso p where "
								+ "e.email=?1 and c.id=?2 and p.id=?3",
						new Object[] { UtilSeguridad.obtenerUsuario(), pp.getCarrera().getId(),
								pp.getProceso().getId() },
						false, new Object[] {});

				if (pp.getAprobado() == null) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"Usted no se encuentra habilitado por parte su tutor.");
				} else {

					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EC_EntregaRecepcion");

					// Caratula c = new Caratula();

					report.addParameter("unidadAcademica", pp.getCarrera().getUnidadAcademica().getNombre());
					// c.setUnidadAcademica(pp.getCarrera().getUnidadAcademica().getNombre());

					report.addParameter("gradoAcademico", pp.getProceso().getId());
					// c.setGradoAcademico(pp.getProceso().getId());

					report.addParameter("carrera", pp.getCarrera().getNombre());
					// c.setCarrera(pp.getCarrera().getNombre());

					report.addParameter("titulo", pp.getTituloInvestigacion().toUpperCase());
					// c.setTitulo(pp.getTituloInvestigacion().toUpperCase());

					report.addParameter("tutor1",
							pp.getEspecialista1().getApellidoNombre() + " (" + pp.getEspecialista1().getId() + ")");
					// c.setTutor1(pp.getEspecialista1().getApellidoNombre() + "
					// (" + pp.getEspecialista1().getId() + ")");

					report.addParameter("tutor2",
							pp.getEspecialista2().getApellidoNombre() + " (" + pp.getEspecialista2().getId() + ")");
					// c.setTutor2(pp.getEspecialista2().getApellidoNombre() + "
					// (" + pp.getEspecialista2().getId() + ")");

					report.addParameter("tutor3",
							pp.getEspecialista3().getApellidoNombre() + " (" + pp.getEspecialista3().getId() + ")");
					// c.setTutor3(pp.getEspecialista3().getApellidoNombre() + "
					// (" + pp.getEspecialista3().getId() + ")");

					report.addParameter("tutorSuplente1", pp.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ pp.getEspecialistaSuplente1().getId() + ")");
					// c.setTutorSuplente1(pp.getEspecialistaSuplente1().getApellidoNombre()
					// + " ("
					// + pp.getEspecialistaSuplente1().getId() + ")");

					report.addParameter("autor1", pp.getEstudiante().getApellidoNombre().toUpperCase() + " - ("
							+ pp.getEstudiante().getId() + ")");
					// c.setAutor1(pp.getEstudiante().getApellidoNombre().toUpperCase()
					// + " - ("
					// + pp.getEstudiante().getId() + ")");

					report.addParameter("lugarMesAnio", "MACHALA\n" + fechaFormatoString(new Date(), "yyyy"));
					// c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(new
					// Date(), "yyyy"));

					// list.add(c);
					// Map<String, Object> parametro = new HashMap<String,
					// Object>();
					// reporteService.generarReportePDF(list, parametro,
					// "EC_EntregaRecepcion",
					// "EC_EntregaRecepcion_" + pp.getEstudiante().getId());

					documento = UtilsReport.ejecutarReporte(report);
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public BigDecimal getCalificacionFinal() {
		return calificacionFinal;
	}

	public StreamedContent getDocumento() {
		return documento;
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

	public InputStream getStream() {
		return stream;
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
		if (ppId == 0) {
			pp = new EstudianteExamenComplexivoPP();
		} else if (ppId == 1) {
			pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select pp from EstudianteExamenComplexivoPP pp where pp.id=?1", new Object[] { ppId }, false,
					new Object[] {});
			calificacionFinal = new BigDecimal(
					estudiantesExamenComplexivoPPService.traerCalificaciones(pp).split("-")[3]);
		} else {
			// System.out.println("Entro en el segundo else");
			// System.out.println(ppId);
			pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select pp from EstudianteExamenComplexivoPP pp where pp.id=?1", new Object[] { ppId }, false,
					new Object[] {});
			if (UtilsMath.newBigDecimal(estudiantesExamenComplexivoPPService.traerCalificaciones(pp).split("-")[3])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0) {
				// System.out.println("Lo que hay en archivo: " +
				// pp.getArchivo());
				calificacionFinal = new BigDecimal(
						estudiantesExamenComplexivoPPService.traerCalificaciones(pp).split("-")[3]);
			} else {
				calificacionFinal = new BigDecimal(0);
			}
		}
	}

	public void paginaAceptacion() {
		try {
			if (ppId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else if (pp.getTituloInvestigacion() == null
					|| pp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No ha generado el título del reactivo práctico");
			} else {

				int cont = 0;
				if (pp.getEe1() != null)
					cont++;
				if (pp.getEe2() != null)
					cont++;
				if (pp.getEe3() != null)
					cont++;
				if (pp.getEs1() != null)
					cont++;

				if (cont >= 3) {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EC_PaginaAceptacion");
					report.addParameter("ID", ppId.toString());
					documento = UtilsReport.ejecutarReporte(report);
					cont = 0;
				} else
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"No puede descargar el reporte, por qué faltan de subir la calificación escrita.");

			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void setCalificacionFinal(BigDecimal calificacionFinal) {
		this.calificacionFinal = calificacionFinal;
	}

	public void setDocumento(StreamedContent documento) {
		this.documento = documento;
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

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public void tapaPasta() {
		try {
			if (ppId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {
				List<Caratula> list = new ArrayList<Caratula>();
				pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
						"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
								+ "inner join fetch pp.carrera c inner join pp.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
						new Object[] { UtilSeguridad.obtenerUsuario(), pp.getCarrera().getId(),
								pp.getProceso().getId() },
						false, new Object[] {});

				if (pp == null || pp.getId() == null) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "No ha seleccionado un reactivo práctico");
				} else if (pp.getTituloInvestigacion() == null
						|| pp.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR, "No ha generado el título del reactivo práctico");
				} else {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("EC_TapaPasta");

					report.addParameter("proceso", pp.getProceso().getId());
					report.addParameter("idEstudiante", pp.getEstudiante().getId());
					// c.setUnidadAcademica(pp.getCarrera().getUnidadAcademica().getNombre());

					report.addParameter("carrera", pp.getCarrera().getNombre());
					// c.setCarrera(pp.getCarrera().getNombre());

					report.addParameter("titulo", pp.getTituloInvestigacion().toUpperCase());
					// c.setTitulo(pp.getTituloInvestigacion().toUpperCase());

					report.addParameter("autor1",
							pp.getEstudiante().getApellidoNombre().toUpperCase() + "-"
									+ (pp.getEstudiante().getGenero().compareTo("F") == 0
											? pp.getCarrera().getNomenclaturaTituloFemenino()
											: pp.getCarrera().getNomenclaturaTituloMasculino()));
					// c.setAutor1(pp.getEstudiante().getApellidoNombre().toUpperCase());

					report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));
					// c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(new
					// Date(), "yyyy"));

					// list.add(c);
					// Map<String, Object> parametro = new HashMap<String,
					// Object>();
					// reporteService.generarReportePDF(list, parametro,
					// "EC_TapaPasta",
					// "EC_TapaPasta" + pp.getEstudiante().getId());
					documento = UtilsReport.ejecutarReporte(report);
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}
}