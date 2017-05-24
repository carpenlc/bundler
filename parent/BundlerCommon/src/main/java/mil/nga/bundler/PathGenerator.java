package mil.nga.bundler;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import mil.nga.PropertyLoader;
import mil.nga.bundler.exceptions.PropertiesNotLoadedException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.model.ValidFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for configuring the path inside of an output 
 * archive in which a given file will be inserted.  It's main use is to 
 * exclude certain directory prefixes that are configurable in an external 
 * properties file.  
 * 
 * @author L. Craig Carpenter
 *
 */
public class PathGenerator 
        extends PropertyLoader 
        implements BundlerConstantsI {
    
    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(PathGenerator.class);
    
    /**
     * List of path prefixes to exclude
     */
    private List<String> prefixExclusions = null;
    
    /**
     * Private constuctor enforcing the singleton design pattern.
     */
    private PathGenerator() {
        super(PROPERTY_FILE_NAME);
        try {
            loadPrefixMap(getProperties());
        }
        catch (PropertiesNotLoadedException pnle) {
            LOGGER.warn("An unexpected PropertiesNotLoadedException " 
                    + "was encountered.  Please ensure the application "
                    + "is properly configured.  Exception message [ "
                    + pnle.getMessage()
                    + " ].  Paths will not be molested.");
        }
    }
    
    /**
     * Method used to load the List of path prefixes that are to be excluded
     * from the entry path that will exist in the output archive file.
     * 
     * @param props Populated properties file. 
     */
    private void loadPrefixMap(Properties props) {
        
        String method = "loadPrefixMap() - ";
        
        if (props != null) {
            if (prefixExclusions == null) {
                prefixExclusions = new ArrayList<String>();
            }
            for (int i=0; i<MAX_NUM_EXCLUSIONS; i++) {
                String exclusion = props.getProperty(
                        PARTIAL_PROP_NAME + Integer.toString(i).trim());
                if ((exclusion != null) && (!exclusion.isEmpty())) {
                    prefixExclusions.add(exclusion);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(method
                                + "Found prefix exclusion [ "
                                + exclusion 
                                + " ] in property [ "
                                + PARTIAL_PROP_NAME + Integer.toString(i).trim()
                                + " ].");
                    }
                }
            }
        }
        else {
            LOGGER.error(method 
                    + "Input Properties object is null.  No prefix exclusions "
                    + "loaded.");
        }
    }
    
    /**
     * This method does the heavy lifting associated with stripping off any 
     * configured prefixes and ensuring the output entry path does not start
     * with a file separator character.
     * 
     * @param path The actual file path.
     * @return The calculated entry path.
     */
    private String getEntryPath(String path) {
        
        String method = "getEntryPath() - ";
        String entryPath = path;
        
        if ((prefixExclusions != null) && (prefixExclusions.size() > 0)) {
            for (String exclusion : prefixExclusions) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(method 
                            + "Testing for exclusion [ "
                            + exclusion
                            + " ].");
                }
                if (entryPath.startsWith(exclusion)) {
                    entryPath = entryPath.replaceFirst(Pattern.quote(exclusion), "");
                }
            }
            
            // Ensure the path does not start with a path separator character.
            if (entryPath.startsWith(System.getProperty("file.separator"))) {
                entryPath = entryPath.replaceFirst(Pattern.quote(
                        System.getProperty("file.separator")), "");
            }
        } 
        else {
            LOGGER.warn(method 
                    + "There are no prefix exclusions available to apply to "
                    + "the input File path.");
        }
        
        return entryPath;
    }
    
    /**
     * Private method that takes one FileEntry object, reads the identified 
     * full path, and then strips off any path prefixes that were identified 
     * as needing stripped by the application properties file.
     * 
     * @param entry FileEntry object associated with one file to be archived.
     */
    public void setOneEntry(FileEntry entry) {
        String path   = entry.getFilePath();
        if ((entry.getEntryPath() == null) || (entry.getEntryPath().isEmpty())) {
            // If the entry path wasn't supplied, calculate it.
            entry.setEntryPath(getEntryPath(path.trim()));
        }
        else {
            // If the entry path was supplied by the client, make sure it 
            // doesn't start with a file separator character.
            if (entry.getEntryPath().startsWith("/")) {
                entry.setEntryPath(entry.getEntryPath().substring(1));
            }
        }
    }
    
    /**
     * Public entry point.  The client supplies a single Archive.  This 
     * method loops through all of the files identified for archive and sets 
     * their entry path in the output archive.
     *  
     * @param archive A single Archive to be sent to the bundler process.
     */
    public void setPaths(Archive archive) {
        if ((archive.getFiles() != null) && 
                (archive.getFiles().size() > 0)) {
            for (FileEntry entry : archive.getFiles()) {
                setOneEntry(entry);
            }
        }
        else {
            LOGGER.warn("The current Archive objects does not contain "
                    + "a list of files to compress.");
        }
    }
    
    /**
     * Set any Entry paths that are not already defined.
     * @param files The list of validated files 
     */
    public void setEntryPaths(List<ValidFile> files) {
        if ((files != null) && (files.size() > 0)) {
            for (ValidFile file : files) {
                if ((file.getEntryPath() == null) || 
                        (file.getEntryPath().isEmpty())) {
                    file.setEntryPath(getEntryPath(file.getPath().trim()));
                }
            }
        }
        else {
            LOGGER.warn("No valid files supplied.");
        }
     }
    
    /**
     * Public entry point.  The client supplies a List of Archives that will 
     * be sent to the bundler process.  This method loops through all of the 
     * files identified for archive and sets their entry path in the archive.
     *  
     * @param archives List of Archives to be sent to the bundler process.
     */
    public void setPaths(List<Archive> archives) {
        String method = "setPaths() - ";
        if ((archives != null) && (archives.size() > 0)) {
            for (Archive archive : archives) {
                if ((archive.getFiles() != null) && 
                        (archive.getFiles().size() > 0)) {
                    for (FileEntry entry : archive.getFiles()) {
                        setOneEntry(entry);
                    }
                }
                else {
                    LOGGER.warn(method 
                            + "The current Archive objects does not contain "
                            + "a list of files to compress.");
                }
            }
        }
        else {
            LOGGER.warn(method 
                    + "The input list of Archives is null or contains zero "
                    + "entries.");
        }
    }
    
    /**
     * Accessor method for the singleton instance of the AeroDataFactory.
     * @return Handle to the singleton instance of the AeroDataFactory.
     */
    public static PathGenerator getInstance() {
        return PathFactoryHolder.getFactorySingleton();
    }
    
    /** 
     * Static inner class used to construct the factory singleton.  This
     * class exploits that fact that inner classes are not loaded until they 
     * referenced therefore enforcing thread safety without the performance 
     * hit imposed by the use of the "synchronized" keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class PathFactoryHolder {
        
        /**
         * Reference to the Singleton instance of the factory
         */
        private static PathGenerator _factory = new PathGenerator();
        
        /**
         * Accessor method for the singleton instance of the factory object.
         * @return The singleton instance of the factory.
         */
        public static PathGenerator getFactorySingleton() {
            return _factory;
        }
    }
    
    
    public static void main(String[] args) {
        PathGenerator.getInstance();
        FileEntry entry = new FileEntry();
        entry.setFilePath("/mnt/raster/dir1/dir2/dir3");
        entry.setEntryPath(null);
        PathGenerator.getInstance().setOneEntry(entry);
        System.out.println(entry.toString());
        
        
        
    }
}
