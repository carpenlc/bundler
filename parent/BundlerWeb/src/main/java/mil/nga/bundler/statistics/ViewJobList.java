package mil.nga.bundler.statistics;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;
import mil.nga.util.FileUtils;

/** 
 * Class used to generate the data model for display in a prime data table.
 * The data displayed will be the list of jobs submitted over the course of the
 * last 30 days.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@ViewScoped
public class ViewJobList 
        extends ChartModel 
        implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 3895819524672705315L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(ViewJobList.class);
    
    /**
     * List of jobs submitted over the course of the last 30 days.
     */
    private List<Job> jobList = null;

    /**
     * How far to go back in time looking for jobs to display.
     */
    private static final int DAYS_IN_PAST = 14;
    
    /**
     * The job ID selected in the statistics page
     */
    private String selectedJobID = null;
    
    /**
     * The job ID selected in the statistics page
     */
    private Job selectedJob = null;
    
    /**
     * Formatter object for time objects.
     */
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    /**
     * This method serves as the constructor which will create and populate 
     * the internal BarChartModel object. 
     */
    @PostConstruct
    public void initialize() {
        jobList = getJobList(DAYS_IN_PAST);
        if ((jobList == null) || (jobList.size() == 0)) {
            LOGGER.error("Unable to find any jobs submitted in the "
                    + "last [ "
                    + DAYS_IN_PAST
                    + " ] days .");
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(" [ "
                        + jobList.size()
                        + " ] jobs selected.");
                
            }
        }
    }
    
    /**
     * Calculate the total size of the individual archive jobs contained 
     * within the job.
     * @param job The target job.
     * @return The accumulated compressed size of the individual archive 
     * jobs.
     */
    public long getCompressedSize(Job job) {
        long accumulator = 0L;
        if (job != null) {
            if (job.getState() == JobStateType.COMPLETE) {
                if ((job.getArchives() != null) && 
                        (job.getArchives().size() > 0)) {
                    for (Archive archive : job.getArchives()) {
                        accumulator += archive.getSize();
                    }
                }
            }
        }
        return accumulator;
    }
    
    /**
     * Calculate the total size of the individual archive jobs contained 
     * within the job.
     * @param job The target job.
     * @return The accumulated compressed size of the invidual archive 
     * jobs.
     */
    public String getCompressedSizeHR(Job job) {
        String size = "n/a";
        if (job != null) {
            if (job.getState() == JobStateType.COMPLETE) {
                size = FileUtils.humanReadableByteCount(
                        getCompressedSize(job), true);
            }
        }
        return size;
        
    }
    
    /**
     * Get the compression percentage value in a printable string format.
     * 
     * @param job The Job object that we wish to get the compressed 
     * percentage from.
     * @return Percent compression.
     */
    public String getCompressionPercent(Job job) {
        String value = "n/a";
        if (job != null) {
            if (job.getState() == JobStateType.COMPLETE) {
                double ratio = getCompressionRatio(job);
                NumberFormat percent = NumberFormat.getPercentInstance();
                percent.setMaximumFractionDigits(1);
                value = percent.format(ratio);
            }
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unable to calculate compression percentage.  "
                        + "Input job is null.");
            }
        }
        return value;
    }

    /**
     * Calculate the compression ratio.
     * @param job The target job.
     * @return Percentage compression obtained.
     */
    public double getCompressionRatio(Job job) {
        
         long   totalSize      = job.getTotalSize();
         long   compressedSize = getCompressedSize(job);
         double ratio          = 0.0;

         if ((compressedSize == 0) || (totalSize == 0)) {
             ratio = 0.0;
         }
         else if (compressedSize < totalSize) {
             ratio = ((double)(totalSize - compressedSize) /
                                 (double)totalSize);
         }
         return ratio;
    }
    
    /**
     * Determine the elapsed time for display in the output data table.
     * Time will be displayed in milliseconds.
     * 
     * @param job Job for which to calculate the elapsed time.
     * @return The elapsed time (in ms).
     */
    public String getElapsedTime(Job job) {
        String elapsedTime = "n/a";
        if (job != null) {
            if ((job.getStartTime() != 0) && (job.getEndTime() != 0)) {
                elapsedTime = Long.toString(
                        job.getEndTime() - job.getStartTime()) 
                        + " ms";
            }
        }
        return elapsedTime;
    }
    
    /**
     * Getter method for the list of jobs submitted over the course of the 
     * last 30 days.
     * @return List of jobs to output in the job list tab of the statistics 
     * page.
     */
    public List<Job> getJobs() {
        return jobList;
    }
    
    /**
     * Getter method for the job currently selected in the statistics page.
     * @return The currently selected job.
     */
    public Job getSelectedJob() {
        return selectedJob;
    }
    
    /**
     * Getter method for the job ID currently selected in the statistics page.
     * @return The currently selected job ID.
     */
    public String getSelectedJobID() {
        return selectedJobID;
    }
    
    /**
     * Construct a printable String from the start time.
     * @param job The target job
     * @return A String formatted from long-based time.
     */
    public String getStartTimeHR(Job job) {
        Date date = new Date(job.getStartTime());
        return sdf.format(date);
    }
    
    /**
     * Return a String containing a human readable version of the uncompressed size
     * of the job that was submitted.
     * 
     * @param job The target job.
     * @return A string representing the job size.
     */
    public String getTotalSizeHR(Job job) {
        String size = "n/a";
        if (job != null) {
            size = FileUtils.humanReadableByteCount(job.getTotalSize(), true);
        }
        return size;
    }
    
    /**
     * Setter method for the job currently selected in the statistics page.
     * @parm value The currently selected job.
     */
    public void setSelectedJob(Job value) {
        selectedJob = value;
    }
    
    /**
     * Setter method for the job ID currently selected in the statistics page.
     * @parm value The currently selected job ID.
     */
    public void setSelectedJobID(String value) {
        selectedJobID = value;
    }
    
    public void rowSelectListener(SelectEvent se) {
        LOGGER.info("rowSelectListener() method called. toString => "
                + se.toString());
    }
}
