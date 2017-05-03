package mil.nga.bundler.model;

import java.io.Serializable;

import javax.persistence.*;

/**
 * JPA Entity implementation containing the overall bundler statistics.
 * 
 * @author L. Craig Carpenter
 */
@Entity
@Table(name="METRICS")
public class BundlerMetrics implements Serializable {

	/**
	 * Eclipse-generated serialVersionUID
	 */
	private static final long serialVersionUID = 3689210963412585988L;

	/**
	 * Primary key
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID")
	private long ID;
	
	/**
	 * Absolute compression is calculated by taking into account the total 
	 * calculated size divided by the total calculated compression.
	 */
	@Column(name="ABSOLUTE_COMPRESSION_RATIO")
	private double absoluteCompressionRatio = 0.0;
	
	/**
	 * The average time, in milliseconds, required for each individual job.
	 */
	@Column(name="AVERAGE_TIME_PER_JOB")
	private long averageTimePerJob = 0L;
	
	/**
	 * The average, uncompresssed size, of each job.
	 */
	@Column(name="AVERAGE_SIZE_PER_JOB")
	private long averageSizePerJob = 0L;
	
	/**
	 * The average time, in milliseconds, required for each individual job.
	 */
	@Column(name="AVERAGE_NUM_ARCHIVES_PER_JOB")
	private double averageNumArchivesPerJob = 0.0;
	
	/**
     * The average number of files per job.
     */
	@Column(name="AVERAGE_NUM_FILES_PER_JOB")
	private long averageNumFilesPerJob = 0L;
	
	/**
	 * Average compression ratio
	 */
	@Column(name="AVERAGE_COMPRESSION_RATIO")
	private double averageCompressionRatio = 0.0;
	
	/**
	 * The latest job start time included in the metrics calculations.
	 */
	@Column(name="END_TIME")
	private long endTime = 0L;
	/**
	 * Total number of Jobs processed.
	 */
	@Column(name="NUM_JOBS_INCOMPLETE")
	private long numJobsIncomplete = 0L;	
	
	/**
	 * The earliest job start time included in the metrics calculations.
	 */
	@Column(name="START_TIME")
	private long startTime = 0L;

	/**
	 * Total number of archives created.
	 */
	@Column(name="TOTAL_NUM_ARCHIVES")
	private long totalNumArchives = 0L;
	
	/**
	 * Total number of Jobs processed.
	 */
	@Column(name="TOTAL_NUM_JOBS")
	private long totalNumJobs = 0L;
	
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
	 * Running total of the compressed size of data compressed for all downloads.
	 */
	@Column(name="TOTAL_COMPRESSED_SIZE")
	private long totalCompressedSize = 0L;
	
	/**
	 * Default Eclipse-generated constructor.
	 */
	public BundlerMetrics() {
		super();
	}
   
	/**
	 * Getter method for the primary key.
	 * @return The primary key.
	 */
	public long getID() {
		return ID;
	}
	
	/**
	 * Getter method for the absolute compression.  This is calculated by 
	 * taking the overall size divided by the overall compressed size.
	 * @return The absolute compression.
	 */
	public double getAbsoluteCompression() {
		return absoluteCompressionRatio;
	}
	
	/**
	 * Getter method for the average compression per job.  
	 * @return The average compression.
	 */
	public double getAverageCompression() {
		return averageCompressionRatio;
	}
	
	/**
	 * Getter method for the average number of archives per job.  
	 * @return The average number of archives per job.
	 */
	public double getAverageNumArchivesPerJob() {
		return averageNumArchivesPerJob;
	}
	
	/**
	 * Getter method for the average number of files per job.  
	 * @return The average number of files per job.
	 */
	public long getAverageNumFilesPerJob() {
		return averageNumFilesPerJob;
	}
	
	/**
	 * Getter method for the average size of the submitted jobs.  
	 * @return The average size per job.
	 */
	public long getAverageSizePerJob() {
		return averageSizePerJob;
	}
	
	/**
	 * Getter method for the average amount of time spent per job.  
	 * @return The average time per job.
	 */
	public long getAverageTimePerJob() {
		return averageTimePerJob;
	}
	
	/**
	 * Getter method for the latest job start time included in the metrics 
	 * calculations.
	 * @return The latest job start time included in the metrics 
	 * calculations.
	 */
	public long getEndTime() {
		return endTime;
	}
	
	/**
	 * Getter method for the total number of jobs that did not complete.
	 * @return The total number of jobs that did not complete.
	 */
	public long getNumJobsIncomplete() {
		return numJobsIncomplete;
	}
	
	/**
	 * Getter method for the earliest job start time included in the metrics 
	 * calculations.
	 * @return The earliest job start time included in the metrics 
	 * calculations.
	 */
	public long getStartTime() {
		return startTime;
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
	 * Getter method for the total number of jobs processed.
	 * @return The total number of jobs processed.
	 */
	public long getTotalNumJobs() {
		return totalNumJobs;
	}
	
	/**
	 * Getter method for the total size of all target jobs.
	 * @return The total size.
	 */
	public long getTotalSize() {
		return totalSize;
	}
	
	/**
	 * Getter method for the total compressed size of all target jobs.
	 * @return The total compressed size.
	 */
	public long getTotalCompressedSize() {
		return totalCompressedSize;
	}
	
	/**
	 * Reset the internal private member values to initial values.
	 */
	public void reset() {
		setAbsoluteCompression(0.0);
		setAverageCompression(0.0);
		setAverageTimePerJob(0L);
		setAverageSizePerJob(0L);
		setAverageNumArchivesPerJob(0.0);
		setAverageNumFilesPerJob(0L);
		setNumJobsIncomplete(0L);
		setTotalNumArchives(0L);
		setTotalNumJobs(0L);
		setTotalNumFiles(0L);
		setTotalSize(0L);
		setTotalCompressedSize(0L);
		setStartTime(0L);
		setEndTime(0L);
	}
	
	/**
	 * Setter method for the primary key.
	 * @param value The primary key.
	 */
	public void setID(long value) {
		ID = value;
	}
	
	/**
	 * Setter method for the absolute compression.  This is calculated by 
	 * taking the overall size divided by the overall compressed size.
	 * @param value The absolute compression.
	 */
	public void setAbsoluteCompression(double value) {
		absoluteCompressionRatio = value;
	}
	
	/**
	 * Setter method for the average compression per job.  
	 * @param value The average compression.
	 */
	public void setAverageCompression(double value) {
		averageCompressionRatio = value;
	}
	
	/**
	 * Setter method for the average number of archives per job.  
	 * @param value The average number of archives per job.
	 */
	public void setAverageNumArchivesPerJob(double value) {
		averageNumArchivesPerJob = value;
	}
	
	/**
	 * Setter method for the average number of files per job.  
	 * @param value The average number of files per job.
	 */
	public void setAverageNumFilesPerJob(long value) {
		averageNumFilesPerJob = value;
	}
	
	/**
	 * Setter method for the average size of the submitted jobs.  
	 * @param value The average size per job.
	 */
	public void setAverageSizePerJob(long value) {
		averageSizePerJob = value;
	}
	
	/**
	 * Setter method for the average amount of time spent per job.  
	 * @param value The average time per job.
	 */
	public void setAverageTimePerJob(long value) {
		averageTimePerJob = value;
	}
	
	/**
	 * Setter method for the latest job start time included in the metrics 
	 * calculations.
	 * @param value The latest job start time included in the metrics 
	 * calculations.
	 */
	public void setEndTime(long value) {
		endTime = value;
	}
	
	/**
	 * Setter method for the total number of jobs that did not complete.
	 * @param value The total number of jobs that did not complete.
	 */
	public void setNumJobsIncomplete(long value) {
		numJobsIncomplete = value;
	}
	
	/**
	 * Setter method for the earliest job start time included in the metrics 
	 * calculations.
	 * @param value The earliest job start time included in the metrics 
	 * calculations.
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
	 * Setter method for the total number of jobs processed.
	 * @param value The total number of jobs processed.
	 */
	public void setTotalNumJobs(long value) {
		totalNumJobs = value;
	}
	
	/**
	 * Setter method for the total compressed size of all target jobs.
	 * @param value The total compressed size.
	 */
	public void setTotalSize(long value) {
		totalSize = value;
	}
	
	public String toString() {
		
		String        newLine = System.getProperty("line.separator");
		StringBuilder sb      = new StringBuilder();
		
		sb.append(newLine);
		sb.append("----------------------------------------");
		sb.append("----------------------------------------");
		sb.append(newLine);
		sb.append("-----              Calculated Statistics");
		sb.append("                                   -----");
		sb.append(newLine);
		sb.append("----------------------------------------");
		sb.append("----------------------------------------");
		sb.append(newLine);
		sb.append(" Total Num Jobs           : ");
		sb.append(getTotalNumJobs());
		sb.append(newLine);
		sb.append(" Num Jobs Incomplete      : ");
		sb.append(getNumJobsIncomplete());
		sb.append(newLine);
		sb.append(" Total Num Archives       : ");
		sb.append(getTotalNumArchives());
		sb.append(newLine);
		sb.append(" Total Num Files          : ");
		sb.append(getTotalNumFiles());
		sb.append(newLine);
		sb.append(" Total Size               : ");
		sb.append(getTotalSize());
		sb.append(newLine);
		sb.append(" Total Compressed Size    : ");
		sb.append(getTotalCompressedSize());
		sb.append(newLine);
		sb.append(" Absolute Compression     : ");
		sb.append(getAbsoluteCompression());
		sb.append(newLine);
		sb.append(" Average Compression      : ");
		sb.append(getAverageCompression());
		sb.append(newLine);
		sb.append(" Average Time Per Job     : ");
		sb.append(getAverageTimePerJob());
		sb.append(newLine);
		sb.append(" Average Size Per Job     : ");
		sb.append(getAverageSizePerJob());
		sb.append(newLine);
		sb.append(" Average Files Per Job    : ");
		sb.append(getAverageNumFilesPerJob());
		sb.append(newLine);
		sb.append(" Average Archives Per Job : ");
		sb.append(getAverageNumArchivesPerJob());
		sb.append(newLine);
		sb.append(" Start Time               : ");
		sb.append(getStartTime());
		sb.append(newLine);
		sb.append(" End Time                 : ");
		sb.append(getEndTime());
		sb.append(newLine);
		sb.append("----------------------------------------");
		sb.append("----------------------------------------");
		sb.append(newLine);
		return sb.toString();
	}
}
