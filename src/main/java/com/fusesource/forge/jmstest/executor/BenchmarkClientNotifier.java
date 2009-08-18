package com.fusesource.forge.jmstest.executor;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.BenchmarkExecutionException;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.command.TestRunConfig;
import com.fusesource.forge.jmstest.config.AcknowledgeMode;
import com.fusesource.forge.jmstest.config.DeliveryMode;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

/**
 * @author  andreasgies
 */
public class BenchmarkClientNotifier implements Releaseable {

    private transient Log log;

    private JMSConnectionProvider connectionProvider;
    private JMSDestinationProvider destinationProvider;
    	
    private Connection conn;
    private Session session;
    private MessageProducer messageProducer;

    
    public JMSConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public void setConnectionProvider(JMSConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	public JMSDestinationProvider getDestinationProvider() {
		return destinationProvider;
	}

	public void setDestinationProvider(JMSDestinationProvider destinationProvider) {
		this.destinationProvider = destinationProvider;
	}

	public ReleaseManager getReleaseManager() {
		return ReleaseManager.getInstance();
	}

	public void initialise() {
        try {
            getReleaseManager().register(this);
            conn = getConnectionProvider().getConnection();
            session = conn.createSession(false, AcknowledgeMode.AUTO_ACKNOWLEDGE.getCode());
            String destName = BenchmarkContext.getInstance().getTestrunConfig().getAdminFromProducer();
            Destination dest = getDestinationProvider().getDestination(session, destName);
            messageProducer = session.createProducer(dest);
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to connect to JMS Provider", e);
        }
    }

    public BenchmarkNotificationPayload startBenchmark(TestRunConfig runConfig) {
        ObjectMessage jmsMessage;
        BenchmarkNotificationPayload payload;
        try {
            payload = new BenchmarkNotificationPayload(runConfig, false);
            jmsMessage = session.createObjectMessage(payload);
        } catch (JMSException e) {
            throw new BenchmarkExecutionException("Unable to create ObjectMessage", e);
        }
        try {
            messageProducer.send(jmsMessage, DeliveryMode.PERSISTENT.getCode(), 1, 0); //high priority send
        } catch (JMSException e) {
            throw new BenchmarkExecutionException("Failed to send ObjectMessage to start benchmark client", e);
        }
        return payload;
    }

    public void release() {
        log().trace(">>> BenchmarkClientNotifier#release");
        getReleaseManager().deregister(this);
        try {
            if (messageProducer != null) {
                messageProducer.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [messageProducer]", e);
        } finally {
        	messageProducer = null;
        }
        
        try {
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [session]", e);
        } finally {
        	session = null;
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [connection]", e);
        } finally {
        	conn = null;
        }
        
        log().trace("<<< BenchmarkClientNotifier#release");
    }

    private Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    public BenchmarkNotificationPayload endBenchmark(TestRunConfig runConfig) {
        // send message with property "endOfTest"
        ObjectMessage jmsMessage;
        BenchmarkNotificationPayload payload;
        try {
            payload = new BenchmarkNotificationPayload(runConfig, true);
            jmsMessage = session.createObjectMessage(payload);
        } catch (JMSException e) {
            throw new BenchmarkExecutionException("Unable to create ObjectMessage", e);
        }
        try {
            messageProducer.send(jmsMessage, DeliveryMode.PERSISTENT.getCode(), 9, 0); 
        } catch (JMSException e) {
            throw new BenchmarkExecutionException("Failed to send ObjectMessage to start benchmark client", e);
        }
        return payload;
    }
}
