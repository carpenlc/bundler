package mil.nga.bundler.types;

import mil.nga.bundler.exceptions.UnknownArchiveTypeException;

/**
 * Enumeration type identifying what type of output archive/compression 
 * algorithms are currently supported. 
 * 
 * @author L. Craig Carpenter
 */
public enum ArchiveType {
    ZIP("zip"),
    TAR("tar"),
    GZIP("gz"),
    BZIP2("bz2");
    
    /**
     * The text field.
     */
    private final String text;
    
    /**
     * Default constructor
     * @param text Text associated with the enumeration value.
     */
    private ArchiveType(String text) {
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
    
    /**
     * Convert an input String to it's associated enumeration type.  There
     * is no default type, if an unknown value is supplied an exception is
     * raised.
     * 
     * @param text Input text information
     * @return The appropriate ArchiveType enum value.
     * @throws UnknownArchiveException Thrown if the caller submitted a String 
     * that did not match one of the existing ArchiveTypes. 
     */
    public static ArchiveType fromString(String text) 
            throws UnknownArchiveTypeException {
        if (text != null) {
            for (ArchiveType type : ArchiveType.values()) {
                if (text.trim().equalsIgnoreCase(type.getText())) {
                    return type;
                }
            }
        }
        throw new UnknownArchiveTypeException("Unknown archive type requested!  " 
                + "Archive requested [ " 
                + text
                + " ].");
    }
}
