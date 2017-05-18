package mil.nga.bundler.statistics;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.ejb.MetricsService;
import mil.nga.bundler.model.BundlerMetrics;

/**
 * Due to the amount of time it takes to retrieve all of the records from the
 * back-end data store, the overall metrics are calculated every 30 minutes 
 * by an Timer bean.
 * 
 * @author L. Craig Carpenter
 */
@ManagedBean
@ViewScoped
public class ViewOverallJobMetrics 
        extends ViewJobMetrics implements Serializable {

    /**
     * Eclipse-generated serialVersionUID
     */
    private static final long serialVersionUID = 6010783594993431230L;

    /**
     * Inject the EJB used to look up the job tracker information.
     * 
     * Note:  JBoss EAP 6.x does not support injection into the application
     * web tier.  When deployed to JBoss EAP 6.x this internal member 
     * variable will always be null.
     */
    @EJB
    MetricsService jobMetrics = null;
    
    /**
     * Static logger for use throughout the class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(
            ViewOverallJobMetrics.class);
    
    /**
     * Handle to the object containing the calculated job metrics.  This 
     * object is populated by the <code>initialize()</code> method.
     */
    private BundlerMetrics metrics = null;
    
    /**
     * The initialize method is called immediately after the Bean is 
     * instantiated.  It's responsibility is to retrieve the metrics data that
     * will be displayed.   In this case, it simply retrieves the 
     * pre-calculated metrics data from the data store.
     */
    @PostConstruct
    public void initialize() {
        if (getMetricsService() != null) {
            metrics = getMetricsService().getMetrics();
        }
        else {
            LOGGER.error("Unable to obtain a reference to the "
                    + "JobMetricsService.");
        }
    }
     
    /**
     * Private method used to obtain a reference to the target EJB.  
     * 
     * @return Reference to the JobService EJB.
     */
    protected MetricsService getMetricsService() {
        if (jobMetrics == null) {
            LOGGER.warn("Application container failed to inject the "
                    + "reference to MetricsService.  Attempting to "
                    + "look it up via JNDI.");
            jobMetrics = EJBClientUtilities
                    .getInstance()
                    .getMetricsService();
        }
        return jobMetrics;
    }
    
    /**
     * Getter method allowing access to the private internal bundler metrics
     * data.
     * @return The populated metrics data.
     */
    public BundlerMetrics getMetrics() {
        return metrics;
    }
}
