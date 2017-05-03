package mil.nga.bundler.exceptions;

public class UnsupportedProductException extends Exception {

	/**
	 * Eclipse generated serialVersionUID
	 */
	private static final long serialVersionUID = 2642690276926290400L;

	/** 
	 * Default constructor requiring a message String.
	 * @param msg Information identifying why the exception was raised.
	 */
	public UnsupportedProductException(String msg) {
		super(msg);
	}
	
}
