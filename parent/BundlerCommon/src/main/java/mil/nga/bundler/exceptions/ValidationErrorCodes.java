package mil.nga.bundler.exceptions;

public enum ValidationErrorCodes {
    INVALID_ARCHIVE_TYPE(
            1005,
            "Request received that contained an invalid archive type."),
    NO_INPUT_FILES_FOUND(
            1010,
            "A request was received that did not contain any input files "
            + "to bundle."),
    NO_VALID_INPUT_FILES_FOUND(
            1015,
            "A request was received that did not contain any valid input "
            + "files.");
    
    /**
     * Error code ID
     */
    private int ID = 0;
    
    /**
     * Error message
     */
    private String message = null;
    
    /**
     * Private constructor setting the ID and error message string associated 
     * with the enum type.
     * 
     * @param id The error code ID.
     * @param msg The error code message.
     */
    private ValidationErrorCodes(int id, String msg) {
        setID(id);
        setMessage(msg);
    }
    
    /**
     * Getter method for the error code message.
     * @return The message associated with the error code.
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Getter method for the error code ID number.
     * @return The ID associated with the error code.
     */
    public int getID() {
        return this.ID;
    }
    
    /**
     * Setter method for the error code ID number.
     * @param value The ID associated with the error code.
     */
    public void setID(int value) {
        this.ID = value;
    }
    
    /**
     * Setter method for the error code message.
     * @param value The message associated with the error code.
     */
    public void setMessage(String value) {
        this.message = value;
    }
}
