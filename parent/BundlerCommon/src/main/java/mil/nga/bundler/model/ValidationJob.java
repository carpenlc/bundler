package mil.nga.bundler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import mil.nga.bundler.types.HashType;
import mil.nga.bundler.types.JobStateType;

/**
 * This class represents the backing data model for an asynchronous 
 * validation job.  A validation job accepts a raw list of files.  It then
 * validates that the file exists and collects some simple metadata on the
 * file.  It then calculates a hash on each file and ultimately returns 
 * the validated list of files to the client.  
 * 
 * The validation job contains a list of individual "groups" of files that 
 * are sent into the cluster via JMS and processed as separate jobs. 
 * 
 * The validation jobs are run asynchronously due to the high CPU requirements
 * for calculating the hash.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="VALIDATION_JOBS")
public class ValidationJob implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 6314699211874523243L;
    
    /**
     * Time when the group was completed.  This value will remain zero
     * until the group is completed.
     */
    @Column(name="END_TIME")
    private long endTime = 0L;
    
    /**
     * The type of hash to calculate for the target file. 
     */
    @Enumerated(EnumType.STRING)
    @Column(name="HASH_TYPE")
    private HashType hashType = HashType.MD5;
    
    /**
     * The state of the current job.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="JOB_STATE")
    private JobStateType state = JobStateType.NOT_STARTED;
    
    /**
     * Primary key.
     */
    @Id
    @Column(name="JOB_ID")
    private String jobID = "";
    
    /**
     * The list of validation file groups associated with the validation job.
     */
    @OneToMany(cascade={ CascadeType.ALL },
            orphanRemoval=true,
            fetch=FetchType.EAGER)
    @JoinColumn(name="JOB_ID")
    private List<ValidationFileGroup> groups = new ArrayList<ValidationFileGroup>();
    
    /**
     * Time when processing of the validation group was started.
     */
    @Column(name="START_TIME")
    private long startTime = 0L;
    
    /**
     * Add a group to the job.
     * @param group Group to add to the target job.
     */
    public void addGroup(ValidationFileGroup group) {
        groups.add(group);
    }
    
    /**
     * Getter method for a specific group. 
     * @param groupID The specific group ID to look for.
     * @return The requested group wrapped in an Optional.
     */
    public Optional<ValidationFileGroup> getGroup(int groupID) {
        return Optional.ofNullable(groups.get(groupID));
    }
    
    /**
     * Getter method for the time the validation job was completed.
     * @param state The end time for the validation group.
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Getter method for the type of hash to calculate.
     * @return The type of hash to calculate.
     */
    public HashType getHashType() {
        return hashType;
    }
    
    /**
     * Getter method for the primary key (i.e. JOB_ID).
     * @return The primary key (i.e. JOB_ID).
     */
    public String getJobID() {
        return jobID;
    }
    
    /**
     * Getter method for the job state
     * @return The state of the validation job.
     */
    public JobStateType getJobState() {
        return state;
    }
    
    /**
     * Getter method for the time the job was started
     * @param state The start time of the job
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Get a list of all files contained in the validation job.
     * @return The list of all validated files in the job.
     */
    public List<ValidFile> getValidFiles() {
        List<ValidFile> files = new ArrayList<ValidFile>();
        if ((groups != null) && (groups.size() > 0)) {
            for (ValidationFileGroup group : groups) {
                if ((group.getFiles() != null) && 
                        (group.getFiles().size() > 0)) {
                    for (ValidFile file : group.getFiles()) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }
    
    /**
     * Setter method for the time the validation job was completed.
     * @param state The completion time of the validation job.
     */
    public void setEndTime(long value) {
        endTime = value;
    }
    
    /**
     * Getter method for the type of hash to calculate.
     * @return The type of hash to calculate.
     */
    public void setHashType(HashType value) {
        hashType = value;
    }
    
    /**
     * Setter method for the primary key (i.e. JOB_ID).
     * @param value The primary key (i.e. JOB_ID).
     */
    public void setJobID(String value) {
        jobID = value;
    }
    
    /**
     * Setter method for the job state
     * @param value The state of the validation job.
     */
    public void setJobState(JobStateType value) {
        state = value;
    }
    
    /**
     * Setter method for the time the job was started
     * @param state The start time of the job
     */
    public void setStartTime(long value) {
        startTime = value;
    }
}
