package mil.nga.bundler.exceptions;

/**
 * Exception raised when an unsupported archive format is requested.
 * 
 * @author carpenlc
 */
public class UnknownArchiveTypeException extends Exception {

	/**
	 * Eclipse generated serialVersionUID
	 */
	private static final long serialVersionUID = -2136985576361134232L;

	/** 
	 * Default constructor requiring a message String.
	 * @param msg Information identifying why the exception was raised.
	 */
	public UnknownArchiveTypeException(String msg) {
		super(msg);
	}
	
}
