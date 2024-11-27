package cat.gencat.ctti.std.composicio.aspect;

import java.net.InetAddress;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import cat.gencat.ctti.canigo.arch.core.utils.CoreUtils;
import cat.gencat.ctti.std.aspect.AspectTraceSTDBase;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.dto.ConfigCall;

/**
 * Live monitoring aspect that inject instrumentation data into logs
 * 
 * @author cscanigo
 *
 */
@Aspect
@Component
public class ComposicioPDFAspectTraceSTD extends AspectTraceSTDBase {

	private final static Logger logger = LoggerFactory.getLogger(ComposicioPDFAspectTraceSTD.class);

	private static final String NOM_METODE_COMPOSAR_PDF_STREAM = "composarPDFStream";
	private static final String NOM_METODE_COMPOSAR_PDF_REMOT = "composarPDFRemot";

	@Override
	public void afterPropertiesSet() throws Exception {
		//do nothing
	}

	@Override
	protected void appendTrace(ProceedingJoinPoint jp, StopWatch watch, String resultat) throws Exception {
		Object[] arguments = jp.getArgs();
		String hostName = CoreUtils.getCurrentHostName();
		String instancia = InetAddress.getLocalHost().getHostName();
		String metodeExecutat = jp.getSignature().toShortString();

		String uuid = NO_INFORMAT_DEFAULT;
		String ambit = NO_INFORMAT_DEFAULT;
		String aplicacio = NO_INFORMAT_DEFAULT;

		if (metodeExecutat != null && (metodeExecutat.indexOf(NOM_METODE_COMPOSAR_PDF_REMOT) != -1
				|| metodeExecutat.indexOf(NOM_METODE_COMPOSAR_PDF_STREAM) != -1)) {

			ConfigCall dadesGeneriques = (ConfigCall) arguments[0];

			uuid = dadesGeneriques.getUuid();
			ambit = dadesGeneriques.getAmbit();
			aplicacio = dadesGeneriques.getAplicacio();

			StringBuffer buffer = new StringBuffer();

			appendHeader(buffer, uuid, hostName, instancia, watch.getLastTaskTimeMillis(), metodeExecutat, ambit,
					aplicacio);

			// Dades especifiques de composarPDFRemot
			if (metodeExecutat != null && (metodeExecutat.indexOf(NOM_METODE_COMPOSAR_PDF_REMOT) != -1)) {
				ComposarPDFRemot composarPDFRemot = (ComposarPDFRemot) arguments[1];
				appendComposarPDFRemot(buffer, composarPDFRemot);
			}

			// Dades especifiques de composarPDFStream
			if (metodeExecutat != null && (metodeExecutat.indexOf(NOM_METODE_COMPOSAR_PDF_STREAM) != -1)) {
				ComposarPDFStream composarPDFStream = (ComposarPDFStream) arguments[1];
				appendComposarPDFStream(buffer, composarPDFStream);
			}

			resultat = replaceSeparator(resultat);

			bufferAppend(buffer, resultat);
			logger.info(buffer.toString());
		}

	}

	private void appendComposarPDFStream(StringBuffer buffer, ComposarPDFStream composarPDFStream) {
		appendComposarPDF(buffer, composarPDFStream.getNomPlantilla(), composarPDFStream.getPorcentatgeEscalat(),
				composarPDFStream.getPosicioEscalatX(), composarPDFStream.getPosicioEscalatY(),
				composarPDFStream.isGenerarCSV(), NO_INFORMAT_DEFAULT, NO_INFORMAT_DEFAULT);
	}

	private void appendComposarPDFRemot(StringBuffer buffer, ComposarPDFRemot composarPDFRemot) {
		appendComposarPDF(buffer, composarPDFRemot.getNomPlantilla(), composarPDFRemot.getPorcentatgeEscalat(),
				composarPDFRemot.getPosicioEscalatX(), composarPDFRemot.getPosicioEscalatY(),
				composarPDFRemot.isGenerarCSV(), composarPDFRemot.getNomFitxerEntrada(),
				composarPDFRemot.getNomFitxerSortida());
	}

	private void appendComposarPDF(StringBuffer buffer, String nomPlantilla, Float porcentatgeEscalat,
			Float posicioEscalatX, Float posicioEscalatY, boolean generarCSV, String nomFitxerEntrada,
			String nomFitxerSortida) {
		bufferAppend(buffer, nomPlantilla);
		bufferAppend(buffer, porcentatgeEscalat);
		bufferAppend(buffer, posicioEscalatX);
		bufferAppend(buffer, posicioEscalatY);
		bufferAppend(buffer, generarCSV);
		bufferAppend(buffer, nomFitxerEntrada);
		bufferAppend(buffer, nomFitxerSortida);

	}

	private void appendHeader(StringBuffer buffer, String uuid, String hostName, String instancia,
			Long lastTaskTimeMillis, String metodeExecutat, String ambit, String aplicacio) {
		bufferAppend(buffer, uuid);
		bufferAppend(buffer, hostName);
		bufferAppend(buffer, instancia);
		bufferAppend(buffer, lastTaskTimeMillis); // temps de
													// resposta
		bufferAppend(buffer, metodeExecutat);
		ambit = replaceSeparator(ambit);
		bufferAppend(buffer, ambit);
		aplicacio = replaceSeparator(aplicacio);
		bufferAppend(buffer, aplicacio);
	}

}