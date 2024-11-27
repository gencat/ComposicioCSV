/**
 * 
 */
package cat.gencat.ctti.std.composicio.estampat;

import java.util.Map;

import com.itextpdf.text.pdf.PdfStamper;

import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.estampat.exceptions.EstampatException;

/**
 * @author CSCanig�
 * 
 */
public interface EstampatPDF {
 

	/**
	 * @param stampPlantilla
	 * @param parametresString
	 * @param parametresImatge
	 * @param numeroPaginaActual
	 * @param numeroPaginesTotal
	 * @throws EstampatException
	 */
	public void estamparPDF(PdfStamper stampPlantilla,
			Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge, int numeroPaginaActual, int numeroPaginesTotal) 
			throws EstampatException;

}
