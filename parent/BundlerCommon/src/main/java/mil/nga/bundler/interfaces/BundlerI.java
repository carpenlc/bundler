package mil.nga.bundler.interfaces;

import java.io.IOException;
import java.util.List;

import mil.nga.bundler.exceptions.ArchiveException;
import mil.nga.bundler.model.FileEntry;

/**
 * Interface enforced by all of the archive/compressor classes.
 * 
 * @author L. Craig Carpenter
 */
public interface BundlerI {

    /**
     * Convenience method implemented for testing.  It will return the name
     * of the output archive file as created by the bundler.  The output 
     * archive filename *may* be different than what was input to the bundler.
     * 
     * @return The full path of the archive file created by the bundler.
     */
    public String getArchiveName();
    
    /**
     * Bundle the files that exist in the input directory.   The files will
     * be bundled in accordance with the archive type supported by the 
     * concrete implementing class (i.e. ZIP, TAR, etc.)
     * 
     * @param directory The directory to be archived
     * @param outputFile Full path of the output archive file (may or may not
     * include the extension)
     * @throws IOException Raised if there are issues constructing the output
     * archive.
     */
    public void bundle(String directory, String outputFile) 
            throws ArchiveException, IOException;
    
    /**
     * Bundle each file in the input list.  Each entry in the list should 
     * be the full path to file that should be added to the archive.  The files 
     * will be bundled in accordance with the archive type supported by the 
     * concrete implementing class (i.e. ZIP, TAR, etc.)
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
    public void bundle(List<String> files, String outputFile, String baseDir) 
            throws ArchiveException, IOException;
    
    public void bundle(List<FileEntry> files, String outputFile)
            throws ArchiveException, IOException;
    
}
