package com.fusesource.forge.jmstest.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultDestinationProvider;
import com.fusesource.forge.jmstest.message.DefaultMessageFactory;
import com.fusesource.forge.jmstest.message.MessageFactory;


/**
 * @author  andreasgies
 */
public class ProducerFactory {

    private static final transient Log LOG = LogFactory.getLog(ProducerFactory.class);

    /**
	 * @uml.property  name="numProducers"
	 */
    private int numProducers = 1;
    private long msgToSend = -1;
    /**
	 * @uml.property  name="deliveryMode"
	 */
    private int deliveryMode = DeliveryMode.PERSISTENT;
    /**
	 * @uml.property  name="destinationName"
	 */
    private String destinationName = "TEST";
    /**
	 * @uml.property  name="sleep"
	 */
    private long sleep = 0L;
    
    /**
	 * @uml.property  name="conProvider"
	 * @uml.associationEnd  
	 */
    private JMSConnectionProvider conProvider = null;
    /**
	 * @uml.property  name="destProvider"
	 * @uml.associationEnd  
	 */
    private JMSDestinationProvider destProvider = null;
    /**
	 * @uml.property  name="msgFactory"
	 * @uml.associationEnd  
	 */
    private MessageFactory msgFactory = null;
    
    /**
	 * @uml.property  name="name"
	 */
    private String name = "Default";
    private long ttl = 0L;

    private AtomicLong sendCounter = null;
    private AtomicLong exceptionCounter = null;
    
    private Map<String, AsyncProducer> producers = null;
    private int counter = 0;
    
    public ProducerFactory() {
    	producers = new HashMap<String, AsyncProducer>();
		destProvider = new DefaultDestinationProvider();
		msgFactory = new DefaultMessageFactory();
	}
    
    /**
	 * @param numProducers
	 * @uml.property  name="numProducers"
	 */
    public void setNumProducers(int numProducers) {
    	this.numProducers = numProducers;
    }
    
    /**
	 * @return
	 * @uml.property  name="numProducers"
	 */
    public int getNumProducers() {
    	return numProducers;
    }
    
    public void setTTL(final long ttl) {
        this.ttl = ttl;    	
    }
    
    public long getTTL() {
    	return this.ttl;
    }
    
    /**
	 * @return
	 * @uml.property  name="deliveryMode"
	 */
    public int getDeliveryMode() {
    	return deliveryMode;
    }
    
    /**
	 * @param newMode
	 * @uml.property  name="deliveryMode"
	 */
    public void setDeliveryMode(final int newMode) {
    	deliveryMode = newMode;
    }
    
    /**
	 * @param name
	 * @uml.property  name="destinationName"
	 */
    public void setDestinationName(final String name) {
    	destinationName = name;
    }
    
    /**
	 * @return
	 * @uml.property  name="destinationName"
	 */
    public String getDestinationName() {
    	return destinationName;
    }
    
	/**
	 * @param sleep
	 * @uml.property  name="sleep"
	 */
	public void setSleep(final long sleep) {
    	this.sleep = sleep;
    }
    
	/**
	 * @return
	 * @uml.property  name="sleep"
	 */
	public long getSleep() {
		return sleep;
	}

    public void setMessagesToSend(long msgToSend) {
    	this.msgToSend = msgToSend;
    }
    
    public long getMessagesToSend() {
    	return msgToSend;
    }

	/**
	 * @return
	 * @uml.property  name="conProvider"
	 */
	public JMSConnectionProvider getConProvider() {
		return conProvider;
	}

	/**
	 * @param conProvider
	 * @uml.property  name="conProvider"
	 */
	public void setConProvider(JMSConnectionProvider conProvider) {
		this.conProvider = conProvider;
	}

	/**
	 * @return
	 * @uml.property  name="destProvider"
	 */
	public JMSDestinationProvider getDestProvider() {
		return destProvider;
	}

	/**
	 * @param destProvider
	 * @uml.property  name="destProvider"
	 */
	public void setDestProvider(JMSDestinationProvider destProvider) {
		this.destProvider = destProvider;
	}

	/**
	 * @return
	 * @uml.property  name="msgFactory"
	 */
	public MessageFactory getMsgFactory() {
		return msgFactory;
	}

	/**
	 * @param msgFactory
	 * @uml.property  name="msgFactory"
	 */
	public void setMsgFactory(MessageFactory msgFactory) {
		this.msgFactory = msgFactory;
	}

	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void start() {
		for(int i=0; i<getNumProducers(); i++) {
			startNewProducer();
		}
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					try {
						synchronized (producers) {
							if (producers.size() == 0) {
								break;
							}
							producers.wait();
						}
					} catch (InterruptedException ie) {}
				}
			}
		}).start();
	}
	
	public void stop() {
		while (producers.size() > 0) {
			String name = producers.keySet().iterator().next();
			AsyncProducer ap = producers.get(name);
			ap.stop();
		}
	}
	
    public void startNewProducer() {
    	AsyncProducer ap = new AsyncProducer(); 
    	String apName = getName() + "-" + (counter++);
    	ap.setName(apName);
    	ap.start();
    }
    
    /**
	 * @author  andreasgies
	 */
    class AsyncProducer implements Runnable {
    	
    	private Thread myThread = null;
    	private AtomicBoolean done = new AtomicBoolean(false);
    	private long locallySent = 0L;
    	/**
		 * @uml.property  name="name"
		 */
    	private String name = null;
    	
    	/**
		 * @param name
		 * @uml.property  name="name"
		 */
    	public void setName(String name) {
    		this.name = name;
    	}
    	
    	/**
		 * @return
		 * @uml.property  name="name"
		 */
    	public String getName() {
    		return name;
    	}
    	
        public void start() {
			if (myThread == null) {
				myThread = new Thread(this, getName());
				myThread.start();
			}
			synchronized (producers) {
		    	producers.put(getName(), this);
			}
		}
          
        public Thread getThread() {
			return myThread;
		}
          
        public void run() {

			Connection conn = null;

			try {
				conn = conProvider.getConnection();
				Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(getDestProvider().getDestination(session, getDestinationName()));
				producer.setDeliveryMode(getDeliveryMode());

				LOG.info("Starting Producer (" + getName() + ")");
				while (!done.get()) {
					try {
						Message msg = msgFactory.createMessage(session);
						producer.send(msg, deliveryMode, 4, ttl);
						sendCounter.incrementAndGet();
						done.set((getMessagesToSend() > 0) && ((++locallySent) == getMessagesToSend()));
						if (sleep > 0L) {
							try {
								Thread.sleep(sleep);
							} catch (InterruptedException ie) {
							}
						}
					} catch (JMSException e) {
						exceptionCounter.incrementAndGet();
					}
				}
			} catch (Exception e) {
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (Throwable e) {
				}
			}
			LOG.info("Producer (" + getName() + ") done");
			synchronized (producers) {
				producers.remove(getName());
				producers.notifyAll();
			}
		}

		public void stop() {
			done.set(true);
		}
	}
}
