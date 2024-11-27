package cat.gencat.ctti.std.csv.endpoints.handlers;

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
import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioErrorsConstants;
import cat.gencat.ctti.std.csv.dto.GenerarCSV;
import cat.gencat.ctti.std.csv.dto.GenerarCSVTimeStamp;
import cat.gencat.ctti.std.csv.endpoints.CodiSegurVerificacioController;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.endpoints.handlers.STDExceptionHandlerBaseTest;
import cat.gencat.ctti.std.utils.STDUtils;

@RunWith(MockitoJUnitRunner.class)
public class CodiSegurVerificacioExceptionHandlerTest extends STDExceptionHandlerBaseTest {

	private MockMvc mockMvc;

	@Mock
	private CodiSegurVerificacioController codiSegurVerificacioController;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(codiSegurVerificacioController)
				.setControllerAdvice(new CodiSegurVerificacioExceptionHandler()).build();
	}

	@Test
	public void checkUnexpectedExceptionsAreCaughtGenerarCSV() throws Exception {

		Integer status = CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT;
		String exceptionMessage = "Unexpected Exception";

		addExceptionGenerarCSV(new RuntimeException(exceptionMessage));

		checkExceptionGenerarCSV(status, exceptionMessage);

	}

	@Test
	public void checkUnexpectedExceptionsAreCaughtGenerarCSVTimeStamp() throws Exception {

		Integer status = CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT;
		String exceptionMessage = "Unexpected Exception";

		addExceptionGenerarCSVTimeStamp(new RuntimeException(exceptionMessage));

		checkExceptionGenerarCSVTimeStamp(status, exceptionMessage);

	}

	@Test
	public void checkComposicioPDFExceptionIsCaughtGenerarCSV() throws Exception {

		Integer status = CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT;
		String exceptionMessage = CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT;

		addExceptionGenerarCSV(new CodiSegurVerificacioException(status, exceptionMessage));

		checkExceptionGenerarCSV(status, exceptionMessage);

	}

	@Test
	public void checkComposicioPDFExceptionIsCaughtGenerarCSVTimeStamp() throws Exception {

		Integer status = CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT;
		String exceptionMessage = CodiSegurVerificacioErrorsConstants.ERROR_MSG_ERROR_GENERANT_CSV_DOCUMENT;

		addExceptionGenerarCSVTimeStamp(new CodiSegurVerificacioException(status, exceptionMessage));

		checkExceptionGenerarCSVTimeStamp(status, exceptionMessage);

	}

	private void addExceptionGenerarCSVTimeStamp(Exception exception) throws CodiSegurVerificacioException {
		Mockito.when(codiSegurVerificacioController.generarCSVTimeStamp(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(GenerarCSVTimeStamp.class))).thenThrow(exception);
	}

	private void checkExceptionGenerarCSVTimeStamp(Integer status, String exceptionMessage) throws Exception {
		checkExceptionResponse(
				mockMvc.perform(
						addRequestParams(
								MockMvcRequestBuilders
										.post(STDUtils.getNotDefaultUriPath(
														CodiSegurVerificacioController.CODI_SEGUR_VERIFICACIO_URI_PATH)
												+ CodiSegurVerificacioController.GENERAR_CSV_TIMESTAMP_URI_PATH)
										.contentType(MediaType.APPLICATION_JSON_VALUE))
												.content(JacksonUtil.toString(new GenerarCSVTimeStamp()))),
				status, exceptionMessage);
	}

	private void checkExceptionGenerarCSV(Integer status, String exceptionMessage) throws Exception {
		checkExceptionResponse(
				mockMvc.perform(
						addRequestParams(
								MockMvcRequestBuilders
										.post(STDUtils.getNotDefaultUriPath(
														CodiSegurVerificacioController.CODI_SEGUR_VERIFICACIO_URI_PATH)
												+ CodiSegurVerificacioController.GENERAR_CSV_URI_PATH)
										.contentType(MediaType.APPLICATION_JSON_VALUE))
												.content(JacksonUtil.toString(new GenerarCSV()))),
				status, exceptionMessage);
	}

	private void addExceptionGenerarCSV(Exception exception) throws CodiSegurVerificacioException {
		Mockito.when(codiSegurVerificacioController.generarCSV(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(GenerarCSV.class))).thenThrow(exception);
	}

}
