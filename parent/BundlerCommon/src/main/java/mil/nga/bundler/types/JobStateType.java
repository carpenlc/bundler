package mil.nga.bundler.types;

/**
 * Enumeration type identifying the status of a Bundler job.
 *  
 * @author L. Craig Carpenter
 */
public enum JobStateType {
    NOT_STARTED("not_started"),
    IN_PROGRESS("in_progress"),
    INVALID_REQUEST("invalid_request"),
    COMPRESSING("compressing"),
    CREATING_HASH("creating_hash"),
    COMPLETE("complete"),
    ERROR("error");
    
    /**
     * The text field.
     */
    private final String text;
    
    /**
     * Default constructor
     * @param text Text associated with the enumeration value.
     */
    private JobStateType(String text) {
        this.text = text;
    }
    
    /**
     * Getter method for the text associated with the enumeration value.
     * 
     * @return The text associated with the instanced enumeration type.
     */
    public String getText() {
        return this.text;
    }
}
