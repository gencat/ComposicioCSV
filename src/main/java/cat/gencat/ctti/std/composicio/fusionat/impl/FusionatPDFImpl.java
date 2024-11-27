/**
 * 
 */
package cat.gencat.ctti.std.composicio.fusionat.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfImportedPage;

import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.fusionat.FusionatPDF;
import cat.gencat.ctti.std.composicio.fusionat.exceptions.FusionatException;


/**
 * @author CSCanig�
 * 
 */
@Component
public class FusionatPDFImpl implements FusionatPDF{
	 
	private static final Logger logger = LoggerFactory.getLogger(FusionatPDFImpl.class);
	
	public void fusioPDF(PdfContentByte contentByte, PdfImportedPage importedPage, float opacitat) 
		throws FusionatException{
		logger.debug("[FusionatPDF][fusioPDF] Inici operaci�");
		
		//Es comprova que les dades passades siguin correctes
		comprovarContentByte(contentByte);
		comprovarImportedPage(importedPage);
		comprovarOpacitat(opacitat);
		
		contentByte.saveState();
		PdfGState gState = new PdfGState();
		gState.setFillOpacity(opacitat);
        contentByte.setGState(gState);
		contentByte.addTemplate(importedPage, 0, 0);
		contentByte.restoreState();
		
		logger.debug("[FusionatPDF][fusioPDF] Fi operaci�");
	}

	/**
	 * @param opacitat
	 * @throws FusionatException
	 */
	private void comprovarOpacitat(float opacitat) throws FusionatException {
		if(opacitat<0 || opacitat>1){
			logger.error("ERROR la opacitat esta fora de rang");
			throw new FusionatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FUSIONANT_DOCUMENT, "ERROR la opacitat esta fora de rang");
		}
	}

	/**
	 * @param importedPage
	 * @throws FusionatException 
	 */
	private void comprovarImportedPage(PdfImportedPage importedPage) throws FusionatException {
		if(importedPage==null){
			logger.error("ERROR la p�gina a escalar no est� definida");
			throw new FusionatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FUSIONANT_DOCUMENT, "ERROR la p�gina a escalar no est� definida");
		}
	}

	/**
	 * @param contentByte
	 * @throws FusionatException 
	 */
	private void comprovarContentByte(PdfContentByte contentByte) throws FusionatException {
		if(contentByte==null){
			logger.error("ERROR contingut a insertar la p�gina escalada no est� definit");
			throw new FusionatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FUSIONANT_DOCUMENT, "ERROR contingut a insertar la p�gina escalada no est� definit");
		}
	}
	
}
