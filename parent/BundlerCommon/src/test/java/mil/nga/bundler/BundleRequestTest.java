package mil.nga.bundler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.messages.BundlerMessageSerializer;
import mil.nga.bundler.types.ArchiveType;

public class BundleRequestTest {

    public static final String TEST_OUTPUT_FILENAME = "file_archive";
    public static final String TEST_USERNAME        = "Bob Marley";
    public static final String TEST_FILE_PATH       = "/some/long/path/to/file.txt";
    public static final String TEST_FILE_PATH2      = "/some/long/path/to/file2.txt";
    public static final String TEST_FILE_PATH3      = "/some/long/path/to/file3.txt";    
    public static final String TEST_FILE_PATH4      = "/some/long/path/to/file4.txt";    
    public static final String TEST_FILE_PATH5      = "/some/long/path/to/file5.txt";
    
    @Test
    public void testCreation() {
        
        System.out.println("[TEST] Testing building objects of type class mil.nga.bundler.BundleRequest...");
        BundleRequest request = new BundleRequest.BundleRequestBuilder()
                                    .maxSize(123)
                                    .type(ArchiveType.TAR)
                                    .redirect(true)
                                    .outputFilename(TEST_OUTPUT_FILENAME)
                                    .userName(TEST_USERNAME)
                                    .build();
        request.add(TEST_FILE_PATH);
        request.add(TEST_FILE_PATH2);
        request.add(TEST_FILE_PATH3);
        request.add(TEST_FILE_PATH4);
        request.add(TEST_FILE_PATH5);
        
        assertEquals(request.getMaxSize(), 123);
        assertEquals(request.getRedirect(), true);
        assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
        assertEquals(request.getUserName(), TEST_USERNAME);
        assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
        
        // Test creation with out-of-range values
        BundleRequest request2 = new BundleRequest.BundleRequestBuilder()
                .maxSize(2400)
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
        System.out.println("[TEST] Testing serialization of class mil.nga.bundler.BundleRequest...");
        BundleRequest request = new BundleRequest.BundleRequestBuilder()
                                    .maxSize(123)
                                    .type(ArchiveType.TAR)
                                    .redirect(true)
                                    .outputFilename(TEST_OUTPUT_FILENAME)
                                    .userName(TEST_USERNAME)
                                    .build();
        request.add(TEST_FILE_PATH);
        request.add(TEST_FILE_PATH2);
        request.add(TEST_FILE_PATH3);
        request.add(TEST_FILE_PATH4);
        request.add(TEST_FILE_PATH5);
        
        assertEquals(request.getMaxSize(), 123);
        assertEquals(request.getRedirect(), true);
        assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
        assertEquals(request.getUserName(), TEST_USERNAME);
        assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
        
        String json = BundlerMessageSerializer.getInstance().serialize(request);
        assertNotNull(json);
        
        BundleRequest request2 = new BundleRequest.BundleRequestBuilder()
                .maxSize(2400)
                .redirect(true)
                .build();
        request2.add(TEST_FILE_PATH);
        request2.add(TEST_FILE_PATH2);
        request2.add(TEST_FILE_PATH3);
        request2.add(TEST_FILE_PATH4);
        request2.add(TEST_FILE_PATH5);
        String json2 = BundlerMessageSerializer.getInstance().serialize(request2);
        assertNotNull(json2);
        System.out.println(json2);
    }
    
    
    @Test
    public void testDeSerialization() {
        System.out.println("[TEST] Testing de-serialization of class mil.nga.bundler.BundleRequest...");
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\"redirect\":true,");
        sb.append("\"max_size\":400,");
        sb.append("\"archive_file\":\"");
        sb.append(TEST_OUTPUT_FILENAME);
        sb.append("\",");
        sb.append("\"type\":\"ZIP\",\"user_name\":\"");
        sb.append(TEST_USERNAME);
        sb.append("\",\"files\":[");
        sb.append("\"/some/long/path/to/file.txt\",");
        sb.append("\"/some/long/path/to/file2.txt\",");
        sb.append("\"/some/long/path/to/file3.txt\",");
        sb.append("\"/some/long/path/to/file4.txt\",");
        sb.append("\"/some/long/path/to/file5.txt\"]}");
            
        System.out.println(sb.toString());
        
        BundleRequest request = BundlerMessageSerializer.getInstance()
                .deserializeToBundleRequest(sb.toString());

        assertEquals(request.getMaxSize(), 400);
        assertEquals(request.getRedirect(), true);
        assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
        assertEquals(request.getUserName(), TEST_USERNAME);
        assertEquals(request.getType().getText(), ArchiveType.ZIP.getText());
        assertEquals(request.getFiles().size(), 5);

        System.out.println("[TEST] Testing de-serialization of class mil.nga.bundler.BundleRequest (String version)...");
        
        sb = new StringBuilder();
        sb.append("{\"redirect\":true,");
        sb.append("\"max_size\":\"400\",");
        sb.append("\"archive_file\":\"");
        sb.append(TEST_OUTPUT_FILENAME);
        sb.append("\",");
        sb.append("\"type\":\"TAR\",\"user_name\":\"");
        sb.append(TEST_USERNAME);
        sb.append("\",\"files\":[");
        sb.append("\"/some/long/path/to/file.txt\",");
        sb.append("\"/some/long/path/to/file2.txt\",");
        sb.append("\"/some/long/path/to/file3.txt\",");
        sb.append("\"/some/long/path/to/file4.txt\",");
        sb.append("\"/some/long/path/to/file5.txt\"]}");

        request = BundlerMessageSerializer.getInstance()
                                    .deserializeToBundleRequest(sb.toString());
        
        assertEquals(request.getMaxSize(), 400);
        assertEquals(request.getRedirect(), true);
        assertEquals(request.getOutputFilename(), TEST_OUTPUT_FILENAME);
        assertEquals(request.getUserName(), TEST_USERNAME);
        assertEquals(request.getType().getText(), ArchiveType.TAR.getText());
        assertEquals(request.getFiles().size(), 5);
    }
    

}
