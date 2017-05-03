package mil.nga.bundler.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import mil.nga.bundler.messages.JobTrackerMessage;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JobTrackerService
 * 
 * This bean is responsible for creating the data required to provide state 
 * information associated with a given bundle job.  The state data is returned
 * via the web tier through the getState() call.  This method differs from 
 * previous versions of the bundler because it calculates the state from the 
 * Job object rather than reading information from a separate table.
 */
@Stateless
@LocalBean
public class JobTrackerService {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			JobTrackerService.class);
	
	/**
	 * Container-injected reference to the JobService EJB.
	 */
	@EJB
	JobService jobService;
	
    /**
     * Default constructor. 
     */
    public JobTrackerService() { }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
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
     * Calculate the elapsed time associated with the job.
     * 
     * @param startTime The time that the job was started.
     * @param endTime The time that the job completed (if available).
     * @return The amount of wall-clock time the job has taken.
     */
    private long getElapsedTime(long startTime, long endTime) {
    	long elapsedTime = 0L;
    	if ((endTime > 0) && (startTime > 0)) {
    		elapsedTime = endTime - startTime;
    	}
    	if ((endTime == 0) && (startTime > 0)) { 
    		elapsedTime = System.currentTimeMillis() - startTime;
    	}
    	return elapsedTime;
    }
    
    /**
     * Construct the JobTrackerMessage and populate it with the current
     * statistics associated with the input Job object.
     * 
     * @param job The Job object.
     * @return Current state information associated with the job.
     */
    private JobTrackerMessage createJobTracker(Job job) {
    	
    	int  numArchivesComplete = 0;
    	long numFilesComplete    = 0L;
    	long totalSizeComplete   = 0L;
    	long elapsedTime         = getElapsedTime(
    								job.getStartTime(), 
    								job.getEndTime());
    	
    	JobTrackerMessage message = new JobTrackerMessage(
    			job.getJobID(),
    			job.getUserName(),
    			job.getNumFiles(),
    			job.getTotalSize(),
    			job.getNumArchives());
    	
    	message.setState(job.getState());
    	
    	if ((job.getArchives() != null) && (job.getArchives().size() > 0)) {
    		for (Archive archive : job.getArchives()) {
    			
    			if (archive.getArchiveState() == JobStateType.COMPLETE) {
    				numArchivesComplete++;
    				message.addArchive(archive);
    			}
    			if ((archive.getFiles() != null) && 
    					(archive.getFiles().size() > 0)) {
    				for (FileEntry file : archive.getFiles()) {
    					if (file.getFileState() == JobStateType.COMPLETE) {
    						numFilesComplete++;
    						totalSizeComplete += file.getSize();
    					}
    				}
    			}
				else {
					LOGGER.warn("Job ID [ "
							+ job.getJobID() 
							+ " ], archive ID [ "
							+ archive.getArchiveID()
							+ " ] does not contain a list of files to "
							+ "archive.");
				}
    		}
    	}
		else {
			LOGGER.warn("Job ID [ "
					+ job.getJobID() 
					+ " ] does not contain any archives to process.");
		}
    	message.setElapsedTime(elapsedTime);
    	message.setNumArchivesComplete(numArchivesComplete);
    	
    	// The number of hashes complete is maintained for backwards 
    	// compatibility.  It will always be the same as the number of 
    	// archives complete
    	message.setNumHashesComplete(numArchivesComplete);
    	message.setNumFilesComplete(numFilesComplete);
    	message.setSizeComplete(totalSizeComplete);
    	return message;	
    }
    
    /**
     * Calculate the current statistics information associated with current 
     * in-progress job.
     * 
     * @param jobID The jobID requested by the client.
     * @return A populated JobTrackerMessage containing the current state of 
     * the job in progress.
     */
    public JobTrackerMessage getJobTracker(String jobID) {
    	
    	JobTrackerMessage message = null;
    	Job               job     = null;
    	
    	if ((jobID != null) && (!jobID.isEmpty())) {
    		if (getJobService() != null) {
    			job = getJobService().getJob(jobID);
    			if (job != null) {
    				message = createJobTracker(job);
    			}
    			else {
    				LOGGER.error("Unable to retrieve job ID [ "
    						+ jobID 
    						+ " ] from the data store.  Method will return "
    						+ "null.");
    			}
    		}
	        else {
                LOGGER.error("Unable to obtain a reference to the JobService "
                		+ "EJB.  Unable to determine state of job ID [ "
                		+ jobID
                		+ " ].");
	        }
    	}
    	else {
    		LOGGER.error("The input job ID is null, or not populated.  Unable "
    				+ "to determine state.");
    	}
    	return message;
    }

}
