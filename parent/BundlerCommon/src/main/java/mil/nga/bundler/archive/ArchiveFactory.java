package mil.nga.bundler.archive;

import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.archive.BZip2Archiver;
import mil.nga.bundler.archive.GZipArchiver;
import mil.nga.bundler.archive.TarArchiver;
import mil.nga.bundler.archive.ZipArchiver;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;
import mil.nga.bundler.interfaces.BundlerConstantsI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class designed to instantiate concrete implementations of 
 * objects that can be utilized to create output archive files.  This 
 * class is implemented using the singleton design pattern and will 
 * support output archives of types contained in the ArchiveType 
 * enumeration. 
 * 
 * @author L. Craig Carpenter
 */
public class ArchiveFactory {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(
            ArchiveFactory.class);
    
    /**
     * Hidden constructor enforcing the Singleton design pattern.
     */
    private ArchiveFactory() {}

    /**
     * Accessor method for the Singleton instance of the ArchiveFactory.
     * object.
     * 
     * @return The Singleton instance.
     */
    public static ArchiveFactory getFactory() {
        return ArchiveFactoryHolder.getSingleton();
    }
    
    /**
     * Construct a concrete instance of a class that will be able to 
     * construct the output archive requested.
     * 
     * @param type The type of archiver requested.
     * @return A concrete class implementing the logic required for 
     * constructing an output archive file.
     */
    public BundlerI getInstance(
            ArchiveType type) throws UnknownArchiveTypeException {
        
        String method = "getInstance() - ";
        if (type.equals(ArchiveType.ZIP)) {
            LOGGER.debug(method + "Client requested ZIP archive format.");
            return new ZipArchiver();
        }
        else if (type.equals(ArchiveType.TAR)) {
            LOGGER.debug(method + "Client requested TAR archive format.");
            return new TarArchiver();
        }
        else if (type.equals(ArchiveType.GZIP)) {
            LOGGER.debug(method + "Client requested GZIP archive format.");
            return new GZipArchiver();
        }
        else if (type.equals(ArchiveType.BZIP2)) {
            LOGGER.debug(method + "Client requested BZIP2 archive format.");
            return new BZip2Archiver();
        }
        
        String msg = "An archive type was requested that is not yet supported!"
            + "  Archive supplied [ " 
            + type.getText()
            + " ].";
        LOGGER.error(method + msg);
        throw new UnknownArchiveTypeException(msg);
    }
    
    /**
     * Static inner class used to construct the Singleton object.  This class
     * exploits the fact that classes are not loaded until they are referenced
     * therefore enforcing thread safety without the performance hit imposed
     * by the <code>synchronized</code> keyword.
     * 
     * @author L. Craig Carpenter
     */
    public static class ArchiveFactoryHolder {
        
        /**
             * Reference to the Singleton instance of the ClientUtility
         */
        private static ArchiveFactory _instance = new ArchiveFactory();
    
        /**
         * Accessor method for the singleton instance of the ClientUtility.
         * @return The Singleton instance of the client utility.
         */
        public static ArchiveFactory getSingleton() {
            return _instance;
        }
        
    }
}
