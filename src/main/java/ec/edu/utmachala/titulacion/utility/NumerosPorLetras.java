package ec.edu.utmachala.titulacion.utility;

import java.math.BigDecimal;

public class NumerosPorLetras {
	private static final String[] UNIDADES;
	private static final String[] DECENAS;
	private static final String[] CENTENAS;

	static {
		UNIDADES = new String[] { "", "\u00daN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ",
				"NUEVE ", "DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECIS\u00c9IS", "DIECISIETE",
				"DIECIOCHO", "DIECINUEVE", "VEINTE" };
		DECENAS = new String[] { "VENTI", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ",
				"NOVENTA ", "CIEN " };
		CENTENAS = new String[] { "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ",
				"SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS " };
	}

	private static String convertNumber(final int number) {
		final String num = String.valueOf(number);
		if (num.length() > 3) {
			throw new NumberFormatException("La longitud maxima debe ser 3 digitos");
		}
		if (num.equals("100")) {
			return "CIEN";
		}
		final StringBuilder output = new StringBuilder();
		if (getDigitAt(num, 2) != 0) {
			output.append(NumerosPorLetras.CENTENAS[getDigitAt(num, 2) - 1]);
		}
		final int k = Integer.parseInt(String.valueOf(getDigitAt(num, 1)) + String.valueOf(getDigitAt(num, 0)));
		if (k <= 20) {
			output.append(NumerosPorLetras.UNIDADES[k]);
		} else if (k > 30 && getDigitAt(num, 0) != 0) {
			output.append(NumerosPorLetras.DECENAS[getDigitAt(num, 1) - 2] + "Y "
					+ NumerosPorLetras.UNIDADES[getDigitAt(num, 0)]);
		} else {
			output.append(
					NumerosPorLetras.DECENAS[getDigitAt(num, 1) - 2] + NumerosPorLetras.UNIDADES[getDigitAt(num, 0)]);
		}
		return output.toString();
	}

	public static String convertNumberToLetter(BigDecimal number) throws NumberFormatException {
		final StringBuilder converted = new StringBuilder();
		number = number.setScale(2, 6);
		if (number.compareTo(new BigDecimal("999999999")) > 0) {
			throw new NumberFormatException("El numero es mayor de 999'999.999, no es posible convertirlo");
		}
		if (number.compareTo(new BigDecimal("0")) < 0) {
			throw new NumberFormatException("El numero debe ser positivo");
		}
		final String[] splitNumber = String.valueOf(number).replace('.', '#').split("#");
		final int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 8))
				+ String.valueOf(getDigitAt(splitNumber[0], 7)) + String.valueOf(getDigitAt(splitNumber[0], 6)));
		if (millon == 1) {
			converted.append("UN MILLON ");
		} else if (millon > 1) {
			converted.append(convertNumber(millon) + "MILLONES ");
		}
		final int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 5))
				+ String.valueOf(getDigitAt(splitNumber[0], 4)) + String.valueOf(getDigitAt(splitNumber[0], 3)));
		if (miles == 1) {
			converted.append("MIL ");
		} else if (miles > 1) {
			converted.append(convertNumber(miles) + "MIL ");
		}
		final int cientos = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0], 2))
				+ String.valueOf(getDigitAt(splitNumber[0], 1)) + String.valueOf(getDigitAt(splitNumber[0], 0)));
		if (cientos == 1) {
			converted.append("UN");
		}
		if (millon + miles + cientos == 0) {
			converted.append("CERO");
		}
		if (cientos > 1) {
			converted.append(convertNumber(cientos));
		}
		final int centavos = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[1], 2))
				+ String.valueOf(getDigitAt(splitNumber[1], 1)) + String.valueOf(getDigitAt(splitNumber[1], 0)));
		if (centavos == 1) {
			converted.append(" CON UN CENTAVO");
		} else if (centavos > 1) {
			converted.append(" CON " + convertNumber(centavos));
		}
		return converted.toString();
	}

	private static int getDigitAt(final String origin, final int position) {
		if (origin.length() > position && position >= 0) {
			return origin.charAt(origin.length() - position - 1) - '0';
		}
		return 0;
	}
}