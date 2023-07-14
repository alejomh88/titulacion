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

import ec.edu.utmachala.titulacion.entity.EstudianteTrabajoTitulacion;
import ec.edu.utmachala.titulacion.entity.FechaProceso;
import ec.edu.utmachala.titulacion.service.EstudianteTrabajoTitulacionService;
import ec.edu.utmachala.titulacion.service.FechaProcesoService;
import ec.edu.utmachala.titulacion.utility.Utils;
import ec.edu.utmachala.titulacion.utility.UtilsAplicacion;
import ec.edu.utmachala.titulacion.utility.UtilsArchivos;
import ec.edu.utmachala.titulacion.utility.UtilsDate;

@Controller
@Scope("session")
public class AntiplagioTTBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private EstudianteTrabajoTitulacionService estudianteTrabajoTitulacionService;

	@Autowired
	private FechaProcesoService fechaProcesoService;

	private List<EstudianteTrabajoTitulacion> listadoEstudianteTrabajoTitulacion;
	private EstudianteTrabajoTitulacion estudianteTrabajoTitulacion;

	private Boolean panelPrincipal;
	private Boolean panelCoincidencia;

	private UploadedFile archivo;
	private InputStream is;

	private Integer paginaActual;

	private BigDecimal antiplagio;

	@PostConstruct
	public void a() {
		panelPrincipal = true;
		panelCoincidencia = false;

		listadoEstudianteTrabajoTitulacion = estudianteTrabajoTitulacionService.obtenerLista(
				"select ett from EstudianteTrabajoTitulacion ett inner join ett.proceso p inner join ett.carrera c "
						+ "inner join ett.estudiante e inner join ett.especialista1 e1 inner join ett.especialistaSuplente1 es1 "
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
						antiplagio = new BigDecimal(p);
					}
				}

				PDFMergerUtility pdfCombinar = new PDFMergerUtility();
				PDDocument pdDestino = new PDDocument();
				File archivoCombinado = new File(estudianteTrabajoTitulacion.getReporteUrkund());
				pdfCombinar.appendDocument(pdDestino, archivo);

				pdDestino.save(archivoCombinado);
				estudianteTrabajoTitulacionService
						.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"reporteUrkund\"='"
								+ estudianteTrabajoTitulacion.getReporteUrkund() + "', antiplagio=" + antiplagio
								+ ", \"observacionesUrkund\" = '" + estudianteTrabajoTitulacion.getObservacionesUrkund()
								+ "' WHERE id=" + estudianteTrabajoTitulacion.getId() + ";");

				EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
						"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
						new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
								estudianteTrabajoTitulacion.getEstudiante().getEmail() },
						false, new Object[] {});

				if (ett2 != null && ett2.getId() != null) {
					estudianteTrabajoTitulacionService
							.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"reporteUrkund\"='"
									+ estudianteTrabajoTitulacion.getReporteUrkund() + "', antiplagio=" + antiplagio
									+ " WHERE id=" + ett2.getId() + ";");
				}

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
						File archivoCombinado = new File(estudianteTrabajoTitulacion.getReporteUrkund());
						pdfCombinar.appendDocument(pdDestino, archivo);

						pdDestino.save(archivoCombinado);
						estudianteTrabajoTitulacionService
								.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"reporteUrkund\"='"
										+ estudianteTrabajoTitulacion.getReporteUrkund() + "', antiplagio=" + antiplagio
										+ ", \"observacionesUrkund\" = '"
										+ estudianteTrabajoTitulacion.getObservacionesUrkund() + "' WHERE id="
										+ estudianteTrabajoTitulacion.getId() + ";");

						EstudianteTrabajoTitulacion ett2 = estudianteTrabajoTitulacionService.obtenerObjeto(
								"select tt from EstudianteTrabajoTitulacion tt inner join tt.estudiante e inner join tt.seminarioTrabajoTitulacion stt where stt.id=?1 and e.email!=?2",
								new Object[] { estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion().getId(),
										estudianteTrabajoTitulacion.getEstudiante().getEmail() },
								false, new Object[] {});

						if (ett2 != null && ett2.getId() != null) {
							estudianteTrabajoTitulacionService
									.actualizarSQL("UPDATE \"estudiantesTrabajosTitulacion\" SET \"reporteUrkund\"='"
											+ estudianteTrabajoTitulacion.getReporteUrkund() + "', antiplagio="
											+ antiplagio + " WHERE id=" + ett2.getId() + ";");
						}

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
			e.printStackTrace();
			presentaMensaje(FacesMessage.SEVERITY_INFO, "Error en el servidor de tipo: " + e.getClass());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
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

	public UploadedFile getArchivo() {
		return archivo;
	}

	public EstudianteTrabajoTitulacion getEstudianteTrabajoTitulacion() {
		return estudianteTrabajoTitulacion;
	}

	public InputStream getIs() {
		return is;
	}

	public List<EstudianteTrabajoTitulacion> getListadoEstudianteTrabajoTitulacion() {
		return listadoEstudianteTrabajoTitulacion;
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
		if (estudianteTrabajoTitulacion.getSeminarioTrabajoTitulacion() == null) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"El estudiante no tiene asignado un tema de investigación por parte del tutor");
		} else if (estudianteTrabajoTitulacion.getAprobado() == null) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el URKUND debido que el estudiante no se encuentra aprobado por el tutor.");
		} else if (estudianteTrabajoTitulacion.getOpcionTitulacion().getNombre()
				.compareToIgnoreCase("ENSAYOS O ÁRTICULOS ACADÉMICOS") == 0) {
			a();
			presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No es necesario cargar el informe URKUND ya que es un Artículo Académico publicado.");
		} else if (!verificarDentroFecha()) {
			a();
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_INFO,
					"No puede registrar el URKUND debido que exedió la fecha determinada en el cronograma del proceso.");
		} else {
			panelPrincipal = false;
			panelCoincidencia = true;
			antiplagio = estudianteTrabajoTitulacion.getAntiplagio();
		}

	}

	public void onPageChange(PageEvent event) {
		// System.out.println("Pagina: " + ((DataTable)
		// event.getSource()).getPage());
		this.setPaginaActual(((DataTable) event.getSource()).getPage());
	}

	public void setAntiplagio(BigDecimal antiplagio) {
		this.antiplagio = antiplagio;
	}

	public void setArchivo(UploadedFile archivo) {
		this.archivo = archivo;
	}

	public void setEstudianteTrabajoTitulacion(EstudianteTrabajoTitulacion estudianteTrabajoTitulacion) {
		this.estudianteTrabajoTitulacion = estudianteTrabajoTitulacion;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public void setListadoEstudianteTrabajoTitulacion(
			List<EstudianteTrabajoTitulacion> listadoEstudianteTrabajoTitulacion) {
		this.listadoEstudianteTrabajoTitulacion = listadoEstudianteTrabajoTitulacion;
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
			estudianteTrabajoTitulacion
					.setReporteUrkund(UtilsArchivos.getRutaUrkundTT(estudianteTrabajoTitulacion.getProceso().getId())
							+ estudianteTrabajoTitulacion.getEstudiante().getId() + "_C"
							+ estudianteTrabajoTitulacion.getCarrera().getId() + "_"
							+ new SimpleDateFormat("MM-dd-yyyy").format(new Date())
							+ event.getFile().getFileName()
									.substring(Utils.posicionCaracterDerIzq(event.getFile().getFileName(), '.'),
											event.getFile().getFileName().length())
									.toLowerCase());
			System.out.println(estudianteTrabajoTitulacion.getReporteUrkund());
			is = event.getFile().getInputstream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean verificarDentroFecha() {
		boolean dentroFecha = false;

		try {
			List<FechaProceso> listadoFechasProcesos = fechaProcesoService.obtenerLista(
					"select fp from FechaProceso fp inner join fp.proceso p where p.id=?1 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo is true and fp.carrera is null order by fp.id asc",
					new Object[] { estudianteTrabajoTitulacion.getProceso().getId() }, 0, false, new Object[] {});

			if (listadoFechasProcesos != null && listadoFechasProcesos.size() >= 1) {

				if (listadoFechasProcesos.get(0).getFechaInicio().compareTo(new Date()) <= 0
						&& listadoFechasProcesos.get(0).getFechaFinal().compareTo(new Date()) >= 0) {

					dentroFecha = true;

				} else {
					listadoFechasProcesos = fechaProcesoService.obtenerLista(
							"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
							new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
									estudianteTrabajoTitulacion.getCarrera().getId() },
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
						"select fp from FechaProceso fp inner join fp.proceso p inner join fp.carrera c where p.id=?1 and c.id=?2 and fp.modalidad='TT' and fp.fase='URCIVA' and fp.activo='true' order by fp.id asc",
						new Object[] { estudianteTrabajoTitulacion.getProceso().getId(),
								estudianteTrabajoTitulacion.getCarrera().getId() },
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
