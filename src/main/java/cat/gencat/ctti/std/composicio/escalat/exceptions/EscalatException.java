package cat.gencat.ctti.std.composicio.escalat.exceptions;

import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;

/**
 * 
 * @author CSCanig�
 *
 */
public class EscalatException extends ComposicioPDFException {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8065046523245393840L;
	
	public EscalatException(Integer anErrorCode, String message) {
		super(anErrorCode, message);
	}

	public EscalatException(Integer anErrorCode, String message, Object[] anErrorMessageArguments) {
		super(anErrorCode, message, anErrorMessageArguments);
	}

	public EscalatException(Throwable cause, Integer anErrorCode, String message) {
		super(cause, anErrorCode, message);
	}

	public EscalatException(Throwable cause, Integer anErrorCode, String message,
			Object[] anErrorMessageArguments) {
		super(cause, anErrorCode, message, anErrorMessageArguments);
	}	
}
