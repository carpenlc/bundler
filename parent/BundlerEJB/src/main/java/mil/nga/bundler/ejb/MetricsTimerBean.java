package mil.nga.bundler.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;

import mil.nga.bundler.MetricsCalculator;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.BundlerMetrics;
import mil.nga.bundler.model.Job;
import mil.nga.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The intent of this timer bean is to update the overall bundler statistics 
 * data in a background process (as opposed to real time).  This was done to
 * improve the responsiveness of the bundler statistics web pages.
 * 
 * This method needs to run only once on only one server in the overall 
 * cluster.  To ensure it runs only once we leverage a clustered semaphore
 * using an Oracle database table (see jboss-ejb3.xml file deployed in the
 * BundlerEJB.jar file).
 * 
 * @author L. Craig Carpenter
 */
@Singleton(name="MetricsTimerBean")
public class MetricsTimerBean 
        extends MetricsCalculator 
        implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(MetricsTimerBean.class);
    
    /**
     * Container-injected reference to the JobService EJB.
     */
    @EJB
    JobService jobService;
    
    /**
     * Container-injected reference to the MetricsService EJB.
     */
    @EJB
    MetricsService metricsService;
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public MetricsTimerBean() {}
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    private JobService getJobService() {
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
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    private MetricsService getMetricsService() {
        if (metricsService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to JobService.  Attempting to "
                    + "look it up via JNDI.");
            metricsService = EJBClientUtilities
                    .getInstance()
                    .getMetricsService();
        }
        return metricsService;
    }
    
    /**
     * Execute the timer method which will start the statistics generation algorithm 
     * every 60 minutes at 30 minutes past the hour..
     * 
     * @param t Container injected Timer object.
     */
    @Schedule(second="0", minute="30", hour="*", dayOfWeek="*",
              dayOfMonth="*", month="*", year="*", info="StatisticsTimer")
    private void scheduledTimeout(final Timer t) {
        
        long startTime = System.currentTimeMillis();
        LOGGER.info("Metrics generation service launched at [ "
                + FileUtils.getTimeAsString(UNIVERSAL_DATE_STRING, startTime)
                + " ].");
        
        if (getJobService() != null) {
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Beginning selection of all Jobs from the data store.");
            }
            
            List<Job> jobs = getJobService().getJobs();
            
            if ((jobs != null) && (jobs.size() > 0)) {
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Selected [ "
                            + jobs.size()
                            + " ] jobs from the data store in [ "
                            + (System.currentTimeMillis() - startTime)
                            + " ] ms.");
                }
                
                if (getMetricsService() != null) {
                    BundlerMetrics metrics = getMetricsService().getMetrics();
                    if (metrics == null) {
                        LOGGER.warn("The BundlerMetrics object retrieved from "
                                + "the data store is null.  This situation is "
                                + "only valid the very first time this timer "
                                + "invoked.");
                        metrics = new BundlerMetrics();
                    }
                    getMetrics(metrics, jobs);
                    getMetricsService().update(metrics);
                    LOGGER.info(metrics.toString());
                }
                else {
                    LOGGER.error("Unable to obtain a reference to the "
                            + "MetricsService EJB.");
                }
            }
            else {
                LOGGER.error("The job list retrieved from the data source " 
                        + "is null or contains no elements.  Unable to "
                        + "calculate statistics.");
            }
         }
        else {
            LOGGER.error("Unable to obtain a reference to the JobService "
                    + "EJB.  We will be unable to obtain a list of Jobs "
                    + "on which to calculate statistics.");
        }
    }
}