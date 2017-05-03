package mil.nga.bundler.messages;

import java.io.Serializable;

/**
 * Simple Java Bean class used to pass information associated with individual 
 * archive jobs between nodes in the cluster.  The object will contain enough
 * information to uniquely identify an ArchiveJob to process.
 * 
 * @author L. Craig Carpenter
 */
public class ArchiveMessage implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -1900130496098489193L;
    
    private String jobID     = null;
    private long    archiveID = -1L;
    
    /**
     * Default constructor.
     */
    public ArchiveMessage() {}
    
    /**
     * Alternate constructor.
     * @param jobID The job ID associated with the archive.
     * @param archiveID The archive ID. 
     */
    public ArchiveMessage(String jobID, long archiveID) {
        setJobID(jobID);
        setArchiveID(archiveID);
    }
    
    /**
     * Getter method for the Archive ID field.
     * @return The ID associated with the target archive.
     */
    public long getArchiveID() {
        return archiveID;
    }

    /**
     * Setter method for the Archive ID field.
     * @param value The ID associated with the target archive.
     */
    public void setArchiveID(long value) {
        archiveID = value;
    }
    
    /**
     * Getter method for the Job ID field.
     * @return The job ID associated with the target archive.
     */
    public String getJobID() {
        return jobID;
    }
    
    /**
     * Setter method for the Job ID field.
     * @param value The Job ID associated with the target archive.
     */
    public void setJobID(String value) {
        jobID = value;
    }
    
    /**
     * Overridden toString method.
     * @return String version of the ArchiveMessage object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArchiveMessage: job ID => [ ");
        sb.append(getJobID());
        sb.append(" ], archive ID => [ ");
        sb.append(getArchiveID());
        sb.append(" ].");
        return sb.toString();
    }
}
