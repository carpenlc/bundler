package mil.nga.bundler.ejb;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.FileNameGenerator;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.util.CaseInsensitiveDirFilter;
import mil.nga.util.FileUtils;

/**
 * Timer Bean implemented to cleanup the staging area directory used by both 
 * the the bundler and the PDF merge utility.  This timer will fire every day 
 * a 12:30 a.m. and delete all staging directories older than 48 hours.
 * 
 * @author L. Craig Carpenter
 */
@Singleton
public class CleanupTimerBean 
        implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(CleanupTimerBean.class);
    
    /**
     * The location of the staging area.
     */
    private File stagingDirectory = null;
    
    /**
     * Default constructor. 
     */
    public CleanupTimerBean() {
        
        String method = "CleanupTimerBean.constructor() - ";
        String dir    = FileNameGenerator.getInstance().getStagingDirectory();
        
        if ((dir != null) && (!dir.isEmpty())) {
            stagingDirectory = new File(dir);
            if (stagingDirectory.exists()) {
                if (!stagingDirectory.isDirectory()) {
                    
                    LOGGER.error(method 
                            + "The location identified by system property ["
                            + STAGING_DIRECTORY_PROPERTY
                            + "] value ["
                            + dir 
                            + "] defines a location that is not a directory.  "
                            + "The cleanup service will not run.");
                    stagingDirectory = null;
                    
                }
            }
            else {
                
                LOGGER.error(method 
                        + "The location identified by system property ["
                        + STAGING_DIRECTORY_PROPERTY
                        + "] value ["
                        + dir 
                        + "] does not exist.  "
                        + "The cleanup bean will not run.");
                stagingDirectory = null;
            }
        }
        else {
            LOGGER.error(method
                    + "Unable to determine the location of the bundler "
                    + "staging directory.  Please check the value of "
                    + "property ["
                    + STAGING_DIRECTORY_PROPERTY
                    + "].  The cleanup bean will not run.");
        }
    }
    
    /**
     * Calculate the time 48 hours ago.
     * @return The time 48 hours ago.
     */
    private long getPurgeTime() {
         Calendar cal = Calendar.getInstance();  
         cal.add(Calendar.DAY_OF_MONTH, -2);  
         return cal.getTimeInMillis(); 
    }
    
    /**
     * Determine whether or not the input file should be deleted.
     * This method ensures the input file is a directory and falls 
     * outside the date threshold.
     * 
     * @param file Candidate for deletion.
     * @return True if the file should be deleted.  False otherwise.
     */
    private boolean delete(File file) {
        boolean delete    = false;
        long    purgeTime = getPurgeTime();
        if ((file != null) && (file.exists()) && (file.isDirectory())) {
            if (file.lastModified() < purgeTime) {
                delete = true;
            }
        }
        return delete;
    }
    
    /**
     * Entry point called by the application container to run the staging 
     * area cleanup algorithm.
     * 
     * @param t Container injected Timer object.
     */
    @Schedule(second="0", minute="30", hour="0", dayOfWeek="*",
              dayOfMonth="*", month="*", year="*", info="CleanupTimer")
    private void scheduledTimeout(final Timer t) {

        String       method  = "cleanup() - ";
        List<String> regexes = FileNameGenerator.getRegEx();
        
        LOGGER.info(method  
                + "Cleanup service launched at [ "
                + FileUtils.getTimeAsString(UNIVERSAL_DATE_STRING, System.currentTimeMillis())
                + " ].");
            
        if (stagingDirectory != null) {
            for (String regex : regexes) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(method  
                            + "Checking for on-disk bundles to remove.  Using staging "
                            + "directory ["
                            + stagingDirectory.getAbsolutePath() 
                            + "] and regex ["
                            + regex
                            + "].");
                }
                
                File[] dirs  = stagingDirectory.listFiles(
                                    new CaseInsensitiveDirFilter(regex));
    
                if ((dirs != null) && (dirs.length > 0)) {
                    
                    for (File file : dirs) {
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(method 
                                    + "Checking directory ["
                                    + file.getAbsolutePath()
                                    + "].");
                        }
                        
                        if (delete(file)) {
                            try {
                                LOGGER.info(method 
                                        + "Deleting expired directory ["
                                        + file.getAbsolutePath()
                                        + "].");
                                FileUtils.delete(file);
                            }
                            catch (IOException ioe) {
                                LOGGER.warn(method 
                                        + "An IOException was encountered while "
                                        + "attempting to delete file ["
                                        + file.getAbsolutePath()
                                        + "].  Error encountered ["
                                        + ioe.getMessage()
                                        + "].");
                            }
                        }
                    }
                }
                else {
                    LOGGER.info(method 
                            + "There are no files in the staging directory.");
                }
            } // end loop: regex
        }
        else {
            LOGGER.error(method 
                    + "Unable to determine the target staging directory.  "
                    + "The cleanup service will not run.");
        }
    }
}