package mil.nga.bundler.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.messages.ArchiveMessage;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;

/**
 * Session Bean implementation class JobRunnerService
 * 
 * This class was designed to implement a parallel processing architecture, 
 * taking advantage of all cluster nodes for creating output Archives.  It 
 * will accept an input Job, or individual Archive and submit it to the target 
 * JMS queue for processing.  The Archive MDBs will then pick up the messages 
 * from the JMS Q and and actually perform the processing.  
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class JobRunnerService 
        extends NotificationService implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */
    static final Logger LOGGER = LoggerFactory.getLogger(RecoveryService.class);
    
    /**
     * Container-injected reference to the JobService EJB.
     */
    @EJB
    JobService jobService;
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public JobRunnerService() { }

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
     * Method initiating processing on a single client-provided Archive 
     * object.  This method was introduced to support the Archive retry
     * logic.
     * 
     * @param archive A single Archive to submit to the JMS queues.
     */
    public void run(Archive archive) {
        
        if (archive != null) {
            
            LOGGER.info("Submitting archive with job ID [ "
                    + archive.getJobID()
                    + " ] and archive ID [ "
                    + archive.getArchiveID()
                    + " ] to the JMS queue for processing.");
            
            super.notify(ARCHIVER_DEST_Q,
                    new ArchiveMessage(
                            archive.getJobID(), 
                            archive.getArchiveID()));
        }
        else {
            LOGGER.error("Client submitted a null archive.  The archive will "
                    + "not be placed on the JMS queue.");
        }
    }
    
    /**
     * This method invokes the bundler processing on an input Job object. 
     * It loops through each archive contained in the job and submits them 
     * into the cluster (via JMS messages).  The method then updates the job
     * status through the JobTracker JPA.
     * 
     * @param job The populated Job object to invoke processing on.
     */
    public void run(Job job) {
        
        if ((job != null) &&
                (job.getArchives() != null) &&
                (job.getArchives().size() > 0)) {
            
            LOGGER.info("Initiating archive processing for job ID [ "
                    + job.getJobID()
                    + " ].");
            
            // Update the job status
            job.setState(JobStateType.IN_PROGRESS);
            job.setStartTime(System.currentTimeMillis());
            
            if (getJobService() != null) {
                job = getJobService().update(job);
            }
            
            for (Archive archive : job.getArchives()) {
                
                ArchiveMessage archiveMsg = new ArchiveMessage(
                        archive.getJobID(), 
                        archive.getArchiveID());
                
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.info("Placing the following message on "
                            + "the JMS queue [ "
                            + archiveMsg.toString()
                            + " ].");
                }
                super.notify(ARCHIVER_DEST_Q, archiveMsg); 
            }
        }
    }
}
