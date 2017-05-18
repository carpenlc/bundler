package mil.nga.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Filename filter implementing a case-insensitive regular expression 
 * search for files.
 * 
 * @author L. Craig Carpenter
 */
public class CaseInsensitiveDirFilter implements FilenameFilter {

    /**
     * Pattern matcher
     */
    private Pattern pattern;

    /**
     * Default constructor requiring clients to supply a populated 
     * regular expression.
     * 
     * @param regex The regular expression to apply.
     */
    public CaseInsensitiveDirFilter(String regex) {
        pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Tests if a specified file should be included in a file list.
     * 
     * @param dir The directory in which the file was found.
     * @param name The name of the file.
     * @return true if and only if the name should be included in the file 
     * list; false otherwise.
     */
    public boolean accept(File dir, String name) {
        return pattern.matcher(new File(name).getName()).matches();
    }
}
