package mil.nga.bundler.archive;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mil.nga.bundler.archive.ArchiveFactory;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;
import mil.nga.bundler.exceptions.ArchiveException;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * jUnit tests for the ZIP archiver.
 * 
 * @author carpenlc
 */
public class ZipArchiverTest extends ArchiveTest {

	public String _archiveFilename1 = "test_archive_1";
	public String _archiveFilename2 = "test_archive_2";

	/**
	 * This method tests that the ZIP archiver can archive a directory 
	 * and all files contained within, maintaining directory integrity.
	 * 
	 * @throws IOException Exception thrown if there are problems writing
	 * the output archive file.  Exceptions will fail the test.
	 */
	@Test
	public void testBundler1() throws ArchiveException, IOException {
	
		// Build the path to the output file
		StringBuilder sb = new StringBuilder();
		sb.append(ArchiveTest._tempDir);
		sb.append(File.separator);
		sb.append(this._archiveFilename1);
		this._archiveFilename1 = sb.toString();
		
		ArchiveFactory factory = ArchiveFactory.getFactory();
		try {
			
			BundlerI bundler = factory.getInstance(ArchiveType.ZIP);
			bundler.bundle(ArchiveTest._dirToArchive, this._archiveFilename1);
			this._archiveFilename1 = bundler.getArchiveName(); 
			System.out.println(this._archiveFilename1);
			File archive = new File(this._archiveFilename1);
			assertTrue(archive.exists());
			double bytes = archive.length();
			System.out.println(bytes);
			
		}
		catch (UnknownArchiveTypeException uae) {
			
			// We *should* never get this exception
			uae.printStackTrace();

		}
	}
	
	
	/**
	 * This method tests that the ZIP archiver can archive each file in 
	 * an input list of files.  
	 * 
	 * @throws IOException Exception thrown if there are problems writing
	 * the output archive file.  Exceptions will fail the test.
	 */
	@Test
	public void testBundler2() throws ArchiveException, IOException {
	
		// Build the path to the output file
		StringBuilder sb = new StringBuilder();
		sb.append(ArchiveTest._tempDir);
		sb.append(File.separator);
		sb.append(this._archiveFilename2);
		this._archiveFilename2 = sb.toString();
		List<String> list = super.getFileList();
		ArchiveFactory factory = ArchiveFactory.getFactory();
		try {
			
			BundlerI bundler = factory.getInstance(ArchiveType.ZIP);
			bundler.bundle(list, this._archiveFilename2, null);
			this._archiveFilename2 = bundler.getArchiveName(); 
			System.out.println(this._archiveFilename2);
			File archive = new File(this._archiveFilename2);
			assertTrue(archive.exists());
			double bytes = archive.length();
			System.out.println(bytes);
			
		}
		catch (UnknownArchiveTypeException uae) {
			
			// We *should* never get this exception
			uae.printStackTrace();

		}
	}
	
}
