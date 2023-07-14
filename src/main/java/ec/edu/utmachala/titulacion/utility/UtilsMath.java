package ec.edu.utmachala.titulacion.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class UtilsMath {
	private static final BigDecimal porcentaje;

	static {
		porcentaje = new BigDecimal("100");
	}

	public static int compareTo(final BigDecimal valor, final BigDecimal valor1) {
		return valor.compareTo(valor1);
	}

	public static int compareTo(final BigDecimal valor, final Integer valor1) {
		return compareTo(valor, new BigDecimal(valor1));
	}

	public static int compareTo(final BigDecimal valor, final Long valor1) {
		return compareTo(valor, new BigDecimal(valor1));
	}

	public static int compareTo(final BigDecimal valor, final String valor1) {
		return compareTo(valor, new BigDecimal(valor1));
	}

	public static BigDecimal divide(final BigDecimal divisor, final BigDecimal dividendo) {
		try {
			return divisor.divide(dividendo, 2, 6);
		} catch (ArithmeticException e) {
			return newBigDecimal();
		}
	}

	public static BigDecimal divide(final BigDecimal divisor, final Integer dividendo) {
		return divide(divisor, new BigDecimal(dividendo));
	}

	public static BigDecimal divide(final BigDecimal divisor, final Long dividendo) {
		return divide(divisor, new BigDecimal(dividendo));
	}

	public static BigDecimal divide(final BigDecimal divisor, final String dividendo) {
		return divide(divisor, new BigDecimal(dividendo));
	}

	public static BigDecimal divide(final Integer divisor, final Integer dividendo) {
		return divide(new BigDecimal(divisor), dividendo);
	}

	public static BigDecimal divideCalificaciones(final BigDecimal divisor, final Integer dividendo) {
		final BigDecimal bd = divisor.divide(new BigDecimal(dividendo), 6, 6);
		final DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		return new BigDecimal(df.format(bd).replace(",", "."));
	}

	public static BigDecimal multiplicar(final BigDecimal multiplicando, final BigDecimal multiplicador) {
		return redondear(multiplicando.multiply(multiplicador));
	}

	public static BigDecimal multiplicar(final BigDecimal multiplicando, final Integer multiplicador) {
		return multiplicar(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal multiplicar(final BigDecimal multiplicando, final Long multiplicador) {
		return multiplicar(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal multiplicar(final BigDecimal multiplicando, final String multiplicador) {
		return multiplicar(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final BigDecimal multiplicador) {
		return divide(multiplicar(multiplicando, multiplicador), UtilsMath.porcentaje);
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final BigDecimal multiplicador,
			final Integer divisor) {
		return divide(multiplicar(multiplicando, multiplicador), divisor);
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final Integer multiplicador) {
		return multiplicarDivide(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final Integer multiplicador,
			final BigDecimal divisor) {
		return divide(multiplicar(multiplicando, multiplicador), divisor);
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final Long multiplicador) {
		return multiplicarDivide(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal multiplicarDivide(final BigDecimal multiplicando, final String multiplicador) {
		return multiplicarDivide(multiplicando, new BigDecimal(multiplicador));
	}

	public static BigDecimal newBigDecimal() {
		return redondear("0.00");
	}

	public static BigDecimal newBigDecimal(final Integer valor) {
		return redondear(valor);
	}

	public static BigDecimal newBigDecimal(final String valor) {
		return redondear(valor);
	}

	public static BigDecimal parse(final Object object) {
		return redondear(String.valueOf(object));
	}

	public static BigDecimal redondear(final BigDecimal numero) {
		return numero.setScale(2, 6);
	}

	public static BigDecimal redondear(final Integer numero) {
		return redondear(new BigDecimal(numero));
	}

	public static BigDecimal redondear(final Long numero) {
		return redondear(new BigDecimal(numero));
	}

	public static BigDecimal redondear(final String numero) {
		return redondear(new BigDecimal(numero));
	}

	public static BigDecimal redondearCalificacion(final BigDecimal numero) {
		final BigDecimal cal = numero.setScale(0, RoundingMode.HALF_UP);
		if (numero.intValue() == cal.intValue()) {
			return numero;
		}
		return cal;
	}

	public static BigDecimal redondearEscala(final BigDecimal numero, final int escala) {
		return numero.setScale(escala, 6);
	}

	public static BigDecimal redondearTotales(final BigDecimal numero) {
		return redondearEscala(numero, 2);
	}

	public static String redondearTotalS(final BigDecimal numero) {
		return String.valueOf(redondearEscala(numero, 2));
	}

	public static String redondearTotalS(final String numero) {
		return String.valueOf(redondearEscala(new BigDecimal(numero), 2));
	}

	public static String rellenarCeros(final BigDecimal numero) {
		final DecimalFormat df = new DecimalFormat("###0.00");
		return df.format(numero).replace(",", ".");
	}

	public static String string(final BigDecimal numero) {
		return String.valueOf(numero);
	}
}