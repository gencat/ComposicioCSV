/**
 * 
 */
package cat.gencat.ctti.std.csv.services.impl;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cat.gencat.ctti.canigo.arch.operation.instrumentation.live.annotation.LiveInstrumentation;
import cat.gencat.ctti.canigo.arch.operation.instrumentation.trace.annotation.Trace;
import cat.gencat.ctti.std.csv.CodiSegurVerificacio;
import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioConstants;
import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioErrorsConstants;
import cat.gencat.ctti.std.csv.dto.GenerarCSV;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.csv.services.CodiSegurVerificacioService;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;
import cat.gencat.ctti.std.validacio.STDValidacio;
import cat.gencat.ctti.std.validacio.exception.STDValidacioException;

/**
 * @author A163617
 * 
 */
@Component
public class CodiSegurVerificacioServiceImpl implements CodiSegurVerificacioService {

	private final Logger logger = LoggerFactory.getLogger(CodiSegurVerificacioServiceImpl.class);

	private final String LOG_INICI_OPERACIO = "Inici Operacio";
	private final String LOG_FINAL_OPERACIO = "Final Operacio";
	
	public static final String TXT_GENERAR_CSV = "[CodiSegurVerificacioServiceImpl][generarCSV] ";
	public static final String TXT_GENERAR_CSV_TIMESTAMP = "[CodiSegurVerificacioServiceImpl][generarCSVTimeStamp] ";

	@Autowired
	private CodiSegurVerificacio codiSegurVerificacio;
	@Autowired
	private STDValidacio sTDValidacio;

	@Value("${" + CodiSegurVerificacioConstants.CLAU_PROPIETAT_MIDA_MAX_CSV + "}")
	String midaMaxCSV;

	@Override
	@Trace
	@LiveInstrumentation
	public ResultSTD generarCSV(ConfigCall config, GenerarCSV generarCSV)
			throws CodiSegurVerificacioException {
		ResultSTD result = new ResultSTD();
		logger.debug(TXT_GENERAR_CSV + LOG_INICI_OPERACIO);
		try {

			validaGenerarCSV(config, generarCSV);

			String timeStamp = getTimeStampFormatCSV();
			result.setKey(codiSegurVerificacio.generarCSV(config, generarCSV.getData(),
					generarCSV.getKey(), timeStamp));
			result.setTimeStamp(timeStamp);
			result.setStatus(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_NO_ERROR);

		} catch (CodiSegurVerificacioException ce) {
			throw ce;
		} catch (Exception ex) {
			logger.error(TXT_GENERAR_CSV
					+ CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
			throw new CodiSegurVerificacioException(ex, CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
		} finally {
			logger.debug(TXT_GENERAR_CSV + LOG_FINAL_OPERACIO);
		}

		return result;
	}

	@Override
	@Trace
	@LiveInstrumentation
	public ResultSTD generarCSVTimeStamp(ConfigCall config,
			GenerarCSVTimeStamp generarCSVTimeStamp) throws CodiSegurVerificacioException {
		ResultSTD result = new ResultSTD();
		logger.debug(TXT_GENERAR_CSV_TIMESTAMP + LOG_INICI_OPERACIO);
		try {

			validaGenerarCSVTimeStamp(config, generarCSVTimeStamp);

			result.setKey(codiSegurVerificacio.generarCSV(config,
					generarCSVTimeStamp.getData(), generarCSVTimeStamp.getKey(),
					generarCSVTimeStamp.getTimeStamp()));
			result.setTimeStamp(generarCSVTimeStamp.getTimeStamp());
			result.setStatus(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_NO_ERROR);

		} catch (CodiSegurVerificacioException ce) {
			throw ce;
		} catch (Exception ex) {
			logger.error(TXT_GENERAR_CSV_TIMESTAMP
					+ CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
			throw new CodiSegurVerificacioException(ex, CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
		} finally {
			logger.debug(TXT_GENERAR_CSV_TIMESTAMP + LOG_FINAL_OPERACIO);
		}

		return result;
	}

	private void validaGenerarCSVTimeStamp(ConfigCall config,
			GenerarCSVTimeStamp generarCSVTimeStamp) throws CodiSegurVerificacioException {
		validaGenerarCSV(config, generarCSVTimeStamp);

		// Validacio que el timestampt esta informat
		validaTimeStamp(config, generarCSVTimeStamp);
	}

	private void validaTimeStamp(ConfigCall config, GenerarCSVTimeStamp generarCSVTimeStamp)
			throws CodiSegurVerificacioException {
		if (generarCSVTimeStamp.getTimeStamp() == null) {
			logger.error("[CodiSegurVerificacioServiceImpl][validaTimeStamp] ERROR timeStamp ve a null");
			throw new CodiSegurVerificacioException(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
		}
	}

	@Override
	public String getTimeStampFormatCSV() {
		logger.debug("[CodiSegurVerificacioServiceImpl][getDateFormatCSV] Generacio del TimeStamp...");
		java.util.Date dataActual = new java.util.Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		formato.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
		return formato.format(dataActual);
	}

	private void validaGenerarCSV(ConfigCall config, GenerarCSV generarCSV)
			throws CodiSegurVerificacioException {
		try {
			// Validacio Ambit/aplicacio informats (tot i que no es facin servir
			// cal informar-ho de cara als logs d'instrumentacio)
			sTDValidacio.validaAmbitAplicacioInformats(config);

			// Validacio Ambit/aplicacio existents al sistema
			sTDValidacio.validaAmbitAplicacioValids(config);

			// Validacio que l'objecte que conté la informacio esta informat
			validaGenerarCSV(generarCSV);

			// valida la mida maxima
			sTDValidacio.validaMidaMax(generarCSV.getData(), midaMaxCSV);
		} catch (STDValidacioException ex) {
			throw new CodiSegurVerificacioException(ex, ex.getCodiError(), ex.getMessage());
		}
	}

	private void validaGenerarCSV(GenerarCSV generarCSV)
			throws CodiSegurVerificacioException, STDValidacioException {
		if (generarCSV == null) {
			logger.error(
					"[CodiSegurVerificacioServiceImpl][validaGenerarCSV] ERROR generarCSV ve a null");
			throw new CodiSegurVerificacioException(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
		}

		// Validacio de la data informda
		sTDValidacio.validaDataInformada(generarCSV.getData());

		// Validacio key
		if (generarCSV.getKey() == null) {
			logger.error("[CodiSegurVerificacioServiceImpl][validaGenerarCSV] ERROR key ve a null");
			throw new CodiSegurVerificacioException(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT,
					CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT);
		}

	}

}
