package cat.gencat.ctti.std.composicio.annexos;

import java.io.InputStream;

import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.forms.webservice.ResultAnnexos;

public interface AnnexosPDF {
	
	/**
	 * Metode que retorna els annexos si en conte o null sino en conte
	 * @param config
	 * @param in
	 * @return
	 * @throws ComposicioPDFException
	 */
	ResultAnnexos getAnnexos(ConfigCall config, InputStream inputStream) throws ComposicioPDFException;

}
