package mil.nga.bundler.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.exceptions.ArchiveException;
import mil.nga.bundler.model.FileEntry;
import mil.nga.bundler.types.ArchiveType;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of the Bundler class that will handle creation 
 * of an output compressed BZip2 archive.  This class is used in conjunction 
 * with the TAR archiver.  First, the TAR archive is created, then the output 
 * TAR archive is run through BZip2 compressor.  
 * 
 * This compressor seems to be ever-so-slightly slower than the other compression
 * algorithms, but the output has better compression.
 * 
 * @author carpenlc
 */
public class BZip2Archiver 
		extends Compressor implements BundlerI {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	Logger LOGGER = LoggerFactory.getLogger(BZip2Archiver.class);
	
	/** 
	 * The archive type handled by this class
	 */
	private ArchiveType _type = ArchiveType.BZIP2;
	
	/**
	 * Default constructor
	 */
	public BZip2Archiver() { }
	
	/**
	 * Compress the data contained in the input file using the BZip2 
	 * compression algorithms storing the compressed data in the file
	 * specified by the outputFile parameter. 
	 * 
	 * @param inputFile The input TAR archive
	 * @param outputFile The compressed output file.
	 */
	@Override
	public void compress(File inputFile, File outputFile) 
			throws IOException {
		
		BufferedInputStream         bIn     = null;
		BZip2CompressorOutputStream bzOut   = null;
		
		try {
			// Create the input stream
			bIn = new BufferedInputStream(
					new FileInputStream(inputFile));
			
			// Create the output stream
			bzOut = new BZip2CompressorOutputStream(
					new FileOutputStream(outputFile));
			
			// Pipe the input stream to the output stream
			compress(bIn, bzOut);
		}
		finally {
			if (bzOut != null) {
				try { bzOut.close(); } catch (Exception e) {}
			}
			if (bIn != null) {
				try { bIn.close(); } catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Bundle the files that exist in the input directory.   The files will
	 * be bundled in a single TAR archive, then compressed using the BZip2 
	 * algorithm.
	 * 
	 * @param directory The directory to be archived
	 * @param outputFile Full path of the output archive file (may or may not
	 * include the extension)
	 * @throws IOException Raised if there are issues constructing the output
	 * archive.
	 */
	@Override
	public void bundle(String directory, String outputFile) 
			throws ArchiveException, IOException {
		
		String method  = "bundle() - ";
		String tarName = null;
		File   tarFile = null;

		LOGGER.info(method + "Creating intermediate TAR file.");
		super.bundle(directory, outputFile);
		
		tarName = super.getArchiveName();
		tarFile = new File(tarName);
		
		if (tarFile.exists()) {
			
			LOGGER.info(method 
					+ "Intermediate TAR file created successfully.  File "
					+ "created:  " 
					+ tarFile.getAbsolutePath()
					+ "Creating output BZip2 file.");
			compress(tarFile, this._type);
			
		}
		else {
			LOGGER.error(method 
					+ "The intermediate TAR file could not be created.  "
					+ "Unable to construct the output BZip2 file.");
		}
	}
	
	/**
	 * Bundle each file in the input list.  Each entry in the list should 
	 * be the full path to file that should be added to the archive.  The 
	 * files will be bundled in a single TAR archive, then compressed using 
	 * the BZip2 algorithm.
	 * 
	 * @param files List of files (full path) to be added to the archive.
	 * @param outputFile Full path of the output archive file (may or may not
	 * include the extension)
	 * @throws IOException Raised if there are issues constructing the output
	 * archive.
	 */
	public void bundle(List<String> files, String outputFile, String baseDir) 
			throws ArchiveException, IOException { 
		
		String method  = "bundle() - ";
		String tarName = null;
		File   tarFile = null;

		LOGGER.info(method + "Creating intermediate TAR file.");
		super.bundle(files, outputFile, baseDir);
		
		tarName = super.getArchiveName();
		tarFile = new File(tarName);
		
		if (tarFile.exists()) {
			LOGGER.info(method 
					+ "Intermediate TAR file created successfully.  File "
					+ "created:  " 
					+ tarFile.getAbsolutePath()
					+ "Creating output BZip2 file.");
			compress(tarFile, this._type);
		}
		else {
			LOGGER.error(method 
					+ "The intermediate TAR file could not be created.  "
					+ "Unable to construct the output BZip2 file.");
		}
	}
	
	@Override
	public void bundle(List<FileEntry> files, String outputFile) 
			throws ArchiveException, IOException {
	
		String method  = "bundle() - ";
		String tarName = null;
		File   tarFile = null;
		
		LOGGER.info(method + "Creating intermediate TAR file.");
		super.bundle(files, outputFile);
		
		tarName = super.getArchiveName();
		tarFile = new File(tarName);
		
		if (tarFile.exists()) {
			LOGGER.info(method 
					+ "Intermediate TAR file created successfully.  File "
					+ "created:  " 
					+ tarFile.getAbsolutePath()
					+ "Creating output BZip2 file.");
			compress(tarFile, this._type);
		}
		else {
			LOGGER.error(method 
					+ "The intermediate TAR file could not be created.  "
					+ "Unable to construct the output BZip2 file.");
		}
		
	}
}
