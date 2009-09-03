package com.fusesource.forge.jmstest.benchmark.command.transport;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.executor.ReleaseManager;
import com.fusesource.forge.jmstest.executor.Releaseable;

public class JMSCommandTransport extends AbstractCommandTransport implements MessageListener, Releaseable, ExceptionListener {
	
	private JMSConnectionProvider jmsConnectionProvider;
	private JMSDestinationProvider jmsDestinationProvider;
	private String destinationName = "topic:benchmark.command";
	
	private Connection connection = null;
	private Session session = null;
	private Boolean started = false;
	private Boolean released = false;

	private Object lock = new Object();
	
	private Log log = null;

	public void start() {
		synchronized (lock) {
			if (started) {
				return;
			}
			ReleaseManager.getInstance().register(this);
			log().info("JMSCommandTransport (re)starting.");
			while (!started && !released) {
				if (getConnection() != null) {
					try {
						getConnection().setExceptionListener(this);
						session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
						MessageConsumer consumer = session.createConsumer(getJmsDestinationProvider().getDestination(session,getDestinationName()));
						consumer.setMessageListener(this);
						getConnection().start();
						started = true;
					} catch (Exception e) {
						log().error("Error creating JMSCommandTransport.", e);
						resetConnection();
					}
				}
				if (!started) {
					try {
						log.warn("Trying to reinitialize JMSTransport Connection in 5 seconds.");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
				}
			}
			if (started) {
				log().info("JMSCommandTransport started.");
			}
		}
	}
	
	public void stop() {
		release();
	}
	
	public void sendCommand(BenchmarkCommand command) {
		log.debug("Sending command message: " + command.toString());
		if (getConnection() != null) {
			try {
				Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination dest = getJmsDestinationProvider().getDestination(session, getDestinationName());
				MessageProducer producer = session.createProducer(dest);
				producer.send(session.createObjectMessage((Serializable)command));
				session.close();
			} catch (Exception je) {
				log().error("Could not send Command " + command.toString() + ".", je);
			}
		} else {
			log.warn("Command not sent as JMS connection is not established.");
		}
	}
	
	public void onMessage(Message msg) {
		try {
			if (!(msg instanceof ObjectMessage)) {
				log().warn("Ignoring msg of type: " + msg.getClass());
			}
			Object obj = ((ObjectMessage)msg).getObject();
			if (!(obj instanceof BenchmarkCommand)) {
				log().warn("Ignoring msg object of type: " + obj.getClass());
			}
			getHandler().handleCommand((BenchmarkCommand)obj);
		} catch (JMSException je) {
			log().error("Unexpected error processing command message", je);
		}
	}
	
	public void onException(JMSException je) {
		resetConnection();
	}
	
	public void release() {
		closeConnection();
		released = true;
		ReleaseManager.getInstance().deregister(this);
	}
	
	public void setJmsConnectionProvider(JMSConnectionProvider jmsConnectionProvider) {
		this.jmsConnectionProvider = jmsConnectionProvider;
	}

	public void setJmsDestinationProvider(
		JMSDestinationProvider jmsDestinationProvider) {
		this.jmsDestinationProvider = jmsDestinationProvider;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public JMSConnectionProvider getJmsConnectionProvider() {
		return jmsConnectionProvider;
	}

	public JMSDestinationProvider getJmsDestinationProvider() {
		return jmsDestinationProvider;
	}

	public String getDestinationName() {
		return destinationName;
	}

	synchronized private void closeConnection() {
		if (!started) {
			return;
		}

		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
				log().error("Error closing Session.", e);
			} finally {
				session = null;
			}
		}
		
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				log().error("Error closing connection.", e);
			} finally {
				connection = null;
			}
		}
		started = false;
	}

	private void resetConnection() {
		closeConnection();
		start();
	}
	
	private Connection getConnection() {
		if (connection == null) {
			try {
				connection = getJmsConnectionProvider().getConnection();
			} catch (Exception e) {
				log().error("Unable to connect to JMS Provider" , e);
			}
		}
		return connection;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
