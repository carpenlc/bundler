package mil.nga;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import mil.nga.bundler.exceptions.PropertiesNotLoadedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple class used to load a Properties file from the classpath and 
 * provide static access to those properties.  This class was meant to 
 * replace the ridiculously complex "Config" classes that were over
 * engineered.
 * 
 * @author L. Craig Carpenter
 */
// TODO:  Fix this issue...no matter what I tried I could not figure 
// out how to deploy a properties file inside an Enterprise Archive (EAR)
// that was readable by all of the applications contained in that EAR.
// Next up was to create a module containing properties files as documented
// by several blogs, however, that didn't work either.  We ended up 
// implementing just reading from an external file.
public class PropertyLoader {

	/**
	 * Set up the Log4j system for use throughout the class
	 */		
	static final Logger LOGGER = LoggerFactory.getLogger(
			PropertyLoader.class);
	
	/**
	 * Static system properties object
	 */
	private static Properties properties = null;
	
	/**
	 * Default property file name.
	 */
	public static final String DEFAULT_PROPERTY_FILE_NAME = "system.properties";
	
	/**
	 * The name of the property file to load.
	 */
	private String propertyFileName = null;
	
	/**
	 * Default constructor allowing that uses the default properties file.
	 */
	public PropertyLoader() {
		setPropertyFileName(DEFAULT_PROPERTY_FILE_NAME);
	}
	
	/**
	 * Alternate constructor allowing clients to supply the name of the target
	 * properties file.
	 * @param propertyFileName The name of the property file to load.
	 * @throws PropertiesNotLoadedException Thrown if the target properties file
	 * was not loaded.
	 */
	public PropertyLoader(String propertyFileName) {
		setPropertyFileName(propertyFileName);
	}
	
	/**
	 * Load the target properties file from the classpath.
	 * @throws PropertiesNotLoadedException Thrown if the target properties 
	 * file was not loaded.
	 */
	private void loadProperties() throws PropertiesNotLoadedException {
		
		InputStream stream = null;
		LOGGER.info("Initiating load of properties file [ "
				+ getPropertyFileName()
				+ " ].");
		try {
			//stream = PropertyLoader.class
			//		.getClassLoader()
			//		.getResourceAsStream(getPropertyFileName());
			//stream = ClassLoader.getSystemResourceAsStream(getPropertyFileName());
			stream = new FileInputStream(new File(getPropertyFileName()));
			if (properties == null) {
				properties = new Properties();
			}
			properties.load(stream);
			
		}
		catch (FileNotFoundException fnfe) {
			String msg = "Unexpected FileNotFoundException raised while "
					+ "attempting to load the target properties file.  "
					+ "Missing file [ "
					+ getPropertyFileName() 
					+ " ], exception message [ "
					+ fnfe.getMessage()
					+ " ].";
			LOGGER.error(msg);
			throw new PropertiesNotLoadedException(msg);
		}
		catch (IOException ioe) {
			String msg = "Unexpected IOException raised while "
					+ "attempting to load the target properties file.  "
					+ "Target properties file [ "
					+ getPropertyFileName() 
					+ " ], exception message [ "
					+ ioe.getMessage()
					+ " ].";
			LOGGER.error(msg);
			throw new PropertiesNotLoadedException(msg);
		}
		finally {
			if (stream != null) {
				try { stream.close(); } catch (Exception e) {}
			}
		}
		
		
	}
	
	/**
	 * Getter method for the name of the target properties file.
	 * @return The name of the target properties file.
	 */
	public String getPropertyFileName() {
		if ((propertyFileName == null) || (propertyFileName.isEmpty())) {
			propertyFileName = DEFAULT_PROPERTY_FILE_NAME;
		}
		return propertyFileName;
	}
	
	/**
	 * Getter method for the system properties.
	 * @return The populated system properties object. 
	 * @throws PropertiesNotLoadedException Thrown if the target properties 
	 * file was not loaded.
	 */
	public Properties getProperties() 
			throws PropertiesNotLoadedException {
		if (properties == null) {
			loadProperties();
		}
		return properties;
	}
	
	/**
	 * Getter method for a single property.
	 * @param key The key of the property to look up.
	 * @throws PropertiesNotLoadedException Thrown if the target properties 
	 * file was not loaded.
	 */
	public String getProperty(String key) 
			throws PropertiesNotLoadedException {
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(key);
	}
	
	/**
	 * Getter method for a single property.
	 * @param key The key of the property to look up.
	 * @param value The default value for the key.
	 * @throws PropertiesNotLoadedException Thrown if the target properties 
	 * file was not loaded.
	 */
	public String getProperty(String key, String value) 
			throws PropertiesNotLoadedException {
		if (properties == null) {
			loadProperties();
		}
		return properties.getProperty(key, value);
	}
	
	/**
	 * Setter method for the name of the target properties file.
	 * @param value The name of the target properties file.
	 */
	public void setPropertyFileName(String value) {
		if ((value == null) || (value.isEmpty())) {
			LOGGER.warn("Null or empty name supplied for property file.  "
					+ "Using the default name [ "
					+ DEFAULT_PROPERTY_FILE_NAME
					+ " ].");
			propertyFileName = DEFAULT_PROPERTY_FILE_NAME;
		}
		propertyFileName = value;
	}
	
	/**
	 * Construct a String representation of the input system 
	 * properties.  If the properties are not populated, a String 
	 * containing "NULL" is returned.
	 */
	@Override
	public String toString() {
		
		String newLine = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		
		if ((properties == null) || (properties.isEmpty())) {
			sb.append("NULL");
		}
		else {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				String key   = (String)entry.getKey();
				String value = (String)entry.getValue();
				sb.append("Key => [ ");
				sb.append(key);
				sb.append(" ], ");
				sb.append("Value => [ ");
				sb.append(value);
				sb.append(" ]");
				sb.append(newLine);
			}
		}
		return sb.toString();
	}
}
