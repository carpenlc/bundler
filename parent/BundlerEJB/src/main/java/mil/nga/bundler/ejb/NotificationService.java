package mil.nga.bundler.ejb;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mil.nga.bundler.interfaces.BundlerConstantsI;
import mil.nga.bundler.messages.ArchiveMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Regular Java class containing a single method used by subclasses to send 
 * JMS messages to a target queue.
 * 
 * @author L. Craig Carpenter
 */
public class NotificationService implements BundlerConstantsI {

    /**
     * Set up the Log4j system for use throughout the class
     */
    static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    
    /**
     * Method used to place an ArchiveMessage object onto a target JMS Queue.
     * 
     * @param queue The target JMS queue on which to place the ArchiveMessage.
     * @param archiveMsg Message to send to the cluster for processing.
     */
    public void notify(String queue, ArchiveMessage archiveMsg) {
        
        QueueConnection conn      = null;
        QueueSession    session   = null;
        QueueSender     publisher = null;
        
        if (archiveMsg != null) {
            
            LOGGER.info("Placing message [ "
                    + archiveMsg.toString()
                    + " ] on JMS queue [ "
                    + queue
                    + " ].");
            
            try {
                
                Context ctx = new InitialContext();
                Queue jmsQueue = (Queue)ctx.lookup(queue);
                
                if (jmsQueue != null) {
                    QueueConnectionFactory factory = (QueueConnectionFactory)
                            ctx.lookup(CONNECTION_FACTORY);
                    if (factory != null) {
                        
                        conn = factory.createQueueConnection();
                        session = conn.createQueueSession(
                                false,
                                Session.AUTO_ACKNOWLEDGE);
                    
                        publisher = session.createSender(jmsQueue);
                        conn.start();
                        
                        ObjectMessage message = session.createObjectMessage(archiveMsg);
                        publisher.send(message);
                    }
                    else {
                        LOGGER.error("Unable to look up the JMS Queue "
                                + "Connection factory JNDI.  Connection "
                                + "factory is null.  Name [ "
                                + CONNECTION_FACTORY
                                + " ].");
                    }
                }
                else {
                    LOGGER.error("Unable to look up the JMS destination queue."
                            + "Queue is null.  Name [ "
                            + queue
                            + " ].");
                }
            }
            catch (NamingException ne) {
                 LOGGER.error("Unexpected NamingException encoutered while "
                         + "attempting to obtain references to the JMS "
                         + "queue [ "
                         + queue
                         + " ].  Error message [ "
                         + ne.getMessage()
                         + " ].");
                 LOGGER.error("Unable to send notification associated with job ID [ "
                        + archiveMsg.getJobId()
                        + " ] and ID [ "
                        + archiveMsg.getArchiveId()
                        + " ] is complete.");
            }
            catch (JMSException je) {
                LOGGER.error("Unexpected JMSException encoutered will "
                        + "attempting to send a JMS message.  Error message [ "
                        + je.getMessage()
                        + " ].");
                LOGGER.error("Unable to send notification associated with job ID [ "
                        + archiveMsg.getJobId()
                        + " ] and ID [ "
                        + archiveMsg.getArchiveId()
                        + " ] is complete.");
            }
            finally {
                if (session != null) {
                    try { session.close(); } catch (Exception e) {}
                }
                if (publisher != null) {
                    try { publisher.close(); } catch (Exception e) {}
                }
                if (conn != null) {
                    try { conn.close(); } catch (Exception e) {}
                }
            }
        }
        else {
            LOGGER.error("Client submitted a null archive.  The archive will "
                    + "not be placed on the JMS queue.");
        }
    }
}
