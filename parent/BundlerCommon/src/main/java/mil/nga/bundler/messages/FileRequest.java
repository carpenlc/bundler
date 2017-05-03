package mil.nga.bundler.messages;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO implemented to allow clients to specify both the file they want 
 * bundled how they want it to appear in the output archive file.  This 
 * class is used in conjunction with the BundleRequest2 object.
 * 
 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileRequest implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 3503863876721434968L;

    /**
     * The full path to the target file to bundle.
     */
    private String file = null;
    
    /**
     * The path inside the output archive file in which the requested file 
     * will be stored.
     */
    private String path = null;
    
    /**
     * No-arg constructor required by JAX-B.
     */
    public FileRequest() {} 
    
    /**
     * Alternate constructor allowing clients to populate internal members on 
     * construction. 
     * 
     * @param file The full path to the target file to bundle.
     * @param path The path inside the output archive file in which the requested file 
     * will be stored.
     */
    public FileRequest(String file, String path) {
        setFile(file);
        setPath(path);
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
    @XmlElement(name="path")
    @JsonProperty(value="path")
    public String getPath() {
        return path;
    }
    
    /**
     * Setter method for the full path to the target file to bundle.
     * 
     * @param valu On-disk path to the target file.
     */
    public void setFile(String value) {
        file = value;
    }
    
    /**
     * Setter method for the path inside the output archive file in which the 
     * requested file will be stored.
     * 
     * @param value The path inside the output archive file in which the requested 
     * file will be stored.
     */
    public void setPath(String value) {
        path = value;
    }
    
    
     /**
     * Overridden toString method to dump the request into a human-readable 
     * format.
     * 
     * @return Printable string
     */
    public String toString() {
        
        String        newLine = System.getProperty("line.separator");
        StringBuilder sb      = new StringBuilder();
        
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("File            : ");
        sb.append(getFile());
        sb.append(newLine);
        sb.append("Path             : ");
        sb.append(getPath());
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        return sb.toString();
    }
}
