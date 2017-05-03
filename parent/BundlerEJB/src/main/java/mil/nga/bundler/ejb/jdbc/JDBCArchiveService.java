package mil.nga.bundler.ejb.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import mil.nga.bundler.ejb.EJBClientUtilities;
import mil.nga.bundler.model.Archive;
import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.types.JobStateType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Session Bean implementation class JDBCArchiveService
 * 
 * This class provides methods that interface with the ARCHIVE_JOBS table 
 * that will allow the Job class to be materialized without the use of the 
 * complex joins that occur in JPA.  This class was added in order to improve
 * performance.
 * 
 * @author L. Craig Carpenter
 */
@Stateless
@LocalBean
public class JDBCArchiveService {
	
	/**
	 * Set up the logging system for use throughout the class
	 */		
	private static final Logger LOGGER = LoggerFactory.getLogger(
			JDBCArchiveService.class);
	
	/**
	 * Container-injected datasource object.
	 */
	@Resource(mappedName="java:jboss/datasources/JobTracker")
	DataSource datasource;
	
	/**
	 * Container-injected reference to the JDBCFileService EJB.
	 */
	@EJB
	JDBCFileService jdbcFileService;
	
    /**
     * Default Eclipse-generated constructor. 
     */
    public JDBCArchiveService() { }

    /**
     * Private method used to obtain a reference to the target EJB.  
     * @return Reference to the JobService EJB.
     */
    private JDBCFileService getJDBCFileService() {
    	if (jdbcFileService == null) {
    		LOGGER.warn("Application container failed to inject the "
    				+ "reference to the JDBCFileService EJB.  Attempting to "
    				+ "look it up via JNDI.");
    		jdbcFileService = EJBClientUtilities
    				.getInstance()
    				.getJDBCFileService();
    	}
    	return jdbcFileService;
    }
    
    /**
     * Delete all individual archives that match the input  job ID.
     * 
     * @param jobID The job ID requested (must not be null, or empty String)
     */
    public void deleteArchive(String jobID) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "delete from ARCHIVE_JOBS where "
				+ "JOB_ID = ?";
		
		if (datasource != null) {
	    	if ((jobID != null) && (!jobID.isEmpty())) {
	    			
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setString(1, jobID);
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised "
    						+ "while attempting to delete ARCHIVE_JOBS "
    						+ "records associated with job ID [ "
    						+ jobID
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
    			LOGGER.warn("The input job ID is null or empty.  Unable to "
    					+ "delete the file list.");
    		}
	    	
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ARCHIVE_JOBS records delected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}	
    }
    
    /**
     * Delete all individual archives that match the input archive ID and 
     * job ID.
     * 
     * @param archiveID The archive ID requested. (must be greater than zero)
     * @param jobID The job ID requested (must not be null, or empty String)
     */
    public void deleteArchive(long archiveID, String jobID) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "delete from ARCHIVE_JOBS where "
				+ "ARCHIVE_ID = ? AND JOB_ID = ?";
		
		if (datasource != null) {
	    	if (archiveID >= 0) {
	    		if ((jobID != null) && (!jobID.isEmpty())) {
	    			
	    			try { 
	    				
	    				conn = datasource.getConnection();
	    				stmt = conn.prepareStatement(sql);
	    				stmt.setLong(1, archiveID);
	    				stmt.setString(2, jobID);
	    				stmt.executeUpdate();
	    				
	    			}
	    			catch (SQLException se) {
	    				LOGGER.error("An unexpected SQLException was raised "
	    						+ "while attempting to delete ARCHIVE_JOBS "
	    						+ "records associated with job ID [ "
	    						+ jobID
	    						+ " ] and archive ID [ "
	    						+ archiveID
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
	    			LOGGER.warn("The input job ID is null or empty.  Unable to "
	    					+ "delete the file list.");
	    		}
	    	}
	    	else {
	    		LOGGER.error("The input archive ID is out-of-range.  The archive "
	    				+ "ID should be greater than, or equal to zero.  Value "
	    				+ "supplied [ "
	    				+ archiveID
	    				+ " ].  Unable to execute delete command.");
	    	}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ARCHIVE_JOBS records delected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}	
    }
    
    /**
     * Delete all of the Archives and FileEntry objects associated with the
     * input job ID.
     * 
     * @param jobID The job ID requested (must not be null, or empty String)
     */
    public void deepDeleteArchive(String jobID) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "delete from ARCHIVE_JOBS where "
				+ "JOB_ID = ?";
		
		if (datasource != null) {
    		if ((jobID != null) && (!jobID.isEmpty())) {
    			
    			if (getJDBCFileService() != null) {
    				getJDBCFileService().deleteFiles(jobID);
    			}
    			else {
    	    		LOGGER.warn("Unable to obtain a reference to the "
    	    				+ "JDBCFileService EJB.  FILE_ENTRY entries for job ID [ "
    	    				+ jobID
    	    				+ " ] were not deleted leaving orphaned records.");
    			}
    			
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setString(1, jobID);
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised "
    						+ "while attempting to delete FILE_ENTRY "
    						+ "records associated with job ID [ "
    						+ jobID
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
    			LOGGER.warn("The input job ID is null or empty.  Unable to "
    					+ "delete the file list.");
    		}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("FILE_ENTRY records delected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
    }
    
    /**
     * Delete all individual archives that match the input archive ID and 
     * job ID.
     * 
     * @param archiveID The archive ID requested. (must be greater than zero)
     * @param jobID The job ID requested (must not be null, or empty String)
     */
    public void deepDeleteArchive(long archiveID, String jobID) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "delete from ARCHIVE_JOBS where "
				+ "ARCHIVE_ID = ? AND JOB_ID = ?";
		
		if (datasource != null) {
	    	if (archiveID >= 0) {
	    		if ((jobID != null) && (!jobID.isEmpty())) {
	    			
	    			if (getJDBCFileService() != null) {
	    				getJDBCFileService().deleteFiles(archiveID, jobID);
	    			}
	    			else {
	    	    		LOGGER.warn("Unable to obtain a reference to the "
	    	    				+ "JDBCFileService EJB.  FILE_ENTRY entries for job ID [ "
	    	    				+ jobID
	    	    				+ " ] were not deleted leaving orphaned records.");
	    			}
	    			
	    			try { 
	    				
	    				conn = datasource.getConnection();
	    				stmt = conn.prepareStatement(sql);
	    				stmt.setLong(1, archiveID);
	    				stmt.setString(2, jobID);
	    				stmt.executeUpdate();
	    				
	    			}
	    			catch (SQLException se) {
	    				LOGGER.error("An unexpected SQLException was raised "
	    						+ "while attempting to delete ARCHIVE_JOBS "
	    						+ "records associated with job ID [ "
	    						+ jobID
	    						+ " ] and archive ID [ "
	    						+ archiveID
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
	    			LOGGER.warn("The input job ID is null or empty.  Unable to "
	    					+ "delete the file list.");
	    		}
	    	}
	    	else {
	    		LOGGER.error("The input archive ID is out-of-range.  The archive "
	    				+ "ID should be greater than, or equal to zero.  Value "
	    				+ "supplied [ "
	    				+ archiveID
	    				+ " ].  Unable to execute delete command.");
	    	}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ARCHIVE_JOBS records delected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}	
    }
    
    /**
     * Retrieve the list of individual archives associated with the input  
     * job ID.  The list of archives returned from this method will not have
     * a populated list of <code>FileEntry</code> objects.  Call 
     * <code>getMaterializedArchives</code> to get a fully materialized list 
     * of archives.
     * 
     * @param jobID The job ID requested (must not be null, or empty String)
     * @return The requested list of archives.
     */
    public List<Archive> getArchives(String jobID) {
    	
    	List<Archive>     archives = new ArrayList<Archive>();
    	Connection        conn     = null;
		PreparedStatement stmt     = null;
		ResultSet         rs       = null;
		long              start    = System.currentTimeMillis();
		String            sql      = "select ID, ARCHIVE_FILE, "
				+ "ARCHIVE_ID, ARCHIVE_STATE, ARCHIVE_TYPE, ARCHIVE_URL, "    
				+ "END_TIME, HASH_FILE, HASH_FILE_URL, HOST_NAME, JOB_ID, "
				+ "NUM_FILES, SERVER_NAME, ARCHIVE_SIZE, START_TIME "
				+ "from ARCHIVE_JOBS where JOB_ID = ? order by ARCHIVE_ID";
		
		if (datasource != null) {
    		if ((jobID != null) && (!jobID.isEmpty())) {
    			
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setString(1, jobID);
    				rs   = stmt.executeQuery();
    				
    				while (rs.next()) {
    					
    					Archive archive = new Archive();
    					archive.setID(rs.getLong("ID"));
    					archive.setArchive(rs.getString("ARCHIVE_FILE"));
    					archive.setArchiveID(rs.getLong("ARCHIVE_ID"));
    					archive.setArchiveState(
    							JobStateType.valueOf(
    									rs.getString("ARCHIVE_STATE")));
    					archive.setArchiveType(
    							ArchiveType.valueOf(
    									rs.getString("ARCHIVE_TYPE")));
    					archive.setArchiveURL(rs.getString("ARCHIVE_URL"));
    					archive.setEndTime(rs.getLong("END_TIME"));
    					archive.setHash(rs.getString("HASH_FILE"));
    					archive.setHashURL(rs.getString("HASH_FILE_URL"));
    					archive.setHostName(rs.getString("HOST_NAME"));
    					archive.setJobID(rs.getString("JOB_ID"));
    					archive.setNumFiles(rs.getInt("NUM_FILES"));
    					archive.setServerName(rs.getString("SERVER_NAME"));
    					archive.setSize(rs.getLong("ARCHIVE_SIZE"));
    					archive.setStartTime(rs.getLong("START_TIME"));
    					archives.add(archive);
    					
    				}
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised while "
    						+ "attempting to retrieve a list of FILE_ENTRY "
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
			LOGGER.debug("[ "
					+ archives.size()
					+ " ] archives selected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
    	
    	return archives;
    }
    
    /**
     * 
     * @param archiveID
     * @param jobID
     * @return
     */
    public Archive getMaterializedArchive(long archiveID, String jobID) {
    	
    	Archive           archive  = new Archive();
    	Connection        conn     = null;
		PreparedStatement stmt     = null;
		ResultSet         rs       = null;
		long              start    = System.currentTimeMillis();
		String            sql      = "select ID, ARCHIVE_FILE, "
				+ "ARCHIVE_ID, ARCHIVE_STATE, ARCHIVE_TYPE, ARCHIVE_URL, "    
				+ "END_TIME, HASH_FILE, HASH_FILE_URL, HOST_NAME, JOB_ID, "
				+ "NUM_FILES, SERVER_NAME, ARCHIVE_SIZE, START_TIME "
				+ "from ARCHIVE_JOBS where JOB_ID = ? and ARCHIVE_ID = ? ";
		
		if (datasource != null) {
	    	if (archiveID >= 0) {
	    		if ((jobID != null) && (!jobID.isEmpty())) {
	    			
	    			try { 
	    				
	    				conn = datasource.getConnection();
	    				stmt = conn.prepareStatement(sql);
	    				stmt.setString(1, jobID);
	    				stmt.setLong(2, archiveID);
	    				rs   = stmt.executeQuery();
	    				
	    				if (rs.next()) {
	    					
	    					archive.setID(rs.getLong("ID"));
	    					archive.setArchive(rs.getString("ARCHIVE_FILE"));
	    					archive.setArchiveID(rs.getLong("ARCHIVE_ID"));
	    					archive.setArchiveState(
	    							JobStateType.valueOf(
	    									rs.getString("ARCHIVE_STATE")));
	    					archive.setArchiveType(
	    							ArchiveType.valueOf(
	    									rs.getString("ARCHIVE_TYPE")));
	    					archive.setArchiveURL(rs.getString("ARCHIVE_URL"));
	    					archive.setEndTime(rs.getLong("END_TIME"));
	    					archive.setHash(rs.getString("HASH_FILE"));
	    					archive.setHashURL(rs.getString("HASH_FILE_URL"));
	    					archive.setHostName(rs.getString("HOST_NAME"));
	    					archive.setJobID(rs.getString("JOB_ID"));
	    					archive.setNumFiles(rs.getInt("NUM_FILES"));
	    					archive.setServerName(rs.getString("SERVER_NAME"));
	    					archive.setSize(rs.getLong("ARCHIVE_SIZE"));
	    					archive.setStartTime(rs.getLong("START_TIME"));
	    			
	    	    			if (getJDBCFileService() != null) {
	    	    				archive.setFiles(
	    	    						getJDBCFileService().
	    	    							getFiles(archiveID, jobID));
	    	    			}
	    	    			else {
	    	    	    		LOGGER.warn("Unable to obtain a reference to "
	    	    	    				+ "the JDBCFileService EJB.  "
	    	    	    				+ "FILE_ENTRY entries for job ID [ "
	    	    	    				+ jobID
	    	    	    				+ " ] and archive ID [ "
	    	    	    				+ archiveID
	    	    	    				+ " ] were not retrieved");
	    	    			}
	    				}
	    			}
	    			catch (SQLException se) {
	    				LOGGER.error("An unexpected SQLException was raised while "
	    						+ "attempting to retrieve a list of FILE_ENTRY "
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
	    					+ "delete the file list.");
	    		}
	    	}
	    	else {
	    		LOGGER.error("The input archive ID is out-of-range.  The archive "
	    				+ "ID should be greater than, or equal to zero.  Value "
	    				+ "supplied [ "
	    				+ archiveID
	    				+ " ].  Unable to retrieve archive data.");
	    	}
		}
		else {
        	LOGGER.warn("DataSource object not injected by the container.  "
        			+ "An empty List will be returned to the caller.");
		}
		
    	if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Retrieval of ARCHIVE_JOB object for job ID [ "
					+ jobID
					+ " ] and archive ID [ "
					+ archiveID
					+ " ] completed in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
		return archive;
    }
    
    /**
     * Retrieve a fully loaded list of individual archives associated with 
     * the input job ID.  The list of archives returned will contain the 
     * list of <code>FileEntry</code> objects retrieved from the back-end 
     * <code>FILE_ENTRY</code> table.
     * 
     * @param jobID The job ID requested (must not be null, or empty String)
     * @return The requested list of archives (may be null).
     */
    public List<Archive> getMaterializedArchives(String jobID) {
    	
    	List<Archive> archives = null;
    	
    	if ((jobID != null) && (!jobID.isEmpty())) {
	    	archives = getArchives(jobID);
	    	if (getJDBCFileService() != null) {
		    	if ((archives != null) && (archives.size() > 0)) { 
		    		for (Archive archive : archives) {
		    			
		    			List<FileEntry> files = 
		    					getJDBCFileService().getFiles(
		    							archive.getArchiveID(), 
		    							archive.getJobID());
		    			archive.setFiles(files);
		    			
		    		}
		    	}
		    	else {
		    		LOGGER.error("Unable to obtain the individual archives "
		    				+ "associated with job ID [ "
		    				+ jobID
		    				+ " ].");
		    	}
	    	}
	    	else {
	    		LOGGER.error("Unable to obtain a reference to the JDBCFileService "
	    				+ "EJB.  The archives will not contain the list of "
	    				+ "FileEntry objects.");
	    	}
    	}
    	else {
    		LOGGER.error("The input job ID is null or empty.");
    	}
    	return archives;
    }
    
    /**
     * Retreive a list of unique hosts that have processed bundler jobs.
     * @return The list of unique hosts that have processed bundler jobs.
     */
    public List<String> getUniqueHosts() {
    	
		Connection        conn   = null;
		List<String>      hosts  = new ArrayList<String>();
		PreparedStatement stmt   = null;
		ResultSet         rs     = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "select unique(HOST_NAME) from ARCHIVE_JOBS";
		
		if (datasource != null) {
			
			try {
				conn = datasource.getConnection();
				stmt = conn.prepareStatement(sql);
				rs   = stmt.executeQuery();
				while (rs.next()) {
					hosts.add(rs.getString("HOST_NAME").trim());
				}
			}
			catch (SQLException se) {
				LOGGER.error("An unexpected SQLException was raised while "
						+ "attempting to retrieve a list of unique hosts from "
						+ "the target data source.  Error message [ "
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
					+ hosts.size() 
					+ " ] hosts selected in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
		
		return hosts;
    }
    
    /**
     * Persist (insert) the information associated with the input 
     * <code>ARCHIVE_JOB</code> object.
     * 
     * @param archive Data to insert into the <code>ARCHIVE_JOB</code> table.
     */
    public void insertArchive(Archive archive) {
    	
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "insert into ARCHIVE_JOBS ("
						+ "ARCHIVE_FILE, ARCHIVE_ID, ARCHIVE_STATE, "
						+ "ARCHIVE_TYPE, ARCHIVE_URL, END_TIME, HASH_FILE, "
						+ "HASH_FILE_URL, HOST_NAME, JOB_ID, "
						+ "NUM_FILES, SERVER_NAME, ARCHIVE_SIZE, START_TIME) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		if (datasource != null) {
	    	if (archive != null) {
	    			
    			if (getJDBCFileService() != null) {
    				getJDBCFileService().insertFiles(archive.getFiles());
    			}
    			else {
    	    		LOGGER.error("Unable to obtain a reference to the "
    	    				+ "JDBCFileService EJB.  FILE_ENTRY entries for "
    	    				+ "job ID [ "
    	    				+ archive.getJobID()
    	    				+ " ] and archive ID [ "
    	    				+ archive.getArchiveID()
    	    				+ " ] were not inserted into the data store.");
    			}
    			
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setString(1, archive.getArchiveFilename());
    				stmt.setLong(2, archive.getArchiveID());
    				stmt.setString(3, archive.getArchiveState().getText());
    				stmt.setString(4, archive.getArchiveType().getText());
    				stmt.setString(5, archive.getArchiveURL());
    				stmt.setLong(6, archive.getEndTime());
    				stmt.setString(7, archive.getHashFilename());
    				stmt.setString(8, archive.getHashURL());
    				stmt.setString(9, archive.getHostName());
    				stmt.setString(10, archive.getJobID());
    				stmt.setInt(11, archive.getNumFiles());
    				stmt.setString(12, archive.getServerName());
    				stmt.setLong(13, archive.getSize());
    				stmt.setLong(14, archive.getStartTime());
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised while "
    						+ "attempting to insert a new ARCHIVE_JOB object "
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
			LOGGER.debug("Insert of ARCHIVE_JOB object for job ID [ "
					+ archive.getJobID()
					+ " ] completed in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
    }
    
    /**
     * Persist (insert) the information associated with the input 
     * list of <code>ARCHIVE_JOB</code> objects.
     * 
     * @param archives List of objects to insert into the 
     * <code>ARCHIVE_JOB</code> table.
     */
    public void insertArchives(List<Archive> archives) {
    	if ((archives != null) && (archives.size() > 0)) {
    		for (Archive archive : archives) {
    			insertArchive(archive);
    		}
    	}
    	else {
    		LOGGER.warn("The input archive list is null or contains no "
    				+ "objects.  Insert operation not performed.");
    	}
    }
    
    /**
     * Persist (update) the information associated with the input 
     * <code>ARCHIVE_JOB</code> object.
     * 
     * @param archive Archive object to update in the 
     * <code>ARCHIVE_JOB</code> table.
     */
    public void updateArchive(Archive archive) {
    	Connection        conn   = null;
		PreparedStatement stmt   = null;
		long              start  = System.currentTimeMillis();
		String            sql    = "update ARCHIVE_JOBS set "
						+ "ARCHIVE_FILE = ?, ARCHIVE_ID = ?, ARCHIVE_STATE = ?, "
						+ "ARCHIVE_TYPE = ?, ARCHIVE_URL = ?, END_TIME = ?, "
						+ "HASH_FILE = ?, HASH_FILE_URL = ?, HOST_NAME = ?, "
						+ "JOB_ID = ?, NUM_FILES = ?, SERVER_NAME = ?, "
						+ "ARCHIVE_SIZE = ?, START_TIME = ? where ID = ?";
		
		if (datasource != null) {
	    	if (archive != null) {
	    			
    			if (getJDBCFileService() != null) {
    				getJDBCFileService().updateFiles(archive.getFiles());
    			}
    			else {
    	    		LOGGER.error("Unable to obtain a reference to the "
    	    				+ "JDBCFileService EJB.  FILE_ENTRY entries for "
    	    				+ "job ID [ "
    	    				+ archive.getJobID()
    	    				+ " ] and archive ID [ "
    	    				+ archive.getArchiveID()
    	    				+ " ] were not updated into the data store.");
    			}
    			
    			try { 
    				
    				conn = datasource.getConnection();
    				stmt = conn.prepareStatement(sql);
    				stmt.setString(1, archive.getArchiveFilename());
    				stmt.setLong(2, archive.getArchiveID());
    				stmt.setString(3, archive.getArchiveState().getText());
    				stmt.setString(4, archive.getArchiveType().getText());
    				stmt.setString(5, archive.getArchiveURL());
    				stmt.setLong(6, archive.getEndTime());
    				stmt.setString(7, archive.getHashFilename());
    				stmt.setString(8, archive.getHashURL());
    				stmt.setString(9, archive.getHostName());
    				stmt.setString(10, archive.getJobID());
    				stmt.setInt(11, archive.getNumFiles());
    				stmt.setString(12, archive.getServerName());
    				stmt.setLong(13, archive.getSize());
    				stmt.setLong(14, archive.getStartTime());
    				stmt.setLong(15, archive.getID());
    				stmt.executeUpdate();
    				
    			}
    			catch (SQLException se) {
    				LOGGER.error("An unexpected SQLException was raised while "
    						+ "attempting to insert a new ARCHIVE_JOB object "
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
			LOGGER.debug("Insert of ARCHIVE_JOB object for job ID [ "
					+ archive.getJobID()
					+ " ] completed in [ "
					+ (System.currentTimeMillis() - start) 
					+ " ] ms.");
		}
    }
    
    /**
     * Persist (update) the information associated with the input 
     * list of <code>ARCHIVE_JOB</code> objects.
     * 
     * @param archives List of objects to update in the 
     * <code>ARCHIVE_JOB</code> table.
     */
    public void updateArchives(List<Archive> archives) {
    	if ((archives != null) && (archives.size() > 0)) {
    		for (Archive archive : archives) {
    			updateArchive(archive);
    		}
    	}
    	else {
    		LOGGER.warn("The input archive list is null or contains no "
    				+ "objects.  Update operation not performed.");
    	}
    }
    
}
