package mil.nga.bundler.interfaces;

/**
 * Interface containing constants used throughout the bundler application.
 * 
 * @author L. Craig Carpenter
 */
public interface BundlerConstantsI {
    
    /**
     * Using statistics collected from the first 5000 jobs run through 
     * the bundler we found that we only achieved an average of 8.4%
     * reduction in size.  This is due to the fact that most of the 
     * files requested are binary and many are already compressed.
     */
    public static final double AVERAGE_COMPRESSION_PERCENTAGE = 8.4;
    
    /**
     * The length (in Base64 characters) of the job ID.  The job ID is used 
     * as the primary key value for archive jobs.
     */
    public static final int JOB_ID_LENGTH = 14;
    
    /**
     * The name of the JPA persistence context used throughout the application.
     */
    public static final String APPLICATION_PERSISTENCE_CONTEXT = "BundlerJPA";
    
    /**
     * Property defining the "base" URL for the output archives (i.e. 
     * the base staging directory will be replaced with this URL allowing
     * HTTP/HTTPS access to the output archives).
     */
    public static final String BASE_URL_PROPERTY = "bundler.base_url";
    
    /**
     * The name of the JMS Connection factory.
     */
    public static final String CONNECTION_FACTORY = "/ConnectionFactory";
    
    /**
     * Username to utilize if it cannot be determined.
     */
    public static final String DEFAULT_USERNAME = "unavailable";
    
    /**
     * Prefix used in both the name of the staging directory and the 
     * archive file.
     */
    public static final String DEFAULT_FILENAME_PREFIX = "nga";
    
    /**
     * Default maximum size for the archive if it wasn't supplied by the 
     * caller (in MB).
     */
    public static final int DEFAULT_MAX_ARCHIVE_SIZE = 400;
    
    /**
     * Extension for the generated hash files.
     */
    public static final String HASH_FILE_EXTENSION = "sha1";
    
    /**
     * Default maximum size for the archive if it wasn't supplied by the 
     * caller (in MB).
     */
    public static final int MAX_ARCHIVE_SIZE = 1024;
    
    /**
     * Part of the property name that will contain path prefix exclusions to 
     * apply.
     */
    public static final String PARTIAL_PROP_NAME = 
            "bundler.exclude_path_prefix_";
    
    /**
     * Rather arbitrary maximum number of prefix exclusions allowed.
     */
    public static final int MAX_NUM_EXCLUSIONS = 100;
    
    /**
     * The number of milliseconds in a 30 day period.
     */
    public static long MILLISECONDS_PER_30_DAYS = 1000 * 60 * 60 * 24 * 30;
    
    /**
     * Default minimum size for the archive if it wasn't supplied by the 
     * caller (in MB).
     */
    public static final int MIN_ARCHIVE_SIZE = 20;
    
    /**
     * The maximum number of archives allowed for a single job.
     */
    public static final long MAX_NUM_ARCHIVES = 150;
    
    /**
     * The name of the properties file to load.
     */
    public static final String PROPERTY_FILE_NAME = "/mnt/eng2/gateway/bundler/bundler.properties";
    
    /**
     * If this property is set, the bundler will serialize the input bundle 
     * requests to disk.  This feature was implemented to support debugging.
     */
    public static final String BUNDLE_REQUEST_OUTPUT_LOCATION_PROP = 
            "bundler.request_output_location";
    
    /**
     * System property identifying the target staging directory.  
     */
    public static final String STAGING_DIRECTORY_PROPERTY = 
            "bundler.staging_directory";
    
    /**
     * Property defining the "base" staging directory (i.e. the portion
     * of the staging directory that will be replaced with a URL).  This
     * property should exist in the default system properties file.
     */
    public static final String STAGING_DIRECTORY_BASE_PROPERTY = 
            "bundler.staging_directory_base";
    
    /**
     * Number of Base64 characters to use in creating unique tokens for output
     * directories and/or filenames.
     */
    public static final int UNIQUE_TOKEN_LENGTH = 4;
 
    /**
     * String to use to output dates in String format for logging purposes.
     */
    public static final String UNIVERSAL_DATE_STRING = "yyyy/MM/dd HH:mm:ss:SSS";
    
    /**
     * The name of the destination queue on which Archiver jobs will be 
     * placed.
     */
    //public static final String ARCHIVER_DEST_Q = "queue/ArchiverMessageQ";
    public static final String ARCHIVER_DEST_Q = "queue/ArchiverMessageQ_TEST";
    
    /**
     * The name of the destination queue on which completed archive jobs 
     * will be placed.
     */
    //public static final String TRACKER_DEST_Q = "queue/TrackerMessageQ";
    public static final String TRACKER_DEST_Q = "queue/TrackerMessageQ_TEST";
    
}
