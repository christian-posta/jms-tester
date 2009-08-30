package com.fusesource.forge.jmstest.executor;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkExecutionException;
import com.fusesource.forge.jmstest.message.MessageFactory;
import com.fusesource.forge.jmstest.probe.CountingProbe;

public class BenchmarkProducer extends AbstractJMSClientComponent {
    private transient Log log;
    
    private MessageProducer messageProducer;
    private CountingProbe messageCounter;
    
    private long msgNumber = 0;

    public BenchmarkProducer(AbstractBenchmarkJMSClient container) {
    	super(container);
    }

	public MessageFactory getMessageFactory() {
		return getContainer().getMessageFactory();
	}

    public void setMessageCounter(CountingProbe messageCounter) {
        this.messageCounter = messageCounter;
    }

    public void start() throws BenchmarkExecutionException {
        try {
        	super.prepare();
            String destName = getContainer().getPartConfig().getTestDestinationName();
            Destination destination = getDestinationProvider().getDestination(getSession(), destName);
            messageProducer = getSession().createProducer(destination);
        } catch (Exception e) {
            throw new BenchmarkExecutionException("Unable to connect to JMS Provider", e);
        }
    }

    public void sendMessage() {
        try {
            Message msg = getMessageFactory().createMessage(getSession());
            msg.setLongProperty("SendTime", System.currentTimeMillis());
            messageProducer.send(msg, getContainer().getPartConfig().getDeliveryMode().getCode(), 4, 0);
            if (messageCounter != null) {
            	messageCounter.increment();
            	msgNumber = messageCounter.getValue().longValue();
            } else {
            	msgNumber++;
            }
            msg.setLongProperty("MessageNumber", msgNumber);
            if (getContainer().getPartConfig().isTransacted()) {
                getSession().commit();
            }
        } catch (Exception e) {
            //TODO attempt to reinitialise producer
            getBenchmarkStatus().setState(BenchmarkRunStatus.State.FAILED);
            log().warn("Failed to send message, entire benchmark will NOW FAIL!", e);
        }
    }

    public void release() {
        log().debug("Releasing Producer for client: " + getContainer().getClientId().toString());
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

        super.release();
    }

    private Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
