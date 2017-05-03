package mil.nga.bundler.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.types.JobStateType;
import mil.nga.util.FileUtils;

/**
 * JPA entity implementation class for Entity: Archive
 * 
 * Also contains JAX-B annotations.  This bean holds the data associated 
 * with a single output archive file. 
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="ARCHIVE_JOBS")
@JsonRootName(value="archive")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Archive implements BundlerConstantsI, Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 9023436709593213577L;

	/**
	 * Foreign key linking the archive and JOB tables.
	 */
	@Column(name="ARCHIVE_ID")
	@JsonIgnore
	private long archiveID     = 0;
	
    /**
     * The size of the output archive file
     */
    @Column(name="ARCHIVE_SIZE")
    @JsonIgnore
    private long size          = 0;
    
    /**
	 * The list of Files to be included in the Archive.
	 */
	@OneToMany(cascade={ CascadeType.ALL },
			orphanRemoval=true,
			fetch=FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name="ARCHIVE_ID", referencedColumnName="ARCHIVE_ID"),
		@JoinColumn(name="JOB_ID", referencedColumnName="JOB_ID")
	})
	@JsonIgnore
    List<FileEntry> files = new ArrayList<FileEntry>();
	
    /**
     * Local path of the  the output archive file
     */
    @Column(name="ARCHIVE_FILE")
    @JsonProperty(value="archive_file")
    private String archive     = null;
    
	/**
     * Added to keep track of the processing state of individual 
     * archives.  Very large archive requests were tending to not be
     * recognized as completed.  This flag was added in order to implement
     * additional checks on the completion of in-progress bundle requests.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="ARCHIVE_STATE")
    @JsonIgnore
    private JobStateType archiveState = JobStateType.NOT_STARTED;

	/**
	 * The type of archive to create with this job.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="ARCHIVE_TYPE")
	@JsonIgnore
	private ArchiveType archiveType = ArchiveType.ZIP;
	
    /**
     * External accessible URL of the output archive file
     */
    @Column(name="ARCHIVE_URL")
    @JsonProperty(value="archive_url")
    private String archiveURL  = null;
    
    /**
     * Time when the archive job was completed.  This value will remain zero
     */
    @Column(name="END_TIME")
    @JsonIgnore
    private long endTime = 0L;
    
    /**
	 * The server that processed the archive job.
	 */
    @Column(name="HOST_NAME")
    @JsonIgnore
    private String hostName    = null;

    /**
     * Local path to the file containing the MD5 hash of the
     * output archive file
     */
    @Column(name="HASH_FILE")
    @JsonProperty(value="hash_filename")
    private String hashFile = null;

    /**
     * External accessible URL of the file containing the MD5 hash of the
     * output archive file
     */
    @Column(name="HASH_FILE_URL")
    @JsonProperty(value="hash_url")
    private String hashFileURL = null;
    
    /**
	 * Primary key
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	@JsonIgnore
	private long ID;
	
	/**
	 * Foreign key linking the JOBS and ARCHIVES tables.
	 */
	@Column(name="JOB_ID")
	@JsonIgnore
	private String jobID = null;

    /**
     * The number of files contained in the archive.
     */
    @Column(name="NUM_FILES")
    @JsonIgnore
    private int numFiles = 0;

    /**
     * The JBoss JVM server name that is responsible for processing the 
     * archive.
     */
    @Column(name="SERVER_NAME")
    @JsonIgnore
    private String serverName  = "";
    
	/**
     * Time when the archive job was started.
     */
    @Column(name="START_TIME")
    @JsonIgnore
    private long startTime = 0L;
	
	/**
	 * Default Eclipse-generated constructor.
	 */
	public Archive() {
		super();
	}
   
	/**
	 * Alternate constructor allowing clients to supply important
	 * values.
	 * @param jobID The associated job ID.
	 * @param archiveID The ID of the archive (foreign key)
	 * @param type The type of output archive to create.
	 */
	public Archive(
			String      jobID, 
			long        archiveID, 
			ArchiveType type) {
		setJobID(jobID);
		setArchiveID(archiveID);
		setArchiveType(type);
	}
	
	/**
	 * Add a file to the archive for later processing.
	 * @param file FileEntry object to add the list.
	 */
	public void add(FileEntry file) {
		if (files == null) {
			files = new ArrayList<FileEntry>();
		}
		files.add(file);
	}
	
	/**
	 * Called by the JobFactory class when we have determined that all files
	 * that will be added have been added.   It then updates internal values
	 * with totals for the archive.
	 */
	public void complete() {
		long sizeAccumulator = 0L;
		if ((getFiles() != null) && (getFiles().size() > 0)) {
			for (FileEntry file : files) {
				sizeAccumulator += file.getSize();
			}
		}
		setNumFiles(getFiles().size());
		setSize(sizeAccumulator);
	}
	
    /**
     * Getter method for the external accessible URL of the output archive 
     * file.
     * @return The URL of the archive file.
     */
    public String getArchive() {
        return archive;
    }
    
    /**
     * Getter method for archive ID (foreign key)
     * @return The ID of the archive (foreign key)
     */
    public long getArchiveID() {
        return archiveID;
    }
    
    /**
     * This method will return the just the filename associated with 
     * the archive file.  This is used for display purposes. 
     * @return The name of the archive file.
     */
    @JsonIgnore
    public String getArchiveFilename() {
        String name = "";
        if ((getArchive() != null) && 
        		(!getArchive().trim().equalsIgnoreCase(""))) {
        	name = (new File(getArchive())).getName();
        }
        return name;
    }
    
    /**
     * Getter method for the state of the archive processing.
     * @return The state of current archive processing.
     */
    @JsonIgnore
    public JobStateType getArchiveState() {
        return archiveState;
    }

    /**
     * Getter method for the type of archive to create.
     * @return The type of output archive.
     */
    @JsonIgnore
    public ArchiveType getArchiveType() {
            return archiveType;
    }
    
    /**
     * Getter method for the external accessible URL of the output archive 
     * file.
     * @return The URL of the archive file.
     */
    @JsonIgnore
    public String getArchiveURL() {
            return archiveURL;
    }
    
	/**
     * Getter method for the time the job was completed
     * @param state The end time of the job
     */
    @JsonIgnore
    public long getEndTime() {
    	return endTime;
    }
    
    /**
     * Return the file size.
     * @return File size in bytes.
     */
    @JsonIgnore
    public long getSize() {
        return size;
    }
    
   
    /**
     * Return the file size.
     * @return File size in bytes.
     */
    @JsonProperty(value="size")
    public String getSizeHR() {
        return FileUtils.humanReadableByteCount(size, true);
    }
    /**
     * Getter method for the name of the JVM that processed the job.
     * @return The name of the JVM that processed the job.
     */
    public String getServerName() {
            return serverName;
    }

    /**
     * Getter method for the list of files to include in the output 
     * archive.
     * @return The list of files in the output archive.
     */
    @JsonIgnore
    public List<FileEntry> getFiles() {
            return files;
    }
    
    /**
     * Getter method for the local path of the file containing 
     * the hash of the output archive file.
     * @return The local path to the hash file.
     */
    @JsonIgnore
    public String getHash() {
            return hashFile;
    }

    /**
     * This method will return the just the filename associated with 
     * the hash file.  This is used for display purposes. 
     * @return The name of the hash file.
     */
    @JsonIgnore
    public String getHashFilename() {
    	String name = "";
    	if ((getHash() != null) && (!getHash().trim().equalsIgnoreCase(""))) {
    		name = (new File(getHash())).getName();
    	}
    	return name;
    }

    /**
     * Getter method for the externally accessible URL of the file containing 
     * the hash of the output archive file.
     * @return The URL of the hash file.
     */
    @JsonIgnore
    public String getHashURL() {
            return hashFileURL;
    }

    /**
     * The server that processed the archive job.
     * @return The server name that processed the archive job.
     */
    @JsonIgnore
    public String getHostName() {
            return hostName;
    }
	
    /**
	 * Getter method for the primary key.
	 * @return The primary key.
	 */
    @JsonIgnore
	public long getID() {
		return ID;
	}
	
	/**
	 * Getter method for the foreign key (i.e. JOB_ID).
	 * @return The foreign key (i.e. JOB_ID).
	 */
    @JsonIgnore
	public String getJobID() {
		return jobID;
	}
	
    /**
     * Getter method for the number of files contained in the output archive.
     * @return The number of files contained in the output archive.
     */
    @JsonIgnore
    public int getNumFiles() {
            return numFiles;
    }
    
	/**
     * Getter method for the time the job was started
     * @param state The start time of the job
     */
    @JsonIgnore
    public long getStartTime() {
    	return startTime;
    }
    
    /** 
     * Setter method for the local path of the file containing 
     * the hash of the output archive file.
     * @param value The URL of the archive file.
     */
    public void setArchive(String value) {
        archive = value;
    }

    /** 
     * Setter method for the archive ID (foreign key).
     * @param value The archive ID.
     */
    public void setArchiveID(long value) {
    	archiveID = value;
    }
    
    /** 
     * Setter method for the current state of archive processing.
     * @param value The current state of archive processing.
     */
    public void setArchiveState(JobStateType value) {
        archiveState = value;
    }
    
    /** 
     * Setter method for the type of archive to create.
     * @param value The type of archive to create.
     */
    public void setArchiveType(ArchiveType value) {
    	archiveType = value;
    }

    /** 
     * Setter method for the external accessible URL of the file containing 
     * the MD5 hash of the output archive file.
     * @param value The URL of the archive file.
     */
    public void setArchiveURL(String value) {
            archiveURL = value;
    }

	/**
     * Setter method for the time the job was completed
     * @param state The completion time of the job
     */
    public void setEndTime(long value) {
    	endTime = value;
    }
    
    /**
     * Setter method for the name of the JVM that processed the job.
     * @param value The name of the JVM that processed the job.
     */
    public void setServerName(String value) {
    	serverName = value;
    }
    
    /**
     * Setter method for the size of the output archive.
     * @param value The size of the output archive file.
     */
    public void setSize(long value) {
            size = value;
    }

    /**
     * Setter method for the list of files to include in the output 
     * archive.
     * @param value The list of files in the output archive.
     */
    public void setFiles(List<FileEntry> value) {
            files = value;
    }
    
    /**
     * Setter method for the local path to the hash file.
     * @param value The local path of the hash file.
     */
    public void setHash(String value) {
            hashFile = value;
    }

    /**
     * Setter method for the URL to the MD5 hash file.
     * @param value The URL of the MD5 hash file.
     */
    public void setHashURL(String value) {
            hashFileURL = value;
    }

    /**
     * Setter method for the host name
     * @param value The server name
     */
    public void setHostName(String value) {
            hostName = value;
    }

	/**
	 * Setter method for the primary key.
	 * @param value The primary key.
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
     * The number of files contained in the output archive.
     * @param value The number of files contained in the output archive.
     */
    public void setNumFiles(int value) {
            numFiles = value;
    }
    
	/**
     * Setter method for the time the job was started
     * @param state The start time of the job
     */
    public void setStartTime(long value) {
    	startTime = value;
    }
    
	/**
     * Convert the internal members to a String (for logging purposes).
     * @return Printable string
     */
    public String toString() {
        String newLine = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("  ID               : ");
        sb.append(getID());
        sb.append(newLine);
        sb.append("  Job ID           : ");
        sb.append(getJobID());
        sb.append(newLine);
        sb.append("  Archive ID       : ");
        sb.append(getArchiveID());
        sb.append(newLine);
        sb.append("  Archive State    : " );
        sb.append(getArchiveState().getText());
        sb.append(newLine);
        sb.append("  Host             : ");
        sb.append(getHostName());
        sb.append(newLine);
        sb.append("  Server           : ");
        sb.append(getServerName());
        sb.append(newLine);
        sb.append("  Archive          : ");
        sb.append(getArchive());
        sb.append(newLine);
        sb.append("  Archive Filename : " );
        sb.append(getArchiveFilename());
        sb.append(newLine);
        sb.append("  Archive URL      : ");
        sb.append(getArchiveURL());
        sb.append(newLine);
        sb.append("  Hash             : ");
        sb.append(getHash());
        sb.append(newLine);
        sb.append("  Hash Filename    : " );
        sb.append(getHashFilename());
        sb.append(newLine);
        sb.append("  Hash URL         : ");
        sb.append(getHashURL());
        sb.append(newLine);
        sb.append("  Num Files        : ");
        sb.append(getNumFiles());
        sb.append(newLine);
        sb.append("  File Size        : ");
        sb.append(getSize());
        sb.append(newLine);
        sb.append("  Start Time       : ");
        sb.append(FileUtils.getTimeAsString(
        		UNIVERSAL_DATE_STRING, 
        		getStartTime()));
        sb.append(newLine);
        sb.append("  End Time         : ");
        sb.append(FileUtils.getTimeAsString(
        		UNIVERSAL_DATE_STRING, 
        		getEndTime()));
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("  File Entry Objects ");
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        for (FileEntry file : getFiles()) {
        	sb.append(file.toString());
        }
        return sb.toString();
    }
	
}
