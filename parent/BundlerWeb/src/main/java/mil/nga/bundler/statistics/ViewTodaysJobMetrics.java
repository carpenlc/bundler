package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mil.nga.bundler.MetricsCalculator;
import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.JobService;
import mil.nga.bundler.ejb.MetricsTimerBean;
import mil.nga.bundler.model.BundlerMetrics;
import mil.nga.bundler.model.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean used to display bundler metrics for "today".  "today" being defined as 
 * from 12:00:00 a.m. until now.  These metrics are calculated on-the-fly when 
 * the statistics display page is accessed.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@ViewScoped
public class ViewTodaysJobMetrics 
        extends ViewJobMetrics implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -6008564386596417914L;

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
     * Handle to the object containing the calculated job metrics.  This 
     * object is populated by the <code>initialize()</code> method.
     */
    private BundlerMetrics metrics = null;
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public ViewTodaysJobMetrics() {}
    
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
     * Obtain a list of jobs on which to calculate metrics.  This method will 
     * create a <code>List</code> of <code>Job</code> objects that have 
     * been submitted today.
     * 
     * @return A list of jobs.  Null if there are any problems obtaining the 
     * list.
     */
    private List<Job> getJobList() {
        
        List<Job> jobList = null;
        DayModel today = new DayModel();
        
        if (getJobService() != null) {
            jobList = getJobService().getJobsByDate(
                    today.getStartTime(), 
                    today.getEndTime());
        }
        else {
            LOGGER.error("Unable to obtain a reference to the "
                    + "JobService EJB.");
        }
        return jobList;
    }
    /**
     * The initialize method is called immediately after the Bean is 
     * instantiated.  It's responsibility is to retrieve the metrics data that
     * will be displayed.   In this case, it simply retrieves the 
     * pre-calculated metrics data from the data store.
     */
    @PostConstruct
    public void initialize() {
        List<Job> jobs = getJobList();
        if ((jobs != null) && (jobs.size() > 0)) {
            metrics = new BundlerMetrics();
            MetricsCalculator calc = new MetricsCalculator();
            calc.getMetrics(metrics, jobs);
        }
        else {
            LOGGER.info("No jobs have been submitted/processed today.");
        }
    }
    
    /**
     * Getter method allowing access to the private internal bundler metrics
     * data.
     * @return The populated metrics data.
     */
    public BundlerMetrics getMetrics() {
        return metrics;
    }
    
}
