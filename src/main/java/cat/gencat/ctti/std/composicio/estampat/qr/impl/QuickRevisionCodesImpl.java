package cat.gencat.ctti.std.composicio.estampat.qr.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import cat.gencat.ctti.std.composicio.estampat.qr.QuickRevisionCodes;

/**
 * 
 * @author CS Canig�
 *
 */
@Component
public class QuickRevisionCodesImpl implements QuickRevisionCodes{
 
	private static final Logger logger = LoggerFactory.getLogger(QuickRevisionCodesImpl.class);
	
	public byte[] qrGeneration(String nom, String tipus) {
		
		ByteArrayOutputStream sortida = null;
		
		try {
			int width = 0;
			int height = 0;
			
			if (Integer.parseInt(tipus) == 0) {
				width = 50; height = 50;
			} else if (Integer.parseInt(tipus) == 1) {
				width = 100; height = 100;
			} else if (Integer.parseInt(tipus) == 2) {
				width = 200; height = 200;
			} else if (Integer.parseInt(tipus) == 3) {
				width = 300; height = 300;
			} else {
				width = 50; height = 50;
			}
			
			sortida = new ByteArrayOutputStream();
			
			String imageFormat = "png";
			BitMatrix bitMatrix = new QRCodeWriter().encode(nom, BarcodeFormat.QR_CODE, width, height);  
    		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, sortida);
    		
    		return sortida.toByteArray();
		} catch (NumberFormatException nfe) {
			logger.error("ERROR al generar el codi QR, opci� no suportada ",nfe);
			return null;
		} catch (IOException ioe) {
			logger.error("ERROR al generar el codi QR",ioe);
			return null;
		} catch (Exception e) {
			logger.error("ERROR al generar el codi QR",e);
			return null;
		} 
	}
}
