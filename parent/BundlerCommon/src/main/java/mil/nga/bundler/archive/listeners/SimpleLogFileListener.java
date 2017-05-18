package mil.nga.bundler.archive.listeners;

import mil.nga.bundler.interfaces.FileCompletionListenerI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class implementing the FileCompletionListenerI interface.  This 
 * class was developed largely to test the feasibility of tracking processing
 * completion of individual files. 
 * 
 * @author L. Craig Carpenter
 */
public class SimpleLogFileListener implements FileCompletionListenerI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static Logger LOGGER = LoggerFactory.getLogger(
            SimpleLogFileListener.class);
    
    /**
     * Simply make a note in the output log file that processing has completed
     * on a given file.
     * @param file The name of the file that completed archive processing.
     */
    public void notify(String file) {
        LOGGER.info("Archive processing has completed for file [ "
                + file
                + " ].");
    }
}
