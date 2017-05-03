package mil.nga.bundler.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.Job;
import mil.nga.bundler.types.JobStateType;

/**
 * Session Bean implementation class JobService
 */
@Stateless
@LocalBean
public class JobService 
        implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JobService.class);

    /**
     * Container-injected persistence context.
     */
    @PersistenceContext(unitName=APPLICATION_PERSISTENCE_CONTEXT)
    private EntityManager em;
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public JobService() { }
    
    /**
     * Alternate constructor allowing the EntityManager to be injected.
     * @param em Object implementing the EntityManager interface.
     */
    public JobService(EntityManager em) {
        this.em = em;
    }


    /**
     * Get a list of Jobs that have not yet completed.
     * 
     * @return A list of jobs in a state other than "COMPLETE".
     */
    public List<Job> getIncompleteJobs() {
        List<Job> jobs = null;
        
        try {
            
            if (this.em != null) {

                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Job> cq =
                                cb.createQuery(Job.class);
                Root<Job> rootEntry = cq.from(Job.class);
                CriteriaQuery<Job> all = cq.select(rootEntry);
                cq.where(cb.notEqual(rootEntry.get("state"), JobStateType.COMPLETE));
                cq.orderBy(cb.desc(rootEntry.get("startTime")));
                TypedQuery<Job> allQuery = em.createQuery(all);
                jobs = allQuery.getResultList();

            }
            else {
                LOGGER.warn("EntityManager object not populated by the "
                        + "container.  A null object will be returned to the "
                        + "caller.");
            }
        }
        catch (NoResultException nre) {
                LOGGER.info("javax.persistence.NoResultException "
                        + "encountered.  Error message [ "
                        + nre.getMessage()
                        + " ].  Returned List<Job> object will be null.");
        }
        
        return jobs;
    }
    
    /**
     * Retrieve a Job object from the target database.
     * 
     * @param jobID The job ID (primary key) of the job to retrieve.
     * @return The target Job object.  Null if the Job could not be found.
     */
    public Job getJob(String jobID) {
        
        Job job = null;
        
        if (this.em != null) {
            if ((jobID != null) && (!jobID.isEmpty())) {
                
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Job> cq = cb.createQuery(Job.class);
                Root<Job> root = cq.from(Job.class);
                // Add the "where" clause
                cq.where(
                        cb.equal(
                                root.get("jobID"), 
                                cb.parameter(String.class, "jobID")));
                // Create the query
                Query query = em.createQuery(cq);
                // Set the value for the where clause
                query.setParameter("jobID", jobID);
                // Retrieve the data
                job = (Job)query.getSingleResult();
                
            }
            else {
                LOGGER.warn("The input job ID is null or empty.  Unable to "
                        + "retrieve an associated job.");
            }
        }
        else {
            LOGGER.error("The container failed to inject the target Entity "
                    + "Manager.  Unable to retrieve job with job ID [ "
                    + jobID
                    + " ].");
        }
        return job;
    }
    
    /**
     * Get a list of all jobIDs currently residing in the target data store.
     * 
     * @return A list of jobIDs
     */
    @SuppressWarnings("unchecked")
    public List<String> getJobIDs() {
        
        List<String> jobIDs = null;

        if (this.em != null) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<Job> cq =
                                cb.createQuery(Job.class);

                Root<Job> e = cq.from(Job.class);
                cq.multiselect(e.get("jobID"));
                Query query = em.createQuery(cq);
                jobIDs = query.getResultList();

        }
        else {
            LOGGER.error("The container failed to inject the target Entity "
                    + "Manager.  Unable to retrieve the list of jobs.");
        }
        return jobIDs;
    }
    
    /**
     * Return a list of all Job objects in the target data store.
     * @return All existing Job objects.
     */
    public List<Job> getJobs() {
        
        List<Job> jobs = null;
        
        if (this.em != null) {
                
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Job> cq = cb.createQuery(Job.class);
            Root<Job> root = cq.from(Job.class);
            // CriteriaQuery<Job> all = cq.select(root);
            
            // Add the "order by" clause sorting by time
            cq.orderBy(cb.desc(root.get("startTime"))); 
            
            // Create the query
            TypedQuery<Job> query = em.createQuery(cq);
            
            // Retrieve the data
            jobs = query.getResultList();
                
        }
        else {
            LOGGER.error("The container failed to inject the target Entity "
                    + "Manager.  Unable to retrieve the list of jobs.");
        }
        return jobs;
    }
    
    /**
     * This method will retrieve Job objects from the database that 
     * have a start_time (startTime) that fall between the input startTime and
     * endTime parameters.  The time data stored in the database are not dates, 
     * but long values.  As such, the two time parameters should be formatted as 
     * long time values (i.e. milliseconds from epoch).  
     * 
     * @param startTime Earliest time in the time slice to query.
     * @param endTime Latest time in the time slice to query.
     * @return A list of jobs in with a start time that fall between the two 
     * input dates.
     */

    public List<Job> getJobsByDate(long startTime, long endTime) {
        
        List<Job> jobs = null;
        
        // Ensure the startTime is earlier than the endTime before submitting
        // the query to the database.
        if (startTime > endTime) {
                LOGGER.warn("The caller supplied a start time that falls "
                        + "after the end time.  Swapping start and end "
                        + "times.");
                long temp = startTime;
                startTime = endTime;
                endTime = temp;
        }
        else if (startTime == endTime) {
            LOGGER.warn("The caller supplied the same time for both start "
                    + "and end time.  This method will likely yield a null "
                    + "job list.");
        }
        
        try {
            if (this.em != null) {
                 CriteriaBuilder cb = em.getCriteriaBuilder();
                 CriteriaQuery<Job> cq =
                                 cb.createQuery(Job.class);
                 Root<Job> rootEntry = cq.from(Job.class);
                 CriteriaQuery<Job> all = cq.select(rootEntry);
    
                 Path<Long> pathToStartTime = rootEntry.get("startTime");
                 cq.where(cb.between(pathToStartTime, startTime, endTime));
    
                 cq.orderBy(cb.desc(pathToStartTime));
                 TypedQuery<Job> allQuery = em.createQuery(all);
                 jobs = allQuery.getResultList();            
            }
            else {
                LOGGER.error("The EntityManager is null.  Unable to select a " 
                        + "list of Jobs by date.");
            }
        }
        catch (NoResultException nre) {
             LOGGER.warn("javax.persistence.NoResultException "
                     + "encountered.  Error message [ "
                     + nre.getMessage()
                     + " ].");    
        }
        return jobs;
    }
    
    /**
     * Update the data in the back end database with the current contents 
     * of the Job.
     * 
     * @param job The Job object to update.
     * @return The container managed Job object.
     */
    public Job update(Job job) {
        Job managedJob = null;
        if (em != null) {
            if (job != null) {
                //em.getTransaction().begin();
                managedJob = em.merge(job);
                //em.getTransaction().commit();
                em.flush();
            }
            else {
                LOGGER.warn("Called with a null or empty Job object.  "
                        + "Object will not be persisted.");
            }
        }
        else {
            LOGGER.error("The EntityManager is null.  Unable to persist the "
                    + "input Job object.");
        }
        return managedJob;
    }

    /**
     * Persist the input Job object into the back-end data store.
     * 
     * @param job The Job object to persist.
     */
    public void persist(Job job) {
        if (em != null) {
            if (job != null) {
                //em.getTransaction().begin();
                em.persist(job);
                //em.getTransaction().commit();
                em.flush();
            }
            else {
                LOGGER.warn("Called with a null or empty Job object.  "
                        + "Object will not be persisted.");
            }
        }
        else {
            LOGGER.error("The EntityManager is null.  Unable to persist the "
                    + "input object.");
        }
    }
}
