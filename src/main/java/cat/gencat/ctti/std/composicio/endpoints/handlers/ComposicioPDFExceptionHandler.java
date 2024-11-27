package cat.gencat.ctti.std.composicio.endpoints.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cat.gencat.ctti.std.composicio.constants.ComposicioPDFErrorsConstants;
import cat.gencat.ctti.std.composicio.endpoints.ComposicioPDFController;
import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;
import cat.gencat.ctti.std.dto.ResultSTD;

@ControllerAdvice(assignableTypes = {ComposicioPDFController.class})
public class ComposicioPDFExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger_csv = LoggerFactory.getLogger(ComposicioPDFExceptionHandler.class);

	@ExceptionHandler({ ComposicioPDFException.class })
	public ResponseEntity<Object> handleComposicioPDFException(ComposicioPDFException exception, WebRequest request) {
		logger_csv.error("[ComposicioExceptionHandler][handleSTDBaseException] Composicio PDF Exception caught ", exception);

		ResultSTD result = new ResultSTD();
		result.setMissatgeError(exception.getMessage());
		result.setStatus(exception.getCodiError());

		return handleExceptionInternal(exception, result, new HttpHeaders(), exception.getHttpStatus(), request);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> customHandleException(Exception exception, WebRequest request) {
		logger_csv.error("[ComposicioExceptionHandler][customHandleException] Exception caught ", exception);

		ResultSTD result = new ResultSTD();
		result.setMissatgeError(exception.getMessage());
		result.setStatus(ComposicioPDFErrorsConstants.ERROR_CODI_ERROR_COMPOSAR_DOCUMENT);

		return handleExceptionInternal(exception, result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}
