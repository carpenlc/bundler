package mil.nga.bundler.interfaces;

import java.util.Collection;

import mil.nga.bundler.model.Archive;
import mil.nga.bundler.types.JobStateType;

/**
 * Interface defining methods that must be implemented in order to provide 
 * status on archive jobs being executed by the server.
 * 
 * @author carpenlc
 */
public interface JobTrackerI {

    /**
     * Return the job ID
     * @return The job ID
     */
    public String getJobID();
    
    /**
     * Return the user who submitted the job
     * @return The user
     */
    public String getUserName();
    
    /**
     * Accessor method for the job state.
     * @return The job state.
     */
    public JobStateType getState();
    
    /**
     * Method called as archive jobs complete.  This saves a list of 
     * completed archive files.
     * @param bundle Metadata associated with a completed archive file.
     */
    public void addArchive(Archive archive);
    
    /**
     * Accessor method for the list of archives that were created by this job.
     * @return The list of archives created.
     */
    public Collection<Archive> getArchives();
    
    /**
     * Setter method for the current state of the job.
     * @param state The state of the job
     */
    public void setState(JobStateType state);
        
}
