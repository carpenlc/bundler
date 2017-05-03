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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of the Bundler class that will handle creation 
 * of an output compressed ZIP archive.  
 *  
 * @author L. Craig Carpenter
 */
public class ZipArchiver extends Archiver implements BundlerI {

    /**
     * Set up the Log4j system for use throughout the class
     */        
    Logger LOGGER = LoggerFactory.getLogger(ZipArchiver.class);
    
    /** 
     * The archive type handled by this class
     */
    private ArchiveType _type = ArchiveType.ZIP;
    
    /**
     * Default constructor
     * @param tracker Handle to the external job tracker assigned to this 
     * archive job (may be null)
     */
    public ZipArchiver() { }
    
    /**
     * Subclasses must provide a mechanism for creating the appropriate 
     * object of type ArchiveEntry.
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
                    + "Adding a non-existant file to the target ZIP archive.");
        }
        return new ZipArchiveEntry(file, name);
    }
    

    @Override
    public void bundle(String directory, String outputFile) 
            throws ArchiveException, IOException {
        
        String                 method = "bundle() - ";
        ZipArchiveOutputStream zaos = null;
        
        File dir = new File(directory);
        if (!dir.exists()) {
            LOGGER.error(method 
                    + "Directory identified for archiving does not exist.  "
                    + "Directory identified:  "
                    + directory);
            return;
        }
        if (!dir.isDirectory()) {
            LOGGER.warn(method 
                    + "Expecting directory as input.  Received single file.");
        }
        
        try {
            
            // Get the actual name of the archive file            
            setArchiveName(outputFile, this._type.getText());
                    
            // Create the output stream zoo
            zaos = new ZipArchiveOutputStream(
                            new BufferedOutputStream(
                                    new FileOutputStream(
                                            getArchiveName())));
            
            // Invoke superclass methods to create the output archive 
            addFile((ArchiveOutputStream)zaos, directory, "");
            
        }
        finally {
            if (zaos != null) {
                try {    
                    zaos.finish();
                    zaos.close();
                }
                catch (Exception e) { 
                    LOGGER.warn(method 
                            + "Uknown exception raised while trying to close "
                            + "the ZipArchiveOutputStream object.");
                }
            }
        }
    }
    
    /**
     * @param baseDir (optional) Specifies that all files will fall under a 
     * base directory.  This parameter is used in the creation of the relative
     * file paths contained within the output archive.
     */
    @Override
    public void bundle(
            List<String> files, 
            String       outputFile,
            String       baseDir) 
            throws ArchiveException, IOException {
        
        String                 method = "bundle() - ";
        ZipArchiveOutputStream zaos   = null;
        
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
            zaos = new ZipArchiveOutputStream(
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
                    zaos.putArchiveEntry(getArchiveEntry(file, name));
                    super.addOneFile(zaos, file);

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
            if (zaos != null) {
                try {    
                    zaos.finish();
                    zaos.close();
                }
                catch (Exception e) { 
                    LOGGER.warn(method 
                            + "Uknown exception raised while trying to close "
                            + "the ZipArchiveOutputStream object.");
                }
            }
        }
    }

    /**
     * 
     */
    @Override
    public void bundle(List<FileEntry> files, String outputFile) 
            throws ArchiveException, IOException {
        
        String                 method = "bundle() - ";
        ZipArchiveOutputStream zaos   = null;
        
        if ((files == null) || (files.size() == 0)) {
            String msg = "No files were identified for archiving.  "
                + "The output archive file will not be created.";
            LOGGER.error(method + msg);
            throw new ArchiveException(msg);
        }
        
        try {
                    
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Creating ZIP output file [ "
                        + outputFile
                        + " ].");
            }
            // Create the output stream zoo
            zaos = new ZipArchiveOutputStream(
                    new BufferedOutputStream(
                        new FileOutputStream(outputFile)));
            
            // Loop through each file in the input list
            for (FileEntry entry : files) {
                
                File file = new File(entry.getFilePath());
                if (file.exists()) {
                    
                    zaos.putArchiveEntry(
                            getArchiveEntry(
                                    file, 
                                    entry.getEntryPath()));
                    
                    super.addOneFile(zaos, file);
                    entry.setFileState(JobStateType.COMPLETE);
                    
                }
                else {
                    LOGGER.warn(method 
                            + "File identified for inclusion in the output "
                            + "archive file does not exist.  File requested [ "
                            + file.getAbsolutePath()
                            + " ].");
                }
            }
        }
        finally {
            if (zaos != null) {
                try {    
                    zaos.finish();
                    zaos.close();
                }
                catch (Exception e) { 
                    LOGGER.warn(method 
                            + "Uknown exception raised while trying to close "
                            + "the ZipArchiveOutputStream object.");
                }
            }
        }
    }
}
