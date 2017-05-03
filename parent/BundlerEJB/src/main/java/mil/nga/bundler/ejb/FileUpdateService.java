package mil.nga.bundler.ejb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import mil.nga.bundler.interfaces.BundlerConstantsI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class FileUpdateService
 */
@Stateless
@LocalBean
public class FileUpdateService implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            FileUpdateService.class);
    
    /**
     * Container-injected persistence context.
     */
    @PersistenceContext(unitName=APPLICATION_PERSISTENCE_CONTEXT)
    private EntityManager em;
    
    /**
     * Default constructor. 
     */
    public FileUpdateService() { }

    
}
