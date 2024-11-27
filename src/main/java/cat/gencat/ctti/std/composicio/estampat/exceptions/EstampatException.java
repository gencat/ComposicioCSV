package cat.gencat.ctti.std.composicio.estampat.exceptions;

import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;

/**
 * 
 * @author CSCanig�
 *
 */
public class EstampatException extends ComposicioPDFException {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8065046523245393840L;
	
	public EstampatException(Integer anErrorCode, String message) {
		super(anErrorCode, message);
	}

	public EstampatException(Integer anErrorCode, String message, Object[] anErrorMessageArguments) {
		super(anErrorCode, message, anErrorMessageArguments);
	}

	public EstampatException(Throwable cause, Integer anErrorCode, String message) {
		super(cause, anErrorCode, message);
	}

	public EstampatException(Throwable cause, Integer anErrorCode, String message,
			Object[] anErrorMessageArguments) {
		super(cause, anErrorCode, message, anErrorMessageArguments);
	}	
	
}
