package mil.nga.bundler.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class is responsible for holding the metrics associated with each
 * individual host in the cluster.  We wanted to know if any particular host 
 * was processing a larger percentage of the individual archive jobs.  We also 
 * wanted to know whether any hosts were markedly slower than others.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="HOST_METRICS")
public class HostMetrics implements Serializable {

    /*
      List of columns supported: 
      
      COMPLETED_ARCHIVES
      END_TIME
      ERROR_ARCHIVES
      HOST_NAME
      ID (primary key)
      INVALID_ARCHIVES
      START_TIME
      TOTAL_COMPRESSED_SIZE
      TOTAL_ELAPSED_TIME
      TOTAL_NUM_ARCHIVES
      TOTAL_NUM_FILES
      TOTAL_SIZE
      
     */
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 3833848427059213460L;

    /**
     * The number of archives that completed successfully.
     */
    @Column(name="COMPLETED_ARCHIVES")
    private long completedArchives = 0L;
    
    /**
     * The latest time included in the calculation.
     */
    @Column(name="END_TIME")
    private long endTime = 0L;
    
    /**
     * The number of archives that completed in an error condition.
     */
    @Column(name="ERROR_ARCHIVES")
    private long errorArchives = 0L;
    
    /**
     * The host that was responsible for processing the data in this metrics
     * record.
     */
    @Column(name="HOST_NAME")
    private String hostName;
    
    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID")
    private long ID;
    
    /**
     * The number of archives that are invalid.
     */
    @Column(name="INVALID_ARCHIVES")
    private long invalidArchives = 0L;
    
    /**
     * The earliest time included in the calculation.
     */
    @Column(name="START_TIME")
    private long startTime = 0L;
    
    /**
     * Running total of the compressed size of data compressed for all downloads.
     */
    @Column(name="TOTAL_COMPRESSED_SIZE")
    private long totalCompressedSize = 0L;
    
    /**
     * Running total amount of time spent running archive jobs.
     */
    @Column(name="TOTAL_ELAPSED_TIME")
    private long totalElapsedTime = 0L;
    
    /**
     * Total number of archives processed by this host.
     */
    @Column(name="TOTAL_NUM_ARCHIVES")
    private long totalNumArchives = 0L;
    
    /**
     * Total number of files compressed for download.
     */
    @Column(name="TOTAL_NUM_FILES")
    private long totalNumFiles = 0L;
    
    /**
     * Running total of the size of data compressed for download.
     */
    @Column(name="TOTAL_SIZE")
    private long totalSize = 0L;
    
    /**
     * Getter method for the total number of completed archives.
     * @return The total number of completed archives.
     */
    public long getCompletedArchives() {
        return completedArchives;
    }
    
    /**
     * Getter method for the date of the last record processed.
     * @return The date of the last record processed.
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Getter method for the total number of errored archives.
     * @return The total number of errored archives.
     */
    public long getErrorArchives() {
        return errorArchives;
    }
    
    /**
     * The server that processed the archive job.
     * @return The server that processed the archive job.
     */
    public String getHostName() {
        return hostName;
    }
    
    /**
     * Getter method for the primary key.
     * @return The primary key.
     */
    public long getID() {
        return ID;
    }
    
    /**
     * Getter method for the total number of invalid archives.
     * @return The total number of invalid archives.
     */
    public long getInvalidArchives() {
        return invalidArchives;
    }
    
    /**
     * Getter method for the date of the earliest record processed.
     * @return The date of the earliest record processed.
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Getter method for the total compressed size of all target jobs.
     * @return The total compressed size.
     */
    public long getTotalCompressedSize() {
        return totalCompressedSize;
    }
    
    /**
     * Getter method for the total elapsed time spent processing archive 
     * jobs. 
     * @return The total elapsed time.
     */
    public long getTotalElapsedTime() {
        return totalElapsedTime;
    }
    
    /**
     * Getter method for the total number of archives associated with all 
     * jobs.
     * @return The total number of archives processed.
     */
    public long getTotalNumArchives() {
        return totalNumArchives;
    }
    
    /**
     * Getter method for the total number of files associated with all 
     * jobs.
     * @return The total number of files processed.
     */
    public long getTotalNumFiles() {
        return totalNumFiles;
    }
    
    /**
     * Getter method for the total size of all target jobs.
     * @return The total size.
     */
    public long getTotalSize() {
        return totalSize;
    }
    
    /**
     * Setter method for the total number of completed archives.
     * @param The total number of completed archives.
     */
    public void setCompletedArchives(long value) {
        completedArchives = value;
    }
    
    /**
     * Setter method for the date of the last record processed.
     * @param value The date of the last record processed.
     */
    public void setEndTime(long value) {
        endTime = value;
    }
    
    /**
     * Setter method for the total number of errored archives.
     * @param value The total number of errored archives.
     */
    public void setErrorArchives(long value) {
        errorArchives = value;
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
     * Setter method for the total number of invalid archives.
     * @param value The total number of invalid archives.
     */
    public void setInvalidArchives(long value) {
        invalidArchives = value;
    }
    
    /**
     * Setter method for the date of the earliest record processed.
     * @param The date of the earliest record processed.
     */
    public void setStartTime(long value) {
        startTime = value;
    }
    
    /**
     * Setter method for the total compressed size of all target jobs.
     * @param value The total compressed size.
     */
    public void setTotalCompressedSize(long value) {
        totalCompressedSize = value;
    }
    
    /**
     * Setter method for the total elapsed time spent processing archive 
     * jobs. 
     * @param value The total elapsed time.
     */
    public void setTotalElapsedTime(long value) {
        totalElapsedTime = value;
    }
    
    /**
     * Setter method for the total number of archives associated with all 
     * jobs.
     * @param value The total number of archives processed.
     */
    public void setTotalNumArchives(long value) {
        totalNumArchives = value;
    }
    
    /**
     * Setter method for the total number of files associated with all 
     * jobs.
     * @param value The total number of files processed.
     */
    public void setTotalNumFiles(long value) {
        totalNumFiles = value;
    }
    
    /**
     * Setter method for the total compressed size of all target jobs.
     * @param value The total compressed size.
     */
    public void setTotalSize(long value) {
        totalSize = value;
    }
}
