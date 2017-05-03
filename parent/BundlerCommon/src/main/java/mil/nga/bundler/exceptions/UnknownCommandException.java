package mil.nga.bundler.exceptions;

/**
 * Exception thrown when clients supply an unknown command.
 * 
 * @author carpenlc
 */
public class UnknownCommandException extends Exception {

    /**
     * Eclipse generated serialVersionUID
     */
    private static final long serialVersionUID = 5152923215196636398L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public UnknownCommandException(String msg) {
        super(msg);
    }
    
}
