package mil.nga.bundler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.model.BundlerMetrics;
import mil.nga.bundler.types.JobStateType;

/**
 * Common class used in conjunction with the statistics calculation algorithms.
 * Based on an input list of Job objects this class will calculate various 
 * metrics.
 * 
 * @author L. Craig Carpenter
 */
public class MetricsCalculator {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            MetricsCalculator.class);
    
    /**
     * Default constructor.
     */
    public MetricsCalculator() { }
    
    /**
     * In order to get the total compressed size of a job we have to loop 
     * through the individual archives contained in a job and obtain the compressed
     * size of the output archive file.
     * @param list A list of Archives contained in the job.
     */
    private long getCompressedSize(List<Archive> list) {
        long accumulator = 0;
        if ((list != null) && (list.size() > 0)) {
            for (Archive archive : list) {
                if (archive.getArchiveState() == JobStateType.COMPLETE) {
                    accumulator += archive.getSize();
                }
            }
        }
        return accumulator;
    }
    
    /**
     * Calculate the compression ratio associated with the job.
     * 
     * @param size Total size of the job.
     * @param compressedSize The total compressed size of the individual job 
     * archives.
     * @return The percentage that the input data was compressed.
     */
    private double getCompressionRatio(long size, long compressedSize) {
         double ratio          = 0.0;
         if ((compressedSize == 0) || (size == 0)) {
             ratio = 0.0;
         }
         else if (compressedSize < size) {
             ratio = ((double)(size - compressedSize) /
                                 (double)size);
         }
         return ratio;
    }
    
    /**
     * Calculate the total amount of time spent processing the input job.
     * 
     * @param job The target Job.
     * @return The elapsed time spent processing the job.
     */
    private long getElapsedTime(Job job) {
        long elapsedTime = 0L;
        if (job != null) {
            if (job.getState() == JobStateType.COMPLETE) {
                if (job.getEndTime() > 0) {
                    if (job.getEndTime() > job.getStartTime()) {
                        elapsedTime = job.getEndTime() - job.getStartTime();
                    }
                    else {
                        LOGGER.warn("Job ID [ "
                                + job.getJobID()
                                + " ] has a start time of [ "
                                + job.getStartTime()
                                + " ] which is greater than the end time of [ "
                                + job.getEndTime()
                                + " ].  This is an invalid combination.");
                    }
                }
                else {
                    LOGGER.warn("Job ID [ "
                            + job.getJobID()
                            + " ] has a state of [ "
                            + job.getState()
                            + " ] but an end time of [ "
                            + job.getEndTime()
                            + " ].  This is an invalid combination.");
                }
            }
        }
        else {
            LOGGER.error("The input Job object is null.");
        }
        return elapsedTime;
    }
    
    public void getMetrics(BundlerMetrics metrics, List<Job> list) {
        
        if (metrics == null) {
            metrics = new BundlerMetrics();
        }
        metrics.reset();
        double compressionRatioAccumulator = 0.0;
        long   elapsedTimeAccumulator      = 0L;
        long   numArchivesAccumulator      = 0L;
        long   endTime                     = 0L;
        long   startTime                   = System.currentTimeMillis();
        
        if ((list != null) && (list.size() > 0)) {
            
            metrics.setTotalNumJobs(list.size());
            
            for (Job job : list) {
                
                if ((job.getStartTime() < startTime) && (job.getStartTime() != 0)) {
                    startTime = job.getStartTime();
                }
                if (job.getEndTime() > endTime) {
                    endTime = job.getEndTime();
                }
                
                if (job.getState() != JobStateType.COMPLETE) {
                    metrics.setNumJobsIncomplete(
                            metrics.getNumJobsIncomplete() + 1);
                }
                
                if (job.getArchives() != null) {
                    numArchivesAccumulator += job.getArchives().size();
                }
                
                metrics.setTotalNumFiles(
                        metrics.getTotalNumFiles()+job.getNumFiles());
                metrics.setTotalNumArchives(
                        metrics.getTotalNumArchives()+job.getNumArchives());
                metrics.setTotalSize(
                        metrics.getTotalSize()+job.getTotalSize());
                
                elapsedTimeAccumulator += getElapsedTime(job);
                
                long compressedSize = getCompressedSize(job.getArchives());
                compressionRatioAccumulator += 
                        getCompressionRatio(job.getTotalSize(), compressedSize); 
                
                metrics.setTotalCompressedSize(
                        metrics.getTotalCompressedSize() + compressedSize);
                
            }
            
            metrics.setAverageCompression(
                    compressionRatioAccumulator / (double)list.size());
            metrics.setAbsoluteCompression(
                    getCompressionRatio(
                            metrics.getTotalSize(), 
                            metrics.getTotalCompressedSize()));
            metrics.setAverageSizePerJob(
                    metrics.getTotalSize() / list.size());
            metrics.setAverageNumFilesPerJob(
                    metrics.getTotalNumFiles() / list.size());
            metrics.setAverageTimePerJob(
                    elapsedTimeAccumulator / list.size());
            metrics.setAverageNumArchivesPerJob(
                    numArchivesAccumulator / list.size());
            metrics.setStartTime(startTime);
            metrics.setEndTime(endTime);
            
        }
        else {
            LOGGER.error("The job list retrieved from the data source " 
                    + "is null or contains no elements.  Unable to "
                    + "calculate metrics.");
        }
    }

}
