package mil.nga.bundler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import mil.nga.bundler.types.JobStateType;

/**
 * This class represents the backing data model for a group of files requiring
 * validation.  All files in a group will be processed on a single cluster 
 * node.  
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="VALIDATION_FILE_GROUP")
public class ValidationFileGroup implements Serializable {

    /**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 5896077351721296322L;
	
    /**
     * Time when the group was completed.  This value will remain zero
     */
    @Column(name="END_TIME")
    private long endTime = 0L;
    
    /**
	 * The list of Files to be validated.
	 */
	@OneToMany(cascade={ CascadeType.ALL },
			orphanRemoval=true,
			fetch=FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name="GROUP_ID", referencedColumnName="GROUP_ID"),
		@JoinColumn(name="JOB_ID", referencedColumnName="JOB_ID")
	})
    List<ValidFile> files = new ArrayList<ValidFile>();
	
	/**
	 * Foreign key linking the VALIDATION_JOB and VALIDATION_FILE_GROUP tables.
	 */
	@Column(name="GROUP_ID")
	private long groupID = 0;
	
	/**
	 * The server that processed the archive job.
	 */
    @Column(name="HOST_NAME")
    private String hostName = null;
    
    /**
	 * Primary key
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private long ID;
    
	/**
	 * Foreign key linking the VALIDATION_JOBS and VALIDATION_FILE_GROUP tables.
	 */
	@Column(name="JOB_ID")
	private String jobID = null;
	
	/**
     * Time when processing of the validation group was started.
     */
    @Column(name="START_TIME")
    private long startTime = 0L;
    
	/**
     * Added to keep track of the processing state of individual validation
     * jobs.  Very large jobs were tending to not be recognized as completed.  
     * This flag was added in order to implement additional checks on the 
     * completion of in-progress bundle requests.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="VALIDATION_STATE")
    private JobStateType validationState = JobStateType.NOT_STARTED;
    
    /**
     * Add a file to the group.
     * @param file File to add to the group.
     */
    public void addFile(ValidFile file) {
    	files.add(file);
    }
    
	/**
     * Getter method for the time the group was completed.
     * @param state The end time for the validation group.
     */
    public long getEndTime() {
    	return endTime;
    }
    
    /**
     * Getter method for the list of files in the group.
     * @return The list of files in the group.
     */
    public List<ValidFile> getFiles() {
    	return files;
    }
    
    /**
     * Getter method for the group ID.
     * @return The group ID.
     */
    public long getGroupID() {
    	return groupID;
    }
    
    /**
     * The server that processed the validation group.
     * @return The server name that processed the validation group.
     */
    public String getHostName() {
            return hostName;
    }
    
    /**
     * Getter method for the ID.
     * @return The ID.
     */
    public long getID() {
    	return ID;
    }
    
	/**
	 * Getter method for the foreign key (i.e. JOB_ID).
	 * @return The foreign key (i.e. JOB_ID).
	 */
	public String getJobID() {
		return jobID;
	}
	
	/**
     * Getter method for the time the group was started.
     * @return The start time for the validation group.
     */
    public long getStartTime() {
    	return startTime;
    }
    
	/**
     * Setter method for the state of the validation group.
     * @return state of the validation group.
     */
    public JobStateType getValidationState() {
    	return validationState;
    }
    
	/**
     * Setter method for the time the group was completed.
     * @param state The completion time of the group.
     */
    public void setEndTime(long value) {
    	endTime = value;
    }
    
    /**
     * Setter method for the list of files in the group.
     * @param value The list of files in the group.
     */
    public void setFiles(List<ValidFile> value) {
    	files = value;
    }
    
    /**
     * Setter method for the group ID.
     * @param value The group ID.
     */
    public void setGroupID(long value) {
    	groupID = value;
    }
    

    /**
     * Setter method for the host name
     * @param value The server name
     */
    public void setHostName(String value) {
    	hostName = value;
    }
    
    /**
     * Setter method for the ID.
     * @param value The ID.
     */
    public void setID(long value) {
    	ID = value;
    }
    
	/**
	 * Setter method for the foreign key (i.e. JOB_ID).
	 * @param value The foreign key (i.e. JOB_ID).
	 */
	public void setJobID(String value) {
		jobID = value;
	}
	
	/**
     * Setter method for the time the group was started
     * @param value The start time of the group
     */
    public void setStartTime(long value) {
    	startTime = value;
    }
    
	/**
     * Setter method for the state of the validation group.
     * @param value state of the validation group.
     */
    public void setValidationState(JobStateType value) {
    	validationState = value;
    }
}
