package mil.nga.bundler.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import mil.nga.PropertyLoader;
import mil.nga.bundler.BundleRequest;
import mil.nga.bundler.JobFactory;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.model.ValidFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JobFactoryService
 */
@Stateless
@LocalBean
public class JobFactoryService 
		extends PropertyLoader implements BundlerConstantsI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			JobFactoryService.class);
	
	/**
	 * Container-injected reference to the JobService EJB.
	 */
	@EJB
	JobService jobService;
	
    /**
     * Default constructor.
     */
    public JobFactoryService() { 
    	super(PROPERTY_FILE_NAME);
    }
    
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JobService EJB.
     */
    private JobService getJobService() {
    	if (jobService == null) {
    		LOGGER.warn("Application container failed to inject the "
    				+ "reference to JobService.  Attempting to "
    				+ "look it up via JNDI.");
    		jobService = EJBClientUtilities
    				.getInstance()
    				.getJobService();
    	}
    	return jobService;
    }
    
    

    
    public Job createJob(
    		BundleRequest request, 
    		List<ValidFile> validatedFiles) {
    	
    	Job job = null;
    	
    	if (request != null) {
	    	JobFactory factory = new JobFactory();
	    	
	    	job = factory.createJob(request, validatedFiles);
	    	LOGGER.info(job.toString());
    	
	    	if (getJobService() != null) {
	    		jobService.persist(job);
	    	}
    	}
    	else {
    		LOGGER.error("Input BundleRequest object is null.  Unable to "
    				+ "create a Job.  Job object returned will be null.");
    	}
    	return job;
    	
    }
    
    public void validate(BundleRequest request) {
    	LOGGER.info("validate() called.");
    }
    


}
