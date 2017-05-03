package mil.nga.bundler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.types.JobStateType;

/**
 * Entity implementation class for Entity: Job
 * 
 * JPA entity following the Java bean pattern that holds information on the
 * status of in-progress jobs.  There are several fields who's only purpose are 
 * to provide status on in-progress jobs to clients.
 * 
 * Updated:  Fields contained in Entity JobMetrics moved local to the job class.
 * Dealing with the normalized JobMetrics class was too much of a pain.
 */
@Entity
@Table(name="JOBS")
public class Job implements Serializable {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 1254855180208366654L;
    
    /**
     * The list of Archives associated with the Job.
     */
    @OneToMany(cascade={ CascadeType.ALL },
            orphanRemoval=true,
            fetch=FetchType.EAGER)
    @JoinColumn(name="JOB_ID")
    List<Archive> archives = new ArrayList<Archive>();
    
    /**
     * The target size of each output archive.
     */
    @Column(name="ARCHIVE_SIZE")
    private long archiveSize = 0;
    
    /**
     * The type of archive to create with this job.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="ARCHIVE_TYPE")
    private ArchiveType archiveType = ArchiveType.ZIP;
    
    /**
     * Time when the bundle job was completed.  This value will remain zero
     * until the job is complete.
     */
    @Column(name="END_TIME")
    private long endTime = 0L;
    
    /**
     * Primary key.
     */
    @Id
    @Column(name="JOB_ID")
    private String jobID = "";
    
    /**
     * Number of archives contained in the job.
     */
    @Column(name="NUM_ARCHIVES")
    private int numArchives = 0;
    
    /**
     * Total number of archives that have completed processing.
     */
    @Column(name="NUM_ARCHIVES_COMPLETE")
    private int numArchivesComplete = 0;
    
    /**
     * Total number of files to be processed by this job.
     */
    @Column(name="NUM_FILES")
    private long numFiles = 0L;
    
    /**
     * Total number of files that have completed processing.
     */
    @Column(name="NUM_FILES_COMPLETE")
    private long numFilesComplete = 0L;
    
    /**
     * The state of the current job.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="JOB_STATE")
    private JobStateType state = JobStateType.NOT_STARTED;
    
    /**
     * Time when the bundle job was started.
     */
    @Column(name="START_TIME")
    private long startTime = 0L;
    
    /**
     * Total accumulated uncompressed size of the list of files to be 
     * processed. 
     */
    @Column(name="TOTAL_SIZE")
    private long totalSize = 0L;
    
    /**
     * Total accumulated uncompressed size of the list of files that have
     * been processed. 
     */
    @Column(name="TOTAL_SIZE_COMPLETE")
    private long totalSizeComplete = 0L;

    /**    
     * The user who submitted the job
     */
    @Column(name="USER_NAME")
    private String userName = "";

    
    /**
     * Default Eclipse-generated constructor.
     */
    public Job() {
        super();
    }
    
    /**
     * Add an archive to the list of archive jobs to process.
     * @param archive An archive job.
     */
    public void addArchive(Archive archive) {
        if (archives == null) {
            archives = new ArrayList<Archive>();
        }
        archives.add(archive);
    }
    
    /**
     * Getter method for the type of output archive to create with this job.
     * @return The archive type 
     * @see mil.nga.bundler.types.ArchiveType
     */
    public ArchiveType getArchiveType() {
        return archiveType;
    }
    
    /**
     * Allow clients to obtain a reference to a specific archive based on
     * an input archive ID.
     * @param archiveID The archive ID to retrieve
     * @return The requested Archive object, or null if the archive cannot 
     * be found.
     */
    public Archive getArchive(long archiveID) {
        Archive archive = null;
        if ((archives != null) && (archives.size() > 0)) {
            for (Archive current : archives) {
                if (current.getArchiveID() == archiveID) {
                    archive = current;
                    break;
                }
            }
        }
        return archive;
    }
    
    /**
     * Getter method for the target size associated with each individual 
     * archive.
     * @return The target size for each individual archive.
     */
    public long getArchiveSize() {
        return archiveSize;
    }
    
    /**
     * Getter method for the list of archives to be created by the job.
     * @return The list of bundles created.
     */
    public List<Archive> getArchives() {
            return archives;
    }
    
    /**
     * Getter method for the time when the archive job completed.
     * @return The time the archive job completed.
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Getter method for total number of archives in the job.
     * @return Total number of archives in the job.
     */
    public int getNumArchives() {
        return numArchives;
    }
    
    /**
     * Getter method for total number of archives in the job that have 
     * completed processing.
     * @return Total number of archives in the job that have completed 
     * processing.
     */
    public int getNumArchivesComplete() {
        return numArchivesComplete;
    }
    
    /**
     * Getter method for total number of files in the job.
     * @return Total number of files in the job.
     */
    public long getNumFiles() {
        return numFiles;
    }
    
    /**
     * Setter method for total number of files in the job that have completed
     * processing.
     * @return value Total number of files in the job that have completed 
     * processing.
     */
    public long getNumFilesComplete() {
        return numFilesComplete;
    }
    
    /**
     * Getter method for the time when the archive job started.
     * @return The time the archive job started.
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Getter method for total size of the job.
     * @return Total size of the job.
     */
    public long getTotalSize() {
        return totalSize;
    }
    
    /**
     * Getter method for the size of the data that has completed processing.
     * @return The accumulated size of the files that have completed 
     * processing.
     */
    public long getTotalSizeComplete() {
        return totalSizeComplete;
    }
    
    /**
     * Getter method for the primary key (i.e. JOB_ID).
     * @return The primary key (i.e. JOB_ID).
     */
    public String getJobID() {
        return jobID;
    }
    
    
    /**
     * Getter method for the current state of the job in progress.
     * @return The current state of the job.
     */
    public JobStateType getState() {
        return state;
    }

    /**
     * Setter method for the username who submitted the job
     * @param value the user name
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Setter method for the target size associated with each individual 
     * archive.
     * @param value The target size for each individual archive.
     */
    public void setArchiveSize(long value) {
        archiveSize = value;
    }
    
    /**
     * Setter method for the list of Archives created by this job.
     * @param values List of Archive objects.
     */
    public void setArchives(List<Archive> values) {
        archives = values;
    }
    
    /**
     * Getter method for the type of output archive to create with this job.
     * @param value The archive type 
     * @see mil.nga.bundler.types.ArchiveType
     */
    public void setArchiveType(ArchiveType value) {
        archiveType = value;
    }
    
    /**
     * Setter method for the time the job was completed
     * @param state The completion time of the job
     */
    public void setEndTime(long value) {
        endTime = value;
    }
    
    /**
     * Setter method for total number of archives in the job.
     * @param value Total number of archives in the job.
     */
    public void setNumArchives(int value) {
        numArchives = value;
    }
    
    /**
     * Setter method for total number of archives in the job that have 
     * completed processing.
     * @param value Total number of archives in the job that have completed 
     * processing.
     */
    public void setNumArchivesComplete(int value) {
        numArchivesComplete = value;
    }
    
    /**
     * Setter method for total number of files in the job.
     * @param value Total number of files in the job.
     */
    public void setNumFiles(long value) {
        numFiles = value;
    }
    
    /**
     * Setter method for total number of files in the job that have completed
     * processing.
     * @param value Total number of files in the job that have completed 
     * processing.
     */
    public void setNumFilesComplete(long value) {
        numFilesComplete = value;
    }
    
    /**
     * Setter method for total size of the job.
     * @param value Total size of the job.
     */
    public void setTotalSize(long value) {
        totalSize = value;
    }
    
    /**
     * Setter method for the size of the data that has completed processing.
     * @param value The accumulated size of the files that have completed 
     * processing.
     */
    public void setTotalSizeComplete(long value) {
        totalSizeComplete = value;
    }
    
    /**
     * Setter method for the primary key (i.e. JOB_ID).
     * @param value The primary key (i.e. JOB_ID).
     */
    public void setJobID(String value) {
        jobID = value;
    }
    
    /**
     * Setter method for the current state of the job.
     * @param state The state of the job
     */
    public void setState(JobStateType value) {
        state = value;
    }
    
    /**
     * Setter method for the time the job was started
     * @param state The start time of the job
     */
    public void setStartTime(long value) {
        this.startTime = value;
    }
    
    /**
     * Setter method for the username who submitted the job
     * @param value the user name
     */
    public void setUserName(String value) {
        userName = value;
    }

    /**
     * Overridden toString method used to output relevant data on the job.
     */
    public String toString() {
        String        newLine = System.getProperty("line.separator");
        StringBuilder sb      = new StringBuilder();
        
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("Job ID                : ");
        sb.append(getJobID());
        sb.append(newLine);
        sb.append("User Name             : ");
        sb.append(getUserName());
        sb.append(newLine);
        sb.append("Num Archives          : ");
        if (getArchives() != null) {
            sb.append(getArchives().size());
        }
        else {
            sb.append("0");
        }
        sb.append(newLine);
        sb.append("Num Files             : ");
        sb.append(getNumFiles());
        sb.append(newLine);
        sb.append("Total Size            : ");
        sb.append(getTotalSize());
        sb.append(newLine);
        sb.append("Num Archives Complete : ");
        sb.append(getNumArchivesComplete());
        sb.append(newLine);
        sb.append("Num Files Complete    : ");
        sb.append(getNumFilesComplete());
        sb.append(newLine);
        sb.append("Num Size Complete     : ");
        sb.append(getTotalSizeComplete());
        sb.append(newLine);

        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        if ((getArchives() != null) && (getArchives().size() > 0)) {
            for (Archive arch : getArchives()) {
                if (arch != null) {
                    sb.append(arch.toString());
                }
            }
        }
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        
        //if ((getMissingFiles() != null) && (getMissingFiles().size() > 0)) {
        //    sb.append("Missing file list : ");
        //    sb.append(newLine);
        //    for (String filename : getMissingFiles()) {
        //        sb.append("     ");
        //        sb.append(filename);
        //        sb.append(newLine);
        //    }
        //}
        
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        
        
        return sb.toString();
    }
    
}
