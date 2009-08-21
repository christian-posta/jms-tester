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
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.benchmark.command.ProducerFinished;
import com.fusesource.forge.jmstest.probe.CountingProbe;
import com.fusesource.forge.jmstest.rrd.BenchmarkSampleRecorder;
import com.fusesource.forge.jmstest.rrd.BenchmarkSampleRecorderImpl;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class BenchmarkProducerWrapper extends BenchmarkClientWrapper implements Runnable {

    private transient Log log;

    private ScheduledThreadPoolExecutor executor;
    BenchmarkIteration iteration = null;
    private List<BenchmarkProducer> producers;
    private TimeUnit sendingDelayUnit = TimeUnit.MICROSECONDS;
    private long sendingDelay;
    private CountDownLatch benchmarkIterationLatch;
    private long maxRatePerProducerThread = 5000;
    private CountingProbe probe;

    public BenchmarkProducerWrapper(BenchmarkClient container, BenchmarkPartConfig partConfig) {
    	super(container, partConfig);
    }
    
	@Override
	public ClientType getClientType() {
		return ClientType.PRODUCER;
	}
    
    public void setMaxRatePerProducerThread(long maxRatePerProducerThread) {
        this.maxRatePerProducerThread = maxRatePerProducerThread;
    }

    public CountingProbe getProbe() {
		return probe;
	}

	public void setProbe(CountingProbe probe) {
		this.probe = probe;
	}

	public void start() {
		log().info("Benchmark (" + getClientId() + ") starting.");
		CountingProbe cp = new CountingProbe();
		cp.setName(getClientId() + "-COUNTER");
		BenchmarkSampleRecorder bmsr = new BenchmarkSampleRecorderImpl();
		bmsr.setAdapter(getSamplePersistenceAdapter());
		cp.setDataConsumer(bmsr);
		setProbe(cp);
		getProbeRunner().addProbe(cp);
		getProbeRunner().start();
        benchmarkIterationLatch = new CountDownLatch(1);
        new Thread(this, "Benchmark").start();
        new Thread(new Runnable() {
			public void run() {
		        try {
		            benchmarkIterationLatch.await();
		    		log().info("Benchmark (" + getClientId() + ") completed.");
		        } catch (InterruptedException e) {
		    		log().warn("Benchmark (" + getClientId() + ") interrupted.");
		        }
			}
		}).start();
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

        // break it down so that we get at least 200 microseconds to publish each message
        int producersNeeded = (int)(rate / maxRatePerProducerThread);
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
	    			log.info("Shutting down producers.");
	    			executor.shutdown();
	    			latch.countDown();
	    		} catch (Exception e) {}
	    	}
	    }, duration, TimeUnit.SECONDS);
	    
	    try {
            latch.await();
        } catch(InterruptedException ie) {}
    }

    /**
     * Iterate through the profile and run the producers
     */
    public void run() {
    	BenchmarkIteration iteration = getIteration();
        log().debug("BenchmarkIteration [" + iteration.getName() + "] starting");
        iteration.startIteration();
    	while(iteration.needsMoreRuns()) {
    		long rate = iteration.nextEffectiveRate();
    		long duration = iteration.getCurrentDuration();
    		
            log().debug("BenchmarkIteration [" + iteration.getName() + "] stepping to " + rate +  " msg/s for "
                    + duration + " s] starting");
            
            runProducers(rate, duration);
    	}
        log().debug("BenchmarkIteration [" + iteration.getName() + "] done.");
    	release();
    }

    public void release() {
        benchmarkIterationLatch.countDown();
        getContainer().sendCommand(new ProducerFinished(this));
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
