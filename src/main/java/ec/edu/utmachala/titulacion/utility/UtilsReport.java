package ec.edu.utmachala.titulacion.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class UtilsReport {
	public static final String serverUrl = "http://titulacion.utmachala.edu.ec:8080/jasperserver/";
	public static final String serverUser = "jasperadmin";
	public static final String serverPassword = "jasperadmin";
	public static final String serverPathReport = "/reports/Titulacion/";

	public static StreamedContent ejecutarReporte(Report report) {
		try {
			return (StreamedContent) new DefaultStreamedContent(
					(InputStream) new FileInputStream(
							JasperserverRestClient.getInstance("http://titulacion.utmachala.edu.ec:8080/jasperserver/",
									"jasperadmin", "jasperadmin").getReportAsFile(report)),
					"application/pdf", report.getName() + "_"
							+ UtilsDate.fechaFormatoString(new Date(), "ddMMyyyy_hhmmss") + "." + report.getFormat());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return null;
	}
}