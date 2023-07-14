package ec.edu.utmachala.titulacion.utility;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

public class UtilsArchivos {
	private static final Properties p;
	private static final String sep;
	private static final String rutaRaiz;

	static {
		p = System.getProperties();
		sep = UtilsArchivos.p.getProperty("file.separator");
		rutaRaiz = ((UtilsArchivos.p.getProperty("os.name").compareToIgnoreCase("linux") == 0) ? "/opt"
				: UtilsArchivos.p.getProperty("user.home")) + UtilsArchivos.sep + "exetasi" + UtilsArchivos.sep;
	}

	public static String cambiarTildes(final String nombreArchivo) {
		return nombreArchivo.replace("\u00c1", "A").replace("\u00c9", "E").replace("\u00cd", "I").replace("\u00d3", "O")
				.replace("\u00da", "U").replace("\u00d1", "N");
	}

	public static byte[] convertir(final File file) {
		final byte[] a = new byte[(int) file.length()];
		try {
			final FileInputStream fis = new FileInputStream(file);
			fis.read(a);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	public static byte[] convertir(final String ruta) {
		final File file = new File(ruta);
		final byte[] a = new byte[(int) file.length()];
		try {
			final FileInputStream fis = new FileInputStream(file);
			fis.read(a);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

	public static byte[] convertirString(final String archivo) {
		try {
			return archivo.getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String crearRuta(final String ruta) {
		final File directorio = new File(ruta);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}
		return ruta;
	}

	public static String determinarExtension(final String nombreImagen) {
		String extension = null;
		final char f = nombreImagen.charAt(nombreImagen.length() - 3);
		if (f == '.') {
			extension = nombreImagen.substring(nombreImagen.length() - 2, nombreImagen.length());
		} else {
			extension = nombreImagen.substring(nombreImagen.length() - 3, nombreImagen.length());
		}
		return extension;
	}

	public static boolean eliminarArchivo(final String archivo) {
		final File directorio = new File(archivo);
		return directorio.delete();
	}

	public static String getRutaBibliotecaEC(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "EC"
				+ UtilsArchivos.sep + "BIBLIOTECA" + UtilsArchivos.sep);
	}

	public static String getRutaBibliotecaTT(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "TT"
				+ UtilsArchivos.sep + "BIBLIOTECA" + UtilsArchivos.sep);
	}

	public static String getRutaCuerpoDocumentoDriveEC(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "EC"
				+ UtilsArchivos.sep + "DOCUMENTODRIVE" + UtilsArchivos.sep);
	}

	public static String getRutaCuerpoDocumentoDriveTT(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "TT"
				+ UtilsArchivos.sep + "DOCUMENTODRIVE" + UtilsArchivos.sep);
	}

	public static String getRutaEC() {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + "PT-031016" + UtilsArchivos.sep + "EC"
				+ UtilsArchivos.sep);
	}

	public static String getRutaImagen() {
		return crearRuta(getRutaRaiz() + "imagenes" + UtilsArchivos.sep);
	}

	public static String getRutaRaiz() {
		return crearRuta(UtilsArchivos.rutaRaiz);
	}

	public static String getRutaReporteExcel() {
		return crearRuta(getRutaRaiz() + "excel" + UtilsArchivos.sep);
	}

	public static String getRutaReportes() {
		return FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reportes") + UtilsArchivos.sep;
	}

	public static String getRutaTT() {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + "PT-031016" + UtilsArchivos.sep + "TT"
				+ UtilsArchivos.sep);
	}

	public static String getRutaUrkundEC(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "EC"
				+ UtilsArchivos.sep + "URKUND" + UtilsArchivos.sep);
	}

	public static String getRutaUrkundTT(final String proceso) {
		return crearRuta(getRutaRaiz() + "trabajos" + UtilsArchivos.sep + proceso + UtilsArchivos.sep + "TT"
				+ UtilsArchivos.sep + "URKUND" + UtilsArchivos.sep);
	}

	public static void guardarArchivo(final String rutaCompletaArchivo, final InputStream contenidoArchivo) {
		try {
			final OutputStream out = new FileOutputStream(new File(rutaCompletaArchivo));
			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = contenidoArchivo.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			contenidoArchivo.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String guardarImagen(final String ruta, final BufferedImage imagen, final String nombreImagen) {
		try {
			final File img = new File(ruta + UtilsArchivos.sep + nombreImagen);
			ImageIO.write(imagen, determinarExtension(nombreImagen), img);
			return img.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String leerArchivo(final InputStream archivo) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(archivo, "UTF-8"));
			System.out.println(br.readLine());
			String linea;
			while ((linea = br.readLine()) != null) {
				sb.append(linea + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static String leerArchivo(final String archivo) {
		final StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), "UTF-8"));
			String linea;
			while ((linea = br.readLine()) != null) {
				sb.append(linea + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static BufferedImage leerImagen(final InputStream imagen, final String ruta) {
		try {
			if (imagen != null) {
				return ImageIO.read(imagen);
			}
			if (ruta != null && ruta.compareTo("") != 0) {
				final File directorio = new File(ruta);
				if (directorio.exists()) {
					return ImageIO.read(directorio);
				}
			}
			return ImageIO.read(new File(getRutaImagen() + UtilsArchivos.sep + "default.png"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static InputStream leerImagen(final String ruta) {
		try {
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final BufferedImage bi = leerImagen(null, ruta);
			if (bi != null) {
				ImageIO.write(bi, "png", os);
			}
			return new ByteArrayInputStream(os.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String moverArchivo(final String archivo, final String archivoFinal) {
		final File a = new File(archivo);
		a.renameTo(new File(archivoFinal));
		return archivoFinal;
	}
}