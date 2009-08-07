package com.fusesource.forge.jmstest.tests.simple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.executor.JMSTest;
import com.fusesource.forge.jmstest.tests.AsyncClient;
import com.fusesource.forge.jmstest.tests.AsyncConsumer;
import com.fusesource.forge.jmstest.tests.AsyncProducer;

public abstract class AbstractNetworkOfBrokersTest extends JMSTest {


	protected static final Log LOG = LogFactory.getLog(SystemaTestExternalBrokers.class);
    
    protected final static int INTERVAL      = 60; //seconds
    protected final static int NUM_CONSUMERS = 5;
    protected final static int NUM_PRODUCERS = 3;
	private static final int NUM_SWAPS = 10;
    
    private AtomicLong sendCount = null;
    private AtomicLong timeOutCount = null;
    
    private AtomicInteger consumerSeq = null;
    private AtomicInteger activeConsumers = null;
    
    private Map<String, JMSConnectionProvider> connProviders = null;
    
    @BeforeTest
    public void prepareTest() {
       sendCount = new AtomicLong(0L);
       timeOutCount = new  AtomicLong(0L);
       
       activeConsumers = new AtomicInteger(0);
       consumerSeq = new AtomicInteger(0);
    }
    
    protected List<AsyncProducer> createProducers(JMSConnectionProvider connProvider, String name) throws Exception {

		List<AsyncProducer> result = new ArrayList<AsyncProducer>();
		ObjectFactory factory = (ObjectFactory) (applicationContext.getBean("AsyncProducerFactory"));

		for (int i = 0; i < NUM_PRODUCERS; i++) {
			AsyncProducer producer = (AsyncProducer) factory.getObject();
			producer.setConnectionProvider(connProvider);
			producer.setTimeOuts(timeOutCount);
			producer.setSent(sendCount);
			producer.setName("Producer-" + name + "-" + i);
			result.add(producer);
			producer.start();
		}
		return result;
	}

    protected void closeProducers(List<AsyncProducer> producers, CountDownLatch latch) {
    	for(Iterator<AsyncProducer> it=producers.iterator(); it.hasNext();) {
			AsyncClient c = it.next();
			c.stop();
			if (latch != null) {
				latch.countDown();
			}
    	}
    }

    protected final Map<String, JMSConnectionProvider> getConnectionProviders() {
    	if (connProviders == null) {
    		connProviders = doGetConnectionProviders();
    	}
    	return connProviders;
    }
    
    abstract protected Map<String, JMSConnectionProvider> doGetConnectionProviders();

    protected void consumerSwap(String fromBroker, String toBroker, long startTime) {
    	
    	JMSConnectionProvider fromConsumer = getConnectionProviders().get(fromBroker);
    	JMSConnectionProvider toConsumer = getConnectionProviders().get(toBroker);
    	
    	Assert.assertNotNull(fromConsumer);
    	Assert.assertNotNull(toConsumer);
    	
    	LOG.debug("Scheduling Consumer Swap from " + fromBroker + " to " + toBroker + " in " + startTime + "s");
    	ConsumerRunner crFrom = new ConsumerRunner(
    		"ReplyGrp-" + consumerSeq.incrementAndGet(),
    		applicationContext,
    		NUM_CONSUMERS,
    		fromConsumer,
    		2 * INTERVAL ,
    		activeConsumers
    	);
    	
    	ConsumerRunner crTo = new ConsumerRunner(
    		"ReplyGrp-" + consumerSeq.incrementAndGet(),
    		applicationContext,
    		NUM_CONSUMERS,
    		toConsumer,
    		2 * INTERVAL ,
    		activeConsumers
    	);
    	
        new ScheduledThreadPoolExecutor(1).schedule(crFrom, startTime, TimeUnit.SECONDS);
        new ScheduledThreadPoolExecutor(1).schedule(crTo, startTime + INTERVAL , TimeUnit.SECONDS);
    }
    
    @Test
    public void testApplicationSwap() {
    	
    	try {
	    	Map<String, JMSConnectionProvider> connProviders = getConnectionProviders();
	    	
	    	String brokerNames[] = connProviders.keySet().toArray(new String[] {});
	    	Random rnd = new Random(System.currentTimeMillis()); 
	    		
	    	LOG.info("======== Starting Test =========" );
	    	
	    	for(int i=0; i<NUM_SWAPS; i++) {
	    		int fromIndex = rnd.nextInt(brokerNames.length);
	    		int toIndex = fromIndex;
	    		do {
	    			toIndex = rnd.nextInt(brokerNames.length);
	    		} while (fromIndex == toIndex);
	    		int delay = 0;
	    		if (i == 0) {
	    			delay = 0;
	    		} else {
	    		  delay = rnd.nextInt(INTERVAL * 2 - 10); // make sure there is always a consumer
	    		}
		    	consumerSwap(brokerNames[fromIndex], brokerNames[toIndex], delay);
	    	}
	    	
	    	// start the requestors
    		Vector<List<AsyncProducer>> producers = new Vector<List<AsyncProducer>>();
	    	for(String brokerName: brokerNames) {
	    		producers.add(createProducers(getConnectionProviders().get(brokerName), brokerName));
	    	}

    	    while (true) {
    	    	synchronized (activeConsumers) {
    	    		activeConsumers.wait();
    	    		if (activeConsumers.get() == 0) {
    	    			break;
    	    		}
				}
    	    }

    	    // Don't count time outs during shutdown
    	    LOG.info("Time Outs: " + timeOutCount.get());
        	Assert.assertTrue(timeOutCount.get() < NUM_CONSUMERS * NUM_SWAPS); // just a rough estimate
    	    
        	for(List<AsyncProducer> producer: producers) {
            	closeProducers(producer, null);
        	}
        	
    	} catch (Exception e) {
    	  e.printStackTrace();
    	  Assert.fail(e.getMessage());
    	}
    }
    
    class ConsumerRunner implements Runnable {
    	
    	private String name;
    	private ApplicationContext appCtxt;
    	private int numConsumers;
    	private JMSConnectionProvider connProvider;
    	private int duration;
    	private AtomicInteger consumerCounter;
    	
    	private List<AsyncConsumer> consumers = null;
    	
        public ConsumerRunner(String name, ApplicationContext appCtxt, int numConsumers, JMSConnectionProvider connProvider, int duration, AtomicInteger consumerCounter) {
    		this.numConsumers = numConsumers;
    		this.appCtxt = appCtxt;
    		this.connProvider = connProvider;
    		this.duration = duration;
    		this.consumerCounter = consumerCounter;
    		this.name = name;
    	}

        private void closeConsumers(List<AsyncConsumer> consumers, CountDownLatch latch) {
        	for(Iterator<AsyncConsumer> it=consumers.iterator(); it.hasNext();) {
    			AsyncClient c = it.next();
    			c.stop();
    			if (latch != null) {
    				latch.countDown();
    			}
        	}
        	consumers.clear();
        }
        
        private void createConsumers() throws Exception {

        	if (consumers == null) {
        		consumers = new ArrayList<AsyncConsumer>();

        		ObjectFactory consumerFactory = (ObjectFactory) (appCtxt.getBean("AsyncConsumerFactory"));
	
	    		for (int i = 0; i < numConsumers; i++) {
	    			AsyncConsumer consumer = (AsyncConsumer) consumerFactory.getObject();
	    			consumer.setConnectionProvider(connProvider);
	    			consumer.setName(name + "-" + i);
	    			consumers.add(consumer);
	    			consumer.start();
	    		}
        	}
    	}
        
    	public void run() {
    		try {
        		LOG.info("===== Replier Grp " + name + " starting =====");
    			consumerCounter.incrementAndGet();
    			createConsumers();
    			final CountDownLatch latch = new CountDownLatch(numConsumers);
        	    new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
        	    	public void run() {
        	    		try {
        	    			LOG.info("======= Shuting down Replier Grp " + name + " ===========");
        	    			closeConsumers(consumers, latch);
        	    		} catch (Exception e) {}
        	    	}
        	    }, duration, TimeUnit.SECONDS);
        	    latch.await();
    		} catch (Exception e) {
    		} finally {
    			synchronized (consumerCounter) {
        			consumerCounter.decrementAndGet();
        			consumerCounter.notifyAll();
				}
    		}
    		LOG.info("===== Replier Grp " + name + " finished =====");
    	}
    }

}
