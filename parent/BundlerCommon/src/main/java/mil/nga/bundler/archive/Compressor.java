package mil.nga.bundler.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import mil.nga.bundler.types.ArchiveType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Compressor extends TarArchiver {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(Compressor.class);
    
    /**
     * Buffer size to use when creating the output compressed file.
     * TODO: Tweak this value for better performance.
     */
    protected static final int BUFFER_SIZE = 8192;
    
    /**
     * Default constructor
     */
    public Compressor() { }
    
    /**
     * This method takes an InputStream and pipes it through a compressor 
     * algorithm.  The specific algorithm used is determined by subclasses.
     * 
     * @param in BufferedInputStream associated with the input TAR file to
     * be compressed.  
     * @param out Concrete implementation of an output Compressor.
     * @throws IOException Thrown if there are problems with any of processing
     * associated with reading or writing the data in the pipe operation. 
     */
    public void compress(BufferedInputStream in, OutputStream out) 
            throws IOException {
        final byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            out.write(buffer, 0, n);
        }
    }
    
    /**
     * 
     * @param tarFile
     * @param type
     */
    protected void compress(File tarFile, ArchiveType type) throws IOException {
        
        String method         = "compress() - ";
        String compressedName = null;
        File   compressedFile = null;
        
        // Reset the output archive name
        super.setArchiveName(tarFile.getAbsolutePath(), type.getText());
        
        compressedName = super.getArchiveName();
        compressedFile = new File(compressedName);
        
        // Compress the tar file.
        compress(tarFile, compressedFile);

        // Check that the output compressed file was created, then
        // delete the intermediate TAR file.
        if (compressedFile.exists()) {
            LOGGER.info(method 
                    + "Compressed file created successfully.  File created: " 
                    + compressedFile.getAbsolutePath() 
                    + "Deleting the intermediate TAR file.");
            tarFile.delete();
        }
        else {
            LOGGER.error(method
                    + "Unknown error encountered.  Unable to create "
                    + "the output GZip file.");
        }
    }
    
    /**
     * Compress the data contained in the input file using the specified 
     * compression algorithms storing the compressed data in the file
     * specified by the outputFile parameter. 
     * 
     * @param inputFile The input TAR archive
     * @param outputFile The compressed output file.
     */
    public abstract void compress(File inputFile, File outputFile) throws 
            IOException;
}
