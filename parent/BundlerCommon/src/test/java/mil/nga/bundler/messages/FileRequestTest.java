package mil.nga.bundler.messages;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test creation, setting, reading, serializing the FileRequest POJO.
 * 
 * @author L. Craig Carpenter
 */
public class FileRequestTest {

	public static String FILE_PATH    = "/some/long/path/to/file.txt";
	public static String ARCHIVE_PATH = "/arbitrary/path/in/archive/file.txt";
	
	@Test
	public void testCreation() {
		System.out.println("[TEST] Testing building objects of type class mil.nga.bundler.FileRequest...");
		FileRequest request = new FileRequest.FileRequestBuilder()
									.file(FILE_PATH)
									.archivePath(ARCHIVE_PATH)
									.build();
		assertEquals(request.getFile(), FILE_PATH);
		assertEquals(request.getArchivePath(), ARCHIVE_PATH);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testInvalidCreation() {
		System.out.println("[TEST] Testing building INVALID objects of type class mil.nga.bundler.FileRequest...");
		FileRequest request = new FileRequest.FileRequestBuilder()
				.file("")
				.archivePath(ARCHIVE_PATH)
				.build();
	}
	
	@Test
	public void testDeSerialization() {
		System.out.println("[TEST] Testing de-serialization of class mil.nga.bundler.FileRequest...");
		String startingJSON = "{\"file\":\"/some/long/path/to/file.txt\","
				+ "\"archive_path\":\"/arbitrary/path/in/archive/file.txt\"}";
		FileRequest deserialized = BundlerMessageSerializer
						.getInstance()
						.deserializeToFileRequest(startingJSON);
		
		assertEquals(deserialized.getFile(), FILE_PATH);
		assertEquals(deserialized.getArchivePath(), ARCHIVE_PATH);
		
	}
	
	@Test
	public void testSerialization() {
		System.out.println("[TEST] Testing serialization of class mil.nga.bundler.FileRequest...");
		FileRequest request = new FileRequest.FileRequestBuilder()
				.file(FILE_PATH)
				.archivePath(ARCHIVE_PATH)
				.build();
		String json = BundlerMessageSerializer.getInstance().serialize(request);
		FileRequest deserialized = BundlerMessageSerializer
				.getInstance()
				.deserializeToFileRequest(json);

		assertEquals(deserialized.getFile(), FILE_PATH);
		assertEquals(deserialized.getArchivePath(), ARCHIVE_PATH);
	}
}
