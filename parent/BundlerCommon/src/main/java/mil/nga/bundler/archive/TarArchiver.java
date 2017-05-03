package mil.nga.bundler.archive;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.types.JobStateType;
import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.exceptions.ArchiveException;
import mil.nga.bundler.model.FileEntry;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete class implementing the logic to create an archive file in 
 * TAR format.
 * 
 * @author carpenlc
 */
public class TarArchiver 
		extends Archiver implements BundlerI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	Logger LOGGER = LoggerFactory.getLogger(TarArchiver.class);
	
	/** 
	 * The archive type handled by this class
	 */
	private ArchiveType _type = ArchiveType.TAR;
	
	/**
	 * The base directory that is being archived.  This may be null.
	 */
	private String _baseDir = null;
	
	/**
	 * Default constructor
	 */
	public TarArchiver( ) { }
	
	/**
	 * Add all files contained in the target input directory to the output
	 * archive file.  
	 * 
	 * @param baseDir The directory to archive
	 * @param outputFile The full path to the target output archive file.  
	 * @throws IOException Thrown If there are errors reading/writing to the
	 * target output file.
	 */
	@Override
	public void bundle(String baseDir, String outputFile) 
			throws ArchiveException, IOException {
		
		String method = "bundle() - ";
		TarArchiveOutputStream taos = null;
		
		// Save the base directory
		setBaseDir(baseDir);
		
		File dir = new File(baseDir);
		if (!dir.exists()) {
			String msg = "Directory identified for archiving does not exist.  "
					+ "Directory identified:  "
					+ baseDir;
			LOGGER.error(method + msg);
			throw new ArchiveException(msg);
		}
		if (!dir.isDirectory()) {
			LOGGER.warn(method 
					+ "Expecting directory as input.  Received single file.");
		}
        
		try {
			
			// Get the actual name of the archive file			
			setArchiveName(outputFile, this._type.getText());
					
			// Create the output stream zoo
	        taos = new TarArchiveOutputStream(
	        		new BufferedOutputStream(
	        			new FileOutputStream(getArchiveName())));
	        
	        // Invoke superclass methods to create the output archive 
	        addFile((ArchiveOutputStream)taos, baseDir, "");
	        
		}
		finally {
			if (taos != null) {
				try {	
					taos.finish();
					taos.close();
				}
				catch (Exception e) { 
					LOGGER.warn(method 
							+ "Uknown exception raised while trying to close "
							+ "the TarArchiveOutputStream object.");
				}
			}
		}
	}
	
	/**
	 * Bundle each file in the input list.  Each entry in the list should 
	 * be the full path to file that should be added to the archive.  
	 * 
	 * @param files List of files (full path) to be added to the archive.
	 * @param outputFile Full path of the output archive file (may or may not
	 * include the extension)
	 * @param baseDir (optional) Specifies that all files will fall under a 
	 * base directory.  This parameter is used in the creation of the relative
	 * file paths contained within the output archive.
	 * @throws IOException Raised if there are issues constructing the output
	 * archive.
	 */
	@Override
	public void bundle(
			List<String> files, 
			String outputFile, 
			String baseDir) 
			throws ArchiveException, IOException {
		
		String                 method = "bundle() - ";
		TarArchiveOutputStream taos   = null;
		
		// Save the base directory
		setBaseDir(baseDir);
		
		if ((files == null) || (files.size() == 0)) {
			String msg = "No files were identified for archiving.  "
				+ "The output archive file will not be created.";
			LOGGER.error(method + msg);
			throw new ArchiveException(msg);
		}
		
		try {
			
			// Get the actual name of the archive file			
			setArchiveName(outputFile, this._type.getText());
					
			// Create the output stream zoo
	        taos = new TarArchiveOutputStream(
	        		new BufferedOutputStream(
	        			new FileOutputStream(getArchiveName())));
			
	        // Loop through each file in the input list
			for (String fileName : files) {
				
				File file = new File(fileName);
				if (file.exists()) {
					
					// Add the archive entry to the output stream
					String name = super.getEntryPath(
								file.getAbsolutePath(), 
								baseDir);
					taos.putArchiveEntry(getArchiveEntry(file, name));
					super.addOneFile(taos, file);
					
				}
				else {
					
					LOGGER.warn(method 
							+ "File identified for inclusion in the output "
							+ "archive file does not exist.  File requested:  "
							+ fileName);
				}
			}
		}
		finally {
			if (taos != null) {
				try {	
					taos.finish();
					taos.close();
				}
				catch (Exception e) { 
					LOGGER.warn(method 
							+ "Uknown exception raised while trying to close "
							+ "the TarArchiveOutputStream object.");
				}
			}
		}
	}
	
	/**
	 * Create a concrete object of type TarArchiveEntry for use in 
	 * constructing TAR archive files.
	 * 
	 * @param file The file that will be added to the Archive.
	 * @param name The name (full path)
	 * @return A concrete ArchiveEntry object (ZipArchiveEntry 
	 * or TarArchiveEntry)
	 */
	@Override
	public ArchiveEntry getArchiveEntry(File file, String name) {
		String method = "getArchiveEntry() - ";
		
		if (!file.exists()) {
			LOGGER.warn(method 
					+ "Adding a non-existant file to the target TAR archive.");
		}
		
		// Ensure the archive name is relative
		name = super.getEntryPath(name, getBaseDir());
		
		// Also ensure that the name is not longer than 100 characters.
		// if it is, return null String.
		if (name.length() >= 100) {
			LOGGER.warn(method 
					+ "Unable to maintain full path to target file!  The "
					+ "TAR archiver cannot handle files with a path greater " 
					+ "than or equal to 100 characters.  The following file "
					+ "will be added to the archive at the root.  File:  "
					+ file.getAbsolutePath());
			name = file.getName();
			
			// Testing revealed that we also occasionally encounter file names
			// with a length greater than 100.  
			if (name.length() >= 100) {
				LOGGER.warn(method 
						+ "Unable to maintain full filename of the target file! "
						+ "The TAR archiver cannot handle files with a name " 
						+ "greater than or equal to 100 characters.  The " 
						+ "the following file will be truncated to 100 "
						+ "characters [  "
						+ name
						+ " ].");
				
			}
		}
		
		return new TarArchiveEntry(file, name);
	}
	
	@Override
	public void bundle(List<FileEntry> files, String outputFile) 
			throws ArchiveException, IOException {
		
		String                 method = "bundle() - ";
		TarArchiveOutputStream taos   = null;
		
		if ((files == null) || (files.size() == 0)) {
			String msg = "No files were identified for archiving.  "
				+ "The output archive file will not be created.";
			LOGGER.error(method + msg);
			throw new ArchiveException(msg);
		}
		
		try {
			
			// Get the actual name of the archive file			
			setArchiveName(outputFile, this._type.getText());
					
			// Create the output stream zoo
	        taos = new TarArchiveOutputStream(
	        		new BufferedOutputStream(
	        			new FileOutputStream(getArchiveName())));
			
	        // Loop through each file in the input list
			for (FileEntry entry : files) {
				
				File file = new File(entry.getFilePath());
				if (file.exists()) {
					
					taos.putArchiveEntry(
							getArchiveEntry(
									file, 
									entry.getEntryPath()));
					super.addOneFile(taos, file);
					entry.setFileState(JobStateType.COMPLETE);
					
				}
				else {
					
					LOGGER.warn(method 
							+ "File identified for inclusion in the output "
							+ "archive file does not exist.  File requested [ "
							+ entry.getFilePath()
							+ " ].");
				}
			}
		}
		finally {
			if (taos != null) {
				try {	
					taos.finish();
					taos.close();
				}
				catch (Exception e) { 
					LOGGER.warn(method 
							+ "Uknown exception raised while trying to close "
							+ "the TarArchiveOutputStream object.");
				}
			}
		}
	}
	
	/**
	 * Getter method for the base directory for the archive.
	 * @return The base directory.
	 */
	public String getBaseDir() {
		return this._baseDir;
	}
	
	/**
	 * If the archive was created from a base directory, save a handle to it.
	 * This will be used when the output archive is created.  
	 * 
	 * @param value The base directory.
	 */
	public void setBaseDir(String value) {
		this._baseDir = value;
	}
	
}
