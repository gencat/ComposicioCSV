package cat.gencat.ctti.std.csv.endpoints.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cat.gencat.ctti.std.csv.constants.CodiSegurVerificacioErrorsConstants;
import cat.gencat.ctti.std.csv.endpoints.CodiSegurVerificacioController;
import cat.gencat.ctti.std.csv.exceptions.CodiSegurVerificacioException;
import cat.gencat.ctti.std.dto.ResultSTD;

@ControllerAdvice(assignableTypes = {CodiSegurVerificacioController.class})
public class CodiSegurVerificacioExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger_ex = LoggerFactory.getLogger(CodiSegurVerificacioExceptionHandler.class);

	@ExceptionHandler({ CodiSegurVerificacioException.class })
	public ResponseEntity<Object> handleCodiSegurVerificacioException(CodiSegurVerificacioException exception, WebRequest request) {
		logger_ex.error("[CodiSegurVerificacioExceptionHandler][handleCodiSegurVerificacioException] Codi Segur Verificacio Exception caught ", exception);

		ResultSTD result = new ResultSTD();
		result.setMissatgeError(exception.getMessage());
		result.setStatus(exception.getCodiError());

		return handleExceptionInternal(exception, result, new HttpHeaders(), exception.getHttpStatus(), request);
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> customHandleException(Exception exception, WebRequest request) {
		logger_ex.error("[CodiSegurVerificacioExceptionHandler][customHandleException] Exception caught ", exception);

		ResultSTD result = new ResultSTD();
		result.setMissatgeError(exception.getMessage());
		result.setStatus(CodiSegurVerificacioErrorsConstants.ERROR_CODI_ERROR_GENERANT_CSV_DOCUMENT);

		return handleExceptionInternal(exception, result, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}
