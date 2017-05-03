package mil.nga.util;
 
import java.util.*;
import java.util.Hashtable;
import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.*;
 
public class MBeanServiceMonitor {
 
    private static MBeanServerConnection connection;
    private static JMXConnector connector;
 
    public static void connect() throws IOException {
 
        Hashtable h = new Hashtable();
        JMXServiceURL address = new JMXServiceURL("service:jmx:http-remoting-jmx://localhost:9990");
        connector = JMXConnectorFactory.connect(address, null);
        connection = connector.getMBeanServerConnection();
 
        System.out.println("Connected to MBean Server");
    }
 
    private static void listWildFlyMBeans() throws Exception {
 
        ObjectName serviceRef = new ObjectName("*.*:*");
        Set<ObjectName> mbeans = connection.queryNames(serviceRef, null);
        for (ObjectName on : mbeans) {
            System.out.println("\t ObjectName : " + on);
            try {
                printAttributes(on);
            } catch (Exception exc) {
 
            }
 
        }
    }
 
    static void printAttributes(final ObjectName http)
            throws Exception {
        MBeanInfo info = connection.getMBeanInfo(http);
        MBeanAttributeInfo[] attrInfo = info.getAttributes();
        MBeanOperationInfo[] operInfo = info.getOperations();
        System.out.println(">Attributes:");
        for (MBeanAttributeInfo attr : attrInfo) {
            System.out.println("  " + attr.getName() + "\n");
        }
        System.out.println(">Operations:");
        for (MBeanOperationInfo attr : operInfo) {
            System.out.println("  " + attr.getName() + "\n");
        }
    }
 
    public static void main(String[] args) throws Exception {
 
        connect();
        System.out.println("Dump WildFly MBeans \n\n");
        listWildFlyMBeans();
        getAttributeExample();
    }
 
    private static void getAttributeExample() {
        try {
            Object obj = connection.getAttribute(new ObjectName("jboss.as:management-root=server"), "releaseCodename");
            System.out.println(obj);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
