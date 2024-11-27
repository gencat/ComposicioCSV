/**
 * 
 */
package cat.gencat.ctti.std.composicio.services;

import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;

/**
 * @author A163617
 * 
 */
public interface ComposicioPDFService {

	/**
	 * Metode composicio pdf des d'un arxiu remot a partir del seu nom de fitxer
	 * 
	 * @param config
	 * @param composarPDFRemot
	 * @return
	 */
	public ResultSTD composarPDFRemot(ConfigCall config, ComposarPDFRemot composarPDFRemot) throws ComposicioPDFException;

	/**
	 * Metode composicio pdf des d'un arxiu enviat per stream
	 * 
	 * @param config
	 * @param composarPDFStream
	 * @return
	 */
	public ResultSTD composarPDFStream(ConfigCall config, ComposarPDFStream composarPDFStream)  throws ComposicioPDFException;


}
