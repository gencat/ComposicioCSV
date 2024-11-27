package cat.gencat.ctti.std.composicio.fusionat.exceptions;

import cat.gencat.ctti.std.composicio.exceptions.ComposicioPDFException;


public class FusionatException extends ComposicioPDFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8065046523245393840L;
	 
	public FusionatException(Integer anErrorCode, String message) {
		super(anErrorCode, message);
	}

	public FusionatException(Integer anErrorCode, String message, Object[] anErrorMessageArguments) {
		super(anErrorCode, message, anErrorMessageArguments);
	}

	public FusionatException(Throwable cause, Integer anErrorCode, String message) {
		super(cause, anErrorCode, message);
	}

	public FusionatException(Throwable cause, Integer anErrorCode, String message,
			Object[] anErrorMessageArguments) {
		super(cause, anErrorCode, message, anErrorMessageArguments);
	}	
	
}
