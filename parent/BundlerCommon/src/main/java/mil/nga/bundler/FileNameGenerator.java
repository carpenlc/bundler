package mil.nga.bundler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.nga.PropertyLoader;
import mil.nga.bundler.exceptions.PropertiesNotLoadedException;
import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.util.FileUtils;

/**
 * Class responsible for generating a staging directory and target filename
 * for output archive files.  The staging directory that is created will follow
 * a simple pattern:
 * 
 * <prefix>_<hostname>_<random chars>
 * 
 * The output archive file created will also have a simple pattern:
 * 
 * <prefix>_data_archive
 * 
 * @author carpenlc
 */
public class FileNameGenerator 
		extends PropertyLoader 
		implements BundlerConstantsI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	static final Logger LOGGER = LoggerFactory.getLogger(
			FileNameGenerator.class);
	
	/**
	 * The location of the staging directory to use for generating the 
	 * output archive files.
	 */
	private String _stagingDirectory = null;
	
	/**
	 * The file path separator
	 */
	private String _pathSeparator = null;
	
	
	/**
	 * Default constructor
	 */
	private FileNameGenerator() {
		super(PROPERTY_FILE_NAME);
		try {
			_pathSeparator = System.getProperty("file.separator");
			setStagingDirectory(getProperty(STAGING_DIRECTORY_PROPERTY));
		}
		catch (PropertiesNotLoadedException pnle) {
			LOGGER.warn("An unexpected PropertiesNotLoadedException " 
					+ "was encountered.  Please ensure the application "
					+ "is properly configured.  Exception message [ "
					+ pnle.getMessage()
					+ " ].");
		}
	}
	
	/**
	 * Return a singleton instance to the FileGenerator object.
	 * @return The FileGenerator
	 */
	public static FileNameGenerator getInstance() {
		return FileNameGeneratorHolder.getFactorySingleton();
	}
	
	/**
	 * Calculate a unique token used to make directories and/or filenames 
	 * unique.
	 * @return A unique string.
	 */
	public String getUniqueToken() {
		return FileUtils.generateUniqueToken(UNIQUE_TOKEN_LENGTH);
	}
	
	/**
	 * Calculate the name for a directory that will be used to store the 
	 * output archive files.  Important note:  This method will return a 
	 * different value for the archive directory every time it's called.
	 *   
	 * @return A full path to an output directory.
	 */
	public String getArchiveDirectory() {
		
		String        method = "getArchiveDirectory() - ";
		StringBuilder sb     = new StringBuilder();
		
		sb.append(getStagingDirectory());
		if (!sb.toString().endsWith(_pathSeparator)) {
			sb.append(_pathSeparator);
		}
		sb.append(DEFAULT_FILENAME_PREFIX);
		sb.append("_");
		sb.append(FileUtils.getHostName());
		sb.append("_");
		sb.append(getUniqueToken());
		
		// Make sure the directory is unique
		File file = new File(sb.toString());
		if (file.exists()) {
			return getArchiveDirectory();
		}
		else {
			// Updated to ensure directory permissions are wide open
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			file.mkdir();
			if (!file.exists()) {
				LOGGER.error(method
						+ "Unable to create the output archive directory.  "
						+ "Attempted to create [" 
						+ file.getAbsolutePath()
						+ "].");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Get the default name of the archive file to use.
	 * 
	 * @return The archive filename.
	 */
	public String getFilename() {
		StringBuilder sb = new StringBuilder();
		sb.append(DEFAULT_FILENAME_PREFIX);
		sb.append("_data_archive");
		return sb.toString();
	}
	
	/**
	 * By appending a random string to the end of the filename, this method 
	 * will create a unique filename. 
	 * @return A unique filename.
	 */
	public String getUniqueFilename() {
		StringBuilder sb = new StringBuilder();
		sb.append(DEFAULT_FILENAME_PREFIX);
		sb.append("_data_archive_");
		sb.append(getUniqueToken());
		return sb.toString();
	}
	
	/**
	 * Create a filename based on the input parameters.
	 * 
	 * @param template The base name of the file.
	 * @param index The index to add.
	 * @param extension The file extension.
	 * @return A filename consisting of the concatenated parts.
	 */
	public String createFilename(
			String template,
			long   index,
			String extension) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(template);
		if (index > 0) {
			sb.append("_");
			sb.append(index);
		}
		sb.append(".");
		sb.append(extension);
		return sb.toString();	
	}
	
	/**
	 * Method to calculate the archive filename based on an archive filename
	 * requested by the client.
	 * 
	 * @param filename The client-requested name for the output file. 
	 * @return The full path to the output archive file.
	 */
	public String getArchiveFile(String filename) {
		if ((filename == null) || 
				(filename.trim().equalsIgnoreCase(""))) {
			filename = getFilename();
		}
		
		// If there is an extension on the filename, strip it off.
		if (filename.contains(".")) {
			filename = filename.substring(0, filename.lastIndexOf('.'));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(getArchiveDirectory());
		sb.append(_pathSeparator);
		sb.append(filename);
		return sb.toString();
	}
	
	/**
	 * Calculate the full path to the output archive file.
	 * 
	 * @return The full path to the target output archive file.
	 */
	public String getArchiveFile() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(getArchiveDirectory());
		sb.append(_pathSeparator);
		sb.append(getFilename());
		return sb.toString();
	}
	
	/**
	 * Calculate a regular expression that can be used to search for 
	 * bundler staging directories.  
	 * 
	 * @return A REGEX used to search for staging directories.
	 */
	public static List<String> getRegEx() {
		
		List<String> regexes = new ArrayList<String>();
		StringBuilder sb           = new StringBuilder();
		
		sb.append(DEFAULT_FILENAME_PREFIX);
		sb.append("_");
		sb.append(FileUtils.getHostName());
		sb.append("_");
		sb.append("[A-Z0-9]{");
		sb.append(2*UNIQUE_TOKEN_LENGTH);
		sb.append("}+");
		regexes.add(sb.toString());
		sb = new StringBuilder();
		sb.append(DEFAULT_FILENAME_PREFIX);
		sb.append("_");
		sb.append(FileUtils.getHostName());
		sb.append("_");
		sb.append("[A-Z0-9]{");
		sb.append(4*UNIQUE_TOKEN_LENGTH);
		sb.append("}+");
		regexes.add(sb.toString());
		return regexes;
	}
	
	/**
	 * Getter method for the target staging directory.
	 * 
	 * @return The location to use for staging archives.
	 */
	public String getStagingDirectory() {
		if ((_stagingDirectory == null) || 
				(_stagingDirectory.equalsIgnoreCase(""))) {
			_stagingDirectory = System.getProperty("java.io.tmpdir");
		}
		return _stagingDirectory;
	}
	
	/**
	 * Setter method for the staging directory.  If the input directory is 
	 * not supplied, the location specified by the <code>java.io.tmpdir</code>
	 * is used.
	 * 
	 * @param dir Location for the staging directory.
	 */
	public void setStagingDirectory(String dir) {
		
		if ((dir == null) || (dir.trim().equalsIgnoreCase(""))) {
			_stagingDirectory = System.getProperty("java.io.tmpdir");
			LOGGER.warn("Application property [ " 
					+ STAGING_DIRECTORY_PROPERTY
					+ " ] is not defined.  Using system property [ "
					+ _stagingDirectory
					+ " ].");
		}
		else {
			_stagingDirectory = dir;
		}
	}
	
	
	/** 
	 * Static inner class used to construct the factory singleton.  This
	 * class exploits that fact that inner classes are not loaded until they 
	 * referenced therefore enforcing thread safety without the performance 
	 * hit imposed by the use of the "synchronized" keyword.
	 * 
	 * @author L. Craig Carpenter
	 */
	public static class FileNameGeneratorHolder {
		
		/**
		 * Reference to the Singleton instance of the factory
		 */
		private static FileNameGenerator _factory = new FileNameGenerator();
		
		/**
		 * Accessor method for the singleton instance of the factory object.
		 * 
		 * @return The singleton instance of the factory.
		 */
		public static FileNameGenerator getFactorySingleton() {
			return _factory;
		}
	}
}
