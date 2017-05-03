package mil.nga.bundler.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is class was designed to encapsulate the logic used for creating the 
 * output archive files.
 * 
 * @author carpenlc
 */
public abstract class Archiver {
	
	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	Logger LOGGER = LoggerFactory.getLogger(Archiver.class);
	
	/**
	 * Protected internal member holding the archive file name.
	 */
	protected String _archiveName = null;
	
	/**
	 * Default constructor.
	 */
	public Archiver() { }
	
	/**
	 * Add a single file to the output stream.
	 * 
	 * @param out The appropriate ArchiveOutputStream
	 * @param file Abstract reference to the file to add
	 */
	public void addOneFile(ArchiveOutputStream out, File file) {
		
		String          method = "addOneFile() - ";
		FileInputStream fis    = null;
		
		try {
			
			fis = new FileInputStream(file);
			IOUtils.copy(fis, out);
			out.closeArchiveEntry();
			
		}
		catch (IOException ioe) {
			LOGGER.error(method 
					+ "An unexpected IOException was encountered while "
					+ "attempting to add "
					+ file.getAbsolutePath()
					+ "to the target archive.  Output archive may be " 
					+ "corrupt.", ioe);
		}
		finally {
			
			// Ensure the current ArchiveEntry is closed.
			if (out != null) {
				try { out.closeArchiveEntry(); } catch (Exception e) {}
			}
			// Ensure the stream is closed
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
			
		}
	}
	
	/**
	 * This will be called recursively.
	 * 
	 * @param out The output stream for the target archive type.
	 * @param path The full path to the file to add.
	 * @param base The parent directory associated with the input file.
	 */
	public void addFile(
			ArchiveOutputStream out, 
			String path, 
			String base) throws IOException {
		
		String method    = "addFile() - ";
		
		// Check the input file name
		if ((path == null) || (path.trim().equalsIgnoreCase(""))) {
			LOGGER.warn(method
					+ "The input file path is null or not defined.  " 
					+ "skipping file processing.");
			return;
		}
	
		File file = new File(path);
		
		// Ensure the file exists
		if (!file.exists()) {
			LOGGER.warn(method 
					+ "Input file does not exist on the file system.  " 
					+ "File requested:  "
					+ path
					+ "  Skipping file processing.");
			return;
		}
		// Ensure the file can be processed
		if (!(file.isFile() || file.isDirectory())) {
			LOGGER.warn(method
					+ "An unknown file was encountered.  "
					+ path 
					+ " was not identified as either a regular file or "
					+ " a directory.  Skipping file processing.");
			return;
		}
		
		// Start the entry
		String entryName = base + file.getName();
		out.putArchiveEntry(getArchiveEntry(file, entryName));
		
		// If the input file is an actual file, add it to the output
		// stream.
		if (file.isFile()) {
			addOneFile(out, file);
		}
		
		// If the input file is a directory, make a recursive call to get
		// to the actual files
		else if (file.isDirectory()) {
			
			// Close the current entry
			out.closeArchiveEntry();
			
			// Attempt to process children
			File[] children = file.listFiles();
			if ((children != null) && (children.length > 0)) {
				for (File child : children) {
					addFile(out, 
							child.getAbsolutePath(), 
							entryName + File.separator);
				}
			}
			else {
				LOGGER.warn(method 
						+ "Target directory does not contain any files.  "
						+ "Current directory: " 
						+ path);
			}
		}
	}
	
	/**
	 * This method is used to calculate the entry path to be added to the
	 * output archive.  This class will also enforce the requirement that 
	 * entry paths cannot exceed 100 characters.
	 * 
	 * @param path The absolute path to the file that will be added to the
	 * archive file.
	 * @param baseDir The base directory.
	 * @return The entry path for the target file.
	 */
	public static String getEntryPath(String targetPath, String baseDir) {

		if ((baseDir == null) || (baseDir.isEmpty())) {
			return targetPath;
		}
		
		// find common path
    	String[] target = targetPath.split(Pattern.quote(File.separator));
    	String[] base = baseDir.split(Pattern.quote(File.separator));

    	String common = "";
    	int commonIndex = 0;
    	for (int i = 0; i < target.length && i < base.length; i++) {
    		if (target[i].equals(base[i])) {
    			common += target[i] + File.separator;
    			commonIndex++;
    		}
    	}
    	
    	String relative = "";
    	// is the target a child directory of the base directory?
    	// i.e., target = /a/b/c/d, base = /a/b/
    	if (commonIndex == base.length) {
    		relative = targetPath.substring(common.length());
    		// relative = "." + File.separator + targetPath.substring(common.length());
    	}
    	else {
    		// determine how many directories we have to backtrack
    		for (int i = 1; i <= commonIndex; i++) {
    			relative += "";
    			//relative += ".." + File.separator;
    		}
    		relative += targetPath.substring(common.length());
    	}

    	return relative;
	}
	
	/**
	 * Ensure the output archive file contains the proper file extension 
	 * based on the archive type being created. 
	 * 
	 * @param outputFile The name of the output file that will be created.
	 * @param extension The extension the output file needs to receive.
	 * @return An output filename with the correct extension.
	 */
	protected String setArchiveName(String outputFile, String extension) {
		StringBuilder sb = new StringBuilder();
		sb.append(outputFile);
		if (!outputFile.endsWith(extension)) {
			if (!outputFile.endsWith(".")) {
				sb.append(".");
			}
			sb.append(extension);
		}
		this._archiveName = sb.toString();
		return this._archiveName;
	}
	
	/**
	 * Getter method for the archive name.  This was added as a convenience 
	 * method for testing.  The archive filename will not be available until 
	 * after one of the <code>bundle</code> methods is called.
	 * 
	 * @return The archive name created by the bundler.
	 */
	public String getArchiveName() {
		return this._archiveName;
	}
	
	/**
	 * Subclasses must provide a mechanism for creating the appropriate 
	 * object of type ArchiveEntry.
	 * 
	 * @param file The file that will be added to the Archive.
	 * @param name The name (full path)
	 * @return A concrete ArchiveEntry object (ZipArchiveEntry 
	 * or TarArchiveEntry)
	 */
	public abstract ArchiveEntry getArchiveEntry(File file, String name);
	
}
