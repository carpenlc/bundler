package mil.nga.bundler.types;

import mil.nga.bundler.exceptions.UnknownHashTypeException;

/**
 * Enumeration type identifying what type of file hashes are supported.
 * 
 * @author L. Craig Carpenter
 */
public enum HashType {
	MD5("md5"),
	SHA1("sha1"),
	SHA256("sha256"),
	SHA384("sha384"),
	SHA512("sha512");
	
	/**
	 * The text field.
	 */
	private final String text;
	
	/**
	 * Default constructor.
	 * 
	 * @param text Text associated with the enumeration value.
	 */
	private HashType(String text) {
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
	 * @return The appropriate HashType enum value.
	 * @throws UnknownHashTypeException Thrown if the caller submitted a String 
	 * that did not match one of the existing HashTypes. 
	 */
	public static HashType fromString(String text) 
			throws UnknownHashTypeException {
		if (text != null) {
			for (HashType type : HashType.values()) {
				if (text.trim().equalsIgnoreCase(type.getText())) {
					return type;
				}
			}
		}
		throw new UnknownHashTypeException("Unknown hash type requested!  " 
				+ "Hash requested [ " 
				+ text
				+ " ].");
	}
}
