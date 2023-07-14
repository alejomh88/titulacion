package ec.edu.utmachala.titulacion.utility;

import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.jsoup.Jsoup;
import org.primefaces.context.RequestContext;

public class UtilsAplicacion {
	public static void enviarVariableVista(final String variable, final boolean valor) {
		RequestContext.getCurrentInstance().addCallbackParam(variable, (Object) valor);
	}

	public static void execute(final List<String> jqs) {
		for (final String jq : jqs) {
			RequestContext.getCurrentInstance().execute(jq);
		}
	}

	public static void execute(final String jq) {
		RequestContext.getCurrentInstance().execute(jq);
	}

	public static String html2text(final String html) {
		return Jsoup.parse(html).text();
	}

	public static String prepararTextoHtml(final String texto) {
		String cadena;
		String cadenaOriginal = cadena = texto;
		for (int i = 0; i < cadena.length(); ++i) {
			final char cc = cadena.charAt(i);
			if (cc == '\n') {
				final String cadenaInsertar = "<br>";
				final String principioCadena = cadenaOriginal.substring(0, i);
				final String finCadena = cadenaOriginal.substring(i);
				cadenaOriginal = (cadena = principioCadena + cadenaInsertar + finCadena);
				i += 5;
			}
		}
		final String espaciado = "<span class=\"Apple-tab-span\" style=\"white-space:pre\">\t</span>";
		cadenaOriginal = cadenaOriginal.replace("<tab>", espaciado);
		return cadenaOriginal;
	}

	public static void presentaMensaje(final FacesMessage.Severity severity, final String mensaje) {
		FacesContext.getCurrentInstance().addMessage((String) null,
				new FacesMessage(severity, "MENSAJE DEL SISTEMA", mensaje));
	}

	public static void presentaMensaje(final FacesMessage.Severity severity, final String mensaje,
			final String variable, final boolean valor) {
		presentaMensaje(severity, mensaje);
		enviarVariableVista(variable, valor);
	}

	public static void redireccionar(final String url) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void update(final List<String> updates) {
		RequestContext.getCurrentInstance().update((Collection) updates);
	}

	public static void update(final String update) {
		FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(update);
	}
}