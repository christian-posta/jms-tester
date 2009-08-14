package com.fusesource.forge.jmstest.executor;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.TestRunConfig;

public class ProducerToConsumerListener implements MessageListener, Releaseable {

	private BenchmarkConsumerWrapper consumerWrapper;
	private JMSConnectionProvider connectionProvider;
	private JMSDestinationProvider destinationProvider;
	private ReleaseManager releaseManager;
    private Connection conn;
    private Session consumerSession;
    private Session producerSession;
    private MessageConsumer consumer;
    private MessageProducer producer;

    private transient Log log;

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
		return BenchmarkContext.getInstance().getReleaseManager();
	}

	public BenchmarkConsumerWrapper getConsumerWrapper() {
		return consumerWrapper;
	}

	public void setConsumerWrapper(BenchmarkConsumerWrapper consumerWrapper) {
		this.consumerWrapper = consumerWrapper;
	}

	public void initialize(TestRunConfig testRunConfig) {
    	release();
        log().debug(">>> Initialising ProducerToConsumerListener");
        try {
        	conn = getConnectionProvider().getConnection();
            consumerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String receiveDestName = testRunConfig.getAdminFromProducer();
            Destination receiveDest = getDestinationProvider().getDestination(consumerSession, receiveDestName);
            consumer = consumerSession.createConsumer(receiveDest);
            consumer.setMessageListener(this);
            producerSession = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String producerDestName = testRunConfig.getAdminFromConsumer();
            Destination producerDest = getDestinationProvider().getDestination(producerSession, producerDestName);
            producer = producerSession.createProducer(producerDest);
            conn.start();
            log().debug("<<< ProducerToConsumerListener");
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to initialise JMS connection", e);
        }
    }

    public void onMessage(Message message) {
        log().debug("Message received by ProducerToConsumerListener");
        if (message instanceof ObjectMessage) {
            try {
                Serializable contents = ((ObjectMessage)message).getObject();
                if (contents instanceof BenchmarkNotificationPayload) {
                    BenchmarkNotificationPayload payload = (BenchmarkNotificationPayload) contents;
                    if (payload.isEndOfTest()) {
                        payload.setClientConfigured(consumerWrapper.closedown());
                    } else {
                        payload.setClientConfigured(consumerWrapper.start(payload.getConfig()));
                    }
                    try {
                        log().info("Replying to configuration message [success? " + payload.isClientConfigured() + "]");
                        Message msg = producerSession.createObjectMessage(payload);
                        producer.send(msg, DeliveryMode.PERSISTENT, 1, 0);
                    } catch (JMSException e) {
                        // cater for the surely rare case that we can't reply
                        log().warn("Failed to respond to Producer with configuration complete message, aborting", e);
                        consumerWrapper.closedown();
                    }
                } else {
                    throw new BenchmarkConfigurationException("This is wrong, initialisation message does not carry TestRunConfig as payload");
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        } else {
            throw new BenchmarkConfigurationException("This is wrong, initialisation message does not carry TestRunConfig as payload");
        }
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    public void release() {
        log().trace(">>> ProducerToConsumerListener#release");
        try {
            if (producer != null) {
                producer.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [producer]", e);
        } finally {
        	producer= null;
        }
        
        try {
            if (producerSession != null) {
                producerSession.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [consumerSession]", e);
        } finally {
        	producerSession = null;
        }
        
        try {
            if (consumer != null) {
                consumer.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [consumer]", e);
        } finally {
        	consumer = null; 
        }
        
        try {
            if (consumerSession != null) {
                consumerSession.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [consumerSession]", e);
        } finally {
        	consumerSession = null;
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
        log().trace("<<< ProducerToConsumerListener#release");
    }
}
