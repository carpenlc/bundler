package mil.nga;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Exception thrown from within the JAX-WS servlet classes.
 *  
 * @author carpenlc
 */
public class WebArchiveException extends WebApplicationException {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -7558172102269292783L;

	public WebArchiveException(String message) {
        super(Response
        		.status(Response.Status.BAD_REQUEST)
        		.entity(message)
        		.type(MediaType.TEXT_PLAIN)
        		.build());
    }
}
