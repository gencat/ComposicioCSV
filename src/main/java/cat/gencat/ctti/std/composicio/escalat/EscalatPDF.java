package cat.gencat.ctti.std.composicio.escalat;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import cat.gencat.ctti.std.composicio.escalat.exceptions.EscalatException;
 
/**
 * 
 * @author CSCanig�
 *
 */
public interface EscalatPDF {
	
	/**
	 * 
	 * @param contentByte
	 * @param importedPage
	 * @param porcentageEscalat_X
	 * @param porcentageEscalat_Y
	 * @param posicioEscalat
	 * @param pdfReader
	 * @param page
	 * @throws EscalatException
	 */
	public void escalarPDF(PdfContentByte contentByte, PdfImportedPage importedPage, 
			float percentageEscalat_X, float percentageEscalat_Y, float posicioEscalat[], PdfReader pdfReader, int page) throws EscalatException;

}