package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.lowagie.text.DocumentException;

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;;

@Controller
@Scope("session")
public class SubirArchivoTTBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private ReporteService reporteService;

	private List<EstudianteTrabajoTitulacion> listaTT;
	private Integer ttId;
	private EstudianteTrabajoTitulacion tt;

	private UploadedFile archivo;

	private boolean sw;

	private boolean habilitarSubirArchivo;

	private boolean renderizarOpcionesSubida;

	private String archivoSubido;

	private InputStream isUrkund;
	private InputStream isCesionDerecho;
	private InputStream isCuerpo;

	private InputStream is;

	private StreamedContent streamedContent;

	private boolean correctaRutaArchivo;

	public SubirArchivoTTBean() {
	}

	@PostConstruct
	public void a() {
		listaTT = estudianteTrabajoTitulacionService.obtenerLista(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaTT != null && listaTT.size() == 1) {
			ttId = listaTT.get(0).getId();
			obtenerTT();
			sw = true;
			if (UtilsMath.newBigDecimal(estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0
					&& (tt.getValidarArchivo() == null || tt.getValidarArchivo() == false)
					&& tt.getNumeroActaCalificacion() != null) {
				habilitarSubirArchivo = true;
				// System.out.println("Entro al if, y se puso true: " +
				// habilitarSubirArchivo);
			} else {
				habilitarSubirArchivo = false;
				// System.out.println("Entro al falso, y se puso false: " +
				// habilitarSubirArchivo);
			}
			if (tt.getArchivo() != null) {
				try {
					is = new FileInputStream(tt.getArchivo());
					streamedContent = new DefaultStreamedContent(is, "application/pdf");
					correctaRutaArchivo = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					correctaRutaArchivo = false;
					e.printStackTrace();
				} catch (IOException ioE) {
					ioE.printStackTrace();
				}
			}
		} else if (listaTT != null && listaTT.size() >= 2) {
			habilitarSubirArchivo = false;
			// System.out.println("Tiene 2 procesos");
		} else {
			tt = new EstudianteTrabajoTitulacion();
			sw = false;
			habilitarSubirArchivo = false;
		}
	}

	public void actualizar() throws IOException {
		if (ttId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera y suba el archivo");
		else if (tt.getAdjuntoPDF() != null && tt.getAdjuntoPDF())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede subir el archivo por que ya fué validado");
		else {

			if (tt.getOpcionTitulacion().getId() == 4) {

				if (isCuerpo == null) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Por favor, debe subir el archivo que se le solicita");
				} else {

					// System.out.println("Ruta del archivo en actualizar: " +
					// tt.getArchivo());

					EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.email!=?2",
							new Object[] { tt.getSeminarioTrabajoTitulacion().getId(), tt.getEstudiante().getEmail() },
							false, new Object[] {});

					PDDocument pdCuerpo = new PDDocument();

					try {
						pdCuerpo = PDDocument.load(isCuerpo);

						determinarRutaArchivo();
						// pdCuerpo = PDDocument.load(obtenerCuerpoDrive());

						PDFMergerUtility pdfCombinar = new PDFMergerUtility();

						PDDocument pdDestino = new PDDocument();
						File archivoCombinado = new File(archivoSubido);

						pdfCombinar.appendDocument(pdDestino, pdCuerpo);

						pdDestino.save(archivoCombinado);

						tt.setArchivo(archivoSubido);

						// System.out.println("Ruta del archivo a guardarse: " +
						// archivoSubido);

						tt.setValidarArchivo(null);

						estudianteTrabajoTitulacionService
								.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\" = "
										+ tt.getValidarArchivo() + ", archivo = '" + tt.getArchivo() + "' WHERE id ="
										+ tt.getId());

						if (ett2 != null && ett2.getId() != null) {

							estudianteTrabajoTitulacionService
									.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\" = "
											+ tt.getValidarArchivo() + ", archivo = '" + tt.getArchivo()
											+ "' WHERE id =" + ett2.getId());
						}
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"El documento ha sido subido correctamente. Actualización exitosa");

					} catch (FileNotFoundException e) {
						e.printStackTrace();
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Archivo urkund no encontrado.");
					} catch (IOException io) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Por favor suba todos los archivos nuevamente y de forma correcta");
						io.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						pdCuerpo.close();
					}
				}

			} else {

				if (isCesionDerecho == null || isCuerpo == null) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Por favor, debe subir todos los archivos que se le solicitan");
				} else {

					// System.out.println("Ruta del archivo en actualizar: " +
					// tt.getArchivo());

					EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
							"select ett from EstudianteTrabajoTitulacion ett inner join ett.seminarioTrabajoTitulacion stt inner join ett.estudiante e where stt.id=?1 and e.email!=?2",
							new Object[] { tt.getSeminarioTrabajoTitulacion().getId(), tt.getEstudiante().getEmail() },
							false, new Object[] {});

					PDDocument pdUrkund = new PDDocument();
					PDDocument pdTurnitin = new PDDocument();
					PDDocument pdCesionDerecho = new PDDocument();
					PDDocument pdCuerpo = new PDDocument();

					try {
						// PDFTextStripper stripper = new PDFTextStripper();
						// stripper.setStartPage(1);
						// stripper.setEndPage(1);
						// String texto1 = stripper.getText(pdUrkund);
						// System.out.println("Contenido del archivo urkund:\n"
						// +
						// texto1);
						// System.out.println("Evaluar archivo urkund: " +
						// texto1.contains("Urkund Analysis Result"));
						// if (texto1.contains("Urkund Analysis Result")) {

						pdUrkund = PDDocument.load(new File(tt.getReporteUrkund()));
						pdCesionDerecho = PDDocument.load(isCesionDerecho);
						pdCuerpo = PDDocument.load(isCuerpo);

						int numeroPáginas = pdUrkund.getPages().getCount();
						PDFTextStripper stripper2 = new PDFTextStripper();

						for (int i = 0; i <= 10; i++) {
							stripper2.setStartPage(pdUrkund.getNumberOfPages() - i);
							stripper2.setEndPage(pdUrkund.getNumberOfPages() - i);
							String textoTurnitin = stripper2.getText(pdUrkund);
							// System.out.println(stripper2.getText(pdUrkund));
							// System.out.println("////////////////////////////");
							if (textoTurnitin.contains("INFORME DE ORIGINALIDAD")
									&& textoTurnitin.contains("INDICE DE SIMILITUD")
									&& textoTurnitin.contains("PUBLICACIONES")) {
								PDPage pagina = pdUrkund.getPage(pdUrkund.getNumberOfPages() - (i + 1));
								pdTurnitin.addPage(pagina);
								break;
							}
						}

						determinarRutaArchivo();
						PDDocument pdTapaPasta;
						PDDocument pdCubierta;
						PDDocument pdPortada;

						pdTapaPasta = PDDocument.load(obtenerTapaPasta());
						pdCubierta = PDDocument.load(obtenerCubierta());
						pdPortada = PDDocument.load(obtenerPortada());
						// pdCuerpo = PDDocument.load(obtenerCuerpoDrive());

						PDFMergerUtility pdfCombinar = new PDFMergerUtility();

						PDDocument pdDestino = new PDDocument();
						File archivoCombinado = new File(archivoSubido);

						pdfCombinar.appendDocument(pdDestino, pdTapaPasta);
						pdfCombinar.appendDocument(pdDestino, pdCubierta);
						pdfCombinar.appendDocument(pdDestino, pdPortada);
						// pdfCombinar.appendDocument(pdDestino, pdUrkund);
						pdfCombinar.appendDocument(pdDestino, pdTurnitin);
						pdfCombinar.appendDocument(pdDestino, pdCesionDerecho);
						pdfCombinar.appendDocument(pdDestino, pdCuerpo);

						pdDestino.save(archivoCombinado);

						tt.setArchivo(archivoSubido);

						// System.out.println("Ruta del archivo a guardarse: " +
						// archivoSubido);

						tt.setValidarArchivo(null);

						estudianteTrabajoTitulacionService
								.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\" = "
										+ tt.getValidarArchivo() + ", archivo = '" + tt.getArchivo() + "' WHERE id ="
										+ tt.getId());

						// estudianteTrabajoTitulacionService.actualizar(tt);

						if (ett2 != null && ett2.getId() != null) {
							// ett2.setArchivo(tt.getArchivo());
							// ett2.setValidarArchivo(tt.getValidarArchivo());
							// estudianteTrabajoTitulacionService.actualizar(ett2);
							estudianteTrabajoTitulacionService
									.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"validarArchivo\" = "
											+ tt.getValidarArchivo() + ", archivo = '" + tt.getArchivo()
											+ "' WHERE id =" + ett2.getId());
						}
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Todos los documentos han sido subidos correctamente. Actualización exitosa");

					} catch (FileNotFoundException e) {
						e.printStackTrace();
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Archivo urkund no encontrado.");
					} catch (IOException io) {
						UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
								"Por favor suba todos los archivos nuevamente y de forma correcta");
						io.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						pdUrkund.close();
						pdTurnitin.close();
						pdCesionDerecho.close();
						pdCuerpo.close();
					}
				}
			}
			if (tt.getArchivo() != null) {
				try {
					is = null;
					streamedContent = null;
					is = new FileInputStream(tt.getArchivo());
					streamedContent = new DefaultStreamedContent(is, "application/pdf");
					correctaRutaArchivo = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					correctaRutaArchivo = false;
					e.printStackTrace();
				}
			}
		}
	}

	public void determinarRutaArchivo() {

		if (ttId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
		else {
			EstudianteTrabajoTitulacion tt2 = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select ett from EstudianteTrabajoTitulacion ett inner join ett.estudiante e inner join ett.seminarioTrabajoTitulacion stt where stt.id=?1 and e.id!=?2",
					new Object[] { tt.getSeminarioTrabajoTitulacion().getId(), tt.getEstudiante().getId() }, false,
					new Object[] {});
			if (tt2 != null && tt2.getId() != null) {
				archivoSubido = UtilsArchivos.getRutaBibliotecaTT(tt.getProceso().getId()) + "TT_"
						+ tt.getEstudiante().getId() + "_" + tt2.getEstudiante().getId() + "_C"
						+ tt.getCarrera().getId() + ".pdf";
			} else {
				archivoSubido = UtilsArchivos.getRutaBibliotecaTT(tt.getProceso().getId()) + "TT_"
						+ tt.getEstudiante().getId() + "_C" + tt.getCarrera().getId() + ".pdf";
			}

		}
	}

	public UploadedFile getArchivo() {
		return archivo;
	}

	public String getArchivoSubido() {
		return archivoSubido;
	}

	public EstudianteTrabajoTitulacionService getEstudianteTrabajoTitulacionService() {
		return estudianteTrabajoTitulacionService;
	}

	public InputStream getIs() {
		return is;
	}

	public InputStream getIsCesionDerecho() {
		return isCesionDerecho;
	}

	public InputStream getIsCuerpo() {
		return isCuerpo;
	}

	public InputStream getIsUrkund() {
		return isUrkund;
	}

	public List<EstudianteTrabajoTitulacion> getListaTT() {
		return listaTT;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public EstudianteTrabajoTitulacion getTt() {
		return tt;
	}

	public Integer getTtId() {
		return ttId;
	}

	public boolean isCorrectaRutaArchivo() {
		return correctaRutaArchivo;
	}

	public boolean isHabilitarSubirArchivo() {
		return habilitarSubirArchivo;
	}

	public boolean isRenderizarOpcionesSubida() {
		return renderizarOpcionesSubida;
	}

	public boolean isSw() {
		return sw;
	}

	public void limpiar(ComponentSystemEvent event) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			// System.out.println("Indica retorno de valor.");
		} else {
			// System.out.println("He ingresado desde un método get.");
			a();
		}
	}

	public InputStream obtenerCubierta() throws FileNotFoundException, DocumentException {
		InputStream input;

		tt = estudianteTrabajoTitulacionService.obtenerObjeto(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e "
						+ "inner join fetch tt.carrera c inner join tt.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
				new Object[] { UtilSeguridad.obtenerUsuario(), tt.getCarrera().getId(), tt.getProceso().getId() },
				false, new Object[] {});

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("TT_Cubierta");

		report.addParameter("proceso", tt.getProceso().getId());
		report.addParameter("idEstudiante", tt.getEstudiante().getId());

		report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));

		input = UtilsReport.ejecutarReporte(report).getStream();

		return input;
	}

	public InputStream obtenerPortada() throws FileNotFoundException {
		InputStream input;

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("TT_Portada");

		report.addParameter("proceso", tt.getProceso().getId());
		report.addParameter("idEstudiante", tt.getEstudiante().getId());

		report.addParameter("fechaSustentacion",
				"Machala, " + fechaFormatoString(tt.getFechaSustentacion(), "dd 'de' MMMM 'de' yyyy"));

		report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(tt.getFechaSustentacion(), "yyyy"));

		input = UtilsReport.ejecutarReporte(report).getStream();
		return input;
	}

	public InputStream obtenerTapaPasta() throws FileNotFoundException, DocumentException {

		tt = estudianteTrabajoTitulacionService.obtenerObjeto(
				"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e "
						+ "inner join fetch tt.carrera c inner join tt.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
				new Object[] { UtilSeguridad.obtenerUsuario(), tt.getCarrera().getId(), tt.getProceso().getId() },
				false, new Object[] {});

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("TT_TapaPasta");

		report.addParameter("proceso", tt.getProceso().getId());
		report.addParameter("idEstudiante", tt.getEstudiante().getId());
		report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));

		InputStream input = UtilsReport.ejecutarReporte(report).getStream();

		return input;
	}

	public void obtenerTT() {
		if (ttId == 0) {
			sw = false;
			tt = new EstudianteTrabajoTitulacion();
			// System.out.println("Entro 1");
			habilitarSubirArchivo = false;
			renderizarOpcionesSubida = true;
		} else if (listaTT.size() == 1) {
			tt = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt where tt.id=?1", new Object[] { ttId }, false,
					new Object[] {});
			sw = true;
			habilitarSubirArchivo = true;
			if (tt.getOpcionTitulacion().getId() == 4) {
				renderizarOpcionesSubida = false;
			} else {
				renderizarOpcionesSubida = true;
			}
		} else {
			// System.out.println("Entro en el segundo else");
			// System.out.println(ttId);
			tt = estudianteTrabajoTitulacionService.obtenerObjeto(
					"select tt from EstudianteTrabajoTitulacion tt where tt.id=?1", new Object[] { ttId }, false,
					new Object[] {});
			archivoSubido = tt.getArchivo();
			sw = true;
			if (UtilsMath.newBigDecimal(estudianteTrabajoTitulacionService.traerCalificaciones(tt).split("-")[2])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0
					&& (tt.getValidarArchivo() == null || tt.getValidarArchivo() == false)
					&& tt.getNumeroActaCalificacion() != null) {
				habilitarSubirArchivo = true;
				// System.out.println("Entro al if, y se puso true: " +
				// habilitarSubirArchivo);
				// System.out.println("Lo que hay en archivo: " +
				// tt.getArchivo());
				if (tt.getOpcionTitulacion().getId() == 4) {
					renderizarOpcionesSubida = false;
				} else {
					renderizarOpcionesSubida = true;
				}
			} else {
				habilitarSubirArchivo = false;
				// System.out.println("Entro al falso, y se puso false: " +
				// habilitarSubirArchivo);
			}
			if (tt.getArchivo() != null) {
				try {
					is = new FileInputStream(tt.getArchivo());
					streamedContent = new DefaultStreamedContent(is, "application/pdf");
					correctaRutaArchivo = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					correctaRutaArchivo = false;
					e.printStackTrace();
				}
			}
		}
	}

	public void setArchivo(UploadedFile archivo) {
		this.archivo = archivo;
	}

	public void setArchivoSubido(String archivoSubido) {
		this.archivoSubido = archivoSubido;
	}

	public void setCorrectaRutaArchivo(boolean correctaRutaArchivo) {
		this.correctaRutaArchivo = correctaRutaArchivo;
	}

	public void setEstudianteTrabajoTitulacionService(
			EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService) {
		this.estudianteTrabajoTitulacionService = estudianteTrabajoTitulacionService;
	}

	public void setHabilitarSubirArchivo(boolean habilitarSubirArchivo) {
		this.habilitarSubirArchivo = habilitarSubirArchivo;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public void setIsCesionDerecho(InputStream isCesionDerecho) {
		this.isCesionDerecho = isCesionDerecho;
	}

	public void setIsCuerpo(InputStream isCuerpo) {
		this.isCuerpo = isCuerpo;
	}

	public void setIsUrkund(InputStream isUrkund) {
		this.isUrkund = isUrkund;
	}

	public void setListaTT(List<EstudianteTrabajoTitulacion> listaTT) {
		this.listaTT = listaTT;
	}

	public void setRenderizarOpcionesSubida(boolean renderizarOpcionesSubida) {
		this.renderizarOpcionesSubida = renderizarOpcionesSubida;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public void setSw(boolean sw) {
		this.sw = sw;
	}

	public void setTt(EstudianteTrabajoTitulacion tt) {
		this.tt = tt;
	}

	public void setTtId(Integer ttId) {
		this.ttId = ttId;
	}

	public void subirArchivoCesionDerecho(FileUploadEvent event) {
		try {
			if (ttId == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
			else {
				// pp.setArchivo(UtilsArchivos.getRutaBibliotecaEC() + "EC_" +
				// pp.getEstudiante().getId() + "_C"
				// + pp.getCarrera().getId()
				// + event.getFile().getFileName()
				// .substring(Utils.posicionCaracterDerIzq(event.getFile().getFileName(),
				// '.'),
				// event.getFile().getFileName().length())
				// .toLowerCase());
				isCesionDerecho = event.getFile().getInputstream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirArchivoCuerpo(FileUploadEvent event) {
		try {
			if (ttId == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
			else {

				isCuerpo = event.getFile().getInputstream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirArchivoCuerpoDocumento(FileUploadEvent event) {
		try {
			if (ttId == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
			else {
				isCuerpo = event.getFile().getInputstream();
				// System.out.println("Cargo el documento pdf.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirArchivoUrkund(FileUploadEvent event) {
		try {
			if (ttId == 0)
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
			else {

				isUrkund = event.getFile().getInputstream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public InputStream obtenerCuerpoDrive() {
	// InputStream cuerpo = null;
	// try {
	//
	// String nombreDocumento =
	// EjemploDrive.exportarDocumento(tt.getEstudiante().getId(),
	// tt.getUrlDrive(),
	// tt.getProceso().getId(), "TT");
	//
	// java.io.File f = new java.io.File(
	// UtilsArchivos.getRutaCuerpoDocumentoDriveTT(tt.getProceso().getId()) +
	// nombreDocumento);
	//
	// System.out.println((f.exists() ? "El archivo si existe." : "El archivo no
	// existe."));
	//
	// System.out.println("Nombre documento: " + nombreDocumento);
	//
	// cuerpo = new FileInputStream(f);
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
	// "Error en el servidor de tipo: " + ex.getClass());
	// }
	// return cuerpo;
	// }
}