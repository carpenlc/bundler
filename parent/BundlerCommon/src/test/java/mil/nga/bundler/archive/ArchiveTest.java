package mil.nga.bundler.archive;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import mil.nga.bundler.archive.Archiver;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

public class ArchiveTest {
	
	public static final String DIR_TO_ARCHIVE       = "dir_to_archive";
	public static final String CHILD_DIR_TO_ARCHIVE = "child_dir_to_archive";
	public static final String FILES_TO_ARCHIVE     = "files_to_archive";
	public static String _tempDir        = null;
	public static String _dirToArchive   = null;
	public static String _filesToArchive = null;
	public static String _childDir       = null;
	
	public static final long TEMP_FILE_SIZE = 1024L;
	
	/**
	 * The initialization method will set up some on-disk files to be 
	 * compressed by the different archiver/compressor plugins.
	 */
	@BeforeClass
	public static void init() throws IOException {
		
		ArchiveTest._tempDir = System.getProperty("java.io.tmpdir");
		
		// Create the directory to compress
		StringBuilder sb = new StringBuilder();
		sb.append(ArchiveTest._tempDir);
		if (!ArchiveTest._tempDir.endsWith(File.separator)) {
			sb.append(File.separator);
		}
		sb.append(DIR_TO_ARCHIVE);
		ArchiveTest._dirToArchive = sb.toString();
		File file = new File(ArchiveTest._dirToArchive);
		file.mkdir();
		
		// Make some files in that directory
		File file1 = new File(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file1.txt");
		file1.createNewFile();
		File file2 = new File(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file2.txt");
		file2.createNewFile();
		File file3 = new File(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file3.txt");
		file3.createNewFile();
		
		
		RandomAccessFile raf1 = new RandomAccessFile(file1, "rw");
		raf1.setLength(TEMP_FILE_SIZE);
		raf1.close();
		RandomAccessFile raf2 = new RandomAccessFile(file2, "rw");
		raf2.setLength(TEMP_FILE_SIZE);
		raf2.close();
		RandomAccessFile raf3 = new RandomAccessFile(file3, "rw");
		raf3.setLength(TEMP_FILE_SIZE);
		raf3.close();
		
		
		// Create the child directory
		sb.append(File.separator);
		sb.append(CHILD_DIR_TO_ARCHIVE);
		file = new File(sb.toString());
		file.mkdir();
		
		// Make some files in the Child directory
		File file4 = new File(
				sb.toString() + 
				File.separator +
				"file4.txt");
		file4.createNewFile();
		File file5 = new File(
				sb.toString() + 
				File.separator +
				"file5.txt");
		file5.createNewFile();
		File file6 = new File(
				sb.toString() + 
				File.separator +
				"file6.txt");
		file6.createNewFile();
		
		RandomAccessFile raf4 = new RandomAccessFile(file4, "rw");
		raf4.setLength(TEMP_FILE_SIZE);
		raf4.close();
		RandomAccessFile raf5 = new RandomAccessFile(file5, "rw");
		raf5.setLength(TEMP_FILE_SIZE);
		raf5.close();
		RandomAccessFile raf6 = new RandomAccessFile(file6, "rw");
		raf6.setLength(TEMP_FILE_SIZE);
		raf6.close();
		
	}
	
	/**
	 * Test the Archive.getEntryPath method.
	 */
	@Test 
	public void testRelativePaths() {
		// Windows version of test
		//String path1     = "C:\\a\\b\\c\\d\\e\\f\\g\\h\\i\\file1.txt";
		//String path2     = "C:\\j\\k\\l\\m\\n\\o\\file2.txt";
		//String base      = "C:\\a\\b";
		// Linux version of test
	    String path1     = "/mnt/dir1/dir2/dir3/dir4/file1.txt";
	    String path2     = "/mnt/dir2/dir3/dir4/file2.txt";
	    String base      = "/mnt/dir1/dir2";
		String relative1 = Archiver.getEntryPath(path1, base);
		String relative2 = Archiver.getEntryPath(path2, base);
		assertEquals("dir3/dir4/file1.txt", relative1);
		assertEquals("dir2/dir3/dir4/file2.txt", relative2);
	}
	
	/**
	 * Construct a list of files based on those file created above.
	 * @return Populated List
	 */
	protected List<String> getFileList() {
		List<String> list = new ArrayList<String>();
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file1.txt");
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file2.txt");
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				"file3.txt");
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				CHILD_DIR_TO_ARCHIVE +
				File.separator +
				"file4.txt");
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				CHILD_DIR_TO_ARCHIVE +
				File.separator +
				"file5.txt");
		list.add(
				ArchiveTest._dirToArchive + 
				File.separator +
				CHILD_DIR_TO_ARCHIVE +
				File.separator +
				"file6.txt");
		return list;
	}
}
