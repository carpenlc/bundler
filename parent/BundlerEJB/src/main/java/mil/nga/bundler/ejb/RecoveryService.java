package mil.nga.bundler.ejb;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;
import mil.nga.util.FileUtils;


/**
 * Session Bean implementation class RecoveryService
 * 
 * After the first iterations of the Bundler went into production we noticed 
 * that if a given JBoss cluster node failed (due to OutOfMemory, stopping/
 * starting JBoss, rebooting the server, etc.) and that node was processing 
 * an Archive job, the overall parent Job could never finish.
 * 
 * Another situation came up where due to a JPA configuration issue, we 
 * had several jobs in a state where all of the archives had completed 
 * successfully, but because JPA was not flushing properly we missed one 
 * (or more) completion events.
 * 
 * The idea behind the recovery logic is that if a node is starting up we 
 * can safely assume that any Archive jobs that were running on this node 
 * and are incomplete can be restarted to complete the parent job.
 * 
 * @author L. Craig Carpenter
 */
@Startup
@Singleton
public class RecoveryService {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    static final Logger LOGGER = LoggerFactory.getLogger(RecoveryService.class);
    
    /**
     * The amount of time to wait before checking to see if a job is actually complete.
     * As of this writing we have never seen a successful job take more than 45 seconds.
     */
    private static final long PROCESSING_TIME_THRESHOLD = (long)(45 * 1000);
    
    /**
     * Container-injected reference to the JobService EJB.
     */
    @EJB
    JobService jobService;
    
    /**
     * Container-injected reference to the JobRunnerService EJB.  Class is 
     * responsible for placing archives to retry on the JMS queue.
     */
    @EJB
    JobRunnerService jobRunnerService;
    
    /**
     * Eclipse-generated constructor. 
     */
    public RecoveryService() { }
    
    /**
     * Simple private method to calculate the elapsed time for a job.
     * 
     * @param job The target job.
     * @return The amount of time elapsed for the current job.
     */
    private long getElapsedTime(Job job) {
        long now = System.currentTimeMillis();
        long elapsedTime = 0L;
        if (job != null) {
            elapsedTime = (now - job.getStartTime());
        }
        return elapsedTime;
    }
    
    /**
     * This is essentially the constructor calling the relevant internal methods.
     */
    @PostConstruct
    public void initialize() {
        checkForJobsToRetry();
        checkForJobsThatAreReallyComplete();    
        checkForInvalidJobs();
    }
    
    /**
     * Another issue came up where clients (specifically the AeroDownload 
     * application) was submitting bundle requests that did not contain any 
     * files to actually bundle.  Since no Archive objects were created, the
     * jobs were sitting out in the database as NOT_STARTED and will 
     * never start.  This method was added to clean the up and give them 
     * a state of 'INVALID_REQUEST'.
     */
    public void checkForInvalidJobs() {
        if (getJobService() != null) {
            List<Job> jobsInProgress = getJobService().getIncompleteJobs();
            if ((jobsInProgress != null) && (jobsInProgress.size() > 0)) {
                for (Job job : jobsInProgress) {
                    if ((job.getState() == JobStateType.NOT_STARTED) && 
                            (job.getNumArchives() == 0)) {
                        job.setState(JobStateType.INVALID_REQUEST);
                        job.setStartTime(System.currentTimeMillis());
                        job.setEndTime(System.currentTimeMillis());
                        getJobService().update(job);
                    }
                }
            }
        }
        else {
            LOGGER.error("RETRY: The container-injected reference to the "
                    + "JobService EJB is null.  Unable to determine if "
                    + "there are any incomplete jobs to retry.");
        }
    }
    
    /**
     * We found a situation where all of the individual archives associated 
     * with a job had completed successfully, but the JobTrackerMDB did not
     * process the archive completion messages.  This method was introduced 
     * to handle that particular situation upon node startup.
     */
    public void checkForJobsThatAreReallyComplete() {
        
        if (getJobService() != null) {
            
            List<Job> jobsInProgress = getJobService().getIncompleteJobs();
            if ((jobsInProgress != null) && (jobsInProgress.size() > 0)) {
                for (Job job : jobsInProgress) {
                    if (getElapsedTime(job) > PROCESSING_TIME_THRESHOLD) {
                        if ((job.getArchives() != null) && (job.getArchives().size() > 0)) {
                            
                            int archivesComplete = 0;
                            long sizeComplete = 0L;
                            long filesComplete = 0L;
                            
                            for (Archive archive : job.getArchives()) {
                                if (archive.getArchiveState() == JobStateType.COMPLETE) {
                                    archivesComplete++;
                                    sizeComplete += archive.getSize();
                                    filesComplete += archive.getNumFiles();
                                }
                            }
                            
                            if (archivesComplete == job.getNumArchives()) {
                                LOGGER.info("RETRY: Job [ "
                                        + job.getJobID()
                                        + " ] was complete, but still marked as [ "
                                        + JobStateType.IN_PROGRESS
                                        + " ].  Updating job state information.");
                                job.setTotalSizeComplete(sizeComplete);
                                job.setNumArchivesComplete(archivesComplete);
                                job.setNumFilesComplete(filesComplete);
                                job.setEndTime(job.getStartTime() + PROCESSING_TIME_THRESHOLD);
                                job.setState(JobStateType.COMPLETE);
                                getJobService().update(job);
                            }
                        }
                    }
                }
            }
            else {
                LOGGER.info("RETRY:  There are no in-progress jobs.  Exiting "
                        + "gracefully.");
            }
        }
        else {
            LOGGER.error("RETRY: The container-injected reference to the "
                    + "JobService EJB is null.  Unable to determine if "
                    + "there are any incomplete jobs to retry.");
        }
        
    }
    
    /**
     * This method should only run once when the application container starts
     * up.  It will obtain a list of jobs that are in-progress from the data 
     * model and then check to see if any of the incomplete Archive jobs were
     * being executed on this server.  If the server names match, the Archive
     * is re-submitted to the cluster for processing. 
     */
    public void checkForJobsToRetry() {
        
        EJBClientUtilities utils      = EJBClientUtilities.getInstance();
        String             serverName = utils.getServerName();
        
        if ((serverName != null) && (!serverName.isEmpty())) {
            if (getJobService() != null) {
                
                List<Job> jobsInProgress = getJobService().getIncompleteJobs();
                if ((jobsInProgress != null) && (jobsInProgress.size() > 0)) {
                    
                    for (Job job : jobsInProgress) {
                        if ((job.getArchives() != null) && 
                                (job.getArchives().size() > 0)) {
                            
                            for (Archive archive : job.getArchives()) {
                                if ((archive.getServerName() != null) && 
                                        (!archive.getServerName().isEmpty())) {
                                    if ((archive.getServerName()
                                            .equalsIgnoreCase(serverName)) && 
                                            (archive.getArchiveState() != 
                                            JobStateType.COMPLETE)) {
                                        retry(archive);
                                    }
                                }
                                else {
                                    LOGGER.info("The server name associated " 
                                            + "with job ID [ "
                                            + job.getJobID()
                                            + " ] is not populated.");
                                }
                            }
                        }
                        else {
                            LOGGER.warn("RETRY:  Job ID [ " 
                                    + job.getJobID()
                                    + " ] does not contain any archive jobs "
                                    + "to process.  This should be "
                                    + "investigated.");
                        }
                    }
                        
                }
                else {
                    LOGGER.info("RETRY:  There are no in-progress jobs to "
                            + "retry.");
                }
            }
            else {
                LOGGER.error("RETRY: The container-injected reference to the "
                        + "JobService EJB is null.  Unable to determine if "
                        + "there are any incomplete jobs to retry.");
            }
        }
        else {
            LOGGER.warn("RETRY: Unable to determine the current JVM name.  "
                    + "Retry logic will not be executed.");
        }
        
    }
    
    /**
     * Previous attempts at processing the archive may have created some of 
     * the output files.  Delete any existing output files.
     * 
     * @param archive The archive that we need to re-run.
     */
    private void cleanupPreviousAttempt(Archive archive) {
        
        if (archive != null) {
            
            if ((archive.getArchiveFilename() != null) &&
                    (!archive.getArchiveFilename().isEmpty())) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("RETRY:  Deleting file from previous "
                                + "attempt [ "
                                + archive.getArchiveFilename()
                                + " ].");
                    }
                    FileUtils.delete(archive.getArchiveFilename());
                }
                catch (IOException ioe) {
                    LOGGER.warn("RETRY: Unable to delete target file [ "
                            + archive.getArchiveFilename()
                            + " ].  Unexpected IOException encountered, error "
                            + "message [ "
                            + ioe.getMessage()
                            + " ].");
                }
            }
            if ((archive.getHashFilename() != null) &&
                    (!archive.getHashFilename().isEmpty())) {
                try {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("RETRY:  Deleting file from previous "
                                + "attempt [ "
                                + archive.getHashFilename()
                                + " ].");
                    }
                    FileUtils.delete(archive.getHashFilename());
                }
                catch (IOException ioe) {
                    LOGGER.warn("RETRY: Unable to delete target file [ "
                            + archive.getHashFilename()
                            + " ].  Unexpected IOException encountered, error "
                            + "message [ "
                            + ioe.getMessage()
                            + " ].");
                }
            }
        }
    }
    
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
     * @return Reference to the JobRunnerService EJB.
     */
    private JobRunnerService getJobRunnerService() {
        if (jobRunnerService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to JobRunnerService.  Attempting to "
                    + "look it up via JNDI.");
            jobRunnerService = EJBClientUtilities
                    .getInstance()
                    .getJobRunnerService();
        }
        return jobRunnerService;
    }
    
    /**
     * Method used to restart processing of a given archive job.
     * 
     * @param archive Archive job to retry.
     */
    private void retry(Archive archive) {
        
        LOGGER.info("RETRY:  Retrying job ID [ "
                + archive.getJobID() 
                + " ], archive ID [ "
                + archive.getArchiveID()
                + " ].");
        
        cleanupPreviousAttempt(archive);
        if (getJobRunnerService() != null) {
            getJobRunnerService().run(archive);
        }
        else {
            LOGGER.error("RETRY: The container-injected reference to the "
                        + "JobRunnerService EJB is null.  Unable to retry "
                        + "the archive job.");
        }
        
    }
}
