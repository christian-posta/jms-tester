package com.fusesource.forge.jmstest.benchmark.command;

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

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.executor.Releaseable;

public class JMSCommandTransport implements MessageListener, Releaseable, ExceptionListener {
	
	private AbstractBenchmarkExecutor benchmarkController;
	
	private Connection connection = null;
	private Session session = null;
	private Boolean started = false;

	private Log log = null;

	public JMSCommandTransport(AbstractBenchmarkExecutor benchmarkController) {
		this.benchmarkController = benchmarkController;
	}
	
	public void start() {
		synchronized (started) {
			if (started) {
				return;
			}
			log().info("JMSCommandTransport (re)starting.");
			while (!started) {
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
					} catch (InterruptedException e) {}
				}
			}
			log().info("JMSCommandTransport started.");
		}
	}
	
	public void sendMessage(BenchmarkCommand command) {
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
			getBenchmarkController().handleCommand((BenchmarkCommand)obj);
		} catch (JMSException je) {
			log().error("Unexpected error processing command message", je);
		}
	}
	
	public void onException(JMSException je) {
		resetConnection();
	}
	
	public void release() {
		closeConnection();
	}
	
	public AbstractBenchmarkExecutor getBenchmarkController() {
		return benchmarkController;
	}

	public void setBenchmarkController(AbstractBenchmarkExecutor benchmarkController) {
		this.benchmarkController = benchmarkController;
	}

	public JMSConnectionProvider getJmsConnectionProvider() {
		return getBenchmarkController().getJmsConnectionProvider();
	}

	public JMSDestinationProvider getJmsDestinationProvider() {
		return getBenchmarkController().getJmsDestinationProvider();
	}

	public String getDestinationName() {
		return getBenchmarkController().getDestinationName();
	}

	private void closeConnection() {
		synchronized (started) {
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
