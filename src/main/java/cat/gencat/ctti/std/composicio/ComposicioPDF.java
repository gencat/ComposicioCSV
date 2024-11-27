/**
 * 
 */
package cat.gencat.ctti.std.composicio;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.forms.webservice.ResultAnnexos;

/**
 * @author A163617
 * 
 */
public interface ComposicioPDF {

	public static final String CLAU_AUTOR_PROPIETATS = "autorDocument";
	public static final String CLAU_TITOL_PROPIETATS = "titolDocument";
	public static final String CLAU_ASSUMPTE_PROPIETATS = "assumpteDocument";
	public static final String CLAU_PARAULES_CLAU_PROPIETATS = "paraulesClauDocument";

	/**
	 * Metode composicio pdf llegint la informacio des dels inputs streams i
	 * escrivint al output stream
	 * 
	 * @param inputStream
	 * @param inputStreamPlantilla
	 * @param inputStreamPlantillaMarcaAigua
	 * @param inputStreamPlantillaHoritzontal
	 * @param inputStreamPlantillaHoritzontalMarcaAigua
	 * @param porcentatgeEscalat
	 * @param posicioEscalatX
	 * @param posicioEscalatY
	 * @param parametresString
	 * @param parametresImatge
	 * @param annexos
	 * @param outputStream
	 * @throws ComposicioPDFException
	 */
	public void composarPDF(InputStream inputStream, InputStream inputStreamPlantilla,
			InputStream inputStreamPlantillaMarcaAigua, InputStream inputStreamPlantillaHoritzontal,
			InputStream inputStreamPlantillaHoritzontalMarcaAigua, Float porcentatgeEscalat, Float posicioEscalatX,
			Float posicioEscalatY, Map<String, StringSegell> parametresString,
			Map<String, ImatgeSegell> parametresImatge, ResultAnnexos annexos, OutputStream outputStream)
			throws ComposicioPDFException;

}
