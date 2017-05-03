package mil.nga.bundler.exceptions;

/**
 * Exception thrown when clients supply an unknown product type.
 * 
 * @author carpenlc
 */
public class UnknownProductException extends Exception {

    /**
     * Eclipse generated serialVersionUID
     */
    private static final long serialVersionUID = -3295970223898626998L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public UnknownProductException(String msg) {
        super(msg);
    }
}
