package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import mil.nga.bundler.model.BundlerMetrics;
import mil.nga.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean class used in conjunction with the bundler metrics display (i.e. PrimeFaces).
 * 
 * @author L. Craig Carpenter
 *
 */
public abstract class ViewJobMetrics implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = -7239094854239590801L;
    
    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(
            ViewJobMetrics.class);
    
    /**
     * DateFormat class used to format the start and end times.
     */
    protected static DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    /** 
     * Eclipse-generated default constructor.
     */
    public ViewJobMetrics() {}
    
    /**
     * Getter method for the total number of files compressed.
     * @return The total number of files compressed.
     */
    public String getTotalNumFiles() {
        long value = -1L;
        if (getMetrics() != null) {
            value = getMetrics().getTotalNumFiles();
        }
        return Long.toString(value);
    }
    
    /**
     * Getter method for the total number of jobs processed.
     * @return The total number of jobs processed.
     */
    public String getTotalNumJobs() {
        long value = -1L;
        if (getMetrics() != null) {
            value = getMetrics().getTotalNumJobs();
        }
        return Long.toString(value);
    }
    
    /**
     * Getter method for the total number of jobs not yet completed.
     * @return The total number of jobs not yet completed.
     */
    public String getNumJobsIncomplete() {
        long value = -1L;
        if (getMetrics() != null) {
            value = getMetrics().getNumJobsIncomplete();
        }
        return Long.toString(value);
    }
    
    /**
     * Get the percentage of jobs that have completed successfully.
     * @return The job completion percentage.
     */
    public String getJobCompletionPercentage() {
            double value = 0.0;
            DecimalFormat df = new DecimalFormat(".#####");
            if (getMetrics() != null) { 
                if (getMetrics().getTotalNumJobs() > 0) {
                value = 100.0 * (
                        (double)(getMetrics().getTotalNumJobs() - 
                        getMetrics().getNumJobsIncomplete())/
                        (double)(getMetrics().getTotalNumJobs()));
                }
            }
            return df.format(value) + " %";
    }
    
    /**
     * Getter method for the total number of archives created.
     * @return The total number of archives created.
     */
    public String getTotalNumArchives() {
        long value = -1L;
        if (getMetrics() != null) {
            value = getMetrics().getTotalNumArchives();
        }
        return Long.toString(value);
    }
    
    /**
     * Getter method for the total amount of uncompressed data transferred.
     * @return The total amount of uncompressed data transferred.
     */
    public String getDataTransferred() {
        String value = "0 MB";
        if (getMetrics() != null) { 
            value = FileUtils.humanReadableByteCount(
                    getMetrics().getTotalSize(), 
                    false);
        }
        return value;
    }
    
    /**
     * Getter method for the total amount of compressed data transferred.
     * @return The total amount of compressed data transferred.
     */
    public String getCompressedDataTransferred() {
        String value = "0 MB";
        if (getMetrics() != null) { 
            value = FileUtils.humanReadableByteCount(
                    getMetrics().getTotalCompressedSize(), 
                    false);
        }
        return value;
    }
    
    /**
     * Getter method for the average number of files processed per job.
     * @return The average number of files processed per job.
     */
    public String getAverageNumFilesPerJob() {
        long value = -1L;
        if (getMetrics() != null) { 
            value = getMetrics().getAverageNumFilesPerJob();
        }
        return Long.toString(value);
    }
    
    /**
     * Getter method for the average number of archives created per job.
     * @return The average number of archives created per job.
     */
    public String getAverageNumArchivesPerJob() {
        double value = -1.0;
        DecimalFormat df = new DecimalFormat(".##");
        if (getMetrics() != null) { 
            value = getMetrics().getAverageNumArchivesPerJob();
        }
        return df.format(value);
    }
    
    /**
     * Getter method for the average amount of time it took to process a job.
     * @return The average amount of time it took to process a job.
     */
    public String getAverageElapsedTimePerJob() {
        long value = -1L;
        if (getMetrics() != null) { 
            value = getMetrics().getAverageTimePerJob();
        }
        return Long.toString(value);
    }
    
    /**
     * Getter method for the average compression percentage.
     * @return The average compression percentage.
     */
    public String getAbsoluteCompression() {
        double value = -1L;
        if (getMetrics() != null) { 
            value = getMetrics().getAbsoluteCompression();
        }
        NumberFormat formatter = NumberFormat.getPercentInstance();
        formatter.setMaximumFractionDigits(2);
        return formatter.format(value);
    }
    
    /**
     * Getter method for the average compression percentage.
     * @return The average compression percentage.
     */
    public String getAverageCompression() {
        double value = -1L;
        if (getMetrics() != null) { 
            value = getMetrics().getAverageCompression();
        }
        NumberFormat formatter = NumberFormat.getPercentInstance();
        formatter.setMaximumFractionDigits(2);
        return formatter.format(value);
    }
    
    /**
     * Getter method for the average size per job.
     * @return The average size per job.
     */
    public String getAverageSizePerJob() {
        String value = "0 MB";
        if (getMetrics() != null) { 
            value = FileUtils.humanReadableByteCount(
                    getMetrics().getAverageSizePerJob(), 
                    false);
        }
        return value;
    }
    
    /**
     * Getter method for the end time of metrics collection.
     * @return The date of the newest job submitted.
     */
    public String getEndTime() {
        String value = "unknown";
        if (getMetrics() != null) { 
            value = df.format(getMetrics().getEndTime());
        }
        return value;
    }
    
    /**
     * Getter method for the start time of metrics collection.
     * @return The date of the earliest job submitted.
     */
    public String getStartTime() {
        String value = "unknown";
        if (getMetrics() != null) { 
            value = df.format(getMetrics().getStartTime());
        }
        return value;
    }
    
    /**
     * Create the label identifying the start/end times for the 
     * output JSF display.  This was added because you apparently cannot
     * embed multiple bean references in a single text field.
     * 
     * @return Start/end time label.
     */
    public String getTimeFrame() {
        StringBuilder sb = new StringBuilder();
        sb.append("From ");
        sb.append(getStartTime());
        sb.append(" to ");
        sb.append(getEndTime());
        return sb.toString();
    }
    
    /**
     * Subclasses must implement their own logic for obtaining metrics 
     * information to display.
     * @return The metrics data that will be displayed to the user.
     */
    public abstract BundlerMetrics getMetrics();
}
