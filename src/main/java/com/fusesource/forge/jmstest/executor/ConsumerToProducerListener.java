package com.fusesource.forge.jmstest.executor;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;

public class ConsumerToProducerListener implements MessageListener, Releaseable {

	private JMSConnectionProvider connectionProvider;
	private JMSDestinationProvider destinationProvider;
    private Connection conn;
    private Session session;
    private MessageConsumer consumer;

    private BenchmarkNotificationPayload waiter;
    private boolean isWaiting = false;

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
		return ReleaseManager.getInstance();
	}

	public void registerWaiter(BenchmarkNotificationPayload payload) {
        this.waiter = payload;
        this.isWaiting = true;
    }

    public void initialise() {
    	release();
        log().debug("ConsumerToProducerListener Initialising");
        getReleaseManager().register(this);
        try {
            conn = getConnectionProvider().getConnection();
            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String fromConsumerDestName = BenchmarkContext.getInstance().getTestrunConfig().getAdminFromConsumer();
            Destination fromConsumerDest = getDestinationProvider().getDestination(session, fromConsumerDestName);      
            consumer = session.createConsumer(fromConsumerDest);
            consumer.setMessageListener(this);
            conn.start();
            log().debug("<<< Initialising");
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to initialise JMS connection", e);
        }
    }

    public void onMessage(Message message) {
        log().debug("Message received");
        if (message instanceof ObjectMessage) {
            try {
                Serializable contents = ((ObjectMessage)message).getObject();
                if (contents instanceof BenchmarkNotificationPayload) {
                    BenchmarkNotificationPayload payload = (BenchmarkNotificationPayload) contents;
                    if (this.waiter != null && this.waiter.equals(payload)) {
                        this.waiter.setClientConfigured(true);
                    }
                    //TODO there is probably a better way of doing this....
                    synchronized (waiter) {
                        this.isWaiting = false;
                        waiter.notifyAll();
                    }
                } else {
                    throw new BenchmarkConfigurationException("This is wrong, initialisation message does not carry BenchmarkNotificationPayload as payload");
                }
            } catch (JMSException e) {
                log.warn("Unable to extract object from message", e);
            }
        } else {
            throw new BenchmarkConfigurationException("This is wrong, initialisation message does not carry BenchmarkNotificationPayload as payload");
        }
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    public void release() {
        log().trace(">>> ConsumerToProducerListener#release");
        getReleaseManager().deregister(this);
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
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            // failed to close, just log it
            log().warn("Error on releasing JMS resources [consumerSession]", e);
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
        log().trace("<<< ProducerToConsumerListener#release");
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public BenchmarkNotificationPayload takeClientReply() {
        BenchmarkNotificationPayload reply;
        synchronized (waiter) {
            reply = waiter;
            waiter = null;
        }
        return reply;
    }
}