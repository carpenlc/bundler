package mil.nga.bundler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import mil.nga.bundler.exceptions.PropertiesNotLoadedException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.util.FileUtils;

public class UrlGenerator 
        extends PropertyLoader 
        implements BundlerConstantsI {
    
    /**
     * Set up the Log4j system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(
            UrlGenerator.class);
    
    /**
     * The base staging directory
     */
    private String baseDir = null;
    
    /**
     * The base URL of the archive.
     */
    private String baseUrl = null;
    
    /**
     * Default constructor
     */
    private UrlGenerator() {
        super(PROPERTY_FILE_NAME);
        
        try {
            setBaseDir(getProperty(STAGING_DIRECTORY_BASE_PROPERTY));
            setBaseURL(getProperty(BASE_URL_PROPERTY));
        }
        catch (PropertiesNotLoadedException pnle) {
            LOGGER.error("An unexpected PropertiesNotLoadedException " 
                    + "was encountered.  Please ensure the application "
                    + "is properly configured.  Exception message [ "
                    + pnle.getMessage()
                    + " ].");
        }
    }
    
    /**
     * Getter method for the base directory used in the translation.
     * @return The client defined base directory.
     */
    public String getBaseDir() {
        return baseDir;
    }
    
    /**
     * Getter method for the base URL used in the translation.
     * @return The client defined base URL.
     */
    public String getBaseURL() {
        return baseUrl;
    }
    
    /**
     * Return a singleton instance to the UrlGenerator object.
     * @return The UrlGenerator
     */
    public static UrlGenerator getInstance() {
        return UrlGeneratorHolder.getFactorySingleton();
    }
    
    /**
     * Typically, the base directory will be read from the system properties.
     * However, this method was implemented to facilitate unit testing.
     * 
     * @param value The value for the base directory.
     */
    public void setBaseDir(String value) {
        baseDir = value;
    }
    
    /**
     * Typically, the base URL will be read from the system properties.
     * However, this method was implemented to facilitate unit testing.
     * 
     * @param value The value for the baseURL.
     */
    public void setBaseURL(String value) {
        baseUrl = value;
    }
    
    /**
     * Convert the input local file String to a full URL.
     * TODO: This probably needs to be made fancier.
     * 
     * @param localFile The full path to the local archive file.
     * @return The associated URL
     */
    public String toURL(String localFile) {
        // For Windows modify the file separator
        String target = FileUtils.getEntryPath(localFile, getBaseDir())
                            .replace('\\', '/');
        return getBaseURL() + target;
    }

    /** 
     * Static inner class used to construct the factory singleton.  This
     * class exploits that fact that inner classes are not loaded until they 
     * referenced therefore enforcing thread safety without the performance 
     * hit imposed by the use of the "synchronized" keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class UrlGeneratorHolder {
        
        /**
         * Reference to the Singleton instance of the factory
         */
        private static UrlGenerator _factory = new UrlGenerator();
        
        /**
         * Accessor method for the singleton instance of the factory object.
         * 
         * @return The singleton instance of the factory.
         */
        public static UrlGenerator getFactorySingleton() {
            return _factory;
        }
    }
}

