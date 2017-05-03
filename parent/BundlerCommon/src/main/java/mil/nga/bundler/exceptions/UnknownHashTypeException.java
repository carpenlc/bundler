package mil.nga.bundler.exceptions;

/**
 * Exception raised when an unsupported archive format is requested.
 * 
 * @author carpenlc
 */
public class UnknownHashTypeException extends Exception {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -8266532817396136239L;

	/** 
	 * Default constructor requiring a message String.
	 * @param msg Information identifying why the exception was raised.
	 */
	public UnknownHashTypeException(String msg) {
		super(msg);
	}
	
}
