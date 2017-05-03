package mil.nga.bundler.exceptions;

/**
 * Custom exception thrown if we were unable to load the target 
 * properties file.
 * 
 * @author L. Craig Carpenter
 */
public class PropertiesNotLoadedException extends Exception {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -3226029139307392741L;

	/** 
	 * Default constructor requiring a message String.
	 * @param msg Information identifying why the exception was raised.
	 */
	public PropertiesNotLoadedException(String msg) {
		super(msg);
	}
}
