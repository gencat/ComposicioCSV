/**
 * 
 */
package cat.gencat.ctti.std.composicio.fusionat;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;

import cat.gencat.ctti.std.composicio.fusionat.exceptions.FusionatException;


/**
 * @author CSCanig�
 * 
 */
public interface FusionatPDF {
	 
	/**
	 * Fusiona el document PDF importat a importedPage amb el contingut del document PDF contentByte
	 * 
	 * @param contentByte
	 * @param importedPage
	 * @param opacitat
	 * @throws FusionatException
	 */
	public void fusioPDF(PdfContentByte contentByte, PdfImportedPage importedPage, float opacitat) 
		throws FusionatException;
	
}
