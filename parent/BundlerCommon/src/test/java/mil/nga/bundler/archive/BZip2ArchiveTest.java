package mil.nga.bundler.archive;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mil.nga.bundler.archive.ArchiveFactory;
import mil.nga.bundler.types.ArchiveType;
import mil.nga.bundler.interfaces.BundlerI;
import mil.nga.bundler.exceptions.ArchiveException;
import mil.nga.bundler.exceptions.UnknownArchiveTypeException;

import org.junit.Test;

public class BZip2ArchiveTest extends ArchiveTest {

	public String _archiveFilename1 = "bzip2_archive_1";
	public String _archiveFilename2 = "bzip2_archive_2";
	
	/**
	 * This method tests that the GZIP archiver can archive a directory 
	 * and all files contained within, maintaining directory integrity.
	 * 
	 * @throws ArchiveException Thrown if there are problems validating the
	 * input data.  Exceptions will fail the test.
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
			
			BundlerI bundler = factory.getInstance(ArchiveType.BZIP2);
			bundler.bundle(ArchiveTest._dirToArchive, this._archiveFilename1);
			File archive = new File(bundler.getArchiveName());
			System.out.println(bundler.getArchiveName());
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
	 * This method tests that the GZIP archiver can archive a list of input
	 * files.
	 * 
	 * @throws ArchiveException Thrown if there are problems validating the
	 * input data.  Exceptions will fail the test.
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
			
			BundlerI bundler = factory.getInstance(ArchiveType.BZIP2);
			bundler.bundle(list, this._archiveFilename2, null);
			File archive = new File(bundler.getArchiveName());
			System.out.println(bundler.getArchiveName());
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
