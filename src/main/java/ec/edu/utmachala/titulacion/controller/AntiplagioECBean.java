package ec.edu.utmachala.titulacion.controller;

import static ec.edu.utmachala.titulacion.utility.UtilsAplicacion.presentaMensaje;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import ec.edu.utmachala.titulacion.entity.EstudianteExamenComplexivoPP;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudiantesExamenComplexivoPPService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.Utils;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class AntiplagioECBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP;
	private EstudianteExamenComplexivoPP estudianteExamenComplexivoPP;

	private Boolean panelPrincipal;
	private Boolean panelCoincidencia;

	private UploadedFile archivo;
	private InputStream is;

	private Integer paginaActual;

	private BigDecimal antiplagio;
	private BigDecimal antiplagio2;

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelCoincidencia = false;

		listadoEstudiantesExamenComplexivoPP = estudiantesExamenComplexivoPPService.obtenerLista(
				"select epp from EstudianteExamenComplexivoPP epp inner join epp.proceso p inner join epp.carrera c "
						+ "inner join epp.estudiante e inner join epp.especialista1 e1 inner join epp.especialistaSuplente1 es1 "
						+ "where (p.fechaInicio<=?1 and p.fechaCierre>=?1) and (e1.email=?2 or es1.email=?2) order by c.nombre, e.apellidoNombre",
				new Object[] { UtilsDate.timestamp(),
						SecurityContextHolder.getContext().getAuthentication().getName() },
				0, false, new Object[0]);
	}

	public void actualizar() {
		try {
			PDDocument archivo;
			archivo = PDDocument.load(is);

			PDFTextStripper stripper1 = new PDFTextStripper();
			stripper1.setStartPage(1);
			stripper1.setEndPage(1);
			String textoUrkund = stripper1.getText(archivo);

			if (textoUrkund.contains("Urkund Analysis Result") && textoUrkund.contains("Analysed Document:")
					&& textoUrkund.contains("Submitted:") && textoUrkund.contains("Submitted By:")) {

				StringTokenizer st = new StringTokenizer(textoUrkund, ":");
				while (st.hasMoreTokens()) {
					String separador = st.nextToken();
					if (separador.contains("Significance")) {
						String p = st.nextToken().split(" ")[1];
						estudianteExamenComplexivoPP.setAntiplagio(new BigDecimal(p));
					}
				}

				PDFMergerUtility pdfCombinar = new PDFMergerUtility();
				PDDocument pdDestino = new PDDocument();
				File archivoCombinado = new File(estudianteExamenComplexivoPP.getReporteUrkund());
				pdfCombinar.appendDocument(pdDestino, archivo);

				pdDestino.save(archivoCombinado);
				estudiantesExamenComplexivoPPService
						.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"reporteUrkund\"='"
								+ estudianteExamenComplexivoPP.getReporteUrkund() + "', antiplagio="
								+ estudianteExamenComplexivoPP.getAntiplagio() + ", \"observacionesUrkund\"='"
								+ estudianteExamenComplexivoPP.getObservacionesUrkund() + "' WHERE id="
								+ estudianteExamenComplexivoPP.getId() + ";");

				presentaMensaje(FacesMessage.SEVERITY_INFO, "Información almacenada satisfactoriamente.");
				panelPrincipal = true;
				panelCoincidencia = false;
				a();
			} else {
				PDFTextStripper stripper2 = new PDFTextStripper();
				int aux = 0;
				for (int i = 0; i <= 10; i++) {
					stripper2.setStartPage(archivo.getNumberOfPages() - i);
					stripper2.setEndPage(archivo.getNumberOfPages() - i);
					String textoTurnitin = stripper2.getText(archivo);

					// System.out.println("Texto turnitin:\n" + textoTurnitin);

					if (textoTurnitin.contains("INFORME DE ORIGINALIDAD")
							&& textoTurnitin.contains("INDICE DE SIMILITUD")
							&& textoTurnitin.contains("PUBLICACIONES")) {
						antiplagio = new BigDecimal(textoTurnitin.split("%")[0]);
						System.out.println("Antiplagio encontrado: " + antiplagio);

						PDFMergerUtility pdfCombinar = new PDFMergerUtility();
						PDDocument pdDestino = new PDDocument();
						File archivoCombinado = new File(estudianteExamenComplexivoPP.getReporteUrkund());
						pdfCombinar.appendDocument(pdDestino, archivo);

						pdDestino.save(archivoCombinado);
						estudiantesExamenComplexivoPPService
								.actualizarSQL("UPDATE \"estudiantesExamenComplexivoPP\" SET \"reporteUrkund\"='"
										+ estudianteExamenComplexivoPP.getReporteUrkund() + "', antiplagio="
										+ antiplagio + ", \"observacionesUrkund\" = '"
										+ estudianteExamenComplexivoPP.getObservacionesUrkund() + "' WHERE id="
										+ estudianteExamenComplexivoPP.getId() + ";");

						presentaMensaje(FacesMessage.SEVERITY_INFO, "Información almacenada satisfactoriamente.");
						panelPrincipal = true;
						panelCoincidencia = false;
						a();
						aux++;
						break;
					}
				}
				if (aux == 0) {
					presentaMensaje(FacesMessage.SEVERITY_INFO,
							"El archivo cargado no es el correcto, suba el archivo correspondiente.");
					panelPrincipal = true;
					panelCoincidencia = false;
					a();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void establecerPanelPrincipal() {
		a();
		panelPrincipal = true;
		panelCoincidencia = false;

	}

	public BigDecimal getAntiplagio() {
		return antiplagio;
	}

	public BigDecimal getAntiplagio2() {
		return antiplagio2;
	}

	public UploadedFile getArchivo() {
		return archivo;
	}

	public EstudianteExamenComplexivoPP getEstudianteExamenComplexivoPP() {
		return estudianteExamenComplexivoPP;
	}

	public EstudiantesExamenComplexivoPPService getEstudiantesExamenComplexivoPPService() {
		return estudiantesExamenComplexivoPPService;
	}

	public InputStream getIs() {
		return is;
	}

	public List<EstudianteExamenComplexivoPP> getListadoEstudiantesExamenComplexivoPP() {
		return listadoEstudiantesExamenComplexivoPP;
	}

	public Integer getPaginaActual() {
		return paginaActual;
	}

	public Boolean getPanelCoincidencia() {
		return panelCoincidencia;
	}

	public Boolean getPanelPrincipal() {
		return panelPrincipal;
	}

	public void insertarCoincidencia() {
		if (estudianteExamenComplexivoPP.getAprobado() == null) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el URKUND debido que el estudiante no se encuentra aprobado por el tutor.");
		} else if (!verificarDentroFecha()) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el URKUND debido que exedió la fecha determinada en el cronograma del proceso.");
		} else {
			panelPrincipal = false;
			panelCoincidencia = true;
		}
	}

	public void onPageChange(PageEvent event) {
		System.out.println("Pagina: " + ((DataTable) event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setAntiplagio(BigDecimal antiplagio) {
		this.antiplagio = antiplagio;
	}

	public void setAntiplagio2(BigDecimal antiplagio2) {
		this.antiplagio2 = antiplagio2;
	}

	public void setArchivo(UploadedFile archivo) {
		this.archivo = archivo;
	}

	public void setEstudianteExamenComplexivoPP(EstudianteExamenComplexivoPP estudianteExamenComplexivoPP) {
		this.estudianteExamenComplexivoPP = estudianteExamenComplexivoPP;
	}

	public void setEstudiantesExamenComplexivoPPService(
			EstudiantesExamenComplexivoPPService estudiantesExamenComplexivoPPService) {
		this.estudiantesExamenComplexivoPPService = estudiantesExamenComplexivoPPService;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public void setListadoEstudiantesExamenComplexivoPP(
			List<EstudianteExamenComplexivoPP> listadoEstudiantesExamenComplexivoPP) {
		this.listadoEstudiantesExamenComplexivoPP = listadoEstudiantesExamenComplexivoPP;
	}

	public void setPaginaActual(Integer paginaActual) {
		this.paginaActual = paginaActual;
	}

	public void setPanelCoincidencia(Boolean panelCoincidencia) {
		this.panelCoincidencia = panelCoincidencia;
	}

	public void setPanelPrincipal(Boolean panelPrincipal) {
		this.panelPrincipal = panelPrincipal;
	}

	public void subirArchivo(FileUploadEvent event) {
		try {
			estudianteExamenComplexivoPP
					.setReporteUrkund(UtilsArchivos.getRutaUrkundEC(estudianteExamenComplexivoPP.getProceso().getId())
							+ estudianteExamenComplexivoPP.getEstudiante().getId() + "_C"
							+ estudianteExamenComplexivoPP.getCarrera().getId() + "_"
							+ new SimpleDateFormat("MM-dd-yyyy").format(new Date())
							+ event.getFile().getFileName()
									.substring(Utils.posicionCaracterDerIzq(event.getFile().getFileName(), '.'),
											event.getFile().getFileName().length())
									.toLowerCase());
			System.out.println(
					"ruta del archivo desde subir archivo: " + estudianteExamenComplexivoPP.getReporteUrkund());
			is = event.getFile().getInputstream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean verificarDentroFecha() {
		boolean dentroFecha = false;

		try {
			List<FechaProceso> listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo is true and fp.carrera is null order by fp.id asc",
					new Object[] { estudianteExamenComplexivoPP.getProceso().getId() }, 0, false, new Object[] {});

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {

				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

					dentroFecha = true;

				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
									estudianteExamenComplexivoPP.getCarrera().getId() },
							0, false, new Object[0]);

					if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
						if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
								&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

							dentroFecha = true;

						}
					}
				}
			} else {
				listadoFechasProcesos = fechaProcesoService.obtenerLista(
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='EC' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteExamenComplexivoPP.getProceso().getId(),
								estudianteExamenComplexivoPP.getCarrera().getId() },
						0, false, new Object[0]);

				if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {
					if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
							&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

						dentroFecha = true;

					}
				}
			}
		} catch (NullPointerException npe) {
			System.out.println("No existe registro de fecha para esta actividad.");
			npe.printStackTrace();

		}

		return dentroFecha;
	}

}
