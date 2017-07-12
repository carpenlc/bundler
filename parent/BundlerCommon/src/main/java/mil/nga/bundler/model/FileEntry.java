package mil.nga.bundler.model;

import java.io.File;
import java.io.Serializable;

import javax.persistence.*;

import mil.nga.bundler.types.JobStateType;

/**
 * Entity implementation class for Entity: FileEntry
 * 
 * Simple Java bean class holding information associated with one file
 * that will be added to the output archive.  This class was introduced in
 * order to provide better metrics on in-progress file archives.
 * 
 * This data is essentially perishable and does not need to exist after the 
 * parent archive job has been processed.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="FILE_ENTRY")
public class FileEntry implements Serializable {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -5956390125792424038L;

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID")
    private long ID;
    
    /**
     * Foreign key linking all the way back to the job this FileEntry 
     * is associated with.  This member is not marked final as it will 
     * change after construction. 
     */
    @Column(name="JOB_ID")
    private String jobID;
    
    /**
     * Foreign key linking the FileEntry table with the Archive table.
     * This member is not marked final as it will change after construction.
     */
    @Column(name="ARCHIVE_ID")
    private long archiveID;
    
    /**
     * This file is used in conjunction with external users who are 
     * looking for status on in-progress bundler jobs.  It will have 
     * a state of either 'NOT_STARTED' or 'COMPLETE'.  There are no 
     * other intermediate states that will be tracked.  This member 
     * is not marked final as it will change after construction.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="FILE_STATE")
    private JobStateType fileState = JobStateType.NOT_STARTED;
    
    /**
     * The absolute path to the on-disk file.
     */
    @Column(name="PATH")
    private String path = "";
    
    /**
     * Path value to be inserted into the output Archive file.
     */
    @Column(name="ARCHIVE_ENTRY_PATH")
    private String entryPath = "";
    
    /**
     * The size of the on-disk file.
     */
    @Column(name="FILE_SIZE")
    private long size = 0L;
    
    /**
     * Default Eclipse-generated constructor.
     */
    public FileEntry() {
        super();
    }
    
    /**
     * Alternate constructor allowing clients to supply params on construction.
     * 
     * @param path The full path to a file.
     * @param size The size of the target file.
     */
    public FileEntry(String path, long size) {
        super();
        setFilePath(path);
        setSize(size);
    }
    
    /**
     * Alternate constructor allowing clients to supply params on construction.
     * 
     * @param path The full path to a file.
     * @param path The archive entry path for a file.
     * @param size The size of the target file.
     */
    public FileEntry(String path, String entryPath, long size) {
        super();
        setFilePath(path);
        setEntryPath(entryPath);
        setSize(size);
    }
    
    /**
     * Alternate constructor allowing clients to supply params on construction.
     * 
     * @param path The full path to a file.
     * @param size The size of the target file.
     */
    public FileEntry(
            String jobID, 
            long   archiveID, 
            String path, 
            long   size) {
        super();
        setJobID(jobID);
        setArchiveID(archiveID);
        setFilePath(path);
        setSize(size);
    }
    
    
    /**
     * Alternate constructor allowing clients to supply the actual file
     * object.
     * 
     * @param file The file that will be archived.
     */
    public FileEntry(File file) {
        if (file != null) {
            if (file.exists() && (!file.isDirectory())) {
                setFilePath(file.getAbsolutePath());
                setSize(file.length());
            }
        }
    }
    
    /**
     * Getter method for the foreign key (i.e. ARCHIVE_ID).
     * 
     * @return The foreign key (i.e. ARCHIVE_ID).
     */
    public long getArchiveID() {
        return archiveID;
    }
    
    /**
     * Getter method for the entry path (i.e. the path within the output 
     * archive file.)  This will be set by the PathFactory class.  If it 
     * is not set, the file path will be returned.
     * 
     * @return The file entry path. 
     */
    public String getEntryPath() {
        return entryPath;
    }
    
    /**
     * Getter method for the full path to the target file.
     * 
     * @return The full path to a file.
     */
    public String getFilePath() {
        return path;
    }
    
    /**
     * Getter method for the state of the file during a bundle operation.
     * @return The file state.
     */
    public JobStateType getFileState() {
        return fileState;
    }
    
    /**
     * Getter method for the primary key.
     * @return The primary key.
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
     * Getter method for the uncompressed size of the file.
     * @return The size of the target file.
     */
    public long getSize() {
        return size;
    }
    
    /**
     * Setter method for the primary key.
     * @param value The primary key.
     */
    public void setArchiveID(long value) {
        archiveID = value;
    }
    
    /**
     * Setter method for the entry path to the target file.
     * 
     * @param value The archive entry path for a file.
     */
    public void setEntryPath(String value) {
        entryPath = value;
    }
    
    /**
     * Setter method for the full path to the target file.
     * 
     * @param value The full path to a file.
     */
    public void setFilePath(String value) {
        path = value;
    }
    
    /**
     * Setter method for the state of the file during a bundle operation.
     * @param value The file state.
     */
    public void setFileState(JobStateType value) {
        fileState = value;
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
     * Getter method for the uncompressed size of the file.
     * @param value The size of the target file.
     */
    public void setSize(long value) {
        size = value;
    }
    
    /**
     * Convert to internal members to String format for log/display.
     */
    public String toString() {
        
        StringBuilder sb      = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("  File to Archive:");
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("  ID            : ");
        sb.append(getID());
        sb.append(newLine);
        sb.append("  Job ID        : ");
        sb.append(getJobID());
        sb.append(newLine);
        sb.append("  Archive ID    : ");
        sb.append(getArchiveID());
        sb.append(newLine);
        sb.append("  File Path     : ");
        if ((getFilePath() == null) || (getFilePath().isEmpty())) {
            sb.append("<null>");
        }
        else {
            sb.append(getFilePath());
        }
        sb.append(newLine);
        sb.append("  Archive Entry : ");
        if ((getEntryPath() == null) || (getEntryPath().isEmpty())) {
            sb.append("<null>");
        }
        else {
            sb.append(getEntryPath());
        }
        sb.append(newLine);
        sb.append("  Size          : ");
        sb.append(Long.toString(getSize()));
        sb.append(newLine);
        sb.append("  File State    : ");
        sb.append(getFileState().getText());
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        return sb.toString();
    }
    
    
}
