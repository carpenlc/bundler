package mil.nga.bundler.ejb;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import mil.nga.bundler.messages.ArchiveMessage;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message-Driven Bean implementation class for: JobTrackerMDB
 * 
 * This class receives JMS messages when an Archive job completes.  
 * It is responsible for ensuring the job state flags and the job
 * statistics information is updated and persisted.
 *  
 */
@MessageDriven(
                name = "TrackerMDB",
                activationConfig = { 
                                @ActivationConfigProperty(
                                                propertyName = "destinationType", 
                                                propertyValue = "javax.jms.Queue"),
                                @ActivationConfigProperty(
                                                propertyName = "destination", 
                                                propertyValue = "queue/TrackerMessageQ_TEST"),
                                @ActivationConfigProperty(
                                                propertyName = "acknowledgeMode", 
                                                propertyValue = "Auto-acknowledge")
                })
public class JobTrackerMDB implements MessageListener {
	
	/**
	 * Set up the Log4j system for use throughout the class
	 */
	static final Logger LOGGER = LoggerFactory.getLogger(JobTrackerMDB.class);
	
	/**
	 * Container-injected reference to the JobService EJB.
	 */
	@EJB
	JobService jobService;
    
	/**
     * Default constructor. 
     */
    public JobTrackerMDB() { }
	
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
     * Calculate the number of archives complete by looping through the 
     * archives and counting how many are complete.
     * 
     * @param job The target job.
     * @return The number of archives complete.
     */
    private int getNumArchivesComplete(Job job) {
    	int archivesComplete = 0;
    	if (job != null) {
    		if ((job.getArchives() != null) && 
    				(job.getArchives().size() > 0)) {
    			for (Archive archive : job.getArchives()) {
    				if (archive.getArchiveState() == JobStateType.COMPLETE) {
    					archivesComplete++;
    				}
    			}
    		}
    		else {
        		LOGGER.error("Input Job does not contain any archives.  "
        				+ "Unable to calculate the number or archives "
        				+ "completed.");
    		}
    	}
    	else {
    		LOGGER.error("Input Job ID is null.  Unable to calculate the "
    				+ "number of archives completed.");
    	}
    	return archivesComplete;
    }
    
    /**
     * Calculate the size completed by the archive by looping through the 
     * FileEntry objects and summing the individual size of each archive.
     * 
     * @return The total size of all files in the archive job (uncompressed).
     */
    private long getSizeComplete(List<FileEntry> files) {
    	long sizeComplete = 0L;
    	if ((files != null) && (files.size() > 0)) {
    		for (FileEntry file : files) {
    			if (file.getFileState() == JobStateType.COMPLETE) {
    				sizeComplete += file.getSize();
    			}
    		}
    	}
    	else {
    		LOGGER.error("Input list of files is null or contains zero files.");
    	}
    	return sizeComplete;
    }
    
    /**
     * Calculate the number of files completed by the archive by looping 
     * through the 
     * FileEntry objects and summing the individual size of each archive.
     * 
     * @return The total size of all files in the archive job (uncompressed).
     */
    private long getFilesComplete(List<FileEntry> files) {
    	long numFiles = 0L;
    	if ((files != null) && (files.size() > 0)) {
    		for (FileEntry file : files) {
    			if (file.getFileState() == JobStateType.COMPLETE) {
    				numFiles++;
    			}
    		}
    		if (numFiles != files.size()) {
    			LOGGER.warn("There is a mismatch between the number of files "
    					+ "in the input list and the number of files that "
    					+ "were compressed in the output Archive.  The input "
    					+ "list contains [ "
    					+ files.size()
    					+ " ] files, but [ "
    					+ numFiles
    					+ " ] were marked complete by the archive processing "
    					+ "algorithm.");
    		}
    	}
    	else {
    		LOGGER.error("Input list of files is null or contains zero files.");
    	}
    	return numFiles;
    }
    
    /**
     * We've seen a few rare cases where an archive has completed, but the 
     * database has not been updated prior to the handling the archive 
     * complete message.  One case was a situation where it took over 
     * 25 seconds for the transactions associated with completing the 
     * the archive to commit on one of the nodes.   
     * 
     * @param archive The archive that has complete.  
     */
    private void checkArchive(Archive archive) { 
    	if (archive.getArchiveState() != JobStateType.COMPLETE) {
    		LOGGER.warn("Archive complete message received for Job ID [ "
    				+ archive.getJobID()
    				+ " ], archive ID [ " 
    				+ archive.getArchiveID()
    				+ " ] but the data store has not been updated.  Updating "
    				+ "archive state to ensure that the overall job "
    				+ "completes.");
    		archive.setArchiveState(JobStateType.COMPLETE);
    		archive.setEndTime(System.currentTimeMillis());
    	}
    }
    
    /**
     * Update the overall state of the job based on the individual completed
     * archive. 
     * 
     * @param job The Overall Job object.
     * @param archive The individual completed archive file.
     */
    private void updateJobState(Job job, Archive archive) {
    	
    	long numFiles              = getFilesComplete(archive.getFiles());
    	long totalNumFilesComplete = job.getNumFilesComplete() + numFiles;
    	long sizeComplete          = getSizeComplete(archive.getFiles());
    	long totalSizeComplete     = job.getTotalSizeComplete() + sizeComplete;
    	int  numArchivesComplete   = getNumArchivesComplete(job);
    
    	if (totalNumFilesComplete > job.getNumFiles()) {
    		LOGGER.warn( "Inconsistency detected in the number of "
    				+ "files completed for job ID [ "
    				+ job.getJobID()
    				+ " ].  Job expects [ "
    				+ job.getNumFiles()
                    + " ] files completed, yet calculations based "
                    + "on archives complete show [ "
                    + totalNumFilesComplete
                    + " ].  Updating based on archives.");
    		totalNumFilesComplete = job.getNumFiles();
    	}
    	
    	job.setNumFilesComplete(totalNumFilesComplete);
    	
    	if (totalSizeComplete > job.getTotalSize()) {
    		LOGGER.warn("Inconsistency detected in the size of the "
                    + "data completed for job ID [ "
                    + job.getJobID()
                    + " ].  expected size [ "
                    + job.getTotalSize()
                    + " ] size completed, yet calculations based "
                    + "on archives complete indicate [ "
                    + totalSizeComplete
                    + " ].  Updating based on archives.");
    		totalSizeComplete = job.getTotalSize();
    	}
    	
    	job.setTotalSizeComplete(totalSizeComplete);
    	job.setNumArchivesComplete(numArchivesComplete);
    	
    	if (job.getNumArchives() == numArchivesComplete) {
    		if (LOGGER.isDebugEnabled()) {
    			LOGGER.debug("Marking job ID [ "
    					+ job.getJobID() 
    					+ " ] complete.");
    		}
    		job.setState(JobStateType.COMPLETE);
    		job.setEndTime(System.currentTimeMillis());
    	}
    	else { 
    		if (LOGGER.isDebugEnabled()) {
    			LOGGER.debug("Job ID [ "
    					+ job.getJobID() 
    					+ " ] not yet complete.  Only [ "
    					+ numArchivesComplete
    					+ " ] archives complete out of [ "
    					+ job.getNumArchives()
    					+ " ] total archives.");
    		}
    	}
    }
    
	/**
	 * Method called when a JMS message is placed on the queue/TrackerMessageQ
	 * message queue.  This method will unmarshal the incoming message, then
	 * retrieve references to the Job and Archive then call the private 
	 * internal methods to update the overall job state.
	 * 
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
    	try {
    		    
	         ObjectMessage  objMessage = (ObjectMessage)message;
	         ArchiveMessage archiveMsg = (ArchiveMessage)objMessage.getObject();
	         
	         if (archiveMsg != null) {
	         
                 LOGGER.info("Archive completed for archive [ "
                         + archiveMsg.toString()
                         + " ].");

                 if (getJobService() != null) {
                	 
                	 Job job = getJobService().getJob(archiveMsg.getJobID());
                	 
                	 if (job != null) {
                		 Archive archive = job.getArchive(archiveMsg.getArchiveID());
                		 if (archive != null) {
                			 checkArchive(archive);
                			 updateJobState(job, archive);
                			 jobService.update(job);
                		 }
                		 else {
                     		 LOGGER.error("Unable to retrieve Archive "
                    				 + "associated with job ID [ "
                    				 + archiveMsg.getJobID()
                    				 + " ] and archive ID [ "
                    				 + archiveMsg.getArchiveID()
                    				 + " ].");
                		 }
                	 }
                	 else {
                		 LOGGER.error("Unable to retrieve Job associated with "
                				 + "job ID [ "
                				 + archiveMsg.getJobID()
                				 + " ].");
                	 }
                 }
                 else {
                     LOGGER.error("The application container did not inject "
                             + "JobFactoryService EJB into the MDB.");
                 }
	         }
	     }
	     catch (JMSException je) {
	         LOGGER.error("Unexpected JMSException encountered while "
	             + "attempting to retrieve the Archive object from "
	             + "the input ObjectMessage.  Error message [ "
	             + je.getMessage()
	             + " ].");
	     }
    }
}
