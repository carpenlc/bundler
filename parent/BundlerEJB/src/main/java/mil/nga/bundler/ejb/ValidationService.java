package mil.nga.bundler.ejb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import mil.nga.bundler.BundleRequest;
import mil.nga.bundler.PathGenerator;
import mil.nga.bundler.exceptions.InvalidRequestException;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;
import mil.nga.bundler.exceptions.ValidationErrorCodes;
import mil.nga.bundler.messages.BundleRequestMessage;
import mil.nga.bundler.messages.FileRequest;
import mil.nga.bundler.model.ValidFile;
import mil.nga.bundler.types.ArchiveType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class ValidationService
 *
 * The validation service encapsulates all of the logic involved in validating
 * input BundleRequest objects.  We have attempted to include checks for all 
 * known problems that have been encountered in testing.  
 * 
 * This session bean should be updated as new error conditions are encountered.
 * 
 * Note: It is O.K. if a request contains individual files that are not valid.  
 * An exception is only thrown if none of the input files are valid.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
public class ValidationService {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ValidationService.class);
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public ValidationService() { }
    
    /**
     * Check to see if the target filename exists on the file system.  If the 
     * file exists, and has a size greater than zero, a populated 
     * <code>ValidFile<code> object is returned.
     * 
     * @param filename The target filename originally supplied by a client.
     * @return A <code>ValidFile<code> object if the file exists on the 
     * file system.  Null otherwise.
     */
    public ValidFile validateOneFile(String filename) {
        
        ValidFile valid = null;
        
        if ((filename != null) && (!filename.isEmpty())) {
            Path file = Paths.get(filename.trim());
            if (Files.exists(file)) {
                try {
                    
                    BasicFileAttributes attrs = Files.readAttributes(
                            file, 
                            BasicFileAttributes.class);
                    
                    FileTime lastModifiedDate = attrs.lastModifiedTime();
                    long size = attrs.size();
                    
                    if (size > 0) {
                        valid = new ValidFile(
                                filename, 
                                size,
                                lastModifiedDate.toMillis());
                        
                        if (LOGGER.isDebugEnabled()) {
                            
                        }
                    }
                    else {
                        LOGGER.warn("File [ "
                                + filename
                                + " ] exists on the file system but has zero "
                                + "length.");
                    }
                }
                catch (IOException ioe) {
                    LOGGER.warn("Unexpected IOException raised while "
                            + "attempting to read file attributes for file [ "
                            + filename
                            + " ].  Exception message [ "
                            + ioe.getMessage()
                            + " ].");
                }
            }
            else {
                LOGGER.warn("File [ "
                        + filename
                        + " ] does not exist on the file system.");
            }
        }
        else {
            LOGGER.warn("Client supplied a null or empty file name.  "
                    + "Skipping.");
        }
        return valid;
    }
    
    /**
     * Validate that each of the files in the input list actually exist on the 
     * file system.
     * 
     * @param files A list of files supplied by a client.
     * @return A list of files that are valid (i.e. they exist on the file 
     * system and are not 0 length).
     */
    public List<ValidFile> validateFiles(List<String> files) 
            throws InvalidRequestException {
        
        List<ValidFile> validFiles = new ArrayList<ValidFile>();
        
        if ((files != null) && (files.size() > 0)) {
            for (String file : files) {
                ValidFile validFile = validateOneFile(file);
                if (validFile != null) {
                    validFiles.add(validFile);
                }
                else {
                    LOGGER.warn("The target file [ " 
                            + file
                            + " ] is not valid.");
                }
            }
        }
        else {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
        return validFiles;
    }
    
    /**
     * Validate that each of the files in the input list actually exist on the 
     * file system.
     * 
     * @param files A list of files supplied by a client.
     * @return A list of files that are valid (i.e. they exist on the file 
     * system and are not 0 length).
     */
    public List<ValidFile> validateFiles2(List<FileRequest> files) 
            throws InvalidRequestException {
        
        List<ValidFile> validFiles = new ArrayList<ValidFile>();
        
        if ((files != null) && (files.size() > 0)) {
            for (FileRequest file : files) {
                ValidFile validFile = validateOneFile(file.getFile().trim());
                if (validFile != null) {
                    validFile.setEntryPath(file.getArchivePath());
                    validFiles.add(validFile);
                }
                else {
                    LOGGER.warn("The target file [ " 
                            + file
                            + " ] is not valid.");
                }
            }
        }
        else {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
        return validFiles;
    }
    
    /**
     * Method used to eliminate duplicate entries from the input List of 
     * String values.  This method was added for the benefit of the Nova-Tech
     * Solutions folks who were sending bundle requests with duplicate entries
     * yet they could not handle the output archives that contained the 
     * duplicate entries that they asked for.
     *  
     * @param files Raw input list of files. 
     * @return A new list containing no duplicate entries.
     */
    private List<String> eliminateDuplicates(List<String> files) {
        
        List<String> deDupList = null;
        
        if (files != null) {
            
            // Stuff the list into a Set (which doesn't allow duplicates) then
            // create a new List out of the Set.
            Set<String> deDupSet = new HashSet<String>(files);
            deDupList = new ArrayList<String>(deDupSet);
        }
        else {
            LOGGER.warn("Null List object supplied to routine for "
                    + "eliminating duplicates.");
        }
        
        return deDupList;
    }
    
    /**
     * Method used to eliminate duplicate entries from the input List of 
     * String values.  This method was added for the benefit of the Nova-Tech
     * Solutions folks who were sending bundle requests with duplicate entries
     * yet they could not handle the output archives that contained the 
     * duplicate entries that they asked for.
     *  
     * @param files Raw input list of files. 
     * @return A new list containing no duplicate entries.
     */
    private List<FileRequest> eliminateDuplicates2(List<FileRequest> files) {
        
        List<FileRequest> deDupList = null;
        HashMap<String, FileRequest> map = new HashMap<String, FileRequest>();
        
        if ((files != null) && (files.size() > 0)) {
            
            // Stuff the keys from the FileRequest object into the key portion
            // of the HashMap.  This will effectively eliminate the duplicates.
            for (FileRequest file : files) {
                map.put(file.getFile().trim(), file);
            }
            
            Collection<FileRequest> deDupCollection = map.values();
            deDupList = new ArrayList<FileRequest>(deDupCollection);
            
        }
        else {
            LOGGER.warn("Null List object supplied to routine for "
                    + "eliminating duplicates.");
        }
        
        return deDupList;
    }
    
    /**
     * Check to see if the client supplied a valid output archive type.  For 
     * valid types @see mil.nga.bundler.types.ArchiveType.
     * 
     * @param value The archive type supplied by the client.  
     * @throws InvalidRequestException Thrown if the client requested an type
     * not supported. 
     */
    private void checkArchiveType(String value) 
            throws InvalidRequestException {
        if ((value == null) || (value.isEmpty())) {
            LOGGER.warn("The type of output archive was not supplied by the "
                    + "client.  It will be defaulted to ZIP.");
        }
        else {
            try {
                // Check the archive type. 
                ArchiveType.fromString(value);
            }
            catch (UnknownArchiveTypeException uate) {
                LOGGER.error("Invalid archive type requested.  Client requested "
                        + "unsupported archive type [ "
                        + value
                        + " ].");
                throw new InvalidRequestException(
                        ValidationErrorCodes.INVALID_ARCHIVE_TYPE);
            }
        }
    }
    
    /**
     * Check to see if the client actually supplied something to bundle.
     * 
     * @param files The list of files requested by the client.
     * @throws InvalidRequestException Thrown if the client neglected to 
     * request any files to bundle.
     */
    private void checkFileList(List<String> files) 
            throws InvalidRequestException {
        if ((files == null) || (files.size() == 0)) {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
    }
    
    /**
     * Check to see if the client actually supplied something to bundle.
     * 
     * @param files The list of files requested by the client.
     * @throws InvalidRequestException Thrown if the client neglected to 
     * request any files to bundle.
     */
    private void checkFileList2(List<FileRequest> files) 
            throws InvalidRequestException {
        if ((files == null) || (files.size() == 0)) {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
    }
    

    
    /**
     * 
     * @param request
     * @throws InvalidRequestException
     */
    public List<ValidFile> validate(BundleRequest request) 
            throws InvalidRequestException {
        
        LOGGER.info("validate() called.");
        List<ValidFile> validFiles = null;
        
        checkArchiveType(request.getType());
        checkFileList(request.getFiles());
        
        // Reset the input list of files with a de-duplicated version
        List<String> deDupList = eliminateDuplicates(request.getFiles());
        request.setFileList(deDupList);
        
        // Next, make sure that clients actually supplied something valid to 
        // bundle.  This was implemented because clients started supplying a 
        // list of empty files. 
        validFiles = validateFiles(deDupList);
        if ((validFiles == null) || (validFiles.size() == 0)) { 
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_VALID_INPUT_FILES_FOUND);
        }
        
        // Not sure this is the best place to put this call, but set all 
        // of the entry paths in the list of valid files.
        PathGenerator.getInstance().setEntryPaths(validFiles);
        
        return validFiles;
    }
    
}
