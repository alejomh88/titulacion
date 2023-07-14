package ec.edu.utmachala.titulacion.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;

@Service
public class ReporteServiceImpl implements ReporteService {
	private FacesContext facesContext;

	private <T> JasperPrint generadorReporte(final String nombreReporte, final Map<String, Object> parametros,
			final List<T> listaReporte) {
		try {
			if (listaReporte == null) {
				return JasperFillManager
						.fillReport(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes")
								+ "/" + nombreReporte + ".jasper", (Map) parametros);
			}
			if (!listaReporte.isEmpty()) {
				return JasperFillManager.fillReport(
						FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + "/"
								+ nombreReporte + ".jasper",
						(Map) parametros, (JRDataSource) new JRBeanCollectionDataSource((Collection) listaReporte));
			}
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_ERROR, "No hay datos para imprimir");
		} catch (JRException e) {
			e.printStackTrace();
		}
		return new JasperPrint();
	}

	public void generarReporteCSV(final List<String> listaReporte, final String nombreReporte) {
		this.facesContext = FacesContext.getCurrentInstance();
		try {
			UtilsCSV.crearCSV((List) listaReporte, nombreReporte);
			this.respondeServidor(nombreReporte + ".csv", 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T> void generarReportePDF(final List<T> listaReporte, final Map<String, Object> parametros,
			final String nombreReporte, final String nombre) {
		this.facesContext = FacesContext.getCurrentInstance();
		parametros.put("SUBREPORT_DIR",
				FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + "/");
		this.respondeServidor(this.generadorReporte(nombreReporte, parametros, listaReporte), nombre);
	}

	public <T> File generarReportePDFFile(final List<T> listaReporte, final Map<String, Object> parametros,
			final String nombreReporte, final String nombre) {
		System.out.println("Entro al generar reporte pdf file");
		this.facesContext = FacesContext.getCurrentInstance();
		if (listaReporte == null || listaReporte.isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_ERROR, "No hay datos para imprimir");
			System.out.println("No entra al else");
			return null;
		}
		System.out.println("ingreso al generar reporte file");
		parametros.put("SUBREPORT_DIR",
				FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + "/");
		return this.responderFile(this.generadorReporte(nombreReporte, parametros, listaReporte), nombre);
	}

	public <T> void generarReportePDFSinLista(final List<T> listaReporte, final Map<String, Object> parametros,
			final String nombreReporte, final String nombre) {
		this.facesContext = FacesContext.getCurrentInstance();
		if (listaReporte == null || listaReporte.isEmpty()) {
			UtilsAplicacion.presentaMensaje(FacesMessage.SEVERITY_ERROR, "No hay datos para imprimir");
		} else {
			parametros.put("SUBREPORT_DIR",
					FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + "/");
			this.respondeServidor(this.generadorReporte(nombreReporte, parametros, listaReporte), nombre);
		}
	}

	public <T> void generarReporteXLS(final List<T> listaReporte, final Map<String, Object> parametros,
			final String nombreReporte, final String nombre) {
		this.facesContext = FacesContext.getCurrentInstance();
		try {
			parametros.put("SUBREPORT_DIR",
					FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + "/");
			final JasperPrint jasperPrint = this.generadorReporte(nombreReporte, parametros, listaReporte);
			final JRXlsExporter exporter = new JRXlsExporter();
			exporter.setExporterInput((ExporterInput) new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nombre + ".xls"));
			final SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
			configuration.setOnePagePerSheet(Boolean.valueOf(false));
			configuration.setDetectCellType(Boolean.valueOf(true));
			configuration.setCollapseRowSpan(Boolean.valueOf(false));
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			final HttpServletResponse response = (HttpServletResponse) this.facesContext.getExternalContext()
					.getResponse();
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-disposition", "attachment; filename=" + nombre + ".xls");
			final ServletOutputStream servletStream = response.getOutputStream();
			final File f = new File(nombre + ".xls");
			final InputStream in = new FileInputStream(f);
			int bit = 256;
			while (bit >= 0) {
				bit = in.read();
				servletStream.write(bit);
			}
			servletStream.flush();
			servletStream.close();
			in.close();
			this.facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generarReporteXLSSencillo(final List<String> listaReporte, final String nombreReporte, final String UA,
			final String C, final String tituloRepor, final int numCol) {
		this.facesContext = FacesContext.getCurrentInstance();
		try {
			UtilsXLSX.crearXLSX((List) listaReporte, nombreReporte, nombreReporte, UA, C, tituloRepor, numCol);
			this.respondeServidor(nombreReporte + ".xlsx", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public File responderFile(final JasperPrint jasperPrint, final String nombreReporte) {
		try {
			final File file = File.createTempFile(nombreReporte, ".pdf");
			final JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput((ExporterInput) new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(file));
			final SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public <T> void responderServidor(final File archivo, final String nombreReporte) {
		final ExternalContext econtext = this.facesContext.getExternalContext();
		try {
			final HttpServletResponse response = (HttpServletResponse) econtext.getResponse();
			response.setContentType("application/pdf");
			response.addHeader("Content-disposition", "attachment; filename=" + nombreReporte + ".pdf");
			final ServletOutputStream servletStream = response.getOutputStream();
			final InputStream in = new FileInputStream(archivo);
			int bit = 256;
			while (bit >= 0) {
				bit = in.read();
				servletStream.write(bit);
			}
			servletStream.flush();
			servletStream.close();
			in.close();
			this.facesContext.responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void respondeServidor(final JasperPrint jasperPrint, final String nombreReporte) {
		final ExternalContext econtext = this.facesContext.getExternalContext();
		try {
			final HttpServletResponse response = (HttpServletResponse) econtext.getResponse();
			response.setContentType("application/pdf");
			response.addHeader("Content-disposition", "attachment; filename=" + nombreReporte + ".pdf");
			final JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterInput((ExporterInput) new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput((OutputStream) response.getOutputStream()));
			final SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			exporter.setConfiguration(configuration);
			exporter.exportReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.facesContext.responseComplete();
	}

	public void respondeServidor(final String nombreReporte, final int tipo) {
		final ExternalContext econtext = this.facesContext.getExternalContext();
		try {
			final HttpServletResponse response = (HttpServletResponse) econtext.getResponse();
			if (tipo == 1) {
				response.setContentType("application/vnd.ms-excel");
			}
			if (tipo == 2) {
				response.setContentType("text/plain");
			}
			response.addHeader("Content-disposition", "attachment; filename=" + nombreReporte);
			final ServletOutputStream servletStream = response.getOutputStream();
			final File f = new File(nombreReporte);
			final InputStream in = new FileInputStream(f);
			int bit = 256;
			while (bit >= 0) {
				bit = in.read();
				servletStream.write(bit);
			}
			servletStream.flush();
			servletStream.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.facesContext.responseComplete();
	}
}