package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import mil.nga.bundler.MetricsCalculator;
import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.JobService;
import mil.nga.bundler.model.BundlerMetrics;
import mil.nga.bundler.model.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean used to display bundler metrics for jobs submitted during the last
 * 30 days.  These metrics are calculated on-the-fly when 
 * the statistics display page is accessed.
 */
@ManagedBean
@ViewScoped
public class ViewLast30DaysJobMetrics 
		 extends ViewJobMetrics implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 2551378935562391006L;

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	static Logger LOGGER = LoggerFactory.getLogger(ViewLast30DaysJobMetrics.class);
	
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
    public ViewLast30DaysJobMetrics() {}
	
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
	 * Simple static method to convert a into a printable date.
	 * @param time time in milliseconds from the epoch
	 * @return Printable date String
	 */
	private String toDateString(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SSS");
		Date d = new Date();
		d.setTime(time);
		return sdf.format(d);
	}
	
    /**
     * Obtain a list of jobs on which to calculate metrics.  This method will 
     * create a <code>List</code> of <code>Job</code> objects that have 
     * been submitted in the last 30 days.
     * 
     * @return A list of jobs.  Null if there are any problems obtaining the 
     * list.
     */
    private List<Job> getJobList() {
    	
    	List<Job> jobList = null;
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
		
		if (getJobService() != null) {
			jobList = getJobService().getJobsByDate(startTime, endTime);
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
	 * will be displayed.   In this case, it retrieves a list of all jobs 
	 * submitted in the last 30 days.
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
    		LOGGER.warn("No jobs have been submitted/processed 30 days.  "
    				+ "This is almost certainly an error.  Please review "
    				+ "previous log entries.");
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
