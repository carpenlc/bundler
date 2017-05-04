package mil.nga.bundler.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.types.ArchiveType;

/**
 * Simple POJO used to hold the contents of a client-initiated bundle
 * request.  This object utilizes JAX-B annotations for marshalling/
 * unmarshalling client supplied JSON data.  Object is populated by 
 * a RESTful (JAX-RS) service call via POST.
 *  
 * This version of the BundleRequest object allows clients to specify the path
 * of the input file within the output archive file.  This was added to support
 * the U.S. Army MPSU software (i.e. FalconView).
 * 
 * Notes:
 * Had some problems deploying this application to JBoss.  Though the Jersey
 * annotations (Xml*) should have been sufficient, JBoss would not 
 * interpret the input as JSON.  We added the the Jackson annotations to work
 * around the issue.
 * 
 * Ran into even more problems deploying to Wildfly.  Had to upgrade to Jackson
 * 2.x annotations (i.e. com.fasterxml vs. org.codehaus). 

 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = BundleRequest2.BundleRequestBuilder.class)
public class BundleRequest2 implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 5588539869510887583L;

    // Internal members
    private final boolean     redirect;
    private final int         maxSize;
    private final String      outputFilename;
    private final String      userName;
    private final ArchiveType type;
    
    /**
     * Annotated list of files that will be processed by the bundler.
     */
    private List<FileRequest> files = new ArrayList<FileRequest>();
            
    /**
     * Private constructor forcing the builder design pattern.  
     * @param The builder object. 
     */
    private BundleRequest2(BundleRequestBuilder builder) {
    	redirect       = builder.redirect;
    	maxSize        = builder.maxSize;
    	outputFilename = builder.outputFilename;
    	userName       = builder.userName;
    	type           = builder.type;
    	files          = builder.files;
    }
    
    /**
     * Method used to add files to the target internal list of files.
     * @param file A full path to a String
     */
    public void add(String file, String path) {
        if (files == null) {
            files = new ArrayList<FileRequest>();
        }
        files.add(new FileRequest.FileRequestBuilder()
        		.file(file)
        		.archivePath(path)
        		.build());
    }
    
    /**
     * Method used to add a new <code>FileRequest</code> object to the 
     * internal list of files requested by the user.
     * 
     * @param value The <code>FileRequest</code> object to add.
     */
    public void add(FileRequest value) {
    	if (files == null) {
            files = new ArrayList<FileRequest>();
        }
    	if (value != null) {
    		files.add(value);
    	}
    }
    
    /**
     * Getter method for the name of the output archive filename.  This is 
     * an optional parameter.  If it is not supplied, a default filename will
     * be calculated.
     * @return The suggested name for the output archive files.
     */
    @XmlElement(name="archive_file")
    @JsonProperty(value="archive_file")
    public String getOutputFilename() {
        return outputFilename;
    }
    
    /**
     * Getter method for the list of files to archive/compress.
     * @return The list of filenames to archive/compress.
     */
    @XmlElement(name="files")
    @JsonProperty(value="files")
    public List<FileRequest> getFiles() {
        return files;
    }
    
    /**
     * Getter method for the maximum size of output archive files.
     * @return The maximum size (in MBytes) of output archive files.
     */
    @XmlElement(name="max_size")
    @JsonProperty(value="max_size")
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * Getter method determining whether the client should be redirected
     * after job initiation.
     * @return If true the user will be redirected to the status page.
     * If false, it is assumed that the client will handle status tracking.
     */
    @XmlElement(name="redirect")
    @JsonProperty(value="redirect")
    public boolean getRedirect() {
        return redirect;
    }
    
    /**
     * Getter method for the type of output archive file to create.  If this
     * parameter is not supplied, it the bundler will create an output zip file.
     * @return The type of output archive file to create.
     */
    @XmlElement(name="type")
    @JsonProperty(value="type")
    public ArchiveType getType() {
        return type;
    }
    
    /**
     * Getter method for the name of the client submitting the bundle request.
     * @return The user name of the client submitting the bundle request.
     */
    @XmlElement(name="user_name")
    @JsonProperty(value="user_name")
    public String getUserName() {
        return userName;
    }
    
    /**
     * Setter allowing classes to reset the file list.  This was added to 
     * support the need for removing duplicate entries from the in out list.
     * 
     * @param value The list of files.
     */
    public void setFiles(List<FileRequest> value) {
        files = value;
    }
    
    /**
     * Overridden toString method to dump the request into a human-readable format.
     * @return Printable string
     */
    public String toString() {
        
        String        newLine = System.getProperty("line.separator");
        StringBuilder sb      = new StringBuilder();
        
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("Type            : ");
        sb.append(type);
        sb.append(newLine);
        sb.append("User Name       : ");
        sb.append(userName);
        sb.append(newLine);
        sb.append("Max Size        : "); 
        sb.append(maxSize);
        sb.append(newLine);
        sb.append("Redirect        : ");
        sb.append(redirect);
        sb.append(newLine);
        sb.append("Output Filename : ");
        sb.append(outputFilename);
        sb.append(newLine);
        sb.append("Files            : ");
        sb.append(newLine);
        if ((files != null) && (files.size() > 0)) {
            for (FileRequest file : files) {
                sb.append("    ");
                sb.append(file);
                sb.append(newLine);
            }
        }
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        return sb.toString();
    }
    
    /**
     * Internal static class implementing the Builder creation pattern for 
     * new QueryRequestAccelerator objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class BundleRequestBuilder implements BundlerConstantsI {
    
    	// Private internal members
    	private int               maxSize        = -1;
        private boolean           redirect       = false;
    	private String            outputFilename = null;
    	private String            userName       = null;
        private ArchiveType       type           = ArchiveType.ZIP;
        private List<FileRequest> files    = new ArrayList<FileRequest>();
        
        
        public BundleRequest2 build() throws IllegalStateException {
        	validateBundleRequestObject();
        	return new BundleRequest2(this);
        }
        
        /**
         * Setter method for the maximum size of output archive files.
         * 
         * @param value The maximum size (in MBytes) of output archive files.
         * @return Handle to the builder object.
         */
        @JsonProperty(value="max_size")
        public BundleRequestBuilder maxSize(int value) {
        	maxSize = value;
        	return this;
        }
        
        /**
         * Setter method for the boolean determining whether the client 
         * should be redirected after job initiation.
         * 
         * @param value If true the user will be redirected to the internal 
         * status page.  If false, it is assumed that the client will handle 
         * status tracking.
         */
        @JsonProperty(value="redirect")
        public BundleRequestBuilder redirect(boolean value) {
            redirect = value;
            return this;
        }
        
        /**
         * Setter method for the type of output archive files to create.
         * 
         * @param value The type of output archives to create.
         * @return Handle to the builder object.
         */
        @JsonProperty(value="type")
        public BundleRequestBuilder type(ArchiveType value) {
        	type = value;
        	return this;
        }
        
        /**
         * Setter method for the name of the output archive file to create.
         * 
         * @param value The name of the output archive file to create.
         */
        @JsonProperty(value="archive_file")
        public BundleRequestBuilder outputFilename(String value) {
            outputFilename = value;
            return this;
        }
        
        /**
         * Setter method for the name of the client submitting the bundle request.
         * 
         * @param value The user name of the client submitting the bundle request.
         */
        @JsonProperty(value="user_name")
        public BundleRequestBuilder userName(String value) {
            userName = value;
            return this;
        }
        
        /**
         * List of files to bundle
         * 
         * @param value The lit of files to bundle.
         */
        @JsonProperty(value="files")
        public BundleRequestBuilder files(List<FileRequest> values) {
        	files = values;
        	return this;
        }
        
        /**
         * Validate internal member variables.  This should be called prior to
         * the actual construction of the parent object.
         * 
         * @param object The <code>BundleRequest</code> object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateBundleRequestObject() throws IllegalStateException {
        	
        	if ((maxSize <= MIN_ARCHIVE_SIZE) || (maxSize > MAX_ARCHIVE_SIZE)) {
        		maxSize = DEFAULT_MAX_ARCHIVE_SIZE;
        	}
        	
        	if ((userName == null) || (userName.isEmpty())) {
        		userName = DEFAULT_USERNAME;
        	}
        	
        	if ((outputFilename == null) || (outputFilename.isEmpty())) {
        		// TODO:  This is probably the wrong setting - find the correct one later.
        		outputFilename = DEFAULT_FILENAME_PREFIX;
        	}
        	
        }
    	
    }
}

