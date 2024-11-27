package cat.gencat.ctti.std.composicio.aplanat;

import java.io.InputStream;

import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ConfigCall;

public interface AplanatPDF {
	
	/**
	 * 
	 * @param config
	 * @param in
	 * @return
	 * @throws ComposicioPDFException
	 */
	public InputStream aplanarPDF(ConfigCall config, InputStream in) throws ComposicioPDFException;

}
