package mil.nga.bundler.exceptions;

/**
 * Generic exception raised when errors are encountered creating output
 * archive files.
 * 
 * @author carpenlc
 */
public class ArchiveException extends Exception {

    /**
     * Eclipse generated serialVersionUID
     */
    private static final long serialVersionUID = 5162419284907604468L;

    /** 
     * Default constructor requiring a message String.
     * @param msg Information identifying why the exception was raised.
     */
    public ArchiveException(String msg) {
        super(msg);
    }
}
