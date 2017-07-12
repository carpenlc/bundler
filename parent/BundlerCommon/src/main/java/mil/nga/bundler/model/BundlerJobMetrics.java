package mil.nga.bundler.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.types.JobStateType;

/**
 * Note: This class contains the persistence annotations but we don't actually
 * use hibernate.  They were left in in order to ensure the container builds the
 * target table.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="BUNDLER_JOB_METRICS")
public class BundlerJobMetrics implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 6753952384456901668L;

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
     * Time required to execute the job.
     */
    @Column(name="ELAPSED_TIME")
    private long elapsedTime = 0L;
    
    /**
     * 16 character job id used as the primary key.
     */
    @Id
    @Column(name="JOB_ID")
    private String jobID;
    
    /**
     * The state of the current job.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="JOB_STATE")
    private JobStateType jobState = JobStateType.NOT_STARTED;
    
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
     * Time when the bundle job was started.
     */
    @Column(name="START_TIME")
    private long startTime = 0L;
    
    /**
     * Total accumulated compressed size of the archives created by the 
     * job.
     */
    @Column(name="TOTAL_COMPRESSED_SIZE")
    private long totalCompressedSize = 0L;
    
    /**
     * Total accumulated uncompressed size of the list of files to be 
     * processed. 
     */
    @Column(name="TOTAL_SIZE")
    private long totalSize = 0L;

    /**    
     * The user who submitted the job.
     */
    @Column(name="USER_NAME")
    private String userName = "";
    
    /**
     * Default no-arg constructor required by hibernate.
     */
    public BundlerJobMetrics() {}
    
    /**
     * Constructor used to build an object from the specified
     * builder.
     * @param builder The builder object.
     */
    public BundlerJobMetrics(BundlerJobMetricsBuilder builder) {
        archiveSize         = builder.archiveSize;
        archiveType         = builder.archiveType;
        elapsedTime         = builder.elapsedTime;
        jobID               = builder.jobID;
        jobState            = builder.jobState;
        numArchives         = builder.numArchives;
        numArchivesComplete = builder.numArchivesComplete;
        numFiles            = builder.numFiles;
        numFilesComplete    = builder.numFilesComplete;
        startTime           = builder.startTime;
        totalCompressedSize = builder.totalCompressedSize;
        totalSize           = builder.totalSize;
        userName            = builder.userName;
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
     * Getter method for the target size associated with each individual 
     * archive.
     * @return The target size for each individual archive.
     */
    public long getArchiveSize() {
        return archiveSize;
    }
    
    /**
     * Getter method for the amount ot time taken to complete the job. 
     * @return The elapsed time for the job.
     */
    public long getElapsedTime() {
        return elapsedTime;
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
     * Getter method for total number of files in the job that have completed
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
     * Getter method for total compressed size of the job.
     * @return Total compressed size of the job.
     */
    public long getTotalCompressedSize() {
        return totalCompressedSize;
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
    public JobStateType getJobState() {
        return jobState;
    }

    /**
     * Getter method for the username who submitted the job
     * @param value the user name
     */
    public String getUserName() {
        return userName;
    }
    
    /**
     * Class implementing the Builder creation pattern for new 
     * BundlerJobMetrics objects.
     * 
     * @author L. Craig Carpenter
     */
    public static class BundlerJobMetricsBuilder {
        
        private long         archiveSize;
        private ArchiveType  archiveType;
        private long         elapsedTime;
        private String       jobID;
        private JobStateType jobState;
        private int          numArchives;
        private int          numArchivesComplete;
        private long         numFiles;
        private long         numFilesComplete;
        private long         startTime;
        private long         totalCompressedSize;
        private long         totalSize;
        private String       userName;
        
        /**
         * Method used to actually construct the BundlerJobMetrics object.
         * @return A constructed and validated BundlerJobMetrics object.
         */
        public BundlerJobMetrics build() throws IllegalStateException {
            BundlerJobMetrics object = new BundlerJobMetrics(this);
            validateBundlerJobMetricsObject(object);
            return object;
        }
        
        /**
         * Setter method for the type of output archive to create with this job.
         * 
         * @param value The type of archive specified by the caller.
         * @return Reference to the parent builder object.
         * @see mil.nga.bundler.types.ArchiveType
         */
        public BundlerJobMetricsBuilder archiveType(ArchiveType value) {
            archiveType = value;
            return this;
        }
        
        /**
         * Setter method for the target size associated with each individual 
         * archive.
         * 
         * @param The size of the archive requested by the user.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder archiveSize(long value) {
            archiveSize = value;
            return this;
        }
        
        /**
         * Setter method for elapsed time for the job.
         * 
         * @param The elapsed time for the job.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder elapsedTime(long value) {
            elapsedTime = value;
            return this;
        }
        
        /**
         * Setter method for total number of archives in the job.
         * 
         * @param The number of archives to be generated.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder numArchives(int value) {
            numArchives = value;
            return this;
        }
        
        /**
         * Setter method for total number of archives in the job that have 
         * completed processing.
         * 
         * @param value The number of archives processed.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder numArchivesComplete(int value) {
            numArchivesComplete = value;
            return this;
        }
        
        /**
         * Setter method for total number of files in the job.
         * 
         * @param value the number of files to be processed.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder numFiles(long value) {
            numFiles = value;
            return this;
        }
        
        /**
         * Setter method for total number of files in the job that have completed
         * processing.
         * 
         * @param value the number of files processed.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder numFilesComplete(long value) {
            numFilesComplete = value;
            return this;
        }
        
        /**
         * Setter method for the time when the archive job started.
         * 
         * @param value the compressed size
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder startTime(long value) {
            startTime = value;
            return this;
        }
        
        /**
         * Setter method for total size of the job.
         * 
         * @param value the total (uncompressed) size of the job.
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder totalSize(long value) {
            totalSize = value;
            return this;
        }
        
        /**
         * Setter method for total compressed size of the job.
         * 
         * @param value the compressed size
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder totalCompressedSize(long value) {
            totalCompressedSize = value;
            return this;
        }
        
        /**
         * Setter method for the primary key (i.e. JOB_ID).
         * 
         * @param value the job ID
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder jobID(String value) {
            jobID = value;
            return this;
        }
        
        /**
         * Setter method for the current state of the job in progress.
         * 
         * @param value the job state
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder jobState(JobStateType value) {
            jobState = value;
            return this;
        }

        /**
         * Setter method for the username who submitted the job
         * 
         * @param value the user name
         * @return Reference to the parent builder object.
         */
        public BundlerJobMetricsBuilder userName(String value) {
            userName = value;
            return this;
        }
        
        /**
         * Validate that all required fields are populated.
         * 
         * @param object The BundlerJobMetrics object to validate.
         * @throws IllegalStateException Thrown if any of the required fields 
         * are not populated.
         */
        private void validateBundlerJobMetricsObject(BundlerJobMetrics object) 
                throws IllegalStateException {
            if ((object.getJobID() == null) || (object.getJobID().isEmpty())) {
                throw new IllegalStateException("Invalid value for JOB_ID.  "
                        + "Value is [ "
                        + object.getJobID()
                        + " ].");
            }
        }
    }
}
