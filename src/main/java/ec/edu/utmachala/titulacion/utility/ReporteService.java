package ec.edu.utmachala.titulacion.utility;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ReporteService {
	void generarReporteCSV(final List<String> p0, final String p1);

	<T> void generarReportePDF(final List<T> p0, final Map<String, Object> p1, final String p2, final String p3);

	<T> File generarReportePDFFile(final List<T> p0, final Map<String, Object> p1, final String p2, final String p3);

	<T> void generarReporteXLS(final List<T> p0, final Map<String, Object> p1, final String p2, final String p3);

	void generarReporteXLSSencillo(final List<String> p0, final String p1, final String p2, final String p3,
			final String p4, final int p5);

	<T> void responderServidor(final File p0, final String p1);
}