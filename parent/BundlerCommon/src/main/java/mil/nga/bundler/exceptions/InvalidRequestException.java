package mil.nga.bundler.exceptions;

/**
 * Custom exception thrown if a client submitted an invalid bundle request.
 * The input message String should supply a reason why the request was 
 * invalid.
 * 
 * @author L. Craig Carpenter
 */
public class InvalidRequestException extends Exception {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -9061259698313395640L;

    /**
     * The error code to raise to the caller.
     */
    private int ID = 0;
    
    /**
     * The text associated with the input error code.
     */
    private String messageText = null;
    
    /** 
     * Default constructor requiring clients to supply a String identifying
     * why an input bundle request was not valid.
     *  
     * @param msg String identifying why the input bundle request was 
     * invalid. 
     */
    public InvalidRequestException(ValidationErrorCodes errorCode) {
        setErrorCode(errorCode.getID());
        setMessageText(errorCode.getMessage());
    }
    
    /**
     * Getter method for the error code ID number.
     * @return The ID associated with the error code.
     */
    public int getErrorCode() {
        return this.ID;
    }
    
    /**
     * Getter method for the error code message.
     * @return The message associated with the error code.
     */
    public String getMessageText() {
        return this.messageText;
    }
    
    /**
     * Get the string error message concatenating the error code and
     * error text.
     */
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Error Code => [ ");
        sb.append(getErrorCode());
        sb.append(" ], Error Text => [ ");
        sb.append(getMessageText());
        sb.append(" ].");
        return sb.toString();
    }
    
    /**
     * Setter method for the error code ID number.
     * @param value The ID associated with the error code.
     */
    public void setErrorCode(int value) {
        this.ID = value;
    }
    
    /**
     * Setter method for the error code message.
     * @param value The message associated with the error code.
     */
    public void setMessageText(String value) {
        this.messageText = value;
    }
}
