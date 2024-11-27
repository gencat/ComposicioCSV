package cat.gencat.ctti.std.composicio.aplanat.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.frontend.ClientProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.XfaForm;

import cat.gencat.ctti.canigo.eforms.IServeisSOAPv1;
import cat.gencat.ctti.std.composicio.aplanat.AplanatPDF;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ConfigCall;
import cat.gencat.ctti.std.utils.FileBufferedInputStream;
import cat.gencat.forms.webservice.Result;

/**
 * 
 * @author CSCanig�
 *
 */
@Component
public class AplanatPDFImpl implements AplanatPDF {

	private static final Logger logger = LoggerFactory.getLogger(AplanatPDFImpl.class);

	@Autowired
	private IServeisSOAPv1 serviceEfomularis;

	public InputStream aplanarPDF(ConfigCall config, InputStream in) throws ComposicioPDFException {

		InputStream result = in;

		logger.debug("[AplanatPDFImpl][aplanarPDF] Inici operacio");
		try {
			boolean isDynamicForm = isDynamicForm(in);

			if (isDynamicForm) {
				if (serviceEfomularis == null) {
					throw new ComposicioPDFException(
							ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_INTEGRACIO_EFORMULARIS,
							"S'ha produït un error en la integració amb eFormularis per l'aplanat del PDF");
				}

				String uuid = config.getUuid() != null ? config.getUuid() : UUID.randomUUID().toString();
				// byte[] bytes = sgdeService.aplanarPDF(uuid, ambit, aplicacio,
				// is));

				// logger.debug("[AplanatPDFImpl][aplanarPDF] conduit Endpoint: "
				// +
				// ClientProxy.getClient(serviceEfomularis).getEndpoint().getEndpointInfo().getAddress());
				// logger.debug("[AplanatPDFImpl][aplanarPDF] conduit Endpoint: "
				// +
				// ClientProxy.getClient(serviceEfomularis).getEndpoint().getEndpointInfo().getTransportId());
				// logger.debug("[AplanatPDFImpl][aplanarPDF] conduit Endpoint: " + ClientProxy
				// .getClient(serviceEfomularis).getEndpoint().getEndpointInfo().getDescription().getBaseURI());
				Result resultat = serviceEfomularis.aplanarPDF(uuid, config.getAmbit(), config.getAplicacio(),
						IOUtils.toByteArray(in));
				ClientProxy.getClient(serviceEfomularis).getResponseContext().clear();

				if (resultat != null && resultat.getStatus() != null && resultat.getStatus().intValue() != 0) {

					if (resultat.getStatus().intValue() == -105 && resultat.getMissatgeError().getValue()
							.equals("ALC-OUT-001-201: Input Document is a already flat PDF Document")) {
						return result;
					}

					logger.debug("[AplanatPDFImpl][aplanarPDF] Error en la resposta");
					logger.debug("[AplanatPDFImpl][aplanarPDF] Status: " + resultat.getStatus());
					logger.debug(
							"[AplanatPDFImpl][aplanarPDF] MissatgeError: " + resultat.getMissatgeError().getValue());
					// throw new SGDEException(resultat.getStatus(),
					// resultat.getMissatgeError().getValue());
					throw new ComposicioPDFException(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_APLANANT_DOCUMENT,
							"ERROR en l'aplanat");
				}

				result = new FileBufferedInputStream(resultat.getArxiu().getValue());
			}
		} catch (Exception ex) {
			if (!(ex instanceof ComposicioPDFException)) {
				logger.error("ERROR en l'aplanat ", ex);
				throw new ComposicioPDFException(ex, ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT,
						"ERROR en l'aplanat");
			} else {
				throw (ComposicioPDFException) ex;
			}
		} finally {
			try {
				if (in != null && in.markSupported()) {
					in.reset();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return result;
	}

	private static boolean isDynamicForm(InputStream in) throws IOException {
		try {
			PdfReader reader = new PdfReader(new FileBufferedInputStream(in));
			AcroFields form = reader.getAcroFields();
			XfaForm xfa = form.getXfa();
			boolean isXfaForm = xfa.isXfaPresent();
			Set<String> fields = form.getFields().keySet();
			// boolean acroForm = !CollectionUtils.isEmpty(fields);
			boolean acroForm = !CollectionUtils.isEmpty(fields) && fields.size() > form.getSignatureNames().size();
			return isXfaForm || acroForm;
		} finally {
			if (in != null && in.markSupported()) {
				in.reset();
			}
		}
	}

}