package com.fusesource.forge.jmstest.executor;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkExecutionException;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.results.ProducerMetricCollector;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.message.MessageFactory;

public class BenchmarkProducer extends ExecutableBenchmarkComponent {
    private transient Log log;
    
    private JMSConnectionProvider connectionProvider;
    private JMSDestinationProvider destinationProvider;
    private MessageFactory messageFactory;
    private ReleaseManager releaseManager;
    
    private Connection conn;
    private Session session;
    private MessageProducer messageProducer;

    private ProducerMetricCollector messageCounter;

    public BenchmarkProducer() {
    }

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
		return releaseManager;
	}

	public void setReleaseManager(ReleaseManager releaseManager) {
		this.releaseManager = releaseManager;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

    public void setMessageCounter(ProducerMetricCollector messageCounter) {
        this.messageCounter = messageCounter;
    }

    public void configure(TestRunConfig testRunConfig, boolean reinitialiseSession) {
        if (reinitialiseSession) {
            initialise(testRunConfig);
        }
    }

    public void initialise(TestRunConfig testRunConfig) throws BenchmarkExecutionException {
    	setTestRunConfig(testRunConfig);
        getReleaseManager().register(this);
        try {
            conn = getConnectionProvider().getConnection();
            session = conn.createSession(getTestRunConfig().isTransacted(), getTestRunConfig().getAcknowledgeMode().getCode());
            String destName = getTestRunConfig().getTestDestinationName();
            Destination destination = getDestinationProvider().getDestination(session, destName);
            messageProducer = session.createProducer(destination);
        } catch (Exception e) {
            throw new BenchmarkExecutionException("Unable to connect to JMS Provider", e);
        }
    }

    public void sendMessage() {
        try {
            Message msg = getMessageFactory().createMessage(session);
            if (BenchmarkRunStatus.State.OK.equals(getBenchmarkStatus().getCurrentState())) {
                msg.setLongProperty("SendTime", System.currentTimeMillis());
                msg.setLongProperty("MessageNumber", messageCounter.increment());
                messageProducer.send(msg, getTestRunConfig().getDeliveryMode().getCode(), 4, 0);
                if (getTestRunConfig().isTransacted()) {
                    session.commit();
                }
            } else {
                log().warn("Ignoring send call, benchmark not OK!");
            }
        } catch (Exception e) {
            //TODO attempt to reinitialise producer
            getBenchmarkStatus().setState(BenchmarkRunStatus.State.FAILED);
            log().warn("Failed to send message, entire benchmark will NOW FAIL!", e);
        }
    }

    public void release() {
        log().trace(">>> BenchmarkProducer#release");
        //TODO refactor into base class
        getReleaseManager().deregister(this);
        getBenchmarkStatus().deleteObserver(this);
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
        log().trace("<<< BenchmarkProducer#release");
    }

    private Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
