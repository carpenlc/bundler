package mil.nga.bundler.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.jdbc.JDBCJobService;
import mil.nga.bundler.model.Job;

public class BundlerEJBClient {

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(BundlerEJBClient.class);
    
    /**
     * Inject the EJB used to look up the job tracker information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JDBCJobService!mil.nga.bundler.ejb.jdbc.JDBCJobService")
    protected JDBCJobService jdbcJobService;

    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    protected JDBCJobService getJDBCJobService() {
        if (jdbcJobService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to JobService.  Attempting to "
                    + "look it up via JNDI.");
            jdbcJobService = EJBClientUtilities
                    .getInstance()
                    .getJDBCJobService();
        }
        return jdbcJobService;
    }
    
    /**
     * Retrieve a list of all job IDs in the target data store.
     * @return The list of all Job IDs in the target data store.
     */
    protected List<String> getJobIDs() {
        List<String> jobIDs = null;
        if (getJDBCJobService() != null) {
            jobIDs = getJDBCJobService().getJobIDs();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the JDBCJobService "
                    + "EJB.  The list of Job IDs will not be populated.");
        }
        return jobIDs;
    }
    
    /**
     * Retrieve a list of all job IDs in the target data store.
     * @return The list of all Job IDs in the target data store.
     */
    protected List<Job> getJobs() {
        List<Job> jobs = null;
        if (getJDBCJobService() != null) {
            jobs = getJDBCJobService().getJobs();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the JDBCJobService "
                    + "EJB.  The list of Jobs will not be populated.");
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
    protected List<Job> getJobs(int days) {
        
        List<Job> jobs = new ArrayList<Job>();
        
        if (getJDBCJobService() != null) {
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
            jobs = getJDBCJobService().getJobsByDate(startTime, endTime);
        }
        else { 
            LOGGER.error("Unable to obtain a reference to the JobService "
                    + "EJB.  Bar chart will not be populated.");
        }
        return jobs;
    }
    
    /**
     * Get the full materialized Job from the back-end data store.
     * @param jobID The job ID to retrieve.
     * @return The fully materialized job.
     */
    protected Job getMaterializedJob(String jobID) {
        Job job = null;
        if (getJDBCJobService() != null) {
            job = getJDBCJobService().getMaterializedJob(jobID);
        }
        else {
            LOGGER.error("Unable to obtain a reference to the JDBCJobService "
                    + "EJB.  The list of Jobs will not be populated.");
        }
        return job;
    }
    
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
}
