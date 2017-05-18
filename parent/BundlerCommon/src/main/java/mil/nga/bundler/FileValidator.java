package mil.nga.bundler;

import mil.nga.bundler.messages.FileRequest;
import mil.nga.bundler.model.FileEntry;
import mil.nga.util.FileFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.nga.bundler.exceptions.InvalidRequestException;
import mil.nga.bundler.exceptions.ValidationErrorCodes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * Class responsible for validating the input list of files that were POSTed
 * to the bundler application in the BundlerRequestMessage.  The function of 
 * this class is to ensure that there are files to bundle, all of the 
 * requested files actually exist on the file system, and that the user did 
 * not request any duplicate files.
 * 
 * @author L. Craig Carpenter
 */
public class FileValidator {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            FileValidator.class);
    
    /**
     * Method used to eliminate duplicate entries from the input List of 
     * String values.  This method was added for the benefit of the Nova-Tech
     * Solutions folks who were sending bundle requests with duplicate entries
     * yet they could not handle the output archives that contained the 
     * duplicate entries that they asked for.
     *  
     * @param filesRequested Raw input list of files. 
     * @return A new list containing no duplicate entries.
     */
    private List<String> eliminateStringDuplicates(List<String> files) {
        
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
     * @param files Raw input list of FileRequest objects. 
     * @return A new list containing no duplicate entries.
     */
    private List<FileRequest> eliminateDuplicates(List<FileRequest> files) {
        
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
     * Accessor method for the singleton instance of the FileValidator.
     * @return Handle to the singleton instance of the FileValidator.
     */
    public static FileValidator getInstance() {
        return FileValidatorHolder.getFactorySingleton();
    }
    
    /**
     * Ensure that the file defined by the input <code>FileRequest</code> 
     * object exists on the file system and is accessible. 
     * 
     * @param requestedFile File requested by the user.
     * @return A populated FileEntry object if the file is valid, 
     * null otherwise.
     */
    public FileEntry validateOneFile(FileRequest requestedFile) {
        FileEntry validated = null;
        if ((requestedFile != null) && 
                (requestedFile.getFile() != null) && 
                (!requestedFile.getFile().isEmpty())) {
            Path file = Paths.get(requestedFile.getFile().trim());
            if (Files.exists(file)) {
                try {
                    long size = Files.size(file);
                    validated = new FileEntry(
                            requestedFile.getFile(),
                            requestedFile.getArchivePath(),
                            size);
                }
                catch (IOException ioe) {
                    LOGGER.warn("Unexpected IOException accessing file [ "
                            + requestedFile.getFile()
                            + " ].  Error message [ "
                            + ioe.getMessage()
                            + " ].  No attempt will be made to bundle the "
                            + "target file.");
                }
            }
            else {
                LOGGER.warn("The requested file [ "
                        + requestedFile.getFile()
                        + " ] does not exist on the file system.");
            }
        }
        return validated;
    }
    
    /**
     * Ensure that the file defined by the input <code>String</code> 
     * object exists on the file system and is accessible. 
     * 
     * @param requestedFile File requested by the user.
     * @return A populated FileEntry object if the file is valid, 
     * null otherwise.
     */
    public FileEntry validateOneFile(String requestedFile) {
        FileEntry validated = null;
        if ((requestedFile != null) && 
                (!requestedFile.isEmpty())) {
            Path file = Paths.get(requestedFile.trim());
            if ((Files.exists(file)) && (!Files.isDirectory(file))) {
                try {
                    long size = Files.size(file);
                    validated = new FileEntry(
                            requestedFile,
                            null,
                            size);
                }
                catch (IOException ioe) {
                    LOGGER.warn("Unexpected IOException accessing file [ "
                            + requestedFile
                            + " ].  Error message [ "
                            + ioe.getMessage()
                            + " ].  No attempt will be made to bundle the "
                            + "target file.");
                }
            }
            else {
                LOGGER.warn("The requested file [ "
                        + requestedFile
                        + " ] does not exist on the file system.");
            }
        }
        return validated;
    }
    
    /**
     * Public method used to validate the list of files to be bundled.
     * 
     * @param filesRequested The raw list of files that were requested by the 
     * client.
     * @return A List of partially populated FileEntry objects.
     * @throws InvalidRequestException Thrown if any validation exceptions are 
     * encountered.
     */
    public List<FileEntry> validateStringList(List<String> filesRequested) 
            throws InvalidRequestException {
        List<FileEntry> validated = new ArrayList<FileEntry>();
        if ((filesRequested == null) || (filesRequested.size() < 1)) {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
        filesRequested = eliminateStringDuplicates(filesRequested);
        filesRequested = expandStringList(filesRequested);
        
        if ((filesRequested != null) && (!filesRequested.isEmpty())) { 
            for (String file : filesRequested) {
                
                
                FileEntry obj = validateOneFile(file);
                if (obj != null) {
                    PathGenerator.getInstance().setOneEntry(obj);
                    validated.add(obj);
                }
            }
        }
        return validated;
        
    }
    
    /**
     * This method was added to facilitate the bundling of directories. 
     * If any directories have been included in the request this method 
     * will walk the directory tree and add all regular files that reside 
     * in the directory to the output List.
     * 
     * @param filesRequested Original user-submitted list of files that 
     * are to be bundled.
     * @return A list of FileEntry objects.
     */
    public List<String> expandStringList(List<String> filesRequested) {
        List<String> expandedList = new ArrayList<String>();
        if ((filesRequested != null) && (!filesRequested.isEmpty())) { 
            for (String file : filesRequested) {
                Path p = Paths.get(file);
                if (Files.isDirectory(p)) {
                    try {
                        List<String> files = FileFinder.find(
                                p.toAbsolutePath().toString());
                        if ((files != null) && (!files.isEmpty())) { 
                            for (String name : files) {
                                expandedList.add(name);
                            }
                        }
                    }
                    catch (IOException ioe) {
                        LOGGER.warn("Client requested bundling of directory [ "
                                + file
                                + " ] but an unexpected IOException was "
                                + "raised while walking the file system.  "
                                + "Error message [ "
                                + ioe.getMessage()
                                + " ].");
                    }
                }
                else {
                    expandedList.add(file);
                }
            }
        }
        return expandedList;
    }
    
    /**
     * This method was added to facilitate the bundling of directories. 
     * If any directories have been included in the request this method 
     * will walk the directory tree and add all regular files that reside 
     * in the directory to the output List.
     * 
     * @param filesRequested Original user-submitted list of files that 
     * are to be bundled.
     * @return A list of FileEntry objects.
     */
    public List<FileRequest> expand(List<FileRequest> filesRequested) {
        List<FileRequest> expandedList = new ArrayList<FileRequest>();
        if ((filesRequested != null) && (!filesRequested.isEmpty())) { 
            for (FileRequest file : filesRequested) {
                Path p = Paths.get(file.getFile());
                if (Files.isDirectory(p)) {
                    try {
                        List<String> files = FileFinder.find(
                                p.toAbsolutePath().toString());
                        if ((files != null) && (!files.isEmpty())) { 
                            for (String name : files) {
                                expandedList.add(
                                        new FileRequest.FileRequestBuilder()
                                        .file(name)
                                        .build());
                            }
                        }
                        
                    }
                    catch (IOException ioe) {
                        LOGGER.warn("Client requested bundling of directory [ "
                                + file.getFile()
                                + " ] but an unexpected IOException was "
                                + "raised while walking the file system.  "
                                + "Error message [ "
                                + ioe.getMessage()
                                + " ].");
                    }
                }
                else {
                    expandedList.add(file);
                }
            }
        }
        return expandedList;
    }
    
    /**
     * Public method used to validate the list of files to be bundled.
     * 
     * @param filesRequested The raw list of files that were requested by the 
     * client.
     * @return A List of partially populated FileEntry objects.
     * @throws InvalidRequestException Thrown if any validation exceptions are 
     * encountered.
     */
    public List<FileEntry> validate(List<FileRequest> filesRequested) 
            throws InvalidRequestException {
        
        List<FileEntry> validated = new ArrayList<FileEntry>();
        
        if ((filesRequested == null) || (filesRequested.size() < 1)) {
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_INPUT_FILES_FOUND);
        }
        
        filesRequested = eliminateDuplicates(filesRequested);
        filesRequested = expand(filesRequested);
        
        if ((filesRequested != null) && (!filesRequested.isEmpty())) { 
            for (FileRequest file : filesRequested) {
                
                FileEntry obj = validateOneFile(file);
                if (obj != null) {
                    PathGenerator.getInstance().setOneEntry(obj);
                    validated.add(obj);
                }
            }
        }
        if ((validated != null) && (validated.isEmpty())) { 
            throw new InvalidRequestException(
                    ValidationErrorCodes.NO_VALID_INPUT_FILES_FOUND);
        }
        return validated;
    }
    
    /** 
     * Static inner class used to construct the factory singleton.  This
     * class exploits that fact that inner classes are not loaded until they 
     * referenced therefore enforcing thread safety without the performance 
     * hit imposed by the use of the "synchronized" keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class FileValidatorHolder {
        
        /**
         * Reference to the Singleton instance of the factory
         */
        private static FileValidator _factory = new FileValidator();
        
        /**
         * Accessor method for the singleton instance of the factory object.
         * @return The singleton instance of the factory.
         */
        public static FileValidator getFactorySingleton() {
            return _factory;
        }
    }
}
