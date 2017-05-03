package mil.nga.bundler.ejb;

import java.io.File;
import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.archive.ArchiveFactory;
import mil.nga.bundler.exceptions.ArchiveException;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.messages.ArchiveMessage;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;
import mil.nga.util.FileUtils;

/**
 * Message-Driven Bean implementation class for: Archiver
 * 
 * Important note:  Make sure that if this class is deployed to a test server 
 * that is even on the same network as a production cluster you need to make 
 * sure the queue names are different.  If they are the same the test cluster 
 * can read messages from the queue and attempt to process them.
 * 
 * The following files need to be modified to change the queue name from 
 * production to test and vice versa:
 * 
 * <code>mil.nga.bundler.ejb.ArchiverMDB</code>
 * <code>mil.nga.bundler.ejb.JobTrackerMDB</code>
 * 
 * @author L. Craig Carpenter 
 */
@MessageDriven(
                // Note to self, if your MDB implements any interfaces other 
                // than MessageListener, you have to specify which one is the 
                // MessageListener.
                messageListenerInterface=MessageListener.class,
                name = "ArchiverMDB",
                activationConfig = {
                                @ActivationConfigProperty(
                                                propertyName = "destinationType",
                                                propertyValue = "javax.jms.Queue"),
                                @ActivationConfigProperty(
                                                propertyName = "destination",
                                                propertyValue = "queue/ArchiverMessageQ_TEST"),
                                @ActivationConfigProperty(
                                                propertyName = "acknowledgeMode",
                                                propertyValue = "Auto-acknowledge")
                })
public class ArchiverMDB 
        extends NotificationService 
        implements MessageListener, BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */
    static final Logger LOGGER = LoggerFactory.getLogger(ArchiverMDB.class);
    
    /**
     * Container-injected reference to the JobService EJB.
     */
    @EJB
    JobService jobService;
    
    /**
     * Container-injected reference to the HashGenerator service.
     */
    @EJB
    HashGeneratorService hashGeneratorService;
    
    /**
     * Default constructor. 
     */
    public ArchiverMDB() { }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * Method implemented because JBoss EAP 6.x was inexplicably NOT always
     * injecting the EJB (i.e. EJB reference was null)
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
     * Private method used to obtain a reference to the target EJB.  
     * 
     * Method implemented because JBoss EAP 6.x was inexplicably NOT always
     * injecting the EJB (i.e. EJB reference was null)
     * 
     * @return Reference to the HashGeneratorService EJB.
     */
    private HashGeneratorService getHashGeneratorService() {
        if (jobService == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to HashGeneratorService.  Attempting to "
                    + "look it up via JNDI.");
            hashGeneratorService = EJBClientUtilities
                    .getInstance()
                    .getHashGeneratorService();
        }
        return hashGeneratorService;
    }
    
    /**
     * Method driving the creation of the output archive file.
     * 
     * @param job The managed JPA job object.
     * @param archive Archive job to run.
     */
    private void createArchive(Job job, long archiveID) 
            throws ArchiveException, IOException { 
        
        long startTime = System.currentTimeMillis();
        
        try {
            
            Archive archive = job.getArchive(archiveID);
            
            if (archive != null) {
                
                // Get the concrete instance of the archiver that will be
                // used to construct the output archive file.
                ArchiveFactory factory = ArchiveFactory.getFactory();
            
                // Get the concrete Bundler object.
                BundlerI bundler = factory.getInstance(
                                job.getArchiveType());
              
                // Here's where the magic happens.
                bundler.bundle(archive.getFiles(), archive.getArchive());
               
                // Generate the hash file associated with the output archive.
                if (getHashGeneratorService() != null) {
                    getHashGeneratorService().generate(
                            archive.getArchive(),
                            archive.getHash());
                }
                else {
                    LOGGER.warn("Unable to obtain a reference to the "
                            + "HashGenerator EJB.  Unable to create the output "
                            + "hash file associated with job ID [ "
                            + archive.getJobID()
                            + " ] and archive ID [ "
                            + archiveID
                            + " ].  Since few, if any customers actually use "
                            + "the hash for anything we just issue a warning "
                            + "and proceed with processing.");
                }
            
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Archive processing for job ID [ "
                            + archive.getJobID()
                            + " ] and archive ID [ "
                            + archiveID
                            + " ].  Completed in [ "
                            + (System.currentTimeMillis() - startTime)
                            + " ] ms.");
                }

            }
            else {
                LOGGER.error("Unable to find archive to process for "
                            + "job ID [ "
                            + job.getJobID()
                            + " ] and archive ID [ "
                            + archiveID
                            + " ].");
            }
        
        }
        catch (UnknownArchiveTypeException uate) {
            // We should never see this exception here.  However, we will log 
            // it as there must a programming error.
            LOGGER.error("Unexpected UnknownArchiveException raised while "
                    + "actually creating the output archive.  This sitation "
                    + "should have been caught much earlier than here.  "
                    + "Error message [ "
                    + uate.getMessage()
                    + " ].");
        }

        
    }
    
    /**
     * This method is used to notify the Tracker MDB that the processing 
     * associated with a single Archive has completed.  The JPA Archive 
     * object is wrapped in an ObjectMessage and then placed on the 
     * appropriate JMS Queue.
     * 
     * @param archive The JPA Archive containing information associated with
     * the output files created.
     */
    private void notify(Archive archive) {
        super.notify(TRACKER_DEST_Q,
                new ArchiveMessage(
                        archive.getJobID(), 
                        archive.getArchiveID()));
    
    }
    
    /**
     * This method invokes the bundler processing for a single archive job.
     * It listens for messages placed on the JMS Queue queue/ArchiverMessageQ.
     * When a message is received it unwraps the Archive object from the 
     * JMS ObjectMessage and then proceeds to perform the bundle operation 
     * specified. 
     * 
     * @see MessageListener#onMessage(Message)
     */
    @Override
    public void onMessage(Message message) {
        
        try {
            
            ObjectMessage objMessage = (ObjectMessage)message;
            ArchiveMessage archiveMsg = (ArchiveMessage)objMessage.getObject();
            
            LOGGER.info("ArchiverMDB received notification to process [ " 
                    + archiveMsg.toString()
                    + " ].");
            
            if (getJobService() != null) {
                
                Job job = getJobService().getJob(archiveMsg.getJobID());
                if (job != null) {
                    
                    Archive archive = job.getArchive(archiveMsg.getArchiveID());
                    if (archive != null) {
                        
                        // Update the archive to reflect that archive processing 
                        // has started.
                        archive.setHostName(FileUtils.getHostName());
                        archive.setServerName(
                                EJBClientUtilities.getInstance().getServerName());
                        archive.setStartTime(System.currentTimeMillis());
                        archive.setArchiveState(JobStateType.IN_PROGRESS);    
                        
                        getJobService().update(job);
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Creating output archive file for "
                                    + "archive [ "
                                    + archive.toString()
                                    + " ].");
                        }
                        try {
                            createArchive(job, archiveMsg.getArchiveID());
                            archive.setArchiveState(JobStateType.COMPLETE);
                        }
                        catch (IOException ioe) {
                            LOGGER.error("Unexpected IOException raised while "
                                    + "creating the output archive.  Archive "
                                    + "state will be set to ERROR for job ID [ "
                                    + job.getJobID()
                                    + " ] archive ID [ "
                                    + archive.getArchiveID()
                                    + " ].  Error message [ "
                                    + ioe.getMessage()
                                    + " ].");
                            archive.setArchiveState(JobStateType.ERROR);
                        }
                        catch (ArchiveException ae) {
                            LOGGER.error("Unexpected ArchiveException raised "
                                    + "while "
                                    + "creating the output archive.  Archive "
                                    + "state will be set to ERROR for job ID [ "
                                    + job.getJobID()
                                    + " ] archive ID [ "
                                    + archive.getArchiveID()
                                    + " ].  Error message [ "
                                    + ae.getMessage()
                                    + " ].");
                            archive.setArchiveState(JobStateType.ERROR);
                        }
                        
                        // Update the end time.
                        archive.setEndTime(System.currentTimeMillis());
                        archive.setSize(
                                getArchiveFileSize(
                                        archive.getArchive()));
                        
                        // Ensure the Job object is persisted.
                        getJobService().update(job);
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Archive complete.  Sending " 
                                    + "notification message file for "
                                    + "archive [ "
                                    + archive.toString()
                                    + " ].");
                        }
                        notify(archive);
                        
                    }
                    else {
                        LOGGER.error("Unable to find an Archive matching [ "
                                + archiveMsg.toString()
                                + " ].");
                    }
                }
                else {
                    LOGGER.error("Unable to find a Job matching [ "
                                + archiveMsg.toString()
                                + " ].");
                }
            }
            else {
                LOGGER.error("Unable to obtain a reference to the JobService "
                        + "EJB.  Unable to process [ "
                        + archiveMsg.toString()
                        + " ].");
            }
        }
        catch (JMSException jmsEx) {
            LOGGER.error("Unexpected JMSException encountered while attempting "
                    + "to retrieve the ArchiveMessage from the target message "
                    + "queue.  Error message [ "
                    + jmsEx.getMessage()
                    + " ].");
        }
    }
    
    /**
     * Simple method used to retrieve the size of the created archive file.
     * 
     * @param archive The completed Archive object.
     */
    private long getArchiveFileSize(String archive) {
        
        long size = 0L;
        
        if ((archive != null) && (!archive.isEmpty())) {
            File file = new File(archive);
            if (file.exists()) {
                size = file.length();
            }
            else {
                LOGGER.error("The expected output archive file [ "
                        + archive
                        + " ] does not exist.");
            }
        }
        return size;
    }

}
