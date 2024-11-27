package cat.gencat.ctti.std.composicio.escalat.impl;

import java.awt.geom.AffineTransform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.escalat.EscalatPDF;
import cat.gencat.ctti.std.composicio.escalat.exceptions.EscalatException;
 
/**
 * 
 * @author CSCanig�
 *
 */
@Component
public class EscalatPDFImpl implements EscalatPDF{
	
	private static final Logger logger = LoggerFactory.getLogger(EscalatPDFImpl.class);
	
	public void escalarPDF(PdfContentByte contentByte, PdfImportedPage importedPage, 
			float percentageEscalat_X, float percentageEscalat_Y, float posicioEscalat[], PdfReader pdfReader, int page) throws EscalatException{
//		logger.debug("INICI escalarPDF");
		
		//Es comprova que les dades passades siguin correctes
		comprovarContentByte(contentByte);
		comprovarImportedPage(importedPage);
		comprovarPorcentatge(percentageEscalat_X);
		comprovarPorcentatge(percentageEscalat_X);
		comprovarPosicio(posicioEscalat);
		
		int pageRotation = pdfReader.getPageRotation(page);
		
		float a = 1;

		float d = 1;
		float e = 0;
		float f = 0;

		/*
		 * Matriu		Matriu escalat	matriu moure    matriu rotació
		 * | a b 0 |	| a 0 0 |		| 1 0 0 |       | a b 0 |
		 * | c d 0 |	| 0 d 0 |		| 0 1 0 |		| c d 0 |
		 * | e f 1 |	| 0 0 1 |		| e f 1 |		| 0 0 1 |
		 */
		
		if (percentageEscalat_X != 0) {
			//es mante la proporcionalitat
			a = percentageEscalat_X;
		}
		if (percentageEscalat_Y != 0) {
			//es mante la proporcionalitat
			d = percentageEscalat_Y;
		}
		
		
		if(posicioEscalat[0]!=0 || posicioEscalat[1]!=0) {
			// numero de pixels a moure a l'eix hortizonal des de baix esquerra
			e = posicioEscalat[0];
			// numero de pixels a moure a l'eix vertical des de baix esquerra
			f = posicioEscalat[1];
		}
		
		AffineTransform af = new AffineTransform();
		
		//Rotation
		double rotation = 0;
		if (pageRotation > 0){
			//Translaciço + Rotació
			switch (pageRotation)
			{
			    case 90:
			    	rotation = Math.PI / 2;
			    	af.translate(e, posicioEscalat[3]);
			    	af.rotate(rotation);
			        break;
	
			    case 180:
			    	rotation = Math.PI;
			    	af.translate(posicioEscalat[2], posicioEscalat[3]);
			    	af.rotate(rotation);
			        break;
	
			    case 270:
			    	rotation = Math.PI * 3 / 2;
			    	af.translate(posicioEscalat[2], f);
			    	af.rotate(rotation);
			        break;
			        
			    default:
			    	break;
			    	
			}
		}else{
			//Translation
			af.translate(e, f);
		}
		
		// Scale
		af.scale(a, d);
		
		//Fer la fusió
    	contentByte.addTemplate(importedPage, (float)af.getScaleX(),  (float)af.getShearX(),  (float)af.getShearY(),  (float)af.getScaleY(),  (float)af.getTranslateX(),  (float)af.getTranslateY());
		
		
//		logger.debug("FI escalarPDF");
	}
	
	/**
	 * @param posicioEscalat
	 * @throws EscalatException 
	 */
	private void comprovarPosicio(float posicioEscalat[]) throws EscalatException {
		if (posicioEscalat == null) {
			logger.error("ERROR posicioEscalat null");
			throw new EscalatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESCALANT_DOCUMENT, "ERROR posicioEscalat null");
		}
	}

	/**
	 * @param porcentageEscalat
	 * @throws EscalatException 
	 */
	private void comprovarPorcentatge(float porcentageEscalat) throws EscalatException {
		if (porcentageEscalat < 0 || porcentageEscalat > 1) {
			logger.error("ERROR porcentageEscalat fora de rang");
			throw new EscalatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESCALANT_DOCUMENT, "ERROR porcentageEscalat fora de rang");
		}
	}

	/**
	 * @param importedPage
	 * @throws EscalatException 
	 */
	private void comprovarImportedPage(PdfImportedPage importedPage) throws EscalatException {
		if(importedPage==null){
			logger.error("ERROR la p�gina a escalar no est� definida");
			throw new EscalatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESCALANT_DOCUMENT, "ERROR la p�gina a escalar no est� definida");
		}
	}

	/**
	 * @param contentByte
	 * @throws EscalatException 
	 */
	private void comprovarContentByte(PdfContentByte contentByte) throws EscalatException {
		if(contentByte==null){
			logger.error("ERROR contingut a insertar la p�gina escalada no est� definit");
			throw new EscalatException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_ESCALANT_DOCUMENT, "ERROR contingut a insertar la p�gina escalada no est� definit");
		}
	}

}