/**
 * 
 */
package cat.gencat.ctti.std.csv.services;

import cat.gencat.ctti.std.csv.dto.GenerarCSV;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;

/**
 * @author A163617
 * 
 */
public interface CodiSegurVerificacioService {

	/**
	 * Metode que genera un codi segur verfiicacio (CSV)
	 * 
	 * @param config
	 * @param generarCSV
	 * @return
	 * @throws CodiSegurVerificacioException
	 */
	public ResultSTD generarCSV(ConfigCall config, GenerarCSV generarCSV) throws CodiSegurVerificacioException;
	
	/**
	 * Metode que genera un codi segur verfiicacio (CSV) amb un timeStamp ja donat
	 * 
	 * @param config
	 * @param generarCSVTimeStamp
	 * @return
	 * @throws CodiSegurVerificacioException
	 */
	public ResultSTD generarCSVTimeStamp(ConfigCall config, GenerarCSVTimeStamp generarCSVTimeStamp) throws CodiSegurVerificacioException;

	/**
	 * Retorna el TimeStamp en format pel CSV
	 * 
	 * @return
	 */
	public String getTimeStampFormatCSV();

}
