/**
 * 
 */
package cat.gencat.ctti.std.csv;

import cat.gencat.ctti.std.dto.ConfigCall;

/**
 * @author A163617
 * 
 */
public interface CodiSegurVerificacio {

	/**
	 * Metode que genera un codi segur verfiicacio (CSV)
	 * 
	 * @param config
	 * @param data
	 * @param key
	 * @param timeStamp
	 * @return
	 * @throws Exception
	 */
	public String generarCSV(ConfigCall config, byte[] data, String key, String timeStamp) throws Exception;

}
