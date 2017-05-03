package mil.nga.bundler.statistics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.JobService;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.Job;

/**
 * Super class that factors out methods common to the bar chart generation 
 * classes.
 * 
 * @see mil.nga.bundler.statistics.ViewDataTransferredChartModel
 * @see mil.nga.bundler.statistics.ViewJobsSubmittedChartModel
 * @author L. Craig Carpenter
 *
 */
public class ChartModel implements BundlerConstantsI {

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(ChartModel.class);
    
    /**
     * Inject the EJB used to look up the job tracker information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JobService!mil.nga.bundler.ejb.JobService")
    protected JobService jobService;
    
    /**
     * Simple static method to convert a into a printable date.
     * @param time time in milliseconds from the epoch
     * @return Printable date String
     */
    protected String toDateString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
        Date d = new Date();
        d.setTime(time);
        return sdf.format(d);
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    protected JobService getJobService() {
        if (jobService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to JobService.  Attempting to "
                    + "look it up via JNDI.");
            jobService = EJBClientUtilities
                    .getInstance()
                    .getJobService();
        }
        return jobService;
    }
    
    /**
     * Construct a List of Job objects that were submitted in the last 30 days.
     * @return A list of Job object submitted the last 30 days.
     */
    protected List<Job> getJobList() {
        
        List<Job> jobs = new ArrayList<Job>();
        
        if (getJobService() != null) {
            long endTime = System.currentTimeMillis();
        
            // Calculate a date 30 days in the past
            Date d = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, -30);
            long startTime = cal.getTimeInMillis();
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Selecting events between start time [ "
                        + toDateString(startTime)
                        + " ] and end time [ "
                        + toDateString(endTime)
                        + " ].");
            }
            jobs = getJobService().getJobsByDate(startTime, endTime);
        }
        else { 
            LOGGER.error("Unable to obtain a reference to the JobService "
                    + "EJB.  Bar chart will not be populated.");
        }
        return jobs;
    }
    
    /**
     * Construct a List of Job objects that have a start time in the last 
     * <code>X</code> days. Where clients pass in <code>X</code>
     * through the days parameter.
     * 
     * @param days How many days from today in the past to retrieve records.
     * @return A list of Job object submitted during the time period specified.
     */
    protected List<Job> getJobList(int days) {
        
        List<Job> jobs = new ArrayList<Job>();
        
        if (getJobService() != null) {
            long endTime = System.currentTimeMillis();
        
            // Calculate a date 30 days in the past
            Date d = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.DATE, -days);
            long startTime = cal.getTimeInMillis();
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Selecting events between start time [ "
                        + toDateString(startTime)
                        + " ] and end time [ "
                        + toDateString(endTime)
                        + " ].");
            }
            jobs = getJobService().getJobsByDate(startTime, endTime);
        }
        else { 
            LOGGER.error("Unable to obtain a reference to the JobService "
                    + "EJB.  Bar chart will not be populated.");
        }
        return jobs;
    }
}
