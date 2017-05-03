package mil.nga.bundler.ejb.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.bundler.model.HostMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JDBCHostMetricsService
 * 
 * This class provides methods that interface with the HOST_METRICS table 
 * used to keep track of metrics on a per-host basis.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class JDBCHostMetricsService {

    /**
     * Set up the logging system for use throughout the class
     */        
    private static final Logger LOGGER = LoggerFactory.getLogger(
            JDBCHostMetricsService.class);
    
    /**
     * Container-injected datasource object.
     */
    @Resource(mappedName="java:jboss/datasources/JobTracker")
    DataSource datasource;
    
    /**
     * Eclipse-generated default constructor. 
     */
    public JDBCHostMetricsService() { }

    /**
     * Delete metrics associated with the input host name.
     * @param hostName The host name to delete.
     */
    public void deleteHostMetrics(String hostName) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "delete from HOST_METRICS where "
                + "HOST_NAME = ?";
        
        if (datasource != null) {
            if ((hostName != null) && (!hostName.isEmpty())) {
                    
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, hostName);
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised "
                            + "while attempting to delete HOST_METRICS "
                            + "records associated with host name[ "
                            + hostName
                            + " ].  Error message [ "
                            + se.getMessage() 
                            + " ].");
                }
                finally {
                    try { 
                        if (stmt != null) { stmt.close(); } 
                    } catch (Exception e) {} 
                    try { 
                        if (conn != null) { conn.close(); } 
                    } catch (Exception e) {}
                }
            }
            else {
                LOGGER.warn("The input host name is null or empty.  Unable to "
                        + "delete the target record.");
            }
            
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("HOST_METRICS record deleted in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }    
    }
    
    /**
     * Retrieve all of the host metrics from the data store.
     * @return The list of host metrics.
     */
    public List<HostMetrics> getHostMetrics() {
        
        List<HostMetrics> metrics = new ArrayList<HostMetrics>();
        Connection        conn    = null;
        PreparedStatement stmt    = null;
        ResultSet         rs      = null;
        long              start   = System.currentTimeMillis();
        String            sql     = "select COMPLETED_ARCHIVES, "
                + "END_TIME, ERROR_ARCHIVES, HOST_NAME, ID, "
                + "INVALID_ARCHIVES, START_TIME, TOTAL_COMPRESSED_SIZE, "
                + "TOTAL_ELAPSED_TIME, TOTAL_NUM_ARCHIVES, TOTAL_NUM_FILES, "
                + "TOTAL_SIZE from HOST_METRICS order by HOST_NAME";
        
        if (datasource != null) {
            
            try { 
                
                conn = datasource.getConnection();
                stmt = conn.prepareStatement(sql);
                rs   = stmt.executeQuery();
                
                while (rs.next()) {
                    
                    HostMetrics row = new HostMetrics();
                    row.setCompletedArchives(rs.getLong("COMPLETED_ARCHIVES"));
                    row.setEndTime(rs.getLong("END_TIME"));
                    row.setErrorArchives(rs.getLong("ERROR_ARCHIVES"));
                    row.setHostName(rs.getString("HOST_NAME"));
                    row.setID(rs.getLong("ID"));
                    row.setInvalidArchives(rs.getLong("INVALID_ARCHIVES"));
                    row.setStartTime(rs.getLong("START_TIME"));
                    row.setTotalCompressedSize(rs.getLong("TOTAL_COMPRESSED_SIZE"));
                    row.setTotalElapsedTime(rs.getLong("TOTAL_ELAPSED_TIME"));
                    row.setTotalNumArchives(rs.getLong("TOTAL_NUM_ARCHIVES"));
                    row.setTotalNumFiles(rs.getLong("TOTAL_NUM_FILES"));
                    row.setTotalSize(rs.getLong("TOTAL_SIZE"));
                    metrics.add(row);
                    
                }
            }
            catch (SQLException se) {
                LOGGER.error("An unexpected SQLException was raised while "
                        + "attempting to retrieve a list of HOST_METRICS "
                        + "objects from the target data source.  Error "
                        + "message [ "
                        + se.getMessage() 
                        + " ].");
            }
            finally {
                try { 
                    if (rs != null) { rs.close(); }
                } catch (Exception e) {}
                try { 
                    if (stmt != null) { stmt.close(); } 
                } catch (Exception e) {}
                try { 
                    if (conn != null) { conn.close(); } 
                } catch (Exception e) {}
            }
                
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[ "
                    + metrics.size()
                    + " ] host metrics records selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        
        return metrics;
    }
    
    /**
     * Retrieve the host metrics from the data store.
     * @param hostName The host name to retrieve.
     */
    public HostMetrics getHostMetrics(String hostName) {
        
        HostMetrics       metrics  = null;
        Connection        conn     = null;
        PreparedStatement stmt     = null;
        ResultSet         rs       = null;
        long              start    = System.currentTimeMillis();
        String            sql      = "select COMPLETED_ARCHIVES, "
                + "END_TIME, ERROR_ARCHIVES, HOST_NAME, ID, "
                + "INVALID_ARCHIVES, START_TIME, TOTAL_COMPRESSED_SIZE, "
                + "TOTAL_ELAPSED_TIME, TOTAL_NUM_ARCHIVES, TOTAL_NUM_FILES, "
                + "TOTAL_SIZE from HOST_METRICS where HOST_NAME = ? "
                + "order by HOST_NAME";
        
        if (datasource != null) {
            if ((hostName != null) && (!hostName.isEmpty())) {
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, hostName);
                    rs   = stmt.executeQuery();
                    
                    if (rs.next()) {
                        
                        metrics = new HostMetrics();
                        metrics.setCompletedArchives(rs.getLong("COMPLETED_ARCHIVES"));
                        metrics.setEndTime(rs.getLong("END_TIME"));
                        metrics.setErrorArchives(rs.getLong("ERROR_ARCHIVES"));
                        metrics.setHostName(rs.getString("HOST_NAME"));
                        metrics.setID(rs.getLong("ID"));
                        metrics.setInvalidArchives(rs.getLong("INVALID_ARCHIVES"));
                        metrics.setStartTime(rs.getLong("START_TIME"));
                        metrics.setTotalCompressedSize(rs.getLong("TOTAL_COMPRESSED_SIZE"));
                        metrics.setTotalElapsedTime(rs.getLong("TOTAL_ELAPSED_TIME"));
                        metrics.setTotalNumArchives(rs.getLong("TOTAL_NUM_ARCHIVES"));
                        metrics.setTotalNumFiles(rs.getLong("TOTAL_NUM_FILES"));
                        metrics.setTotalSize(rs.getLong("TOTAL_SIZE"));
                        
                    }
                    else {
                        LOGGER.info("No matching HOST_METRICS records were "
                                + "found for host name [ "
                                + hostName
                                + " ].");
                    }
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to retrieve a list of HOST_METRICS "
                            + "objects from the target data source.  Error "
                            + "message [ "
                            + se.getMessage() 
                            + " ].");
                }
                finally {
                    try { 
                        if (rs != null) { rs.close(); }
                    } catch (Exception e) {}
                    try { 
                        if (stmt != null) { stmt.close(); } 
                    } catch (Exception e) {}
                    try { 
                        if (conn != null) { conn.close(); } 
                    } catch (Exception e) {}
                }
                
            }
            else {
                LOGGER.warn("The input job ID is null or empty.  Unable to "
                        + "retrieve the list of individual archives.");
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("HOST_METRICS record for [ " 
                    + hostName 
                    + " ] selected in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
        return metrics;
    }
    
    /**
     * Persist (update) the information associated with the input 
     * <code>HOST_METRICS</code> object.
     * 
     * @param metrics Host metrics object to update in the 
     * <code>HOST_METRICS</code> table.
     */
    public void updateHostMetrics(HostMetrics metrics) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "update HOST_METRICS set "
                        + "COMPLETED_ARCHIVES = ?, END_TIME = ?, "
                        + "ERROR_ARCHIVES = ?, HOST_NAME = ?, ID, "
                        + "INVALID_ARCHIVES = ?, START_TIME = ?, "
                        + "TOTAL_COMPRESSED_SIZE = ?, TOTAL_ELAPSED_TIME = ?, "
                        + "TOTAL_NUM_ARCHIVES = ?, TOTAL_NUM_FILES = ?, "
                        + "TOTAL_SIZE = ? where ID = ?";
        
        if (datasource != null) {
            if (metrics != null) {
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setLong(1, metrics.getCompletedArchives());
                    stmt.setLong(2, metrics.getEndTime());
                    stmt.setLong(3, metrics.getErrorArchives());
                    stmt.setString(4, metrics.getHostName());
                    stmt.setLong(5, metrics.getInvalidArchives());
                    stmt.setLong(6, metrics.getStartTime());
                    stmt.setLong(7, metrics.getTotalCompressedSize());
                    stmt.setLong(8, metrics.getTotalElapsedTime());
                    stmt.setLong(9, metrics.getTotalNumArchives());
                    stmt.setLong(10, metrics.getTotalNumFiles());
                    stmt.setLong(11, metrics.getTotalSize());
                    stmt.setLong(12, metrics.getID());
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to insert a new HOST_METRICS object "
                            + "into the data store.  Error message [ "
                            + se.getMessage() 
                            + " ].");
                }
                finally {
                    try { 
                        if (stmt != null) { stmt.close(); } 
                    } catch (Exception e) {}
                    try { 
                        if (conn != null) { conn.close(); } 
                    } catch (Exception e) {}
                }
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Update of HOST_METRICS object for host ID [ "
                    + metrics.getHostName()
                    + " ] completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
    /**
     * Persist (update) the information associated with the input 
     * <code>HOST_METRICS</code> object.
     * 
     * @param metrics Host metrics object to update in the 
     * <code>HOST_METRICS</code> table.
     */
    public void insertHostMetrics(HostMetrics metrics) {
        
        Connection        conn   = null;
        PreparedStatement stmt   = null;
        long              start  = System.currentTimeMillis();
        String            sql    = "insert into HOST_METRICS ("
                        + "COMPLETED_ARCHIVES, END_TIME, ERROR_ARCHIVES, "
                        + "HOST_NAME, ID, INVALID_ARCHIVES, START_TIME, "
                        + "TOTAL_COMPRESSED_SIZE, TOTAL_ELAPSED_TIME, "
                        + "TOTAL_NUM_ARCHIVES, TOTAL_NUM_FILES, "
                        + "TOTAL_SIZE) "
                        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (datasource != null) {
            if (metrics != null) {
                
                try { 
                    
                    conn = datasource.getConnection();
                    stmt = conn.prepareStatement(sql);
                    stmt.setLong(1, metrics.getCompletedArchives());
                    stmt.setLong(2, metrics.getEndTime());
                    stmt.setLong(3, metrics.getErrorArchives());
                    stmt.setString(4, metrics.getHostName());
                    stmt.setLong(5, metrics.getInvalidArchives());
                    stmt.setLong(6, metrics.getStartTime());
                    stmt.setLong(7, metrics.getTotalCompressedSize());
                    stmt.setLong(8, metrics.getTotalElapsedTime());
                    stmt.setLong(9, metrics.getTotalNumArchives());
                    stmt.setLong(10, metrics.getTotalNumFiles());
                    stmt.setLong(11, metrics.getTotalSize());
                    stmt.executeUpdate();
                    
                }
                catch (SQLException se) {
                    LOGGER.error("An unexpected SQLException was raised while "
                            + "attempting to insert a new HOST_METRICS object "
                            + "into the data store.  Error message [ "
                            + se.getMessage() 
                            + " ].");
                }
                finally {
                    try { 
                        if (stmt != null) { stmt.close(); } 
                    } catch (Exception e) {}
                    try { 
                        if (conn != null) { conn.close(); } 
                    } catch (Exception e) {}
                }
            }
        }
        else {
            LOGGER.warn("DataSource object not injected by the container.  "
                    + "An empty List will be returned to the caller.");
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Insert of HOST_METRICS object for host ID [ "
                    + metrics.getHostName()
                    + " ] completed in [ "
                    + (System.currentTimeMillis() - start) 
                    + " ] ms.");
        }
    }
    
}
