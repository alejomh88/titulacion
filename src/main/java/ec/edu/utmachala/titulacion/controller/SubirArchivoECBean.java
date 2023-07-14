package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsDate.fechaFormatoString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.lowagie.text.DocumentException;

import ec.edu.utmachala.titulacion.drive.EjemploDrive;
import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.utility.PdfManager;
import ec.edu.utmachala.titulacion.utility.Report;
import ec.edu.utmachala.titulacion.utility.ReporteService;
import ec.edu.utmachala.titulacion.utility.UtilSeguridad;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsMath;
import ec.edu.utmachala.titulacion.utility.UtilsReport;;

@Controller
@Scope("session")
public class SubirArchivoECBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	private List<EstudianteExamenComplexivoPP> listaPP;
	private EstudianteExamenComplexivoPP pp;
	private Integer ppId;

	private UploadedFile archivo;
	private InputStream is;
	private InputStream isUrkund;
	private InputStream isCesionDerecho;
	private InputStream isCuerpo;

	private boolean sw;

	private boolean habilitarSubirArchivo;
	private String archivoSubido;

	private PdfManager pdfManager;

	@Autowired
	private ReporteService reporteService;

	private InputStream issc;

	private StreamedContent streamedContent;

	private boolean correctaRutaArchivo;

	public SubirArchivoECBean() {
	}

	@PostConstruct
	public void a() {
		listaPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e where e.email=?1",
				new Object[] { UtilSeguridad.obtenerUsuario() }, 0, false, new Object[] {});

		if (listaPP != null && listaPP.size() == 1) {
			ppId = listaPP.get(0).getId();
			obtenerPP();
			archivoSubido = pp.getArchivo();
			sw = true;
			if (UtilsMath.newBigDecimal(estudiantesExamenComplexivoPPService.traerCalificaciones(pp).split("-")[3])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0
					&& (pp.getValidarArchivo() == null || pp.getValidarArchivo() == false)
					&& pp.getNumeroActaCalificacion() != null) {
				habilitarSubirArchivo = true;
				// System.out.println("Entro al if, y se puso true: " +
				// habilitarSubirArchivo);
				// System.out.println("Lo que hay en archivo: " +
				// pp.getArchivo());

			} else {
				habilitarSubirArchivo = false;
				// System.out.println("Entro al falso, y se puso false: " +
				// habilitarSubirArchivo);
			}
			if (pp.getArchivo() != null) {
				try {
					issc = new FileInputStream(pp.getArchivo());
					streamedContent = new DefaultStreamedContent(issc, "application/pdf");
					correctaRutaArchivo = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					correctaRutaArchivo = false;
					e.printStackTrace();
				}
			}
		} else if (listaPP != null && listaPP.size() >= 2) {
			habilitarSubirArchivo = false;
			// System.out.println("Tiene 2 procesos");
		} else {
			pp = new EstudianteExamenComplexivoPP();
			sw = false;
		}
	}

	public void actualizar() throws IOException {
		if (ppId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera y suba el archivo");
		else if (pp.getAdjuntoPDF() != null && pp.getAdjuntoPDF())
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede subir el archivo por que ya fué validado");
		else {

			if (isCesionDerecho == null || isCuerpo == null) {
				UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
						"Por favor, debe subir todos los archivos que se le solicitan");
			} else {

				PDDocument pdUrkund;
				PDDocument pdCesionDerecho;
				PDDocument pdCuerpo;

				pdUrkund = new PDDocument();
				pdCesionDerecho = new PDDocument();
				pdCuerpo = new PDDocument();

				try {
					// PDFTextStripper stripper = new PDFTextStripper();
					//
					// stripper.setStartPage(1);
					// stripper.setEndPage(1);
					// String texto1 = stripper.getText(pdUrkund);
					// System.out.println("Contenido del archivo urkund:\n" +
					// texto1);
					// System.out.println("Evaluar archivo urkund: " +
					// texto1.contains("Urkund Analysis Result"));
					// if (texto1.contains("Urkund Analysis Result")) {

					pdUrkund = PDDocument.load(new File(pp.getReporteUrkund()));
					pdCesionDerecho = PDDocument.load(isCesionDerecho);
					pdCuerpo = PDDocument.load(isCuerpo);

					int numeroPáginas = pdUrkund.getPages().getCount();

					if (numeroPáginas != 1) {
						for (int i = numeroPáginas; i > 1; i--) {
							pdUrkund.removePage(i - 1);
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
					pdfCombinar.appendDocument(pdDestino, pdUrkund);
					pdfCombinar.appendDocument(pdDestino, pdCesionDerecho);
					pdfCombinar.appendDocument(pdDestino, pdCuerpo);

					pdDestino.save(archivoCombinado);

					pp.setArchivo(archivoSubido);

					pp.setValidarArchivo(null);

					// System.out.println("Ruta del archivo a guardarse: " +
					// archivoSubido);

					estudiantesExamenComplexivoPPService
							.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"validarArchivo\" = "
									+ pp.getValidarArchivo() + ", archivo = '" + pp.getArchivo() + "' WHERE id ="
									+ pp.getId());

					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Todos los documentos han sido subidos correctamente. Actualización exitosa");

				} catch (IOException io) {
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Por favor suba todos los archivos nuevamente y de forma correcta");
					UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
							"Procure que los anexos que tenga en su documento de google drive consten con descripción en la parte inferior del mismo.");
					io.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pdUrkund.close();
					pdCesionDerecho.close();
					pdCuerpo.close();
				}
			}
		}
		if (pp.getArchivo() != null) {
			try {
				issc = null;
				streamedContent = null;
				issc = new FileInputStream(pp.getArchivo());
				streamedContent = new DefaultStreamedContent(is, "application/pdf");
				correctaRutaArchivo = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				correctaRutaArchivo = false;
				e.printStackTrace();
			}
		}
	}

	public void determinarRutaArchivo() {

		if (ppId == 0)
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO, "Seleccione una carrera");
		else {
			archivoSubido = UtilsArchivos.getRutaBibliotecaEC(pp.getProceso().getId()) + "EC_"
					+ pp.getEstudiante().getId() + "_C" + pp.getCarrera().getId() + ".pdf";
		}
	}

	public UploadedFile getArchivo() {
		return archivo;
	}

	public String getArchivoSubido() {
		return archivoSubido;
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

	public InputStream getIssc() {
		return issc;
	}

	public InputStream getIsUrkund() {
		return isUrkund;
	}

	public List<EstudianteExamenComplexivoPP> getListaPP() {
		return listaPP;
	}

	public PdfManager getPdfManager() {
		return pdfManager;
	}

	public EstudianteExamenComplexivoPP getPp() {
		return pp;
	}

	public Integer getPpId() {
		return ppId;
	}

	public StreamedContent getStreamedContent() {
		return streamedContent;
	}

	public boolean isCorrectaRutaArchivo() {
		return correctaRutaArchivo;
	}

	public boolean isHabilitarSubirArchivo() {
		return habilitarSubirArchivo;
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

		pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
						+ "inner join fetch pp.carrera c inner join pp.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
				new Object[] { UtilSeguridad.obtenerUsuario(), pp.getCarrera().getId(), pp.getProceso().getId() },
				false, new Object[] {});

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("EC_Cubierta");

		report.addParameter("proceso", pp.getProceso().getId());
		report.addParameter("idEstudiante", pp.getEstudiante().getId());
		report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));

		input = UtilsReport.ejecutarReporte(report).getStream();

		return input;
	}

	public InputStream obtenerCuerpoDrive() {
		InputStream cuerpo = null;
		try {
			ByteArrayOutputStream baout = (ByteArrayOutputStream) EjemploDrive.obtenerOutputDocumento(pp.getUrlDrive());
			ByteArrayInputStream bainput = new ByteArrayInputStream(baout.toByteArray());
			cuerpo = (InputStream) bainput;
		} catch (Exception ex) {
			ex.printStackTrace();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"Error en el servidor de tipo: " + ex.getClass());
		}
		return cuerpo;
	}

	public InputStream obtenerPortada() throws FileNotFoundException {
		InputStream input;

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("EC_Portada");

		report.addParameter("proceso", pp.getProceso().getId());
		report.addParameter("idEstudiante", pp.getEstudiante().getId());

		report.addParameter("fechaSustentacion",
				"Machala, " + fechaFormatoString(
						(pp.getFechaSustentacionGracia() == null ? pp.getFechaSustentacionOrdinaria()
								: pp.getFechaSustentacionGracia()),
						"dd 'de' MMMM 'de' yyyy"));

		report.addParameter("lugarMesAnio",
				"MACHALA-" + fechaFormatoString(
						(pp.getFechaSustentacionGracia() == null ? pp.getFechaSustentacionOrdinaria()
								: pp.getFechaSustentacionGracia()),
						"dd 'de' MMMM 'de' yyyy"));

		input = UtilsReport.ejecutarReporte(report).getStream();
		return input;
	}

	public void obtenerPP() {
		if (ppId == 0) {
			sw = false;
			pp = new EstudianteExamenComplexivoPP();
			// System.out.println("Entro 1");
		} else if (listaPP.size() == 1) {
			pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select pp from EstudianteExamenComplexivoPP pp where pp.id=?1", new Object[] { ppId }, false,
					new Object[] {});
			sw = true;
		} else {
			// SYSTEM.OUT.PRINTLN("ENTRO EN EL SEGUNDO ELSE");
			// System.out.println(ppId);
			pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
					"select pp from EstudianteExamenComplexivoPP pp where pp.id=?1", new Object[] { ppId }, false,
					new Object[] {});
			archivoSubido = pp.getArchivo();
			sw = true;
			if (UtilsMath.newBigDecimal(estudiantesExamenComplexivoPPService.traerCalificaciones(pp).split("-")[3])
					.compareTo(UtilsMath.newBigDecimal("69.5")) >= 0
					&& (pp.getValidarArchivo() == null || pp.getValidarArchivo() == false)
					&& pp.getNumeroActaCalificacion() != null) {
				habilitarSubirArchivo = true;
				// System.out.println("Entro al if, y se puso true: " +
				// habilitarSubirArchivo);
				// System.out.println("Lo que hay en archivo: " +
				// pp.getArchivo());

			} else {
				habilitarSubirArchivo = false;
				// System.out.println("Entro al falso, y se puso false: " +
				// habilitarSubirArchivo);
			}
			if (pp.getArchivo() != null) {
				try {
					issc = new FileInputStream(pp.getArchivo());
					streamedContent = new DefaultStreamedContent(issc, "application/pdf");
					correctaRutaArchivo = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					correctaRutaArchivo = false;
					e.printStackTrace();
				}
			}
		}
	}

	public InputStream obtenerTapaPasta() throws FileNotFoundException, DocumentException {

		pp = estudiantesExamenComplexivoPPService.obtenerObjeto(
				"select pp from EstudianteExamenComplexivoPP pp inner join pp.estudiante e "
						+ "inner join fetch pp.carrera c inner join pp.proceso p where e.email=?1 and c.id=?2 and p.id=?3",
				new Object[] { UtilSeguridad.obtenerUsuario(), pp.getCarrera().getId(), pp.getProceso().getId() },
				false, new Object[] {});

		Report report = new Report();
		report.setFormat("PDF");
		report.setPath(UtilsReport.serverPathReport);
		report.setName("EC_TapaPasta");

		report.addParameter("proceso", pp.getProceso().getId());
		report.addParameter("idEstudiante", pp.getEstudiante().getId());
		report.addParameter("lugarMesAnio", "MACHALA-" + fechaFormatoString(new Date(), "yyyy"));

		InputStream input = UtilsReport.ejecutarReporte(report).getStream();

		return input;
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

	public void setIssc(InputStream issc) {
		this.issc = issc;
	}

	public void setIsUrkund(InputStream isUrkund) {
		this.isUrkund = isUrkund;
	}

	public void setListaPP(List<EstudianteExamenComplexivoPP> listaPP) {
		this.listaPP = listaPP;
	}

	public void setPdfManager(PdfManager pdfManager) {
		this.pdfManager = pdfManager;
	}

	public void setPp(EstudianteExamenComplexivoPP pp) {
		this.pp = pp;
	}

	public void setPpId(Integer ppId) {
		this.ppId = ppId;
	}

	public void setStreamedContent(StreamedContent streamedContent) {
		this.streamedContent = streamedContent;
	}

	public void setSw(boolean sw) {
		this.sw = sw;
	}

	public void subirArchivoCesionDerecho(FileUploadEvent event) {
		try {
			if (ppId == 0)
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

	public void subirArchivoCuerpoDocumento(FileUploadEvent event) {
		try {
			if (ppId == 0)
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
				isCuerpo = event.getFile().getInputstream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subirArchivoUrkund(FileUploadEvent event) {
		try {
			if (ppId == 0)
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
				isUrkund = event.getFile().getInputstream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}