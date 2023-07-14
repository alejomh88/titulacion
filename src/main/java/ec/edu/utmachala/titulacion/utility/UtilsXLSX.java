package ec.edu.utmachala.titulacion.utility;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UtilsXLSX {
	public static void crearXLSX(final List<String> list, final String namesheet, final String filename,
			final String UA, final String C, final String tituloRepor, int numCol) {
		try {
			final XSSFWorkbook wb = new XSSFWorkbook();
			final XSSFSheet sheet = wb.createSheet(namesheet);
			final XSSFCellStyle styleCabecera = wb.createCellStyle();
			final XSSFFont fontCabecera = wb.createFont();
			fontCabecera.setFontHeightInPoints((short) 12);
			fontCabecera.setFontName("Arial");
			fontCabecera.setBoldweight((short) 700);
			fontCabecera.setColor(IndexedColors.AUTOMATIC.getIndex());
			styleCabecera.setFont((Font) fontCabecera);
			styleCabecera.setAlignment((short) 2);
			--numCol;
			XSSFRow row = sheet.createRow(0);
			createCell(wb, row, 0, "SUNIVERSIDAD T\u00c9CNICA DE MACHALA", styleCabecera);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, numCol));
			row = sheet.createRow(1);
			createCell(wb, row, 0, "S" + UA, styleCabecera);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, numCol));
			row = sheet.createRow(2);
			createCell(wb, row, 0, "SCARRERA DE " + C, styleCabecera);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, numCol));
			row = sheet.createRow(3);
			createCell(wb, row, 0, "S" + tituloRepor, styleCabecera);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, numCol));
			final XSSFCellStyle styleCuerpo = wb.createCellStyle();
			final XSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11);
			font.setFontName("Arial");
			font.setBoldweight((short) 400);
			font.setColor(IndexedColors.AUTOMATIC.getIndex());
			styleCuerpo.setFont((Font) font);
			for (int i = 0; i < list.size(); ++i) {
				row = sheet.createRow(i + 5);
				final String[] as = list.get(i).split("¬");
				for (int col = 0; col < as.length; ++col) {
					createCell(wb, row, col, as[col], styleCuerpo);
				}
				sheet.autoSizeColumn(i + 5);
			}
			final FileOutputStream fileOut = new FileOutputStream(filename + ".xlsx");
			wb.write((OutputStream) fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createCell(final XSSFWorkbook wb, final XSSFRow row, final int col, final String value,
			final XSSFCellStyle style) {
		final XSSFCell cell = row.createCell(col);
		if (value.charAt(0) == 'I' || value.charAt(0) == 'D') {
			if (value.charAt(0) == 'I') {
				style.setDataFormat(wb.createDataFormat().getFormat("0"));
			} else if (value.charAt(0) == 'D') {
				style.setDataFormat(wb.createDataFormat().getFormat("#,##0.00"));
			}
			cell.setCellStyle((CellStyle) style);
			cell.setCellType(0);
			cell.setCellValue((double) new Double(value.substring(1).trim()));
		} else if (value.charAt(0) == 'S') {
			style.setDataFormat(wb.createDataFormat().getFormat("text"));
			cell.setCellStyle((CellStyle) style);
			cell.setCellType(1);
			cell.setCellValue((RichTextString) new XSSFRichTextString(value.substring(1).trim()));
		}
	}
}
