package mil.nga.bundler.view;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.model.Job;
import mil.nga.util.FileUtils;


@ManagedBean
@ViewScoped
public class ViewBundlerJobs 
        extends BundlerEJBClient implements Serializable {
    
    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 7635868242297918883L;

    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = 
            LoggerFactory.getLogger(ViewBundlerJobs.class);
    
    /**
     * List of jobs to display in the 
     */
    private List<Job> jobList;
    
    /**
     * The job ID selected in the statistics page
     */
    private Job currentlySelectedJob;
    
    /**
     * How far to go back in time looking for jobs to display.
     */
    private static final int DAYS_IN_PAST = 14;
    
    /**
     * This method serves as the constructor which will create and populate 
     * the internal lists that are displayed in the data tables.  Population
     * of the lists was moved to the constructor so that when the View 
     * commandButton is pressed the lists are not re-loaded from the back-end
     * data store.
     */
    @PostConstruct
    public void initialize() {
        jobList = super.getJobs(DAYS_IN_PAST);
        if ((jobList == null) || (jobList.size() == 0)) {
            LOGGER.error("Unable to find any jobs submitted in the "
                    + "last 2 weeks.");
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
     * Get the pre-populated Job list.
     * @return The list of Jobs.
     */
    public List<Job> getJobs() {
        return jobList;
    }
    
    /**
     * Getter method for the job currently selected in the job status page.
     * @return The currently selected job.
     */
    public Job getSelectedJob() {
        return currentlySelectedJob;
    }
    
    //public Job getMaterializedJob() {
    //    return super.getMaterializedJob(currentlySelectedJob.getJobID());
    //}
    
    /**
     * Getter method for the human readable start time associated with the
     * job.
     * @param job The current job.
     * @return The start time for the current job.
     */
    public String getStartTime(Job job) {
        return super.toDateString(job.getStartTime());
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
     * Setter method for the job currently selected in the job status page.
     * @param value The currently selected job.
     */
    public void setSelectedJob(Job value) {
        if (value != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Job selected [ "
                        + value.getJobID()
                        + " ].");
            }
        }
        else {
            LOGGER.info("Selected job is null.");
        }
        currentlySelectedJob = value;
    }
}
