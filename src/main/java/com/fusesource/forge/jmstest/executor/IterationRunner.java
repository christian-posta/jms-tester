package com.fusesource.forge.jmstest.executor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.results.ProducerMetricCollector;
import com.fusesource.forge.jmstest.config.MeasureTimeUnit;
import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class IterationRunner extends ExecutableBenchmarkComponent implements Runnable {

    private transient Log log;

    private ReleaseManager releaseManager;
    
    private ScheduledThreadPoolExecutor executor;
    private ObjectFactory producerFactory;
    private BenchmarkIteration iteration;
    private List<BenchmarkProducer> producers;
    private TimeUnit sendingDelayUnit = TimeUnit.MICROSECONDS;
    private long sendingDelay;
    private CountDownLatch benchmarkIterationLatch;
    private long maxRatePerProducerThread = 5000; //default
    private TestRunConfig testRunConfig;
    
    public TestRunConfig getTestRunConfig() {
		return testRunConfig;
	}

	public void setTestRunConfig(TestRunConfig testRunConfig) {
		this.testRunConfig = testRunConfig;
	}

	public ReleaseManager getReleaseManager() {
		return releaseManager;
	}

	public void setReleaseManager(ReleaseManager releaseManager) {
		this.releaseManager = releaseManager;
	}

    public void setProducerFactory(ObjectFactory producerFactory) {
        this.producerFactory = producerFactory;
    }

    public void setIteration(BenchmarkIteration iteration) {
        this.iteration = iteration;
    }

    public void setBenchmarkIterationLatch(CountDownLatch benchmarkIterationLatch) {
        this.benchmarkIterationLatch = benchmarkIterationLatch;
    }

    public void setMaxRatePerProducerThread(long maxRatePerProducerThread) {
        this.maxRatePerProducerThread = maxRatePerProducerThread;
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
        ProducerMetricCollector messageCounter = new ProducerMetricCollector();
        producers = new ArrayList<BenchmarkProducer>(producersNeeded);
        sendingDelay = delayInMicroSeconds * producersNeeded;
        executor = new ScheduledThreadPoolExecutor(producersNeeded);
        
        for (int i = 0; i < producersNeeded; i++) {
            try {
                BenchmarkProducer producer = (BenchmarkProducer) producerFactory.getObject();
                producer.initialise(getTestRunConfig());
                producer.setMessageCounter(messageCounter);
                producer.observeStatus(this.getBenchmarkStatus());
                producers.add(producer);
            } catch (Exception e) {
                getBenchmarkStatus().setState(BenchmarkRunStatus.State.FAILED);
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
            log.info(messageCounter.getThroughput(MeasureTimeUnit.SECONDS));
        } catch(InterruptedException ie) {}
    }

    /**
     * Iterate through the profile and run the producers
     */
    public void run() {
        log().debug("BenchmarkIteration [" + iteration.getName() + "] starting");
        getReleaseManager().register(this);
        iteration.startIteration();
    	while(iteration.needsMoreRuns()) {
    		long rate = iteration.nextEffectiveRate();
    		long duration = iteration.getDuration();
    		
            log().debug("BenchmarkIteration [" + iteration.getName() + "] stepping to " + rate +  " msg/s for "
                    + duration + " s] starting");
            
            runProducers(rate, duration);
    	}
    	
    	release();
    }

    public void release() {
        log().trace(">>> IterationRunner#release");
        getReleaseManager().deregister(this);
        getBenchmarkStatus().deleteObserver(this);
        benchmarkIterationLatch.countDown();
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    /**
	 * As small a unit of work as possible.
	 */
    private class MessageSender implements Runnable {
        private BenchmarkProducer producer;

        private MessageSender(BenchmarkProducer producer) {
            this.producer = producer;
        }

        public void run() {
            //TODO send special marker messages to say "stop/start collecting stats"??
            producer.sendMessage();
        }
    }
}
