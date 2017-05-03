package mil.nga.bundler.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.model.BundlerMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class MetricsService
 */
@Stateless
@LocalBean
public class MetricsService implements BundlerConstantsI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			MetricsService.class);

	/**
	 * Container-injected persistence context.
	 */
	@PersistenceContext(unitName=APPLICATION_PERSISTENCE_CONTEXT)
    private EntityManager em;
    
    /**
     * Default Eclipse-generated constructor. 
     */
    public MetricsService() { }
    
    /**
     * Alternate constructor allowing the EntityManager to be injected.
     * @param em Object implementing the EntityManager interface.
     */
    public MetricsService(EntityManager em) {
        this.em = em;
    }


    public BundlerMetrics getMetrics() {
    	
    	BundlerMetrics metrics = null;
    	
    	try {
	    	if (this.em != null) {
	    			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<BundlerMetrics> cq = cb.createQuery(BundlerMetrics.class);
				Root<BundlerMetrics> root = cq.from(BundlerMetrics.class);
				cq.select(root);
				Query query = em.createQuery(cq);
				metrics = (BundlerMetrics)query.getSingleResult();
				
	    	}
	    	else {
	    		LOGGER.error("The container injected EntityManager object is "
	    				+ "null.  Unable to retrieve the BundlerMetrics "
	    				+ "object from the data store.");
	    	}
    	}
        catch (NoResultException nre) {
	       	 LOGGER.warn("Unable to retrieve BundlerMetrics object from target "
	       			 + "data store.  javax.persistence.NoResultException "
	                 + "encountered.  Error message [ "
	                 + nre.getMessage()
	                 + " ].");	
        }
    	
    	return metrics;
    	
    }
    
    
    public BundlerMetrics update(BundlerMetrics metrics) {
    	
    	BundlerMetrics managedMetrics = null;
        
    	if (em != null) {
            if (metrics != null) {
            	
            	managedMetrics = em.merge(metrics);
                em.flush();
                
            }
            else {
            	LOGGER.warn("Called with a null or empty BundlerMetrics "
            			+ "object.  Object will not be persisted.");
            }
        }
        else {
    		LOGGER.error("The container injected EntityManager object is "
    				+ "null.  Unable to persist the BundlerMetrics "
    				+ "object from the data store.");
        }
        return managedMetrics;
    }
}
