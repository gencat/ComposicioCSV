package cat.gencat.ctti.std.composicio.endpoints.handlers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cat.gencat.ctti.canigo.arch.core.utils.JacksonUtil;
import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFRemot;
import cat.gencat.ctti.std.composicio.dto.ComposarPDFStream;
import cat.gencat.ctti.std.composicio.endpoints.ComposicioPDFController;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.endpoints.handlers.STDExceptionHandlerBaseTest;
import cat.gencat.ctti.std.utils.STDUtils;

@RunWith(MockitoJUnitRunner.class)
public class ComposicioPDFExceptionHandlerTest extends STDExceptionHandlerBaseTest {

	private MockMvc mockMvc;

	@Mock
	private ComposicioPDFController composicioPDFController;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(composicioPDFController)
				.setControllerAdvice(new ComposicioPDFExceptionHandler()).build();
	}

	@Test
	public void checkUnexpectedExceptionsAreCaughtComposarPDFRemot() throws Exception {

		Integer status = ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT;
		String exceptionMessage = "Unexpected Exception";

		addExceptionComposarPDFRemot(new RuntimeException(exceptionMessage));

		checkExceptionResponseComposarPDFRemot(status, exceptionMessage);

	}

	@Test
	public void checkUnexpectedExceptionsAreCaughtComposarPDFStream() throws Exception {

		Integer status = ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT;
		String exceptionMessage = "Unexpected Exception";

		addExceptionComposarPDFStream(new RuntimeException(exceptionMessage));

		checkExceptionsResponseComposarPDFStream(status, exceptionMessage);

	}

	@Test
	public void checkComposicioPDFExceptionIsCaughtComposarPDFRemot() throws Exception {

		Integer status = ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_REMOT;
		String exceptionMessage = ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT;

		addExceptionComposarPDFRemot(new ComposicioPDFException(status, exceptionMessage));

		checkExceptionResponseComposarPDFRemot(status, exceptionMessage);

	}

	@Test
	public void checkComposicioPDFExceptionIsCaughtComposarPDFStream() throws Exception {

		Integer status = ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_REMOT;
		String exceptionMessage = ComposicioPDFErrorsConstants.ERROR_MSG_ERROR_COMPOSAR_REMOT;

		addExceptionComposarPDFStream(new ComposicioPDFException(status, exceptionMessage));

		checkExceptionsResponseComposarPDFStream(status, exceptionMessage);

	}

	private void checkExceptionsResponseComposarPDFStream(Integer status, String exceptionMessage) throws Exception {
		checkExceptionResponse(mockMvc.perform(addRequestParams(MockMvcRequestBuilders
				.post(STDUtils.getNotDefaultUriPath(ComposicioPDFController.COMPOSICIO_PDF_URI_PATH)
						+ ComposicioPDFController.COMPOSAR_PDF_STREAM_URI_PATH)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).content(JacksonUtil.toString(new ComposarPDFStream()))),
				status, exceptionMessage);
	}

	private void addExceptionComposarPDFStream(Exception Exception) throws ComposicioPDFException {
		Mockito.when(composicioPDFController.composarPDFStream(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(ComposarPDFStream.class))).thenThrow(Exception);
	}

	private void checkExceptionResponseComposarPDFRemot(Integer status, String exceptionMessage) throws Exception {
		checkExceptionResponse(mockMvc.perform(addRequestParams(MockMvcRequestBuilders
				.post(STDUtils.getNotDefaultUriPath(ComposicioPDFController.COMPOSICIO_PDF_URI_PATH)
						+ ComposicioPDFController.COMPOSAR_PDF_REMOT_URI_PATH)
				.contentType(MediaType.APPLICATION_JSON_VALUE)).content(JacksonUtil.toString(new ComposarPDFRemot()))),
				status, exceptionMessage);
	}

	private void addExceptionComposarPDFRemot(Exception exception) throws ComposicioPDFException {
		Mockito.when(composicioPDFController.composarPDFRemot(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(ComposarPDFRemot.class))).thenThrow(exception);
	}

}
