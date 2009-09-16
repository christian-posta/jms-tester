package com.fusesource.forge.jmstest.executor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.benchmark.command.ProducerFinishedCommand;
import com.fusesource.forge.jmstest.probe.CountingProbe;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class BenchmarkJMSProducerWrapper extends AbstractBenchmarkJMSClient implements Runnable {

    private transient Log log;

    private ScheduledThreadPoolExecutor executor;
    BenchmarkIteration iteration = null;
    private List<BenchmarkProducer> producers;
    private TimeUnit sendingDelayUnit = TimeUnit.MICROSECONDS;
    private long sendingDelay;
    private CountDownLatch benchmarkIterationLatch;
    private CountingProbe probe;
    private Boolean started = false;

    public BenchmarkJMSProducerWrapper(BenchmarkClient container, BenchmarkPartConfig partConfig) {
    	super(container, partConfig);
    }
    
	@Override
	public ClientType getClientType() {
		return ClientType.PRODUCER;
	}

    public CountingProbe getProbe() {
		return probe;
	}

	public void setProbe(CountingProbe probe) {
		this.probe = probe;
	}

	public void start() {
		synchronized (started) {
			if (!started) {
				log().info("ProducerWrapper (" + getClientId() + ") starting.");
				super.start();
				CountingProbe cp = new CountingProbe(getClientId() + "-COUNTER");
				cp.addObserver(getSamplePersistenceAdapter());
				setProbe(cp);
				getProbeRunner().addProbe(cp);
		        benchmarkIterationLatch = new CountDownLatch(1);
		        new Thread(this, getClientId().toString()).start();
		        new Thread(new Runnable() {
					public void run() {
				        try {
				            benchmarkIterationLatch.await();
				    		log().info("ProducerWrapper (" + getClientId() + ") completed.");
				        } catch (InterruptedException e) {
				    		log().warn("Benchmark (" + getClientId() + ") interrupted.");
				        }
					}
				}).start();
			}
		}
    }

	private void runProducers(long rate, long duration) {

    	BigDecimal bd = new BigDecimal(1000000).divide(new BigDecimal(rate), BigDecimal.ROUND_HALF_DOWN);
        long delayInMicroSeconds;
        try {
             delayInMicroSeconds = bd.longValueExact();
        } catch (ArithmeticException e) {
            delayInMicroSeconds = bd.longValue();
            log().warn("Publish rate cannot be expressed as a precise microsecond value, rounding to nearest value "
                     + "[actualDelay: " + delayInMicroSeconds + "]");
        }

        int producersNeeded = (int)(rate / getPartConfig().getMaxConsumerRatePerThread());
        if (producersNeeded == 0) {
            producersNeeded++;
        }
        
        log.debug("Running " + producersNeeded + " producers for " + duration + "s");
        producers = new ArrayList<BenchmarkProducer>(producersNeeded);
        sendingDelay = delayInMicroSeconds * producersNeeded;
        executor = new ScheduledThreadPoolExecutor(producersNeeded);
        
        for (int i = 0; i < producersNeeded; i++) {
            try {
                BenchmarkProducer producer = new BenchmarkProducer(this);
                producer.start();
                producer.setMessageCounter(getProbe());
                producers.add(producer);
            } catch (Exception e) {
                throw new BenchmarkConfigurationException("Unable to create BenchmarkProducer instance", e);
            }
        }
        for (BenchmarkProducer producer : producers) {
            //TODO should really hold onto these and monitor for failures until the executor is shutdown
            executor.scheduleAtFixedRate(new MessageSender(producer), 0, sendingDelay, sendingDelayUnit);
        }
        
        final CountDownLatch latch = new CountDownLatch(1);

	    new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
	    	public void run() {
	    		try {
	    			log.debug("Shutting down producers.");
	    			executor.shutdown();
	    			for (BenchmarkProducer producer: producers) {
	    				try {
	    					producer.release();
	    				} catch (Exception e) {
	    					log().error("Error releasing producer.");
	    				}
	    			}
	    			latch.countDown();
	    		} catch (Exception e) {}
	    	}
	    }, duration, TimeUnit.SECONDS);
	    
	    try {
            latch.await();
        } catch(InterruptedException ie) {
        	log().warn("Producer run has been interrupted ...");
        }
    }

    /**
     * Iterate through the profile and run the producers
     */
    public void run() {
    	BenchmarkIteration iteration = getIteration(getPartConfig().getProfileName());
        log().debug("BenchmarkIteration [" + getClientId() + "] starting");
        iteration.startIteration();
    	while(iteration.needsMoreRuns()) {
    		long rate = iteration.nextEffectiveRate();
    		long duration = iteration.getCurrentDuration();
    		
            log().info("BenchmarkIteration [" + iteration.getName() + "] stepping to " + rate +  " msg/s for "
                    + duration + " s]");
            
            runProducers(rate, duration);
    	}
        getContainer().sendCommand(new ProducerFinishedCommand(this));
        log().debug("BenchmarkIteration [" + getClientId() + "] done");
    	release();
    }

    public void release() {
    	if (benchmarkIterationLatch != null && benchmarkIterationLatch.getCount() > 0) {
    		benchmarkIterationLatch.countDown();
    	}
    	super.release();
    }

    private class MessageSender implements Runnable {
        private BenchmarkProducer producer;

        private MessageSender(BenchmarkProducer producer) {
            this.producer = producer;
        }

        public void run() {
            producer.sendMessage();
        }
    }
    
    private Log log() {
    	if (log == null) {
    		log = LogFactory.getLog(this.getClass());
    	}
    	return log;
    }
}
