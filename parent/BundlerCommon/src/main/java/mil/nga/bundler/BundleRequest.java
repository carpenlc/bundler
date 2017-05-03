package mil.nga.bundler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple POJO used to hold the contents of a client-initiated bundle
 * request.  This object utilizes JAX-B annotations for marshalling/
 * unmarshalling client supplied JSON data.  Object is populated by 
 * a RESTful (JAX-RS) service call via POST.
 *  
 * Had some problems deploying this application to JBoss.  Though the Jersey
 * annotations (Xml*) should have been sufficient, JBoss would not 
 * interpret the input as JSON.  We added the the Jackson annotations to work
 * around the issue.
 * 
 * Ran into even more problems deploying to Wildfly.  Had to upgrade to Jackson
 * 2.x annotations (i.e. com.fasterxml vs. org.codehaus).  We also had to remove
 * the Jersey (i.e. XML) annotations.

 * @author L. Craig Carpenter
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class BundleRequest implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = -1482179251859700103L;

	/**
	 * The type of archive to produce.
	 */
	private String type;
	
	/**
	 * Maximum size of the output archives
	 */
	private int maxSize = 400;
	
	/**
	 * The name to use for the output archive files
	 */
	private String filename = null;
	
	/**
	 * If true, redirect the client to the status page
	 */
	private boolean redirect = false;
	
	/**
	 * Annotated list of files that will be processed by the bundler.
	 */
	private List<String> files = new ArrayList<String>();
	
	/**
	 * The user name of the client submitting the bundle request.  If it 
	 * is not set in the input bundle request, an attempt will be made to 
	 * extract it from the input request headers.
	 */
	private String userName = null;
			
	/**
	 * No argument constructor required by JAX-B
	 */
	public BundleRequest() {}
	
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
	public String getFilename() {
		return filename;
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
	 * @return The type of output archive file to create.
	 */
	@XmlElement(name="type")
	@JsonProperty(value="type")
	public String getType() {
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
	 * Setter method for the name of the output archive filename.  This is 
	 * an optional parameter.  If it is not supplied, a default filename will
	 * be calculated.
	 * @param value The suggested name for the output archive files.
	 */
	public void setFilename(String value) {
		filename = value;
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
	 * Setter method for the maximum size of output archive files.  We're not 
	 * allowing a max size greater than 1G.
	 * @param value The maximum size (in MBytes) of output archive files.
	 */
	public void setMaxSize(int value) {
		if ((value < 1) || (value > 1000)) {
			maxSize = 400;
		}
		else {
			maxSize = value;
		}
	}
	
	/**
	 * Setter method determining whether the client should be redirected
	 * after job initiation.
	 * 
	 * @param value If true the user will be redirected to the status page.
	 * If false, it is assumed that the client will handle status tracking.
	 */
	public void setRedirect(boolean value) {
		redirect = value;
	}
	
	/**
	 * Setter method for the type of output archive file to create.  If this
	 * parameter is not supplied, it the bundler will create an output zip file.
	 * @return The type of output archive file to create.
	 */
	public void setType(String value) {
		if ((value == null) || (value.equalsIgnoreCase(""))) {
			type = "zip";
		}
		else {
			type = value;
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
		sb.append(filename);
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
}

