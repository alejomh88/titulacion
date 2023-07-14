package ec.edu.utmachala.titulacion.utility;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class UtilsCSV {
	public static void crearCSV(final List<String> list, final String filename) {
		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			fichero = new FileWriter(filename + ".csv");
			pw = new PrintWriter(fichero);
			for (final String s : list) {
				pw.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (null != fichero) {
					fichero.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				if (null != fichero) {
					fichero.close();
				}
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}
}