package mil.nga.bundler;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.ValidFile;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.util.FileUtils;

/**
 * This class is responsible for breaking up a bundle request into individual 
 * archives that will be processed by the cluster. 
 * 
 * All of the files that can be bundled must be accessible by the server on 
 * which the application container is running. 
 * 
 * Updated to require a list of validated files as input rather than a raw 
 * list of Strings.
 * 
 * @author L. Craig Carpenter
 */
public class JobFactory implements BundlerConstantsI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			JobFactory.class);
	
	/**
	 * Temp pointer to an Archive used during Job creation.
	 */
	private Archive tempArchive = null;
	
	/**
	 * Handle to the job that we're creating. 
	 */
	private Job job = null;
	
	/**
	 * Archive size accumulator used during Job creation.
	 */
	private long archiveSizeAccumulator = 0;
	
	/**
	 * Index number of the current archive. 
	 */
	private int archiveNumber = -1;
	
	/**
	 * Number of files in the job.
	 */
	private int totalNumFilesAccumulator = 0;
	
	/**
	 * Total size accumulator used during Job creation.
	 */
	private long totalSizeAccumulator = 0;
	
	/**
	 * Suggested name for the target output archive file.
	 */
	private String  archiveFilenameTemplate  = null;
	
    /**
     * Default constructor.
     */
    public JobFactory() { }
	
    /**
	 * Add a file to the job.  Method has been deprecated in favor of new 
	 * method signature that requires a validated file as input.
	 * 
	 * @param file The validated file that will be added to the Job.
     */
    private void addFileToJob(ValidFile file) {
    	
    	if (tempArchive == null) {
			archiveNumber = 0;
			archiveSizeAccumulator = 0;
			totalSizeAccumulator = 0;
			tempArchive = new Archive(
					job.getJobID(),
					archiveNumber,
					job.getArchiveType());
			job.addArchive(tempArchive);
		}
		else if (getEstimatedArchiveSize(file.getSize()) 
				> job.getArchiveSize()) {
			archiveNumber++;
			tempArchive = new Archive(
					job.getJobID(),
					archiveNumber,
					job.getArchiveType());
			job.addArchive(tempArchive);
			archiveSizeAccumulator = 0;
		}
		
    	archiveSizeAccumulator += file.getSize();
		totalNumFilesAccumulator++;
		totalSizeAccumulator += file.getSize();
		tempArchive.add(
				FileEntryMapper.getInstance().getFileEntry(
						job.getJobID(),
						tempArchive.getArchiveID(),
						file));
    }
    
	/**
	 * Add a file to the job.  Method has been deprecated in favor of new 
	 * method signature that requires a validated file as input.
	 * 
	 * @param file The file that will be archived.
	 * @param size The size of the file to archive.
	 * @deprecated
	 */
	private void addFileToJob(File file, long size) {
		
		if (tempArchive == null) {
			archiveNumber = 0;
			archiveSizeAccumulator = 0;
			totalSizeAccumulator = 0;
			tempArchive = new Archive(
					job.getJobID(),
					archiveNumber,
					job.getArchiveType());
			job.addArchive(tempArchive);
		}
		else if (getEstimatedArchiveSize(size) > job.getArchiveSize()) {
			archiveNumber++;
			tempArchive = new Archive(
					job.getJobID(),
					archiveNumber,
					job.getArchiveType());
			job.addArchive(tempArchive);
			archiveSizeAccumulator = 0;
		}
		archiveSizeAccumulator += size;
		totalNumFilesAccumulator++;
		totalSizeAccumulator += size;
		tempArchive.add(
				new FileEntry(
						job.getJobID(),
						tempArchive.getArchiveID(),
						file.getAbsolutePath(), 
						size));
	}
	
	/** 
	 * This method performs some post-processing steps on the constructed
	 * job prior to persisting.
	 */
	private void complete() {
		
		int numArchives = 0;
		job.setNumFiles(totalNumFilesAccumulator);
		job.setTotalSize(totalSizeAccumulator);
		
		List<Archive> archives = job.getArchives();
		for (Archive archive : archives) {
			numArchives++;
			archive.setArchive(
					FileNameGenerator
						.getInstance()
						.createFilename(
								this.getArchiveFilenameTemplate(),
								archive.getArchiveID(), 
								archive.getArchiveType()
									.getText()
									.toLowerCase()));
			archive.setArchiveURL(
					UrlGenerator.getInstance().toURL(archive.getArchive()));
			archive.setHash(
					FileNameGenerator
						.getInstance()
						.createFilename(
								this.getArchiveFilenameTemplate(),
								archive.getArchiveID(), 
								HASH_FILE_EXTENSION));
			archive.setHashURL(
					UrlGenerator.getInstance().toURL(archive.getHash()));
			PathGenerator.getInstance().setPaths(archive);
			archive.complete();
		}
		job.setNumArchives(numArchives);
	}
	
	/**
	 * Method driving the generation of an archive "Job".  A job will consist 
	 * of one or more individual archives which contain one or more files.  
	 * Each archive will be sent into the cluster (via JMS) and processed on 
	 * one of the nodes in the cluster.
	 * 
	 * @param request Client-supplied bundle request.
	 * @param validatedFiles validated list of files to use in creating the 
	 * individual archives.
	 * @return A populated Job object.
	 */
	public Job createJob(
			BundleRequest request, 
			List<ValidFile> validatedFiles) {
		
		job = null;
		
		if (request != null) {
			if ((validatedFiles != null) && 
					(validatedFiles.size() > 0)) { 
				job = getNewJobInstance(
						request.getUserName(),
						request.getType(),
						request.getMaxSize(),
						request.getFilename());
				createArchives(validatedFiles);
			}
			else {
				LOGGER.error("Input BundleRequest did not contain any valid "
						+ "files to process.  Job object returned will be "
						+ "null.");
			}
		}
		else {
    		LOGGER.error("Input BundleRequest object is null.  Unable to "
    				+ "create a Job.  Job object returned will be null.");
    	}
		return job;
	}
    
    /**
	 * This method will take a list of files that were supplied by the
	 * end-user and divvy them up into individual archives for later
	 * processing.
	 * 
	 * @param files A list of files to be bundled.
	 */
    private void createArchives(List<ValidFile> files) {
    	if ((files != null) && (files.size() > 0)) {
    		
    		LOGGER.debug("Processing [ " 
    				+ files.size() 
    				+ " ] files for job ID [ "
    				+ job.getJobID()
    				+ " ].");
    	
    		for (ValidFile file : files) {
    			addFileToJob(file);
    		}
    		complete();
    	}
    	else {
    		LOGGER.error("Input ValidFile list is null or contains no entries."
    				+ "  This is an error condition.  No archives will be "
    				+ "created.");
    	}
    }
    
    /**
     * Getter method for the template name of the output archive
     * file.
     * @return The template for the value of the output 
     * archive.
     */
    private String getArchiveFilenameTemplate() {
    	return archiveFilenameTemplate;
    }
    
	/**
     * Ensure that the requested archive size falls within some allowable 
     * parameters.  
     * 
     * This method has been ammended to ensure that someone doesn't request an 
     * archive size that is too small.  We ran into issues testing with the 
     * FalconView "Easy Button" software where users would request an 
     * obscenely large number of files with a size of 1M creating too many
     * threads for the system to handle.
     * 
     * @param size The suggested size as provided by the external clients.
     * @return Actual target size of individual archives.
     */
    public static long getArchiveSize(long size) {
        if (size < MIN_ARCHIVE_SIZE) {
        	LOGGER.info("User requested archive size smaller than " 
        			+ "what we currently allow [ "
        			+ size
        			+ " MB ].  It's being adjusted to [ "
        			+ MIN_ARCHIVE_SIZE
        			+ " MB ].");
        }
        else if(size > MAX_ARCHIVE_SIZE) {
        	LOGGER.info("User requested archive size greater than " 
        			+ "what we currently allow [ "
        			+ size
        			+ " MB ].  It's being adjusted to [ "
        			+ MAX_ARCHIVE_SIZE
        			+ " MB ].");
            size = MAX_ARCHIVE_SIZE;
        }
        return size*(1024L * 1024L);
    }
    
    /**
     * Calculate an estimate of the size of the output archive file.
     * 
     * @param size The size of a candidate input file.
     * @return The estimated size of the output archive with a file of 
     * the input size added.
     */
    private long getEstimatedArchiveSize(long size) { 
    	double multiplier = (100.0 - AVERAGE_COMPRESSION_PERCENTAGE) / 100.0;
    	double estimatedSize = multiplier * (double)(archiveSizeAccumulator + size);
    	return (long)estimatedSize;
    }
    
    /**
     * Private method used to create a new Job.  Logic needed to populate some of the 
     * Job attributes is contained here.
     * 
     * @param userName The user submitting the job.
     * @param type The type of archive to produce.
     * @param size The suggested size of the output archive.
     * @param archiveFilename The suggested name for the output archive.
     * @return A new job instance.
     */
    private Job getNewJobInstance (
    		String userName,
			String type,
			long   archiveSize,
			String archiveFilename) {
	
    	Job job = new Job();
    	job.setJobID(getNewID());
    	job.setArchiveSize(getArchiveSize(archiveSize));
    	setArchiveFilenameTemplate(
    			FileNameGenerator
    			.getInstance()
    			.getArchiveFile(archiveFilename));
    
    	if ((userName == null) || (userName.isEmpty())) {
    		LOGGER.info("Name of user submitting request is unavailable.");
    		userName = DEFAULT_USERNAME;
    	}
    	job.setUserName(userName);
    	
        try {
            job.setArchiveType(ArchiveType.fromString(type));
        }
        catch (UnknownArchiveTypeException uae) {
            LOGGER.warn("Client requested an unknown/unsupported archive type [ "
                    + type
                    + " ].  Using default value of [ "
                    + ArchiveType.ZIP.getText()
                    + " ].");
            job.setArchiveType(ArchiveType.ZIP);
        }
        return job;
    }
    
    /**
     * Generate a unique ID assigned to archive jobs used to track completion.
     * 
     * @return A job ID (length defined by JOB_ID_LENGTH).
     */
    public static String getNewID() {
    	return FileUtils.generateUniqueToken(JOB_ID_LENGTH);
    }

    /**
     * Setter method for the template name of the output archive
     * file.
     * @param value The template for the value of the output 
     * archive.
     */
    private void setArchiveFilenameTemplate(String value) {
    	archiveFilenameTemplate = value;
    }
}
