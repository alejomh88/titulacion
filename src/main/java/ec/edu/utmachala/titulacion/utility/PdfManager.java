package ec.edu.utmachala.titulacion.utility;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfManager {
	private PDFParser parser;
	private PDFTextStripper pdfStripper;
	private PDDocument pdDoc;
	private COSDocument cosDoc;
	private String Text;
	private String filePath;
	private File file;

	public void setFilePath(final String filePath) {
		this.filePath = filePath;
		System.out.println("Desde el manager: " + filePath);
	}

	public String ToText() throws IOException {
		this.pdfStripper = null;
		this.pdDoc = null;
		this.cosDoc = null;
		System.out.println("Desde el manager: " + this.filePath);
		this.file = new File(this.filePath);
		(this.parser = new PDFParser((RandomAccessRead) new RandomAccessFile(this.file, "r"))).parse();
		this.cosDoc = this.parser.getDocument();
		this.pdfStripper = new PDFTextStripper();
		(this.pdDoc = new PDDocument(this.cosDoc)).getNumberOfPages();
		this.pdfStripper.setStartPage(1);
		this.pdfStripper.setEndPage(this.pdDoc.getNumberOfPages());
		return this.Text = this.pdfStripper.getText(this.pdDoc);
	}
}