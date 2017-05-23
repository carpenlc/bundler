package mil.nga.bundler;

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
 * The original-design POJO used to hold the contents of a client-initiated 
 * bundle request.  This object utilizes JAX-B annotations for serializing/
 * deserializing client supplied JSON data.  Object is populated by 
 * a RESTful (JAX-RS) service call via POST.
 * 
 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = BundleRequest.BundleRequestBuilder.class)
public class BundleRequest implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -1482179251859700103L;

    // Internal members
    private final boolean     redirect;
    private final int         maxSize;
    private final String      outputFilename;
    private final ArchiveType type;
    
    /**
     * Username is not set as final because it is usually set outside of 
     * construction.
     */
    private String            userName;
    
    /**
     * Annotated list of files that will be processed by the bundler.
     */
    private List<String> files = new ArrayList<String>();
            
    /**
     * Default constructor.
     */
    private BundleRequest(BundleRequestBuilder builder) {
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
    public void add(String file) {
        if (files == null) {
            files = new ArrayList<String>();
        }
        files.add(file);
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
    public List<String> getFiles() {
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
     * 
     * @return The type of output archive file to create.
     */
    @XmlElement(name="type")
    @JsonProperty(value="type")
    public ArchiveType getType() {
        return type;
    }
    
    /**
     * Getter method for the name of the client submitting the bundle request.
     * 
     * @return The user name of the client submitting the bundle request.
     */
    @XmlElement(name="user_name")
    @JsonProperty(value="user_name")
    public String getUserName() {
        return userName;
    }
    
    
    /**
     * Method allowing for the replacement of internal file list.  This was 
     * added to support the validation of an incoming list of files. 
     * @param value The (hopefully) validated list of files to process.
     */
    public void setFileList(List<String> value) {
        if ((value != null) && (value.size() > 0)) {
            files = value;
        }
    }
    
    /**
     * Setter method for the name of the client submitting the bundle request.
     * @param value The user name of the client submitting the bundle request.
     */
    public void setUserName(String value) {
        userName = value;
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
        if ((files != null) && (files.size() > 0)) {
            for (String file : files) {
                sb.append("File            : ");
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
     * new BundleRequest objects.  
     * 
     * @author L. Craig Carpenter
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static class BundleRequestBuilder implements BundlerConstantsI {
        
        // Private internal member objects.
        private boolean      redirect       = false;
        private int          maxSize        = -1;
        private String       outputFilename = null;
        private String       userName       = null;
        private ArchiveType  type           = ArchiveType.ZIP;
        private List<String> files          = new ArrayList<String>();
        
        /**
         * Method used to construct an object of type BundleRequest.
         * 
         * @return A newly constructed object of type 
         * <code>BundleRequest</code>
         * @throws IllegalStateException Thrown if there are issues with
         * client supplied data.
         */
        public BundleRequest build() throws IllegalStateException {
            validateBundleRequestObject();
            return new BundleRequest(this);
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
        public BundleRequestBuilder files(List<String> values) {
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
                outputFilename = FileNameGenerator
                        .getInstance()
                        .getFilename();
            }
        }
    }
}

