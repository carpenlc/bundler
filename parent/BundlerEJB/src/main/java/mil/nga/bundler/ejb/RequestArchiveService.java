package mil.nga.bundler.ejb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mil.nga.PropertyLoader;
import mil.nga.bundler.BundleRequest;
import mil.nga.bundler.exceptions.PropertiesNotLoadedException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.messages.BundleRequest2;

/**
 * Session Bean implementation class RequestArchiveService
 * 
 * This class is mainly for debugging purposes.  As input it takes a 
 * BundlerRequest object and the job ID assigned to that request and
 * marshals the data in JSON format to an on disk file.  
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class RequestArchiveService 
        extends PropertyLoader 
        implements BundlerConstantsI {
    /**
     * Set up the LogBack system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            RequestArchiveService.class);
    
    /**
     * Default date format added to generated job IDs.
     */
    private static final String DATE_FORMAT = "yyyyMMdd-HH:mm:ss:SSS";
    
    /**
     * Filename extension to add to the output JSON file.
     */
    private static final String EXTENSION = ".json";
    
    /**
     * String to prepend to the front of generated job IDs. 
     */
    private static final String DEFAULT_JOB_ID = "UNAVAILABLE";
    
    /**
     * Sub-directory underneath the staging directory in which the output 
     * are stored.
     */
    private static final String DEFAULT_SUB_DIRECTORY = "debug";
    
    /**
     * Calculated path in which the request data will be stored.
     */
    private String outputPath = null;
    
    /**
     * Default constructor. 
     */
    public RequestArchiveService() { 
        super(BundlerConstantsI.PROPERTY_FILE_NAME);
        try {
            setOutputPath(getProperty(STAGING_DIRECTORY_PROPERTY));
            checkOutputPath();
        }
        catch (PropertiesNotLoadedException pnle) {
            LOGGER.warn("An unexpected PropertiesNotLoadedException " 
                    + "was encountered.  Please ensure the application "
                    + "is properly configured.  Exception message [ "
                    + pnle.getMessage()
                    + " ].");
        }
    }

    /**
     * Ensure that the output path exists.
     */
    private void checkOutputPath() {

        File file = new File(getOutputPath());
        
        // Set file permissions on the hash file to wide open.
        if (!file.exists()) {
            LOGGER.warn(
                    "Expected output directory does not exist.  Creating "
                    + "directory [ "
                    + getOutputPath()
                    + " ].");
            file.mkdir();
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true, false);
        }
    }
    
    /**
     * If the job ID is not supplied we'll still export the request data but
     * the job ID will be generated from the current system time.
     * 
     * @return A default job ID.
     */
    private String generateBogusJobID() {
        
        StringBuilder sb = new StringBuilder();
        DateFormat  df = new SimpleDateFormat(DATE_FORMAT);
        
        sb.append(DEFAULT_JOB_ID);
        sb.append("_");
        sb.append(df.format(System.currentTimeMillis()));
        
        return sb.toString();
    }
    
    /**
     * Method to assemble the full path to the target output file.
     * 
     * @param jobID The "main" part of the filename.
     * @return The full path to the target output file.
     */
    private String getFilePath(String jobID) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(getOutputPath());
        if (!sb.toString().endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(jobID.trim());
        sb.append(EXTENSION);
        
        return sb.toString();
    }
    
    /**
     * Save the input String containing pretty-printed JSON data to an output 
     * file.  The output file path is calculated using the input jobID.
     * 
     * @param request The "pretty-printed" JSON data.
     * @param jobID The job ID (used to calculate the output file name)
     */
    private void saveToFile(String request, String jobID) {
        
        FileWriter writer = null;
        String fileName = getFilePath(jobID);
        
        if ((request != null) && (!request.isEmpty())) {
            
            LOGGER.info("Saving request information for job ID [ "
                        + jobID 
                        + " ] in file name [ "
                        + fileName
                        + " ].");
            
            try {
                
                File file = new File(fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                
                writer = new FileWriter(file);
                writer.write(request);
                writer.flush();
            }
            catch (IOException ioe) {
                LOGGER.error("Unexpected IOException encountered while " 
                        + "attempting to archive the request associated with "
                        + "job ID [ "
                        + jobID 
                        + " ] in filename [ "
                        + fileName
                        + " ].  Error message [ "
                        + ioe.getMessage()
                        + " ].");
            }
            finally {
                if (writer != null) {
                    try { writer.close(); } catch (Exception e) { }
                }
            }
        }
        else {
            LOGGER.warn("Unable to marshal the request data associated with "
                    + "job ID [ "
                    + jobID 
                    + " ].  The output String is null or empty.");
        }
    }
    
    /**
     * External interface used to marshal a BundleRequest into a JSON-based
     * String and then store the results in an on-disk file.
     * 
     * @param request Incoming BundleRequest object.
     * @param jobID The job ID assigned to input BundleRequest object.
     */
    public void archiveRequest(BundleRequest request, String jobID) {
        
        if (request != null) {
            if ((jobID == null) || (jobID.isEmpty())) {
                jobID = generateBogusJobID();
                LOGGER.warn("The input Job ID is null, or not populated.  "
                        + "Using generated job ID [ "
                        + jobID
                        + " ].");
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Archiving incoming request for job ID [ "
                        + jobID
                        + " ].");
            }
            
            try { 
                
                ObjectMapper mapper = new ObjectMapper();
                String requestString = 
                        mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(request);
                saveToFile(requestString, jobID);
                
            }
            catch (JsonProcessingException jpe) {
                LOGGER.error("Unexpected JsonProcessingException encountered "
                        + "while attempting to marshal the client supplied "
                        + "BundleRequest object for job ID [ "
                        + jobID
                        + " ].  Error message [ "
                        + jpe.getMessage()
                        + " ].");
            }
        }
        else {
            LOGGER.error("The input BundleRequest is null.  Unable to "
                    + "archive the incoming request information.");
        }
    }
    
    /**
     * External interface used to marshal a BundleRequest2 into a JSON-based
     * String and then store the results in an on-disk file.
     * 
     * @param request Incoming BundleRequest2 object.
     * @param jobID The job ID assigned to input BundleRequest2 object.
     */
    public void archiveRequest2(BundleRequest2 request, String jobID) {
        
        if (request != null) {
            if ((jobID == null) || (jobID.isEmpty())) {
                jobID = generateBogusJobID();
                LOGGER.warn("The input Job ID is null, or not populated.  "
                        + "Using generated job ID [ "
                        + jobID
                        + " ].");
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Archiving incoming request for job ID [ "
                        + jobID
                        + " ].");
            }
            
            try { 
                
                ObjectMapper mapper = new ObjectMapper();
                String requestString = 
                        mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(request);
                saveToFile(requestString, jobID);
                
            }
            catch (JsonProcessingException jpe) {
                LOGGER.error("Unexpected JsonProcessingException encountered "
                        + "while attempting to marshal the client supplied "
                        + "BundleRequest2 object for job ID [ "
                        + jobID
                        + " ].  Error message [ "
                        + jpe.getMessage()
                        + " ].");
            }
        }
        else {
            LOGGER.error("The input BundleRequest2 is null.  Unable to "
                    + "archive the incoming request information.");
        }
    }
    
    /**
     * Getter method for the target output path.
     * 
     * @return The location to use for storing the incoming request.
     */
    private String getOutputPath() {
        if ((outputPath == null) || 
                (outputPath.equalsIgnoreCase(""))) {
            outputPath = System.getProperty("java.io.tmpdir");
            if (!outputPath.endsWith(File.separator)) {
                outputPath = outputPath + File.separator;
            }
            outputPath = outputPath + DEFAULT_SUB_DIRECTORY;
        }
        return outputPath;
    }
    
    /**
     * Setter method for the output path.  If the input directory is 
     * not supplied, the location specified by the <code>java.io.tmpdir</code>
     * is used.
     * 
     * @param dir Location for storing the output data.
     */
    private void setOutputPath(String dir) {
        
        if ((dir == null) || (dir.trim().equalsIgnoreCase(""))) {
            outputPath = System.getProperty("java.io.tmpdir");    
            LOGGER.warn("Application property [ " 
                    + STAGING_DIRECTORY_PROPERTY
                    + " ] is not defined.  Using system property.  Output "
                    + "path is [ "
                    + outputPath
                    + " ].");
        }
        else {
            outputPath = dir;
        }
        
        if (!outputPath.endsWith(File.separator)) {
            outputPath = outputPath + File.separator;
        }
        outputPath = outputPath + DEFAULT_SUB_DIRECTORY;
    }
}
