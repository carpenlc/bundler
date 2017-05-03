package mil.nga.bundler.test;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.jdbc.JDBCJobService;

public class ViewJobIDList {

	/**
	 * Static logger for use throughout the class.
	 */
	static final Logger LOGGER = 
			LoggerFactory.getLogger(ViewJobIDList.class);
	
	/**
	 * Inject the EJB used to look up the job tracker information.
	 * 
	 * Note:  JBoss EAP 6.x does not support injection into the application
	 * web tier.  When deployed to JBoss EAP 6.x this internal member 
	 * variable will always be null.
	 */
	@EJB(lookup="java:global/BundlerEAR/BundlerEJB/JDBCJobService!mil.nga.bundler.ejb.JDBCJobService")
	protected JDBCJobService jobService;
	
	/**
	 * List of job IDs retrieved on construction.
	 */
	protected List<String> jobIDs;
	
	/**
	 * Handle to the job ID that is selected in the Prime Faces data table.
	 */
	private String selectedJobID;
	
    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    protected JDBCJobService getJDBCJobService() {
    	if (jobService == null) {
    		LOGGER.warn("Application container failed to inject the "
    				+ "reference to JobService.  Attempting to "
    				+ "look it up via JNDI.");
    		jobService = EJBClientUtilities
    				.getInstance()
    				.getJDBCJobService();
    	}
    	return jobService;
    }
    
    
	/**
	 * This method serves as the constructor which will create and populate 
	 * the internal List of Job IDs.
	 */
	@PostConstruct
	public void initialize() {
		
		jobService = getJDBCJobService();
		if (jobService != null) {
			jobIDs = jobService.getJobIDs();

			if ((jobIDs == null) && (jobIDs.size() == 0)) {
				LOGGER.error("Unable to find any job IDs in the data store.");
			}
			else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" [ "
							+ jobIDs.size()
							+ " ] jobs selected.");
				}
			}
		}
		else {
			
		}
	}
	
	/**
	 * Getter method for the list of jobs that exist in the back-end data 
	 * store.
	 * @return Complete list of job IDs.
	 */
	public List<String> getJobIDs() {
		return jobIDs;
	}
	
	/**
	 * Setter method for the selected Job ID.
	 * @return The job ID selected in the data table.  May be null.
	 */
	public String getSelectedJobID() {
		return selectedJobID;
	}
	
	/**
	 * Setter method for the selected Job ID.
	 * @param jobID The job ID selected in the data table.
	 */
	public void setSelectedJobID(String jobID) {
		selectedJobID = jobID;
	}
}
