package mil.nga.bundler.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import mil.nga.bundler.types.HashType;
import mil.nga.bundler.types.JobStateType;

/**
 * Data structure containing information on a validated file.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="VALID_FILES")
public class ValidFile implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -8997938319579420023L;

    /**
     * The full path to the on-disk file.
     */
    @Column(name="PATH")
    private String path = null;
    
    /**
     * The entry path (i.e. the path within the output archive) of the target
     * file.  Note that this field is not used in conjunction with the 
     * validation jobs and is not persisted.
     */
    @Transient
    private String entryPath = null;
    
    /**
     * Foreign key linking the VALIDATION_FILE_GROUP and VALID_FILES tables.
     */
    @Column(name="GROUP_ID")
    private long groupID = 0;
    
    /**
     * String containing the file hash.
     */
    @Column(name="HASH")
    private String hash = null;
    
    /**
     * The type of hash.  Defaulted to the most common (i.e. MD5) 
     */
    @Enumerated(EnumType.STRING)
    @Column(name="HASH_TYPE")
    private HashType hashType = HashType.MD5;
    
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID")
    private long ID;
    
    /**
     * The size of the file in bytes.
     */
    @Column(name="SIZE")
    private long size = 0L;
    
    /**
     * The last modified date on the file.
     */
    @Column(name="DATE")
    private String date;
    
    /**
     * Added to keep track of the processing state of individual files.
     */
    @Enumerated(EnumType.STRING)
    @Column(name="FILE_STATE")
    private JobStateType validationState = JobStateType.NOT_STARTED;
    
    /**
     * Used to convert the last modified date to a String.
     */
    private static final SimpleDateFormat sdf = 
            new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    
    /**
     * Default constructor.
     */
    public ValidFile() {} 
    
    /**
     * Alternate constructor allowing clients to set the values of some of 
     * the internal members at construction time.
     *  
     * @param path The full path to the on-disk file.
     * @param size The size of the on-disk file.
     */
    public ValidFile(String path, long size) {
        setPath(path);
        setSize(size);
    }
    
    /**
     * Alternate constructor allowing clients to set the values of some of 
     * the internal members at construction time.
     *  
     * @param path The full path to the on-disk file.
     * @param size The size of the on-disk file.
     * @param date The last modified time (milliseconds from epoch)
     */
    public ValidFile(String path, long size, long date) {
        setPath(path);
        setDate(date);
        setSize(size);
    }
    
    /**
     * Alternate constructor allowing clients to set the values of some of 
     * the internal members at construction time.
     *  
     * @param path The full path to the on-disk file.
     * @param size The size of the on-disk file.
     */
    public ValidFile(String path, String entryPath, long size) {
        setPath(path);
        setEntryPath(entryPath);
        setSize(size);
    }
    
    /**
     * Setter method for the last modified date of the file.
     * @param value The last modified date (in milliseconds from epoch)
     */
    public String getDate() {
        return date;
    }
    
    /**
     * The full path within the output archive file.
     * @return The entry path within the output archive file.
     */
    public String getEntryPath() {
        return entryPath;
    }
    
    /**
     * Getter method for the hash calculated for the file.
     * @return The calculated hash.
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Getter method for the type of hash calculated for the file.
     * @return The hash type.
     */
    public HashType getHashType() {
        return hashType;
    }
    
    /**
     * Getter method for the full file path.
     * @return The full path to the target file (wrapped in Optional)
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Getter method for the value associated with the on-disk file size.
     * @return The size in bytes of the on-disk file.
     */
    public long getSize() {
        return size;
    }
    
    /**
     * Getter method for the state of the individual file.
     * @return The state of the individual file.
     */
    public JobStateType getValidationState() {
        return validationState;
    }
    
    /**
     * Setter method for the last modified date of the file.
     * @param value The last modified date (in milliseconds from epoch)
     */
    public void setDate(long value) {
        date = sdf.format(value);
    }
    
    /**
     * Setter method for the entry path within the output archive file.
     * @param value The value for the archive entry path for the file.
     */
    public void setEntryPath(String value) {
        entryPath = value;
    }
    
    /**
     * Getter method for the hash calculated for the file.
     * @param value The calculated hash.
     */
    public void setHash(String value) {
        hash = value;
    }
    
    /**
     * Getter method for the type of hash calculated for the file.
     * @param value  The hash type.
     */
    public void setHashType(HashType value) {
        hashType = value;
    }
    
    /**
     * Setter method for the full file path.
     * @param value The value for the full file path.
     */
    public void setPath(String value) {
        path = value;
    }
    
    /**
     * Setter method for the value associated with the on-disk file size.
     * @param value The size in bytes of the on-disk file.
     */
    public void setSize(long value) {
        size = value;
    }
    
    /**
     * Setter method for the state of the individual file.
     * @param value The state of the individual file.
     */
    public void setValidationState(JobStateType value) {
        validationState = value;
    }
    /**
     * Convert to a printable String.
     */
    public String toString() { 
        StringBuilder sb = new StringBuilder();
        String        newLine = System.getProperty("line.separator");
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        sb.append("Path             : ");
        sb.append(getPath());
        sb.append(newLine);
        if ((getEntryPath() != null) && (!getEntryPath().isEmpty())) { 
            sb.append("Entry Path       : ");
            sb.append(getEntryPath());
            sb.append(newLine);
        }
        sb.append("Size             : ");
        sb.append(getSize());
        sb.append(newLine);
        sb.append("Date             : ");
        sb.append(getDate());
        sb.append(newLine);
        sb.append("Hash Type        : ");
        sb.append(getHashType());
        sb.append(newLine);
        sb.append("Hash             : ");
        sb.append(getHash());
        sb.append(newLine);
        sb.append("----------------------------------------");
        sb.append("----------------------------------------");
        sb.append(newLine);
        return sb.toString();
    }
}
