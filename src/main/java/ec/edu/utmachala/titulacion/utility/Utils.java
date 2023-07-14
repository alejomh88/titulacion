package ec.edu.utmachala.titulacion.utility;

public class Utils {
	public static String getText(String html, final String openString, final String closeString) {
		final int indexOfOpenString = html.indexOf(openString);
		final int indexOfCloseString = html.indexOf(closeString);
		if (indexOfOpenString != -1 && indexOfCloseString != -1) {
			final String substring = html.substring(indexOfOpenString, indexOfCloseString + 1);
			html = getText(html.replace(substring, ""), openString, closeString);
		}
		html = html.trim();
		html = html.replace("&nbsp;", " ");
		html = html.replace("&quot;", "\"");
		html = html.replace("&#40;", "(");
		html = html.replace("&#41;", ")");
		html = html.replace("&#123;", "{");
		html = html.replace("&#125;", "}");
		html = html.replace("&#91;", "[");
		html = html.replace("&#93;", "]");
		return html;
	}

	public static int posicionCaracterDerIzq(final String cadena, final char caracter) {
		for (int i = cadena.length() - 1; i >= 0; --i) {
			if (cadena.charAt(i) == caracter) {
				return i--;
			}
		}
		return 0;
	}

	public static String so() {
		if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
			return "WINDOWS";
		}
		if (System.getProperty("os.name").toUpperCase().contains("LINUX")) {
			return "LINUX";
		}
		return null;
	}
}