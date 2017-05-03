package mil.nga;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.BundleRequest;
import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.JobFactoryService;
import mil.nga.bundler.ejb.JobRunnerService;
import mil.nga.bundler.ejb.JobTrackerService;
import mil.nga.bundler.ejb.RequestArchiveService;
import mil.nga.bundler.ejb.ValidationService;
import mil.nga.bundler.exceptions.InvalidRequestException;
import mil.nga.bundler.messages.JobTrackerMessage;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.model.ValidFile;
import mil.nga.util.FileUtils;

@Path("")
public class Bundler extends PropertyLoader {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            Bundler.class);
    /**
     * Common header names in which the client CN is inserted
     */
    public static final String[] CERT_HEADERS = {
        "X-SSL-Client-CN",
        "SSL_CLIENT_S_DN_CN",
        "SM_USER",
        "SM_USER_CN"
    };
    
    /**
     * The name of the application
     */
    public static final String APPLICATION_NAME = "Bundler";
    
    /**
     * URI information extracted from the servlet context
     */
    @Context
    private UriInfo uriInfo;
    
    /**
     * Inject the EJB used to parse the incoming request into individual 
     * jobs that will be processed by server-side EJBs.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JobFactoryService!mil.nga.bundler.ejb.JobFactoryService")
    private JobFactoryService jobFactoryService;
    
    /**
     * Inject the EJB used to parse the incoming request into individual 
     * jobs that will be processed by server-side EJBs.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JobRunnerService!mil.nga.bundler.ejb.JobRunnerService")
    private JobRunnerService jobRunnerService;
    
    /**
     * Inject the EJB used to parse the incoming request into individual 
     * jobs that will be processed by server-side EJBs.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JobTrackerService!mil.nga.bundler.ejb.JobTrackerService")
    private JobTrackerService jobTrackerService;
    
    /**
     * Inject the EJB used to parse the incoming request into individual 
     * jobs that will be processed by server-side EJBs.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/RequestArchiveService!mil.nga.bundler.ejb.RequestArchiveService")
    private RequestArchiveService requestArchiveService;
    
    /**
     * Inject the EJB used to validate the incoming BundleRequest object.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB(lookup="java:global/BundlerEAR/BundlerEJB/JobFactoryServiceBean!mil.nga.bundler.ejb.ValidationService")
    private ValidationService validationService;
    
    /**
     * Default constructor initializes the System configuration
     */
    public Bundler() { }
    
    
    /**
     * Construct a URI for use in redirecting clients to the bundler status 
     * page.
     * 
     * @param params Any command line parameters required by the bundler status
     * @return URI associated with the Bundler status page.
     */
    private URI getURI(Map<String, String> params) {
        
        URI uri = null;
        
        try {
            URI baseUri = this.uriInfo.getBaseUri();
            System.out.println("Old base URI: " + baseUri.toString());
            // 
            String base = baseUri.toString();
            base = base.replace("rest", "");
            URI newBase = new URI(base);
            System.out.println("New base URI: " + newBase.toString());
            StringBuilder sb = new StringBuilder();
            //sb.append(BUNDLER_STATUS_JSP);
            //String query = getString(params);
            //if ((query != null) && (!query.trim().equalsIgnoreCase(""))) {
            //    sb.append("?");
            //    sb.append(query);
            //}
            System.out.println("Query: " + sb.toString());
            uri = newBase.resolve(sb.toString());
            System.out.println("New URI: " + uri.toString());
        }
        catch (URISyntaxException use) {
            use.printStackTrace();
        }
        System.out.println(uri.toString());
        return uri;
        
    }

    /**
     * Helper method used to look up the JobFactoryService EJB.      
     * JBoss EAP 6.x does not support injection into the web tier.  This 
     * method was written to ensure the necessary EJB references are 
     * available.
     * 
     * @return The JobFactoryService EJB.
     * @throws NamingException Thrown if there is an error accessing the 
     * JNDI.
     */
    private JobFactoryService getJobFactoryService() 
            throws NamingException {
        
        String method = "getJobFactoryService() - ";
        
        if (this.jobFactoryService == null) {
            this.jobFactoryService = 
                    EJBClientUtilities.getInstance().getJobFactoryService();
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(method 
                        + "JobFactoryService already populated.");
            }
        }
        return this.jobFactoryService;
    }
    
    /**
     * Helper method used to look up the JobFactoryService EJB.      
     * JBoss EAP 6.x does not support injection into the web tier.  This 
     * method was written to ensure the necessary EJB references are 
     * available.
     * 
     * @return The JobFactoryService EJB.
     * @throws NamingException Thrown if there is an error accessing the 
     * JNDI.
     */
    private JobRunnerService getJobRunnerService() 
            throws NamingException {
        
        String method = "getJobRunnerService() - ";
        
        if (this.jobRunnerService == null) {
            this.jobRunnerService = 
                    EJBClientUtilities.getInstance().getJobRunnerService();
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(method 
                        + "JobFactoryService already populated by the container.");
            }
        }
        return this.jobRunnerService;
    }
    
    /**
     * Helper method used to look up the JobFactoryService EJB.      
     * JBoss EAP 6.x does not support injection into the web tier.  This 
     * method was written to ensure the necessary EJB references are 
     * available.
     * 
     * @return The JobFactoryService EJB.
     * @throws NamingException Thrown if there is an error accessing the 
     * JNDI.
     */
    private JobTrackerService getJobTrackerService() 
            throws NamingException {
        
        String method = "getJobTrackerService() - ";
        
        if (this.jobTrackerService == null) {
            this.jobTrackerService = 
                    EJBClientUtilities.getInstance().getJobTrackerService();
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(method 
                        + "JobTrackerService already populated by the container.");
            }
        }
        return this.jobTrackerService;
    }
    
    /**
     * Helper method used to look up the RequestArchiveService EJB.      
     * JBoss EAP 6.x does not support injection into the web tier.  This 
     * method was written to ensure the necessary EJB references are 
     * available.
     * 
     * @return The RequestArchiveService EJB.
     * @throws NamingException Thrown if there is an error accessing the 
     * JNDI.
     */
    private RequestArchiveService getRequestArchiveService() 
            throws NamingException {
        
        String method = "getRequestArchiveService() - ";
        
        if (this.requestArchiveService == null) {
            this.requestArchiveService = 
                    EJBClientUtilities.getInstance().getRequestArchiveService();
        }
        else {
            LOGGER.info(method 
                    + "RequestArchiveService populated.");
        }
        return this.requestArchiveService;
    }
    
    /**
     * Helper method used to look up the ValidationService EJB.      
     * JBoss EAP 6.x does not support injection into the web tier.  This 
     * method was written to ensure the necessary EJB references are available.
     * 
     * @return The ValidationService EJB.
     * @throws NamingException Thrown if there is an error accessing the 
     * JNDI.
     */
    private ValidationService getValidationService() 
            throws NamingException {
        
        String method = "getValidationService() - ";
        
        if (this.validationService == null) {
            this.validationService = 
                    EJBClientUtilities.getInstance().getValidationService();
        }
        else {
            LOGGER.info(method 
                    + "JobFactoryService populated.");
        }
        return this.validationService;
    }
    
    /**
     * Try a couple of different headers to see if we can get a user 
     * name for the incoming request.  About 50% of the time this function 
     * doesn't work because the AJAX callers do not insert the request
     * headers.
     * 
     * @param headers HTTP request headers
     * @return The username if it could be extracted from the headers
     */
    public String getUser(HttpHeaders headers) {
        
        String method = "getUser() - ";
        String user   = null;
        
        if (headers != null) {
            MultivaluedMap<String, String> map = headers.getRequestHeaders();
            for (String key : map.keySet()) {
                for (String header : CERT_HEADERS) {
                    if (header.equalsIgnoreCase(key)) {
                        user = map.get(key).get(0);
                        break;
                    }
                }
            }
        }
        else {
            LOGGER.warn(method 
                    + "HTTP request headers are not available.");
        }
        if ((user == null) || (user.isEmpty())) {
            user = "not available";
        }
        return user;
    }
    

    /**
     * Simple method used to determine whether or not the bundler 
     * application is responding to requests.
     */
    @GET
    @Path("/isAlive")
    public Response isAlive(@Context HttpHeaders headers) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("Application [ ");
        sb.append(APPLICATION_NAME);
        sb.append(" ] on host [ ");
        sb.append(FileUtils.getHostName());
        sb.append(" ] running in JVM [ ");
        sb.append(EJBClientUtilities.getInstance().getServerName());
        sb.append(" ] and called by user [ ");
        sb.append(getUser(headers));
        sb.append(" ] is alive!");
        
        return Response.status(Status.OK).entity(sb.toString()).build();
        
    }
    
    /**
     * Alternate version of the bundler entry point allowing clients to 
     * call the bundler with media type of text/plain.  
     * @param headers
     * @param request
     * @return
     */
    @Consumes(MediaType.TEXT_PLAIN)
    public Response bundleText(
            @Context HttpHeaders headers,
            String request) {
        
        return Response.status(Status.OK).build();
    }
    
    /**
     * 
     * @param request
     * @return
     */
    @POST
    @Path("/BundleFilesJSON")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response bundle(
            @Context HttpHeaders headers,
            BundleRequest request) {
        
        String              method      = "bundle() - ";
        JobTrackerMessage   message     = null;
        
        // Make sure the input request was parsed.
        if (request != null) {
            
            // If the client user name was not set in the request, attempt to 
            // extract it from the input request headers.
            if ((request.getUserName() == null) || 
                    (request.getUserName().isEmpty())) {
                request.setUserName(getUser(headers));
            }
            
            LOGGER.info(method 
                    + "Incoming request parsed [ "
                    + request.toString()
                    + " ].");
            
            try {
                
                // Validate the incoming request to ensure all aspects
                if (getValidationService() != null) { 
                    List<ValidFile> validatedFiles = getValidationService().validate(request);
                    
                
                    if (getJobFactoryService() != null) {
                        
                        Job job = getJobFactoryService().createJob(
                                    request,
                                    validatedFiles);
                    
                        // If enabled, save a copy of the client-supplied bundle 
                        // request. 
                        if (getRequestArchiveService() != null) {
                            getRequestArchiveService().archiveRequest(
                                    request, 
                                    job.getJobID());
                        }
                        else {
                            LOGGER.warn("Unable to obtain a reference to the "
                                    + "RequestArchiveService EJB.  Incoming "
                                    + "request object will not be archived.");
                        }
                        
                        if (job != null) {
                            if (getJobRunnerService() != null) {
                                getJobRunnerService().run(job);
                            }
                            else {
                                LOGGER.error("Unable to obtain a reference to the "
                                        + "JobRunnerService EJB.  Unable to start " 
                                        + "the job.");
                            }
                            
                            // Get the current state of the job to return to the
                            // client.
                            if (getJobTrackerService() != null) {
                                message = getJobTrackerService()
                                            .getJobTracker(job.getJobID());
                            }
                            else {
                                LOGGER.error("Unable to obtain a reference to the "
                                        + "JobTrackerService EJB.  Returned state associated "
                                        + "with job ID [ "
                                        + job.getJobID()
                                        + " ] will be null.");
                            }
                        }
                        
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Successfully created job [ " 
                                    + job.toString()
                                    + " ].");
                            LOGGER.debug("Job status returned to the client [ "
                                    + message.toString()
                                    + " ].");
                        }
                    }
                }
            }
            catch (InvalidRequestException ire) {
                LOGGER.error(method
                        + "Request validation failed with error code [ "
                        + ire.getErrorCode()
                        + " ], description [ "
                        + ire.getMessageText()
                        + "].");
                throw new WebArchiveException("Request validation failed with "
                        + "error code [ "
                        + ire.getErrorCode()
                        + " ], description [ "
                        + ire.getMessageText()
                        + "].");
            }
             catch (NamingException ne) {
                LOGGER.error(method 
                        + "An unexpected JNDI NamingException encountered "
                        + "while looking up EJBs.  Error message [ "
                        + ne.getMessage()
                        + " ].");
                return Response.serverError().build();
            }
        }
        else {
            LOGGER.error(method 
                    + "Invalid request received.  Input request object is "
                    + "null.");
            return Response.serverError().build();
        }
        
        // Return the state of the job to the caller.
        if (message != null) {
            return Response.ok(message, MediaType.APPLICATION_JSON).build();
        }
        else {
            LOGGER.error("Unable to create the JobTrackerMessage!");
            return Response.serverError().build();
        }
    }
            
    /**
     * Provide status information on the bundle operations associated with the
     * input job id.
     * 
     * @param jobID The ID of the job in question.
     * @return JSON representation of the job status, or error if not found.
     */
    @GET
    @Path("/GetState")
    @Produces(MediaType.APPLICATION_JSON)
    public JobTrackerMessage getState(
                    @QueryParam("job_id") String jobID) {

        JobTrackerMessage status = null;
        
        if ((jobID != null) && (!jobID.isEmpty())) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Retrieving state for job ID [ "
                        + jobID
                        + " ].");
            }
            try {
                if (getJobTrackerService() != null) {
                    status = getJobTrackerService().getJobTracker(jobID);
                }
                else {
                    LOGGER.error("Unable to obtain a reference to the "
                            + "JobTrackerService EJB.  Returned state associated "
                            + "with job ID [ "
                            + jobID
                            + " ] will be null.");
                }
            }
            catch (NamingException ne) {
                String msg = "An unexpected JNDI NamingException encountered "
                        + "while looking up EJBs.  Error message [ ";
                        LOGGER.error(msg);
                throw new WebArchiveException(msg);
            }
        }
        else {
            String msg =  "Null or empty job_id provided in request.";
            LOGGER.error(msg);
            throw new WebArchiveException(msg);
        }
        return status;
    }
    
    
    
    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void validate(BundleRequest request) {
        
        if (request != null) {
            List<String> candidates = request.getFiles();
            if ((candidates != null) && (candidates.size() > 0)) {
                
            }
        }
        Response.serverError().build();
    }
    
    
}

