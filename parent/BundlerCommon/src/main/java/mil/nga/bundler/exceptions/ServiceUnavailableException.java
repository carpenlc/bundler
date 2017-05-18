package mil.nga.bundler.exceptions;

/**
 * Exception thrown if the application is unable to look up a target EJB.
 * 
 * @author L. Craig Carpenter
 */
public class ServiceUnavailableException extends Exception {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 7864162168405064085L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public ServiceUnavailableException(String msg) {
        super(msg);
    }

}