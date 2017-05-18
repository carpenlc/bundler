package mil.nga.bundler.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * POJO implemented to allow clients to specify both the file they want 
 * bundled how they want it to appear in the output archive file.  
 * 
 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = FileRequest.FileRequestBuilder.class)
public class FileRequest implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 3503863876721434968L;

    // Private internal members
    private final String file;
    private String path;
    
    /**
     * No-arg constructor required by JAX-B.
     */
    public FileRequest(FileRequestBuilder builder) {
    	file = builder.file;
    	path = builder.path;
    } 
    
    /**
     * Getter method for the full path to the target file to bundle.
     * @return On-disk path to the target file.
     */
    @XmlElement(name="file")
    @JsonProperty(value="file")
    public String getFile() {
        return file;
    }
    
    /**
     * Getter method for the path inside the output archive file in which the 
     * requested file will be stored.
     * 
     * @return The path inside the output archive file in which the requested 
     * file will be stored.
     */
    @XmlElement(name="archive_path")
    @JsonProperty(value="archive_path")
    public String getArchivePath() {
        return path;
    }
    
    /**
     * Setter method for the path inside the output archive file in which the 
     * requested file will be stored.
     * 
     * @param value The path inside the output archive file in which the requested 
     * file will be stored.
     */
    public void setArchivePath(String value) {
        path = value;
    }
    
    /**
     * Overridden toString method to dump the request into a human-readable 
     * format.
     * 
     * @return Printable string
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        sb.append("File => [ ");
        sb.append(getFile());
        sb.append(" ]");
        if (getArchivePath() != null) {
        	sb.append(", Archive Path => [ ");
            sb.append(getArchivePath());
            sb.append(" ]");
        }
        return sb.toString();
        
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new QueryRequestAccelerator objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class FileRequestBuilder {
    	
    	// Private internal members
    	private String file;
    	private String path;
    	
        /**
         * Method used to actually construct the FileRequest object.
         * 
         * @return A constructed and validated FileRequest object.
         * @throws IllegalStateException Thrown if any of the internal members
         * are invalid or inconsistent.
         */
        public FileRequest build() 
                throws IllegalStateException {
            
        	FileRequest object = new FileRequest(this);
        	validateFileRequestObject(object);
            return object;
            
        }
        
        /**
         * Setter method for String representing the full path to the target
         * file.
         * 
         * @param value The path to the target file.
         */
        @JsonProperty(value="file")
        public FileRequestBuilder file(String value) {
        	if ((value != null) && (!value.isEmpty())) {
        		file = value.trim();
        	}
        	else {
        		file = null;
        	}
            return this;
        }
        
        /**
         * Setter method for String representing the full path to the target
         * file within the output archive.
         * 
         * @param value The path to the target file within the output archive.
         */
        @JsonProperty(value="archive_path")
        public FileRequestBuilder archivePath(String value) {
        	if ((value != null) && (!value.isEmpty())) {
        		path = value.trim();
        	}
        	else {
                path = null;
        	}
            return this;
        }
        
        /**
         * Validate internal member variables.
         * 
         * @param object The Product object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateFileRequestObject(
                FileRequest object) 
                    throws IllegalStateException {
            
            if (object != null) {
            	if (object.file == null) {
            		throw new IllegalStateException("Input file name is null "
            				+ "or empty.  Target file name cannot be empty.");
            	}
            }
        }
    }
}
