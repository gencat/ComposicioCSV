/**
 * 
 */
package cat.gencat.ctti.std.csv.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cat.gencat.ctti.std.csv.CodiSegurVerificacio;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.std.csv.CSV;

/**
 * @author A163617
 * 
 */
@Component
public class CodiSegurVerificacioImpl implements CodiSegurVerificacio {

	private final Logger logger = LoggerFactory.getLogger(CodiSegurVerificacioImpl.class);

	private final String LOG_INICI_OPERACIO = "Inici Operacio";
	private final String LOG_FINAL_OPERACIO = "Final Operacio";
	

	public String generarCSV(ConfigCall config, byte[] data, String key, String timeStamp) throws Exception {
		logger.debug("[CodiSegurVerificacioImpl][generarCSV] " + LOG_INICI_OPERACIO);
		// Obtenci� dels bytes que representen el timeStamp
		byte[] arrayDataTimeStamp = timeStamp.getBytes();
		byte[] both = ArrayUtils.addAll(data, arrayDataTimeStamp);
		String csv = CSV.genCodiByte(both, key);
		logger.debug("[CodiSegurVerificacioImpl][generarCSV] " + LOG_FINAL_OPERACIO);
		return csv;
	}
	
}

