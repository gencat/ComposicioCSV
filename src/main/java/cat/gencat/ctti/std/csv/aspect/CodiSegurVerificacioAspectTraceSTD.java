package cat.gencat.ctti.std.csv.aspect;

import java.net.InetAddress;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import cat.gencat.ctti.canigo.arch.core.utils.CoreUtils;
import cat.gencat.ctti.std.aspect.AspectTraceSTDBase;
import cat.gencat.ctti.std.dto.ConfigCall;

/**
 * Live monitoring aspect that inject instrumentation data into logs
 * 
 * @author cscanigo
 *
 */
@Aspect
@Component
public class CodiSegurVerificacioAspectTraceSTD extends AspectTraceSTDBase {

	private final static Logger logger = LoggerFactory.getLogger(CodiSegurVerificacioAspectTraceSTD.class);

	private static final String NOM_METODE_GENERAR_CSV = "generarCSV";
	
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

		if (metodeExecutat != null && (metodeExecutat.indexOf(NOM_METODE_GENERAR_CSV) != -1)) {

			ConfigCall dadesGeneriques = (ConfigCall) arguments[0];

			uuid = dadesGeneriques.getUuid();
			ambit = dadesGeneriques.getAmbit();
			aplicacio = dadesGeneriques.getAplicacio();
			
			StringBuffer buffer = new StringBuffer();

			appendHeader(buffer, uuid, hostName, instancia, watch.getLastTaskTimeMillis(), metodeExecutat, ambit,
					aplicacio);

			bufferAppend(buffer, resultat);
			logger.info(buffer.toString());
		}

	}

	private void appendHeader(StringBuffer buffer, String uuid, String hostName, String instancia,
			Long lastTaskTimeMillis, String metodeExecutat, String ambit, String aplicacio) {
		bufferAppend(buffer,uuid);
		bufferAppend(buffer,hostName);
		bufferAppend(buffer,instancia);
		bufferAppend(buffer,lastTaskTimeMillis); // temps de
																	// resposta
		bufferAppend(buffer,metodeExecutat);
		ambit = replaceSeparator(ambit);
		bufferAppend(buffer,ambit);
		aplicacio = replaceSeparator(aplicacio);
		bufferAppend(buffer,aplicacio);
	}
	
}