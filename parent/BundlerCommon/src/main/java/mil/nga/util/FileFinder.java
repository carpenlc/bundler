package mil.nga.util;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This class implements logic that works much like the UNIX "find" command.
 * Clients must supply a starting path location and a pattern to match.  
 * This class will then walk through the file tree looking for files that
 * match the input pattern.
 * 
 * Note: This class will only work in Java 1.7 or above.
 * 
 * @author L. Craig Carpenter
 */
public class FileFinder {

    /**
     * Default search pattern.
     */
    public static final String DEFAULT_PATTERN = "*";
    
	/**
	 * Execute a search on the filesystem for files that match the input 
	 * pattern.
	 * 
	 * @param path The starting location for the search.
	 * @param pattern The file pattern to look for.
	 * @exception IOException Thrown during the search process.
	 */
	public static List<Path> find(String path, String pattern) 
			throws IOException {
		
		Path start = null;
		
		if ((path == null) || (path.isEmpty())) {
			start = Paths.get("");
		}
		else {
			start = Paths.get(path);
		}
		
		Finder finder = new Finder(pattern);
		Files.walkFileTree(start, finder);
		return finder.getResults();
	}
	
	/**
     * This method accepts as input a starting directorty name.  It then walks
     * through the filesystem returning a list of all regular files (omitting 
     * directories.)
     * 
     * @param path The starting location for the search.
     * @exception IOException Thrown during the search process.
     */
	public static List<String> find(String path) throws IOException {
	   
	    List<String> files = new ArrayList<String>();
	    if ((path == null) || (path.isEmpty())) { 
	        path = "";
	    }
	    Finder finder = new Finder("*");
	    Files.walkFileTree(Paths.get(path), finder);
	    List<Path> paths = finder.getResults();
	    if ((paths != null) && (!paths.isEmpty())) { 
	        for (Path p : paths) {
	            files.add(p.toAbsolutePath().toString());
	        }
	    }
	    
	    return files;
	    
	}
	
	/**
	 * Internal class that extends the SimpleFileVisitor class that implements
	 * the actual search.
	 * 
	 * @author carpenlc
	 *
	 */
	public static class Finder extends SimpleFileVisitor<Path> {
		
		/**
		 * Internal PathMatcher object.
		 */
		private final PathMatcher _matcher;
		
		/**
		 * Accumulator saving the list of matches found on the file system.
		 */
		private List<Path> _matches = null;
		
		/**
		 * Constructor setting up the search.
		 * 
		 * @param pattern The global search pattern to utilize for the search.
		 * @throws IOException Thrown if the client-supplied pattern is not
		 * defined.
		 */
		public Finder(String pattern) throws IOException {
			if ((pattern == null) || (pattern.isEmpty())) {
				pattern = DEFAULT_PATTERN;
			}
			_matcher = FileSystems.getDefault().getPathMatcher(
						"glob:" + pattern);
		}
		
        /** 
         * Compares the glob pattern against the file and/or directory name.
         * 
         * @param file The file to perform the comparison against.
         */
        public void find(Path file) {
            Path name = file.getFileName();
            if ((name != null) 
                    && (_matcher.matches(name)
                            && (!Files.isDirectory(file)))) {
                if (_matches == null) {
                	_matches = new ArrayList<Path>();
                }
            	_matches.add(file);
            }
        }
        
        /**
         * Accessor method for the results of the search.
         * 
         * @return Any results that were accumulated during the search 
         * (may be null). 
         */
        public List<Path> getResults() {
        	return _matches;
        }
        
        /**
         * Invoke the pattern matching method on each directory in the file 
         * tree.
         */
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attrs) {
            find(dir);
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * Invoke the pattern matching method on each file in the file tree.
         */
        @Override
        public FileVisitResult visitFile(
        		Path file,
                BasicFileAttributes attrs) {
            find(file);
            return FileVisitResult.CONTINUE;
        }
        
        /**
         * If the file visit failed issue an informational message to System.err
         */
        @Override
        public FileVisitResult visitFileFailed(Path file,
                IOException exc) {
            System.err.println("WARN:  Find command failed visiting file.  " 
            		+ "Error message [ " 
            		+ exc.getMessage()
            		+ " ].");
            return FileVisitResult.CONTINUE;
        }
	}
	
	
	public static void main(String[] args) {
	    
	    
	    
	    
	    
	    try {
    	    List<Path> list = FileFinder.find("/mnt/raster/cadrg/cdrgxpk100k_7", "*");
    	    if ((list != null) && (!list.isEmpty())) {
    	        for (Path path : list) {
    	            System.out.println(path.toAbsolutePath());
    	        }
    	    }
    	    
    	    List<String> strings = FileFinder.find("/mnt/raster/cadrg/cdrgxpk100k_7");
            for (String path : strings) {
                System.out.println(path);
            }
	    }
	    catch (IOException ioe) {
	        ioe.printStackTrace();
	    }
	}
}
