package cat.gencat.ctti.std.composicio.exceptions;

import org.springframework.http.HttpStatus;

import cat.gencat.ctti.std.exceptions.STDBaseException;

/**
 * 
 * @author CSCanig�
 *
 */
public class ComposicioPDFException extends STDBaseException {
	
	private static HttpStatus HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

	/**
	 * 
	 */
	private static final long serialVersionUID = 779521116073681109L;

	public ComposicioPDFException(Integer anErrorCode, String message) {
		super(anErrorCode, message, HTTP_STATUS);
	}

	public ComposicioPDFException(Integer anErrorCode, String message, Object[] anErrorMessageArguments) {
		super(anErrorCode, message, anErrorMessageArguments, HTTP_STATUS);
	}

	public ComposicioPDFException(Throwable cause, Integer anErrorCode, String message) {
		super(cause, anErrorCode, message, HTTP_STATUS);
	}

	public ComposicioPDFException(Throwable cause, Integer anErrorCode, String message,
			Object[] anErrorMessageArguments) {
		super(cause, anErrorCode, message, anErrorMessageArguments, HTTP_STATUS);
	}

 
}
