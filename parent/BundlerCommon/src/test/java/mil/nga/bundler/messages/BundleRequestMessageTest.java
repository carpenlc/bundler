package mil.nga.bundler.messages;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.types.ArchiveType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test creation, setting, reading, serializing the BundleRequest POJO.
 * 
 * @author L. Craig Carpenter
 */
public class BundleRequestMessageTest {

	public static final String TEST_OUTPUT_FILENAME = "file_archive";
	public static final String TEST_USERNAME        = "Bob Marley";
	public static final String TEST_FILE_PATH       = "/some/long/path/to/file.txt";
	public static final String TEST_ARCHIVE_PATH    = "/arbitrary/path/in/archive/file.txt";
	public static final String TEST_FILE_PATH2      = "/some/long/path/to/file2.txt";
	public static final String TEST_ARCHIVE_PATH2   = "/arbitrary/path/in/archive/file2.txt";
	
	@Test
	public void testCreation() {
		
		System.out.println("[TEST] Testing building objects of type class mil.nga.bundler.message.BundleRequestMessage...");
		BundleRequestMessage request = new BundleRequestMessage.BundleRequestMessageBuilder()
									.maxSize(123)
									.type(ArchiveType.TAR)
									.redirect(true)
									.outputFilename(TEST_OUTPUT_FILENAME)
									.userName(TEST_USERNAME)
									.build();
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH)
						.archivePath(TEST_ARCHIVE_PATH)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH2)
						.archivePath(TEST_ARCHIVE_PATH2)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH2)
						.build());
		
		assertEquals(request.getMaxSize(), 123);
		assertEquals(request.getRedirect(), true);
		assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
		assertEquals(request.getUserName(), TEST_USERNAME);
		assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
		
		// Test creation with out-of-range values
		BundleRequestMessage request2 = new BundleRequestMessage.BundleRequestMessageBuilder()
				.maxSize(1200)
				.redirect(true)
				.build();
		assertEquals(request2.getMaxSize(), BundlerConstantsI.DEFAULT_MAX_ARCHIVE_SIZE);
		assertEquals(request2.getRedirect(), true);
		assertEquals(request2.getOutputFilename(), "nga_data_archive");
		assertEquals(request2.getUserName(), BundlerConstantsI.DEFAULT_USERNAME);
		assertEquals(request2.getType().getText(), ArchiveType.ZIP.getText());
		
		
	}
	
	@Test
	public void testSerialization() {
		System.out.println("[TEST] Testing serialization of class mil.nga.bundler.message.BundleRequestMessage...");
		BundleRequestMessage request = new BundleRequestMessage.BundleRequestMessageBuilder()
									.maxSize(123)
									.type(ArchiveType.TAR)
									.redirect(true)
									.outputFilename(TEST_OUTPUT_FILENAME)
									.userName(TEST_USERNAME)
									.build();
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH)
						.archivePath(TEST_ARCHIVE_PATH)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH2)
						.archivePath(TEST_ARCHIVE_PATH2)
						.build());
		request.add(new FileRequest.FileRequestBuilder()
						.file(TEST_FILE_PATH2)
						.build());
		
		assertEquals(request.getMaxSize(), 123);
		assertEquals(request.getRedirect(), true);
		assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
		assertEquals(request.getUserName(), TEST_USERNAME);
		assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
		
		String json = BundlerMessageSerializer.getInstance().serialize(request);
		assertNotNull(json);
		
		BundleRequestMessage request2 = new BundleRequestMessage.BundleRequestMessageBuilder()
				.maxSize(1200)
				.redirect(true)
				.build();
		request2.add(new FileRequest.FileRequestBuilder()
				.file(TEST_FILE_PATH)
				.archivePath(TEST_ARCHIVE_PATH)
				.build());
		request2.add(new FileRequest.FileRequestBuilder()
				.file(TEST_FILE_PATH)
				.build());
		request2.add(new FileRequest.FileRequestBuilder()
				.file(TEST_FILE_PATH2)
				.archivePath(TEST_ARCHIVE_PATH2)
				.build());
		request2.add(new FileRequest.FileRequestBuilder()
				.file(TEST_FILE_PATH2)
				.build());
		String json2 = BundlerMessageSerializer.getInstance().serialize(request2);
		assertNotNull(json2);
	}
	
	@Test
	public void testDeSerialization() {
	    System.out.println("[TEST] Testing de-serialization of class mil.nga.bundler.message.BundleRequestMessage...");
	    
        StringBuilder sb = new StringBuilder();
        sb.append("{\"redirect\":true,\"max_size\":123,");
        sb.append("\"archive_file\":\"file_archive\",\"user_name\":\"Bob Marley\",");
        sb.append("\"type\":\"TAR\",\"files\":[");
        sb.append("{\"file\":\"/some/long/path/to/file.txt\",\"archive_path\":");
        sb.append("\"/arbitrary/path/in/archive/file.txt\"},");
        sb.append("{\"file\":\"/some/long/path/to/file.txt\"},");
        sb.append("{\"file\":\"/some/long/path/to/file2.txt\",\"archive_path\":");
        sb.append("\"/arbitrary/path/in/archive/file2.txt\"},");
        sb.append("{\"file\":\"/some/long/path/to/file2.txt\"}]}");
	        
        BundleRequestMessage request = BundlerMessageSerializer.getInstance()
                .deserializeToBundleRequestMessage(sb.toString());

        assertEquals(request.getMaxSize(), 123);
        assertEquals(request.getRedirect(), true);
        assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
        assertEquals(request.getUserName(), TEST_USERNAME);
        assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
        assertEquals(request.getFiles().size(), 4);

	    System.out.println("[TEST] Testing de-serialization of class mil.nga.bundler.messages.BundleRequestMessage (String version)...");
		
		sb = new StringBuilder();
		sb.append("{\"redirect\":true,\"max_size\":\"123\",");
		sb.append("\"archive_file\":\"file_archive\",\"user_name\":\"Bob Marley\",");
		sb.append("\"type\":\"TAR\",\"files\":[");
		sb.append("{\"file\":\"/some/long/path/to/file.txt\",\"archive_path\":");
		sb.append("\"/arbitrary/path/in/archive/file.txt\"},");
		sb.append("{\"file\":\"/some/long/path/to/file.txt\"},");
		sb.append("{\"file\":\"/some/long/path/to/file2.txt\",\"archive_path\":");
		sb.append("\"/arbitrary/path/in/archive/file2.txt\"},");
		sb.append("{\"file\":\"/some/long/path/to/file2.txt\"}]}");

		request = BundlerMessageSerializer.getInstance()
									.deserializeToBundleRequestMessage(sb.toString());
		
		assertEquals(request.getMaxSize(), 123);
		assertEquals(request.getRedirect(), true);
		assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
		assertEquals(request.getUserName(), TEST_USERNAME);
		assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
		assertEquals(request.getFiles().size(), 4);
	}
	
}
