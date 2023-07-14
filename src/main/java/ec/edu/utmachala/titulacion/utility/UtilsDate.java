package ec.edu.utmachala.titulacion.utility;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UtilsDate {
	private static final SimpleDateFormat formatoFecha;
	private static final long ms = 86400000L;

	static {
		formatoFecha = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES"));
	}

	public static Calendar calendarCompleto(final Date fecha) {
		final Calendar c = Calendar.getInstance();
		final Calendar c2 = Calendar.getInstance();
		c.setTime(fecha);
		c.set(11, c2.get(11));
		c.set(12, c2.get(12));
		c.set(13, c2.get(13));
		return c;
	}

	public static int compareTo(final Date fecha, final Date fecha1) {
		return fechaFormatoDate(fecha).compareTo(fechaFormatoDate(fecha1));
	}

	public static String convertirDateATexto(final Date fecha) {
		final String fechaText = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES")).format(fecha);
		String fech = "";
		fech += NumerosPorLetras.convertNumberToLetter(new BigDecimal(fechaText.split("/")[0])).trim();
		fech += " d\u00edas del mes de ";
		fech += new SimpleDateFormat("MMMM", new Locale("ES")).format(fecha);
		fech += " de ";
		fech += NumerosPorLetras.convertNumberToLetter(new BigDecimal(fechaText.split("/")[2]));
		return fech.toLowerCase().trim();
	}

	public static Date date(final Timestamp fecha) {
		return new Date(fecha.getTime());
	}

	public static Date dateCompleto(final Date fecha) {
		return new Date(fecha.getTime() + 86399999L);
	}

	public static String dateFin(final Date fecha) {
		return fechaFormatoString(fecha) + " 23:59:59.999";
	}

	public static String dateInicio(final Date fecha) {
		return fechaFormatoString(fecha) + " 00:00:00.000";
	}

	public static Long diasMora(final Date fecha, final Date fechaMora) {
		return (fechaFormatoDate(fecha).getTime() - fechaFormatoDate(fechaMora).getTime()) / 86400000L;
	}

	public static Date fechaFormatoCorte(final String fecha) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd", new Locale("ES")).parse(fecha);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date fechaFormatoDate(final Date fecha) {
		return fechaFormatoDate(fechaFormatoString(fecha));
	}

	public static Date fechaFormatoDate(final String fecha) {
		try {
			return UtilsDate.formatoFecha.parse(fecha);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public static Date fechaFormatoDate(final String fecha, final String formato) {
		try {
			return new SimpleDateFormat(formato, new Locale("ES")).parse(fecha);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String fechaFormatoString(final Date fecha) {
		return UtilsDate.formatoFecha.format(fecha);
	}

	public static String fechaFormatoString(final Date fecha, final String formato) {
		return new SimpleDateFormat(formato, new Locale("ES")).format(fecha);
	}

	public static String fechaFormatoString(final String fecha) {
		return fechaFormatoString(fechaFormatoDate(fecha));
	}

	public static String fechaFormatoString(final Timestamp fecha, final String formato) {
		return new SimpleDateFormat(formato, new Locale("ES")).format(fecha);
	}

	public static Date getPrimerDiaDelMes(final Date fecha) {
		final Calendar calendario = Calendar.getInstance();
		calendario.setTime(fecha);
		calendario.set(calendario.get(1), calendario.get(2), calendario.getActualMinimum(5));
		return calendario.getTime();
	}

	public static Date getUltimoDiaDelMes(final Date fecha) {
		final Calendar calendario = Calendar.getInstance();
		calendario.setTime(fecha);
		calendario.set(calendario.get(1), calendario.get(2), calendario.getActualMaximum(5));
		return calendario.getTime();
	}

	public static String horasMinutos(final Timestamp fechaFin, final Timestamp fechaInicio) {
		final long diff = fechaFin.getTime() - fechaInicio.getTime();
		final long diffMinutes = diff / 60000L % 60L;
		final long diffHours = diff / 3600000L;
		return diffHours + ":" + ((diffMinutes == 0L) ? "00" : Long.valueOf(diffMinutes));
	}

	public static Date sumarDias(final Date fechaInicio, final int dias) {
		final Calendar calendar = new GregorianCalendar();
		calendar.setTime(fechaInicio);
		calendar.add(5, dias);
		return calendar.getTime();
	}

	public static Timestamp timestamp() {
		return new Timestamp(new Date().getTime());
	}

	public static Timestamp timestamp(final Date fecha) {
		return new Timestamp(fecha.getTime());
	}

	public static Timestamp timestamp(final long fecha) {
		return new Timestamp(fecha);
	}

	public static Timestamp timestamp(final String fecha) {
		return new Timestamp(fechaFormatoDate(fecha).getTime());
	}

	public static Timestamp timestampCompleto(final Date fecha) {
		final Calendar c = Calendar.getInstance();
		final Calendar c2 = Calendar.getInstance();
		c.setTime(fecha);
		c.set(11, c2.get(11));
		c.set(12, c2.get(12));
		c.set(13, c2.get(13));
		return new Timestamp(c.getTime().getTime());
	}
}