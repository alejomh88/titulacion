package ec.edu.utmachala.titulacion.reporte;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;
import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entityAux.Caratula;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;

@Controller
@Scope("session")
public class ReporteEstudianteTTBean {

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private ReporteService reporteService;

	private List<EstudianteTrabajoTitulacion> listaTT;
	private EstudianteTrabajoTitulacion tt;
	private Integer ttId;
	private String proceso;

	private StreamedContent documento;
	private InputStream stream = null;
	private FileInputStream fileInputStream;

	private BigDecimal calificacionFinal;

	@PostConstruct
	public void a() {
		listaTT = estudianteTrabajoTitulacionService.obtenerLista(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaTT != null && listaTT.size() == 1) {
			ttId = listaTT.get(0).getId();
			obtenerTT();
			calificacionFinal = new BigDecimal(
					estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2]);
		} else
			tt = new EstudianteTrabajoTitulacion();
	}

	public boolean comprobarCitasBibliograficas() {
		boolean a1 = false;
		boolean a2 = false;
		if (tt.getCitas1() != null && tt.getCitas1() >= 25)
			a1 = true;

		if (tt.getCitas2() != null && tt.getCitas2() >= 25)
			a2 = true;

		return (a1 || a2) ? true : false;
	}

	public boolean comprobarCoincidencia() {
		boolean a1 = false;
		boolean a2 = false;
		if (tt.getAntiplagio() != null && tt.getAntiplagio().compareTo(new BigDecimal("10")) <= 0)
			a1 = true;

		if (tt.getAntiplagio2() != null && tt.getAntiplagio2().compareTo(new BigDecimal("10")) <= 0)
			a2 = true;

		return (a1 || a2) ? true : false;
	}

	public void contraportada() {
		try {
			if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
			} else {
				List<Caratula> list = new ArrayList<Caratula>();
				Caratula c = new Caratula();
				list.add(c);

				Map<String, Object> parametro = new HashMap<String, Object>();
				reporteService.generarReportePDF(list, parametro, "ContraPortada", "ContraPortada");
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void cubierta() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {
				if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
				} else {
					List<Caratula> list = new ArrayList<Caratula>();

					if (tt == null || tt.getSeminarioTrabajoTitulacion() == null) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
					} else if (tt.getTituloInvestigacion() == null
							|| tt.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
						presentaMensaje(FacesMessage.SEVERITY_ERROR,
								"No ha generado el título a partir del tema de investigación");
					} else {
						Caratula c = new Caratula();
						c.setUnidadAcademica(tt.getCarrera().getUnidadAcademica().getNombre());
						c.setCarrera(tt.getCarrera().getNombre());
						c.setTitulo(tt.getTituloInvestigacion().toUpperCase());

						EstudianteTrabajoTitulacion e2 = estudianteTrabajoTitulacionService.obtenerObjeto(
								"select ett from EstudianteTrabajoTitulacion ett "
										+ "inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt "
										+ "where e.id!=?1 and stt.id=?2",
								new Object[] { tt.getEstudiante().getId(), tt.getSeminarioTrabajoTitulacion().getId() },
								false, new Object[] {});

						String nombreArchivo = null;
						if (e2 == null) {
							c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase());
							nombreArchivo = tt.getEstudiante().getId();
						} else {
							if (tt.getEstudiante().getApellidoNombre()
									.compareTo(e2.getEstudiante().getApellidoNombre()) < 0) {
								c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ e2.getEstudiante().getApellidoNombre().toUpperCase());
								nombreArchivo = tt.getEstudiante().getId() + "_" + e2.getEstudiante().getId();
							} else {
								c.setAutor1(e2.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ tt.getEstudiante().getApellidoNombre().toUpperCase());
								nombreArchivo = e2.getEstudiante().getId() + "_" + tt.getEstudiante().getId();
							}
						}

						c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(new Date(), "yyyy"));

						list.add(c);
						Map<String, Object> parametro = new HashMap<String, Object>();
						reporteService.generarReportePDF(list, parametro, "TT_Cubierta",
								"TT_Cubierta_" + nombreArchivo);
					}
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void entregaRecepcion() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {

				List<Caratula> list = new ArrayList<Caratula>();

				if (tt.getAprobado() == null) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"Usted no se encuentra habilitado por parte de su tutor");
				} else {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("TT_EntregaRecepcion");

					// Caratula c = new Caratula();
					report.addParameter("unidadAcademica", tt.getCarrera().getUnidadAcademica().getNombre());
					// c.setUnidadAcademica(tt.getCarrera().getUnidadAcademica().getNombre());

					report.addParameter("gradoAcademico", tt.getProceso().getId());
					// c.setGradoAcademico(tt.getProceso().getId());

					report.addParameter("carrera", tt.getCarrera().getNombre());
					// c.setCarrera(tt.getCarrera().getNombre());

					report.addParameter("titulo", tt.getTituloInvestigacion().toUpperCase());
					// c.setTitulo(tt.getTituloInvestigacion().toUpperCase());

					report.addParameter("tutor1",
							tt.getEspecialista1().getApellidoNombre() + " (" + tt.getEspecialista1().getId() + ")");
					// c.setTutor1(tt.getEspecialista1().getApellidoNombre() + "
					// (" + tt.getEspecialista1().getId() + ")");

					report.addParameter("tutor2",
							tt.getEspecialista2().getApellidoNombre() + " (" + tt.getEspecialista2().getId() + ")");
					// c.setTutor2(tt.getEspecialista2().getApellidoNombre() + "
					// (" + tt.getEspecialista2().getId() + ")");

					report.addParameter("tutor3",
							tt.getEspecialista3().getApellidoNombre() + " (" + tt.getEspecialista3().getId() + ")");
					// c.setTutor3(tt.getEspecialista3().getApellidoNombre() + "
					// (" + tt.getEspecialista3().getId() + ")");

					report.addParameter("tutorSuplente1", tt.getEspecialistaSuplente1().getApellidoNombre() + " ("
							+ tt.getEspecialistaSuplente1().getId() + ")");
					// c.setTutorSuplente1(tt.getEspecialistaSuplente1().getApellidoNombre()
					// + " ("
					// + tt.getEspecialistaSuplente1().getId() + ")");

					EstudianteTrabajoTitulacion e2 = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett "
									+ "inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt "
									+ "where e.id!=?1 and stt.id=?2",
							new Object[] { tt.getEstudiante().getId(), tt.getSeminarioTrabajoTitulacion().getId() },
							false, new Object[] {});

					if (e2 == null) {
						report.addParameter("autor1", tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
								+ tt.getEstudiante().getId());
						// c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase()
						// + "-"
						// + tt.getEstudiante().getId());

						report.addParameter("autor2", "1");
						// c.setAutor2("1");
					} else {
						if (tt.getEstudiante().getApellidoNombre()
								.compareTo(e2.getEstudiante().getApellidoNombre()) < 0) {
							report.addParameter("autor1",
									tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ tt.getEstudiante().getId() + "-"
											+ e2.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ e2.getEstudiante().getId());
							// c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase()
							// + "-"
							// + tt.getEstudiante().getId() + "-"
							// +
							// e2.getEstudiante().getApellidoNombre().toUpperCase()
							// + "-"
							// + e2.getEstudiante().getId());
						} else {
							report.addParameter("autor1",
									e2.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ e2.getEstudiante().getId() + "-"
											+ tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ tt.getEstudiante().getId());
							// c.setAutor1(e2.getEstudiante().getApellidoNombre().toUpperCase()
							// + "-"
							// + e2.getEstudiante().getId() + "-"
							// +
							// tt.getEstudiante().getApellidoNombre().toUpperCase()
							// + "-"
							// + tt.getEstudiante().getId());
						}
						report.addParameter("autor2", "2");
						// c.setAutor2("2");
					}
					report.addParameter("lugarMesAnio", "MACHALA\n" + fechaFormatoString(new Date(), "yyyy"));
					// c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(new
					// Date(), "yyyy"));

					// list.add(c);
					// Map<String, Object> parametro = new HashMap<String,
					// Object>();
					// reporteService.generarReportePDF(list, parametro,
					// "TT_EntregaRecepcion",
					// "TT_EntregaRecepcion_" + tt.getEstudiante().getId());

					documento = UtilsReport.ejecutarReporte(report);
					// System.out.println("LLegó después ejecutar reporte");
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Descargado con éxito.");
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
			e.printStackTrace();
		}
	}

	public BigDecimal getCalificacionFinal() {
		return calificacionFinal;
	}

	public StreamedContent getDocumento() {
		return documento;
	}

	public List<EstudianteTrabajoTitulacion> getListaTT() {
		return listaTT;
	}

	public InputStream getStream() {
		return stream;
	}

	public EstudianteTrabajoTitulacion getTt() {
		return tt;
	}

	public Integer getTtId() {
		return ttId;
	}

	public void licenciaPublicar() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else if (tt.getFechaSustentacion() == null) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No puede descargar el reporte por qué no tiene fecha de sustentación asignada.");
			} else if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
			} else if (tt.getTituloInvestigacion() == null
					|| tt.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "No ha generado el título del trabajo de titulación.");
			} else {

				EstudianteTrabajoTitulacion ett2 = new EstudianteTrabajoTitulacion();
				ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
						"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.id!=?2",
						new Object[] { tt.getSeminarioTrabajoTitulacion().getId(), tt.getEstudiante().getId() }, false,
						new Object[0]);

				if (ett2 != null && ett2.getId() != null) {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("TT_CesionPublicacion2");
					report.addParameter("ID1", ttId.toString());
					report.addParameter("ID2", ett2.getId().toString());
					documento = UtilsReport.ejecutarReporte(report);
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Descargado con éxito.");
				} else {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("TT_CesionPublicacion");
					report.addParameter("ID", ttId.toString());
					documento = UtilsReport.ejecutarReporte(report);
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Descargado con éxito.");
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
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
		if (ttId == 0) {
			tt = new EstudianteTrabajoTitulacion();
		} else if (ttId == 1) {
			tt = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt inner join fetch tt.carrera c where tt.id=?1",
					new Object[] { ttId }, false, new Object[] {});
			proceso = tt.getProceso().getId();
			calificacionFinal = new BigDecimal(
					estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2]);
		} else {
			// System.out.println("Entro en el segundo else");
			// System.out.println(ttId);
			tt = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt inner join fetch tt.carrera c where tt.id=?1",
					new Object[] { ttId }, false, new Object[] {});
			proceso = tt.getProceso().getId();
			calificacionFinal = new BigDecimal(
					estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2]);
			if (UtilsMath.newBigDecimal(estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0) {
				calificacionFinal = new BigDecimal(
						estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2]);
			}
		}
	}

	public void paginaAceptacion() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			}

			else if (tt == null || tt.getSeminarioTrabajoTitulacion() == null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
			} else if (tt.getTituloInvestigacion() == null
					|| tt.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No ha generado el título a partir del tema de investigación");
			} else {

				int cont = 0;
				if (tt.getEe1() != null)
					cont++;
				if (tt.getEe2() != null)
					cont++;
				if (tt.getEe3() != null)
					cont++;
				if (tt.getEs1() != null)
					cont++;

				if (cont >= 3) {
					// System.out.println("ID: " + ttId);
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("TT_PaginaAceptacion");
					report.addParameter("ID", ttId.toString());
					documento = UtilsReport.ejecutarReporte(report);
				} else
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"No puede descargar el reporte, por qué faltan de subir la calificación escrita.");

			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void porcentajeCoincidencia() {
		try {
			if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR,
						"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
			} else {
				if (tt.getReporteUrkund() != null) {
					try {
						fileInputStream = new FileInputStream(tt.getReporteUrkund());
						if (fileInputStream.read() == -1)
							presentaMensaje(FacesMessage.SEVERITY_ERROR,
									"El archivo cargado por el evaluador se encuentra dañado");
						else {
							stream = new FileInputStream(tt.getReporteUrkund());
							documento = new DefaultStreamedContent(stream, "application/pdf",
									"reporteUrkund_" + tt.getEstudiante().getId() + ".pdf");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"El reporte no ha sido cargado a la plataforma por el evaluador");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		}
	}

	public void portada() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {
				if (calificacionFinal.compareTo(new BigDecimal("69.5")) == -1) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"No puede descargar el reporte por qué no alcanza el puntaje final sobre 70 puntos.");
				} else {
					List<Caratula> list = new ArrayList<Caratula>();

					if (tt == null || tt.getSeminarioTrabajoTitulacion() == null) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
					} else if (tt.getTituloInvestigacion() == null
							|| tt.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
						presentaMensaje(FacesMessage.SEVERITY_ERROR,
								"No ha generado el título a partir del tema de investigación");
					} else if (tt.getFechaSustentacion() == null) {
						presentaMensaje(FacesMessage.SEVERITY_ERROR, "No tiene asignada una fecha de sustentación");
					} else {
						Caratula c = new Caratula();
						c.setUnidadAcademica(tt.getCarrera().getUnidadAcademica().getNombre());
						c.setCarrera(tt.getCarrera().getNombre());
						c.setTutor3("TRABAJO DE TITULACIÓN\n" + tt.getOpcionTitulacion().getNombre());
						c.setTitulo(tt.getTituloInvestigacion().toUpperCase());

						EstudianteTrabajoTitulacion e2 = estudianteTrabajoTitulacionService.obtenerObjeto(
								"select ett from EstudianteTrabajoTitulacion ett "
										+ "inner join fetch ett.carrera c inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt "
										+ "where e.id!=?1 and stt.id=?2",
								new Object[] { tt.getEstudiante().getId(), tt.getSeminarioTrabajoTitulacion().getId() },
								false, new Object[] {});
						String nombreArchivo = null;

						if (e2 == null) {
							c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
									+ (tt.getEstudiante().getGenero().compareTo("F") == 0
											? tt.getCarrera().getNomenclaturaTituloFemenino()
											: tt.getCarrera().getNomenclaturaTituloMasculino()));
							nombreArchivo = tt.getEstudiante().getId();
						} else {
							if (tt.getEstudiante().getApellidoNombre()
									.compareTo(e2.getEstudiante().getApellidoNombre()) < 0) {
								c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ (tt.getEstudiante().getGenero().compareTo("F") == 0
												? tt.getCarrera().getNomenclaturaTituloFemenino()
												: tt.getCarrera().getNomenclaturaTituloMasculino())
										+ "\n\n" + e2.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ (e2.getEstudiante().getGenero().compareTo("F") == 0
												? e2.getCarrera().getNomenclaturaTituloFemenino()
												: e2.getCarrera().getNomenclaturaTituloMasculino()));
								nombreArchivo = tt.getEstudiante().getId() + "_" + e2.getEstudiante().getId();
							} else {
								c.setAutor1(e2.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ (e2.getEstudiante().getGenero().compareTo("F") == 0
												? e2.getCarrera().getNomenclaturaTituloFemenino()
												: e2.getCarrera().getNomenclaturaTituloMasculino())
										+ "\n\n" + tt.getEstudiante().getApellidoNombre().toUpperCase() + "\n"
										+ (tt.getEstudiante().getGenero().compareTo("F") == 0
												? tt.getCarrera().getNomenclaturaTituloFemenino()
												: tt.getCarrera().getNomenclaturaTituloMasculino()));
								nombreArchivo = e2.getEstudiante().getId() + "_" + tt.getEstudiante().getId();
							}
						}

						c.setTutor1(tt.getSeminarioTrabajoTitulacion().getDocenteSeminario().getDocente()
								.getApellidoNombre());

						c.setFechaSustentacion(
								"Machala, " + fechaFormatoString(tt.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));
						c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(tt.getFechaSustentacion(), "yyyy"));

						list.add(c);
						Map<String, Object> parametro = new HashMap<String, Object>();
						reporteService.generarReportePDF(list, parametro, "TT_Portada", "TT_Portada" + nombreArchivo);
					}
				}
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

	public void setListaTT(List<EstudianteTrabajoTitulacion> listaTT) {
		this.listaTT = listaTT;
	}

	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	public void setTt(EstudianteTrabajoTitulacion tt) {
		this.tt = tt;
	}

	public void setTtId(Integer ttId) {
		this.ttId = ttId;
	}

	public void tapaPasta() {
		try {
			if (ttId == 0) {
				presentaMensaje(FacesMessage.SEVERITY_ERROR, "Seleccione una carrera");
			} else {
				List<Caratula> list = new ArrayList<Caratula>();
				if (tt == null || tt.getSeminarioTrabajoTitulacion() == null) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Usted no tiene asignado un tema de investigación, por favor acuda con su tutor");
				} else if (tt.getTituloInvestigacion() == null
						|| tt.getTituloInvestigacion().compareToIgnoreCase("") == 0) {
					presentaMensaje(FacesMessage.SEVERITY_ERROR,
							"No ha generado el título a partir del tema de investigación");
				} else {
					Report report = new Report();
					report.setFormat("PDF");
					report.setPath(UtilsReport.serverPathReport);
					report.setName("TT_TapaPasta");

					// Caratula c = new Caratula();

					report.addParameter("proceso", proceso);
					report.addParameter("idEstudiante", tt.getEstudiante().getId());

					report.addParameter("unidadAcademica", tt.getCarrera().getUnidadAcademica().getNombre());
					// c.setUnidadAcademica(tt.getCarrera().getUnidadAcademica().getNombre());

					report.addParameter("carrera", tt.getCarrera().getNombre());
					// c.setCarrera(tt.getCarrera().getNombre());

					report.addParameter("titulo", tt.getTituloInvestigacion().toUpperCase());
					// c.setTitulo(tt.getTituloInvestigacion().toUpperCase());

					EstudianteTrabajoTitulacion e2 = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett "
									+ "inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt "
									+ "inner join fetch ett.carrera c where e.id!=?1 and stt.id=?2",
							new Object[] { tt.getEstudiante().getId(), tt.getSeminarioTrabajoTitulacion().getId() },
							false, new Object[] {});
					String nombreArchivo = null;

					if (e2 == null) {
						report.addParameter("autor1",
								tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
										+ (tt.getEstudiante().getGenero().compareTo("F") == 0
												? tt.getCarrera().getNomenclaturaTituloFemenino()
												: tt.getCarrera().getNomenclaturaTituloMasculino()));
						// c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase()
						// + "\n"
						// + (tt.getEstudiante().getGenero().compareTo("F") == 0
						// ? tt.getCarrera().getNomenclaturaTituloFemenino()
						// : tt.getCarrera().getNomenclaturaTituloMasculino()));
						nombreArchivo = tt.getEstudiante().getId();
					} else {
						if (tt.getEstudiante().getApellidoNombre()
								.compareTo(e2.getEstudiante().getApellidoNombre()) < 0) {
							report.addParameter("autor1",
									tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ (tt.getEstudiante().getGenero().compareTo("F") == 0
													? tt.getCarrera().getNomenclaturaTituloFemenino()
													: tt.getCarrera().getNomenclaturaTituloMasculino())
											+ "--" + e2.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ (e2.getEstudiante().getGenero().compareTo("F") == 0
													? e2.getCarrera().getNomenclaturaTituloFemenino()
													: e2.getCarrera().getNomenclaturaTituloMasculino()));
							// c.setAutor1(tt.getEstudiante().getApellidoNombre().toUpperCase()
							// + "\n"
							// + (tt.getEstudiante().getGenero().compareTo("F")
							// == 0
							// ? tt.getCarrera().getNomenclaturaTituloFemenino()
							// :
							// tt.getCarrera().getNomenclaturaTituloMasculino())
							// + "\n\n" +
							// e2.getEstudiante().getApellidoNombre().toUpperCase()
							// + "\n"
							// + (e2.getEstudiante().getGenero().compareTo("F")
							// == 0
							// ? e2.getCarrera().getNomenclaturaTituloFemenino()
							// :
							// e2.getCarrera().getNomenclaturaTituloMasculino()));
							nombreArchivo = tt.getEstudiante().getId() + "_" + e2.getEstudiante().getId();
						} else {
							report.addParameter("autor1",
									e2.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ (e2.getEstudiante().getGenero().compareTo("F") == 0
													? e2.getCarrera().getNomenclaturaTituloFemenino()
													: e2.getCarrera().getNomenclaturaTituloMasculino())
											+ "--" + tt.getEstudiante().getApellidoNombre().toUpperCase() + "-"
											+ (tt.getEstudiante().getGenero().compareTo("F") == 0
													? tt.getCarrera().getNomenclaturaTituloFemenino()
													: tt.getCarrera().getNomenclaturaTituloMasculino()));
							// c.setAutor1(e2.getEstudiante().getApellidoNombre().toUpperCase()
							// + "\n"
							// + (e2.getEstudiante().getGenero().compareTo("F")
							// == 0
							// ? e2.getCarrera().getNomenclaturaTituloFemenino()
							// :
							// e2.getCarrera().getNomenclaturaTituloMasculino())
							// + "\n\n" +
							// tt.getEstudiante().getApellidoNombre().toUpperCase()
							// + "\n"
							// + (tt.getEstudiante().getGenero().compareTo("F")
							// == 0
							// ? tt.getCarrera().getNomenclaturaTituloFemenino()
							// :
							// tt.getCarrera().getNomenclaturaTituloMasculino()));
							nombreArchivo = e2.getEstudiante().getId() + "_" + tt.getEstudiante().getId();
						}
					}
					report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));
					// c.setLugarMesAnio("MACHALA\n" + fechaFormatoString(new
					// Date(), "yyyy"));

					// list.add(c);
					// Map<String, Object> parametro = new HashMap<String,
					// Object>();
					// reporteService.generarReportePDF(list, parametro,
					// "TT_TapaPasta", "TT_TapaPasta_" + nombreArchivo);
					documento = UtilsReport.ejecutarReporte(report);
				}
			}
		} catch (Exception e) {
			presentaMensaje(FacesMessage.SEVERITY_ERROR, "Error en el servidor de tipo: " + e.getClass());
		}
	}

}
