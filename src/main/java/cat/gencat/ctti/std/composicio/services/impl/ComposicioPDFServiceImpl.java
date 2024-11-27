/**
 * 
 */
package cat.gencat.ctti.std.composicio.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cat.gencat.ctti.std.csv.utils.TraceUtils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import cat.gencat.ctti.canigo.arch.operation.instrumentation.live.annotation.LiveInstrumentation;
import cat.gencat.ctti.canigo.arch.operation.instrumentation.trace.annotation.Trace;
import cat.gencat.ctti.std.composicio.ComposicioPDF;
import cat.gencat.ctti.std.composicio.annexos.AnnexosPDF;
import cat.gencat.ctti.std.composicio.aplanat.AplanatPDF;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFConstants;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.composicio.estampat.dto.ImatgeSegell;
import cat.gencat.ctti.std.composicio.estampat.dto.StringSegell;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.composicio.services.ComposicioPDFService;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.csv.services.CodiSegurVerificacioService;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.dto.ResultSTD;
import cat.gencat.ctti.std.utils.FileBufferedInputStream;
import cat.gencat.ctti.std.utils.FileBufferedOutputStream;
import cat.gencat.ctti.std.utils.STDUtils;
import cat.gencat.ctti.std.validacio.STDValidacio;
import cat.gencat.ctti.std.validacio.exception.STDValidacioException;
import cat.gencat.forms.webservice.ResultAnnexos;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

/**
 * @author A163617
 * 
 */
@Component
public class ComposicioPDFServiceImpl implements ComposicioPDFService {

	private final Logger logger = LoggerFactory.getLogger(ComposicioPDFServiceImpl.class);

	private final String LOG_INICI_OPERACIO = "Inici Operacio";
	private final String LOG_FINAL_OPERACIO = "Final Operacio";

	public static final String TXT_COMPOSAR_REMOT = "[ComposicioPDFImpl][composarPDFRemot] ";
	public static final String TXT_COMPOSAR_STREAM = "[ComposicioPDFImpl][composarPDFStream] ";
	public static final String TXT_COMPOSAR_PDF = "[ComposicioPDFImpl][composarPDF] ";
	public static final String TXT_NO_EXISTEIX = " no existeix";
	public static final String TXT_LA_PLANTILLA = "La plantilla ";
	public static final String ERROR_LECTURA_PLANTILLA = "ERROR de lectura de la plantilla marca d'aigua associada a la plantilla ";

	@Autowired
	private ComposicioPDF composicioPDF;
	@Autowired
	private AplanatPDF aplanatPDF;
	@Autowired
	private AnnexosPDF annexosPDF;
	@Autowired
	private STDValidacio sTDValidacio;
	@Autowired
	private CodiSegurVerificacioService codiSegurVerificacioService;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_INICIAL + "}")
	private String pathInicial;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_ENTRADA_COMPOSICIO + "}")
	private String pathFitxerEntrada;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_SORTIDA_COMPOSICIO + "}")
	private String pathFitxerSortida;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_PATH_FITXER_PLANTILLA_COMPOSICIO + "}")
	private String pathFitxerPlantilla;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_MIDA_MAX_COMPOSICIO_REMOT + "}")
	private String midaMaxPDFRemot;
	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_MIDA_MAX_COMPOSICIO_STREAM + "}")
	private String midaMaxPDFStream;

	@Value("${" + ComposicioPDFConstants.CLAU_PROPIETAT_LIMIT_NUMERO_FITXERS_MATEIX_NOM + "}")
	private int limitNumeroFitxersMateixNom;

	@Override
	@Trace
	@LiveInstrumentation
	public ResultSTD composarPDFRemot(ConfigCall config, ComposarPDFRemot composarPDFRemot)
			throws ComposicioPDFException {
		ResultSTD result = new ResultSTD();
		logger.debug(TXT_COMPOSAR_REMOT + LOG_INICI_OPERACIO);
		try {

			validaComposarPDFRemot(config, composarPDFRemot);

			String timeStampFormatCSV = codiSegurVerificacioService.getTimeStampFormatCSV();
			logger.debug("[ComposicioPDFImpl][composarPDFRemot] TimeStamp Generat: " + timeStampFormatCSV);

			try (InputStream doc = composarPDF(config,
					new FileBufferedInputStream(getFitxerEntrada(config, composarPDFRemot)),
					composarPDFRemot.getNomPlantilla(), composarPDFRemot.getPorcentatgeEscalat(),
					composarPDFRemot.getPosicioEscalatX(), composarPDFRemot.getPosicioEscalatY(),
					composarPDFRemot.getParametresString(), composarPDFRemot.getParametresImatge(),
					composarPDFRemot.isGenerarCSV(), timeStampFormatCSV);) {
				if (doc != null) {
					String pathNomFitxerSortida = writeInputToFile(config, composarPDFRemot.getNomFitxerSortida(), doc);

					result.setKey(pathNomFitxerSortida);
					if (composarPDFRemot.isGenerarCSV()) {
						result.setTimeStamp(timeStampFormatCSV);
					}

					boolean checkCSV = checkCSVIfNecessary(composarPDFRemot.getParametresString(),
							pathNomFitxerSortida);

					if (checkCSV) {
						result.setStatus(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_NO_ERROR);
						logger.debug(TXT_COMPOSAR_REMOT
								+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_NO_ERROR);
					} else {
						result.setStatus(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_CHECK_CSV);
						logger.debug(TXT_COMPOSAR_REMOT
								+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_CHECK_CSV);
						TraceUtils.writeTrace("collisions", "COL.LISIÓ DETECTADA!!!!!: " + pathNomFitxerSortida);

					}

				} else {
					logger.error(TXT_COMPOSAR_REMOT
							+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT);
					throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_REMOT,
							ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT);
				}
			}

		} catch (ComposicioPDFException ce) {
			logger.error(TXT_COMPOSAR_REMOT + ce.getLocalizedMessage(), ce);
			throw ce;
		} catch (Exception ex) {
			logger.error(TXT_COMPOSAR_REMOT
					+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT, ex);
			throw new ComposicioPDFException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_REMOT,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT);
		} finally {
			logger.debug(TXT_COMPOSAR_REMOT + LOG_FINAL_OPERACIO);
		}

		return result;
	}

	@Override
	@Trace
	@LiveInstrumentation
	public ResultSTD composarPDFStream(ConfigCall config, ComposarPDFStream composarPDFStream)
			throws ComposicioPDFException {

		ResultSTD result = new ResultSTD();

		logger.debug(TXT_COMPOSAR_STREAM + LOG_INICI_OPERACIO);
		try {

			validaComposarPDFStream(config, composarPDFStream);

			String timeStampFormatCSV = codiSegurVerificacioService.getTimeStampFormatCSV();

			logger.debug("[ComposicioPDFImpl][composarPDFStream] TimeStamp Generat: " +
					timeStampFormatCSV);

			try (InputStream doc = composarPDF(config,
					new FileBufferedInputStream(composarPDFStream.getInputStreamEntrada()),
					composarPDFStream.getNomPlantilla(),
					composarPDFStream.getPorcentatgeEscalat(),
					composarPDFStream.getPosicioEscalatX(),
					composarPDFStream.getPosicioEscalatY(),
					composarPDFStream.getParametresString(),
					composarPDFStream.getParametresImatge(),
					composarPDFStream.isGenerarCSV(), timeStampFormatCSV);) {
				if (doc != null) {
					result.setKey(ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_NO_ERROR);

					if (composarPDFStream.isGenerarCSV()) {
						result.setTimeStamp(timeStampFormatCSV);
					}
					result.setArxiu(IOUtils.toByteArray(doc));

					result.setStatus(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_NO_ERROR);
					logger.debug(TXT_COMPOSAR_STREAM
							+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_NO_ERROR);
				} else {
					logger.error(TXT_COMPOSAR_STREAM
							+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_STREAM);

					throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_STREAM,
							ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_STREAM);
				}
			}

		} catch (ComposicioPDFException ce) {
			logger.error(TXT_COMPOSAR_REMOT +
					ce.getLocalizedMessage(), ce);

			throw ce;

		} catch (Exception ex) {
			logger.error(TXT_COMPOSAR_STREAM
					+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_STREAM, ex);

			throw new ComposicioPDFException(ex,
					ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_STREAM,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_STREAM);

		} finally {
			logger.debug(TXT_COMPOSAR_STREAM + LOG_FINAL_OPERACIO);
		}

		return result;

	}

	// oarmadans: afegim el synchronized perque no doni error de que dos theads
	// determinin el mateix nom de fitxer
	private synchronized String writeInputToFile(ConfigCall config, String nomFitxerSortida, InputStream doc)
			throws IOException {
		String pathNomFitxerSortida = getPathNomFitxerSortida(config, nomFitxerSortida);
		STDUtils.writeInputToFile(doc, new File(pathNomFitxerSortida));
		return pathNomFitxerSortida;
	}

	private void validaComposarPDFRemot(ConfigCall config, ComposarPDFRemot composarPDFRemot)
			throws ComposicioPDFException {
		try {
			// Validacio Ambit/aplicacio informats (tot i que no es facin servir
			// cal
			// informar-ho de cara als logs d'instrumentacio)
			sTDValidacio.validaAmbitAplicacioInformats(config);

			// Validacio Ambit/aplicacio existents al sistema
			sTDValidacio.validaAmbitAplicacioValids(config);

			// Validacio que l'objecte que conté la informacio esta informat
			validaComposarPDFRemotContingut(config, composarPDFRemot);

		} catch (STDValidacioException ex) {
			throw new ComposicioPDFException(ex, ex.getCodiError(), ex.getMessage());
		}
	}

	private File getFitxerEntrada(ConfigCall config, ComposarPDFRemot composarPDFRemot) {
		File file = new File(STDUtils.getPathFitxerEntradaComposicio(pathInicial, config, pathFitxerEntrada)
				+ composarPDFRemot.getNomFitxerEntrada() + ComposicioPDFConstants.EXTENSIO_PDF);
		if (file == null || (file != null && !file.exists())) {
			file = new File(STDUtils.getPathFitxerEntradaComposicio(pathInicial, config, pathFitxerEntrada)
					+ composarPDFRemot.getNomFitxerEntrada() + ComposicioPDFConstants.EXTENSIO_PDF.toUpperCase());
		}
		return file;
	}

	private void validaComposarPDFStream(ConfigCall config, ComposarPDFStream composarPDFStream)
			throws ComposicioPDFException {
		try {
			// Validacio Ambit/aplicacio informats (tot i que no es facin servir
			// cal
			// informar-ho de cara als logs d'instrumentacio)
			sTDValidacio.validaAmbitAplicacioInformats(config);

			// Validacio Ambit/aplicacio existents al sistema
			sTDValidacio.validaAmbitAplicacioValids(config);

			// Validacio que l'objecte que conté la informacio esta informat
			validaComposarPDFStreamContingut(config, composarPDFStream);

		} catch (STDValidacioException ex) {
			throw new ComposicioPDFException(ex, ex.getCodiError(), ex.getMessage());
		}
	}

	private String getPathNomFitxerSortida(ConfigCall config, String nomFitxerSortida) {
		return STDUtils.comprovarPathNomCanviNom(
				STDUtils.getPathFitxerSortidaComposicio(pathInicial, config, pathFitxerSortida) + nomFitxerSortida,
				limitNumeroFitxersMateixNom);
	}

	private void validaNomFitxerEntrada(String nomFitxerEntrada) throws ComposicioPDFException {
		if (nomFitxerEntrada == null || (nomFitxerEntrada != null && "".equals(nomFitxerEntrada.trim()))) {
			logger.error("[ComposicioPDFImpl][validaNomFitxerEntrada] ERROR nom fitxer entrada null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY,
					"ERROR nom fitxer entrada null");
		}
	}

	private void validaFitxerEntrada(File fitxerEntrada) throws ComposicioPDFException {
		if (fitxerEntrada == null || (fitxerEntrada != null && !fitxerEntrada.exists())) {
			logger.error("[ComposicioPDFImpl][validaFitxerEntrada] ERROR fitxer d'entrada null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY,
					"ERROR fitxer d'entrada null");
		}
	}

	private void validaComposarPDFRemotContingut(ConfigCall config, ComposarPDFRemot composarPDFRemot)
			throws ComposicioPDFException, STDValidacioException {
		if (composarPDFRemot == null) {
			logger.error("[ComposicioPDFImpl][validaComposarPDFRemotInformat] ERROR composarPDFRemot ve a null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_REMOT,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT);
		}

		// Validacio que el nom del fitxer estigui informat
		validaNomFitxerEntrada(composarPDFRemot.getNomFitxerEntrada());

		// Validacio que el fitxer existeixi
		File fitxerEntrada = getFitxerEntrada(config, composarPDFRemot);
		validaFitxerEntrada(fitxerEntrada);

		// Validacio de la mida del fitxer
		sTDValidacio.validaMidaMax(fitxerEntrada, midaMaxPDFRemot);

		// Validacio que el nom del fitxer de sortida estigui informat
		validaNomFitxerSortida(composarPDFRemot.getNomFitxerSortida());
	}

	private void validaNomFitxerSortida(String nomFitxerSortida) throws ComposicioPDFException {
		if (nomFitxerSortida == null || (nomFitxerSortida != null && "".equals(nomFitxerSortida.trim()))) {
			logger.error("[ComposicioPDFImpl][validaNomFitxerSortida] ERROR nom fitxer sotrida null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_FILE_EMPTY,
					"ERROR nom fitxer sortida null");
		}
	}

	private void validaComposarPDFStreamContingut(ConfigCall config, ComposarPDFStream composarPDFStream)
			throws ComposicioPDFException, STDValidacioException {
		if (composarPDFStream == null) {
			logger.error("[ComposicioPDFImpl][validaComposarPDFRemotInformat] ERROR composarPDFStream ve a null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_STREAM,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_STREAM);
		}

		// Validacio que la data del fitxer estigui informat
		sTDValidacio.validaDataInformada(composarPDFStream.getInputStreamEntrada());

		// Validacio de la mida del fitxer
		sTDValidacio.validaMidaMax(composarPDFStream.getInputStreamEntrada(), midaMaxPDFStream);
	}

	private InputStream composarPDF(ConfigCall config, InputStream inputStream, String nomPlantilla,
			Float porcentatgeEscalat, Float posicioEscalatX, Float posicioEscalatY,
			Map<String, StringSegell> parametresString, Map<String, ImatgeSegell> parametresImatge, boolean generarCSV,
			String dateFormatCSV) throws ComposicioPDFException {
		InputStream pdf = null;

		logger.debug(TXT_COMPOSAR_PDF + LOG_INICI_OPERACIO);

		try {
			inputStream.reset();
		} catch (IOException ex) {
			logger.error(TXT_COMPOSAR_PDF
					+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_DOCUMENT, ex);
		}

		try {
			if (generarCSV) {
				// Generar CSV automaticament
				generarCSVPutToParametres(config, inputStream, dateFormatCSV, parametresString);
			}

			// Obtenim els annexos si en conte
			ResultAnnexos annexos = annexosPDF.getAnnexos(config, inputStream);

			// Aplanem el PDF abans de realitzar la composicio
			InputStream inputStreamAplanat = aplanatPDF.aplanarPDF(config, inputStream);

			pdf = composarPDF(config, inputStreamAplanat, nomPlantilla, porcentatgeEscalat, posicioEscalatX,
					posicioEscalatY, parametresString, parametresImatge, annexos);

		} catch (ComposicioPDFException ce) {
			logger.error(TXT_COMPOSAR_PDF + ce.getLocalizedMessage(), ce);
			throw ce;
		} catch (Exception ex) {
			logger.error(TXT_COMPOSAR_PDF
					+ ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_DOCUMENT, ex);
			throw new ComposicioPDFException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_DOCUMENT);
		}

		logger.debug(TXT_COMPOSAR_PDF + LOG_FINAL_OPERACIO);
		return pdf;
	}

	private void generarCSVPutToParametres(ConfigCall config, InputStream inputStream, String dateFormatCSV,
			Map<String, StringSegell> parametresString) throws CodiSegurVerificacioException, IOException {
		logger.debug("[ComposicioPDFImpl][composarPDF] Inici generacio CSV i put csv a parametres");

		String cSV = generarCSV(config, inputStream, dateFormatCSV);
		putCSVtoParametres(cSV, parametresString);

		logger.debug("[ComposicioPDFImpl][composarPDF] Fi generacio CSV i put csv a parametres");
	}

	private String generarCSV(ConfigCall config, InputStream inputStream, String timeStamp)
			throws IOException, CodiSegurVerificacioException {
		String cSV = null;

		logger.debug("[ComposicioPDFImpl][generarCSV] Inici generacio CSV");
		try {
			String key = new String();
			logger.debug("[ComposicioPDFImpl][generarCSV] Inici generacio del Hash de l'arxiu");
			byte[] dataMD5 = getMD5Checksum(inputStream);
			logger.debug("[ComposicioPDFImpl][generarCSV] Inici genera un codi segur (CSV)");
			GenerarCSVTimeStamp generarCSVTimeStamp = new GenerarCSVTimeStamp();
			generarCSVTimeStamp.setData(dataMD5);
			generarCSVTimeStamp.setKey(key);
			generarCSVTimeStamp.setTimeStamp(timeStamp);
			cSV = codiSegurVerificacioService.generarCSVTimeStamp(config, generarCSVTimeStamp).getKey();
			logger.debug("[ComposicioPDFImpl][generarCSV] Fi genera un codi segur (CSV)");
		} finally {
			logger.debug("[ComposicioPDFImpl][generarCSV] FI generacio CSV");
			inputStream.reset();
		}

		return cSV;
	}

	/***
	 * Realiza la suma de verificaci?n de un byte array mediante MD5
	 * 
	 * @param archivo
	 *                | archivo a que se le aplicara la suma de verificaci?n
	 * @return valor de la suma de verificaci?n.
	 * @throws IOException
	 */
	private byte[] getMD5Checksum(InputStream in) throws IOException {
		return DigestUtils.md5Digest(in);
	}

	private void putCSVtoParametres(String cSV, Map<String, StringSegell> parametresString) {
		StringSegell segell = new StringSegell();
		segell.setText(cSV);

		parametresString.put("CSV", segell);
	}

	private InputStream composarPDF(ConfigCall config, InputStream inputStream, String nomPlantilla,
			Float porcentatgeEscalat, Float posicioEscalatX, Float posicioEscalatY,
			Map<String, StringSegell> parametresString, Map<String, ImatgeSegell> parametresImatge,
			ResultAnnexos annexos) throws ComposicioPDFException {

		InputStream inputStreamPlantilla = null;
		InputStream inputStreamPlantillaMarcaAigua = null;

		InputStream inputStreamPlantillaHoritzontal = null;
		InputStream inputStreamPlantillaHoritzontalMarcaAigua = null;

		logger.debug("[ComposicioPDFServiceImpl][composarPDF] " + LOG_INICI_OPERACIO);
		try {

			comprovarNomPlantilla(nomPlantilla);

			String pathFitxerPlantilla = getPathPlantillesComposicio(config);

			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Comprovant plantilla " + pathFitxerPlantilla
					+ nomPlantilla + ComposicioPDFConstants.EXTENSIO_PDF + " ...");
			inputStreamPlantilla = getInputStreamPlantilla(
					pathFitxerPlantilla + nomPlantilla + ComposicioPDFConstants.EXTENSIO_PDF);
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Plantilla Comprovada OK");
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Comprovant Plantilla Marca Aigua "
					+ pathFitxerPlantilla + nomPlantilla + ComposicioPDFConstants.FINAL_PLANTILLA_MARCA_AIGUA
					+ ComposicioPDFConstants.EXTENSIO_PDF + " ...");
			inputStreamPlantillaMarcaAigua = getInputStreamMarcaAigua(pathFitxerPlantilla + nomPlantilla
					+ ComposicioPDFConstants.FINAL_PLANTILLA_MARCA_AIGUA + ComposicioPDFConstants.EXTENSIO_PDF);
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Plantilla Marca Aigua Comprovada OK");

			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Comprovant plantilla horitzontal");
			inputStreamPlantillaHoritzontal = getInputStreamPlantillaHoritzontal(pathFitxerPlantilla + nomPlantilla
					+ ComposicioPDFConstants.PLANTILLA_HORITZONTAL + ComposicioPDFConstants.EXTENSIO_PDF);
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Plantilla Comprovada OK");
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Comprovant Plantilla Horitzontal Marca Aigua ");
			inputStreamPlantillaHoritzontalMarcaAigua = getInputStreamHoritzontalMarcaAigua(
					pathFitxerPlantilla + nomPlantilla + ComposicioPDFConstants.PLANTILLA_HORITZONTAL
							+ ComposicioPDFConstants.FINAL_PLANTILLA_MARCA_AIGUA + ComposicioPDFConstants.EXTENSIO_PDF);
			logger.debug("[ComposicioPDFServiceImpl][composarPDF] Plantilla Marca Aigua Comprovada OK");

			FileBufferedInputStream outputDocumentComposat = null;
			try (FileBufferedOutputStream outputStreamDocumentComposat = new FileBufferedOutputStream();) {
				composicioPDF.composarPDF(inputStream, inputStreamPlantilla, inputStreamPlantillaMarcaAigua,
						inputStreamPlantillaHoritzontal, inputStreamPlantillaHoritzontalMarcaAigua, porcentatgeEscalat,
						posicioEscalatX, posicioEscalatY, parametresString, parametresImatge, annexos,
						outputStreamDocumentComposat);

				outputDocumentComposat = new FileBufferedInputStream(outputStreamDocumentComposat);
			}

			logger.debug("[ComposicioPDFServiceImpl][composarPDF] " + LOG_FINAL_OPERACIO);

			return outputDocumentComposat;
		} catch (ComposicioPDFException ce) {
			logger.error(TXT_COMPOSAR_REMOT + ce.getLocalizedMessage(), ce);
			throw ce;
		} catch (Exception ex) {
			logger.error("ERROR en la composicio ", ex);
			throw new ComposicioPDFException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_DOCUMENT);
		} finally {
			tancarInputStream(inputStreamPlantilla);
			tancarInputStream(inputStreamPlantillaMarcaAigua);
			// tancarOutputStream(outputStreamDocumentComposat);
		}
	}

	private String getPathPlantillesComposicio(ConfigCall config) throws ComposicioPDFException {
		String property = STDUtils.getPathInicialAmbitAplicacio(pathInicial, config) + pathFitxerPlantilla;
		logger.debug(
				"[ComposicioPDFServiceImpl][getPathPlantillesComposicio] Path plantilles composicio = " + property);

		return property;
	}

	/**
	 * @param nomPlantilla
	 * @throws ComposicioPDFException
	 */
	private void comprovarNomPlantilla(String nomPlantilla) throws ComposicioPDFException {
		if (nomPlantilla == null || (nomPlantilla != null && "".equals(nomPlantilla.trim()))) {
			logger.error("ERROR nom de la plantilla null");
			throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_PLANTILLA_NO_EXISTEIX,
					"ERROR no es troba plantilla amb aquest nom");
		}
	}

	/**
	 * 
	 * @param outputStream
	 * @throws ComposicioPDFException
	 */
	private void tancarOutputStream(OutputStream outputStream) throws ComposicioPDFException {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				logger.error("ERROR de tancant l'OutputStream ", e);
				throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"ERROR de tancant l'OutputStream ");
			}
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @throws ComposicioPDFException
	 */
	private void tancarInputStream(InputStream inputStream) throws ComposicioPDFException {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("ERROR de tancant l'InputStream ", e);
				throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"ERROR de tancant l'InputStream ");
			}
		}
	}

	/**
	 * @param pathNomFitxerPlantilla
	 * @return
	 * @throws ComposicioPDFException
	 */
	private InputStream getInputStreamPlantilla(String pathNomFitxerPlantilla) throws ComposicioPDFException {
		return getInputStreamPlantilla(pathNomFitxerPlantilla, true);
	}

	/**
	 * @param pathNomFitxerPlantilla
	 * @return
	 * @throws ComposicioPDFException
	 */
	private InputStream getInputStreamPlantillaHoritzontal(String pathNomFitxerPlantilla)
			throws ComposicioPDFException {
		return getInputStreamPlantilla(pathNomFitxerPlantilla, false);
	}

	/**
	 * @param pathNomFitxerPlantilla
	 * @param throwExceptionIfNotFound
	 * @return
	 * @throws ComposicioPDFException
	 */
	private InputStream getInputStreamPlantilla(String pathNomFitxerPlantilla, boolean throwExceptionIfNotFound)
			throws ComposicioPDFException {
		InputStream inputStreamPlantilla = null;
		try {
			File filePlantilla = new File(pathNomFitxerPlantilla);
			if (filePlantilla.exists()) {
				inputStreamPlantilla = new FileInputStream(filePlantilla);
			} else {
				if (throwExceptionIfNotFound) {
					logger.error(TXT_LA_PLANTILLA + pathNomFitxerPlantilla + TXT_NO_EXISTEIX);
					throw new ComposicioPDFException(
							ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_PLANTILLA_NO_EXISTEIX,
							TXT_LA_PLANTILLA + pathNomFitxerPlantilla + TXT_NO_EXISTEIX);
				} else {
					logger.debug(TXT_LA_PLANTILLA + pathNomFitxerPlantilla + TXT_NO_EXISTEIX);
				}
			}
		} catch (ComposicioPDFException compe) {
			throw new ComposicioPDFException(compe, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_PLANTILLA_NO_EXISTEIX,
					TXT_LA_PLANTILLA + pathNomFitxerPlantilla + TXT_NO_EXISTEIX);
		} catch (IOException e) {
			logger.error("ERROR de lectura de la plantilla " + pathNomFitxerPlantilla + " ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					"ERROR de lectura de la plantilla " + pathNomFitxerPlantilla);
		}
		return inputStreamPlantilla;
	}

	/**
	 * @param pathNomFitxerPlantillaMarcaAigua
	 * @return
	 * @throws ComposicioPDFException
	 */
	private InputStream getInputStreamMarcaAigua(String pathNomFitxerPlantillaMarcaAigua)
			throws ComposicioPDFException {

		InputStream inputStreamPlantillaMarcaAigua = null;
		try {
			File filePlantillaMarcaAigua = new File(pathNomFitxerPlantillaMarcaAigua);
			if (filePlantillaMarcaAigua.exists()) {
				inputStreamPlantillaMarcaAigua = new FileInputStream(filePlantillaMarcaAigua);
			} else {
				logger.error("ERROR la plantilla marca d'aigua associada a la plantilla "
						+ pathNomFitxerPlantillaMarcaAigua + TXT_NO_EXISTEIX);
				throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"La plantilla marca d'aigua associada a la plantilla " + pathNomFitxerPlantillaMarcaAigua
								+ TXT_NO_EXISTEIX);
			}
		} catch (IOException e) {
			logger.error(ERROR_LECTURA_PLANTILLA
					+ pathNomFitxerPlantillaMarcaAigua + " ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					ERROR_LECTURA_PLANTILLA
							+ pathNomFitxerPlantillaMarcaAigua);
		}
		return inputStreamPlantillaMarcaAigua;
	}

	/**
	 * @param pathNomFitxerPlantillaMarcaAigua
	 * @return
	 * @throws ComposicioPDFException
	 */
	private InputStream getInputStreamHoritzontalMarcaAigua(String pathNomFitxerPlantillaMarcaAigua)
			throws ComposicioPDFException {

		InputStream inputStreamPlantillaMarcaAigua = null;
		try {
			File filePlantillaMarcaAigua = new File(pathNomFitxerPlantillaMarcaAigua);
			if (filePlantillaMarcaAigua.exists()) {
				inputStreamPlantillaMarcaAigua = new FileInputStream(filePlantillaMarcaAigua);
			} else {
				logger.debug("La plantilla marca d'aigua associada a la plantilla " + pathNomFitxerPlantillaMarcaAigua
						+ TXT_NO_EXISTEIX);
			}
		} catch (IOException e) {
			logger.error(ERROR_LECTURA_PLANTILLA
					+ pathNomFitxerPlantillaMarcaAigua + " ", e);
			throw new ComposicioPDFException(e, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
					ERROR_LECTURA_PLANTILLA
							+ pathNomFitxerPlantillaMarcaAigua);
		}
		return inputStreamPlantillaMarcaAigua;
	}

	private boolean checkCSVIfNecessary(Map<String, StringSegell> params, String filename)
			throws ComposicioPDFException {

		return true;
		/*
		 * try {
		 * 
		 * String traceFile = "collisions";
		 * String matchesText = "";
		 * PdfReader pdfReader = new PdfReader(filename);
		 * 
		 * StringSegell matches = params.get("CSV");
		 * if (matches == null)
		 * matches = params.get("csv");
		 * 
		 * if (matches == null) {
		 * TraceUtils.writeTrace(traceFile, filename +
		 * "- No s'ha trobat el paràmetre CSV");
		 * 
		 * //Només activar per debug dels paràmetres
		 * //for (Map.Entry<String, StringSegell> entry : params.entrySet()) {
		 * // writeTraceCSV(entry.getKey() + " - " + entry.getValue().getText());
		 * //}
		 * 
		 * return true;
		 * }
		 * else {
		 * matchesText = matches.getText();
		 * TraceUtils.writeTrace(traceFile, filename + "- Contingut CSV a localitzar: "
		 * + matchesText);
		 * }
		 * 
		 * 
		 * int pages = pdfReader.getNumberOfPages();
		 * 
		 * for(int i=1; i<=pages; i++) {
		 * String pageContent =
		 * PdfTextExtractor.getTextFromPage(pdfReader, i);
		 * 
		 * if (pageContent.contains(matchesText)) {
		 * TraceUtils.writeTrace(traceFile, filename +
		 * "- Contingut CSV localitzat a pàgina " + i + " : " + matchesText);
		 * pdfReader.close();
		 * return true;
		 * } else {
		 * TraceUtils.writeTrace(traceFile, filename +
		 * "- Contingut CSV NO localitzat a pàgina " + i + " : " + matchesText);
		 * }
		 * }
		 * 
		 * pdfReader.close();
		 * } catch (Exception e) {
		 * logger.
		 * error("ERROR de lectura del fitxer generat per a la comprovació del codi CSV "
		 * + filename + " ", e);
		 * throw new ComposicioPDFException(e,
		 * ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
		 * "ERROR de lectura del fitxer generat per a la comprovació del codi CSV en el fitxer "
		 * + filename);
		 * }
		 * 
		 * return false;
		 */
	}

}
