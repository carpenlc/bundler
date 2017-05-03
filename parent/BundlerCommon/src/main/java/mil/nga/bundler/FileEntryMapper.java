package mil.nga.bundler;

import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.model.ValidFile;
import mil.nga.bundler.types.JobStateType;


/**
 * Class introduced that contains logic to map <code>ValidFile</code>
 * objects into <code>FileRequest</code> objects.  This class was introduced
 * so we can keep the legacy JPA data model while leveraging the new validation
 * routines. 
 * 
 * @author L. Craig Carpenter
 */
public class FileEntryMapper {

	/**
	 * Private constructor enforcing the Singleton design pattern.
	 */
	private FileEntryMapper() {}
	
	
	/**
	 * Accessor method for the singleton instance of the AeroDataFactory.
	 * @return Handle to the singleton instance of the AeroDataFactory.
	 */
	public static FileEntryMapper getInstance() {
		return FileEntryMapperHolder.getFactorySingleton();
	}
	
	/**
	 * Map data from the <code>ValidFile</code> object into an object of 
	 * type <code>FileRequest</code>.
	 * 
	 * @param file 
	 * @return
	 */
	public FileEntry getFileEntry(String jobID, long archiveID, ValidFile file) {
		FileEntry fEntry = new FileEntry();
		fEntry.setJobID(jobID);
		fEntry.setArchiveID(archiveID);
		fEntry.setEntryPath(file.getEntryPath());
		fEntry.setFilePath(file.getPath());
		fEntry.setSize(file.getSize());
		fEntry.setFileState(JobStateType.NOT_STARTED);
		return fEntry;
	}
	
	/** 
	 * Static inner class used to construct the factory singleton.  This
	 * class exploits that fact that inner classes are not loaded until they 
	 * referenced therefore enforcing thread safety without the performance 
	 * hit imposed by the use of the "synchronized" keyword.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class FileEntryMapperHolder {
		
		/**
		 * Reference to the Singleton instance of the factory
		 */
		private static FileEntryMapper _factory = new FileEntryMapper();
		
		/**
		 * Accessor method for the singleton instance of the factory object.
		 * @return The singleton instance of the factory.
		 */
		public static FileEntryMapper getFactorySingleton() {
			return _factory;
		}
	}
}
