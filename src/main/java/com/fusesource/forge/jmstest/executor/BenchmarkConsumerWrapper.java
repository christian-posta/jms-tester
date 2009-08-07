package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.results.MetricsFlusher;
import com.fusesource.forge.jmstest.benchmark.results.RawMetricCollector;
import com.fusesource.forge.jmstest.config.TestRunConfig;

/**
 * @author  andreasgies
 */
public class BenchmarkConsumerWrapper {

    private transient Log log;

    private ScheduledThreadPoolExecutor scheduler;
    private boolean running = false;
    
    private ObjectFactory flusherFactory;
    
    private ObjectFactory clientFactory;
    private List<BenchmarkConsumer> consumers;

    private RawMetricCollector metricCollector;

    public BenchmarkConsumerWrapper() {
    }

    public void setMetricsFlusherFactory(ObjectFactory factory) {
    	this.flusherFactory = factory;
    }
    
    public MetricsFlusher getMetricsFlusher() {
    	if (flusherFactory != null) {
    	  return (MetricsFlusher)flusherFactory.getObject();
    	}
    	return null;
    }
    
    public void setConsumerFactory(ObjectFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public void setMetricCollector(RawMetricCollector metricCollector) {
        this.metricCollector = metricCollector;
    }

    public RawMetricCollector getMetricCollector() {
        return metricCollector;
    }

    public boolean initialise(TestRunConfig testRunConfig)  {
        consumers = new ArrayList<BenchmarkConsumer>(testRunConfig.getNumConsumers());
        boolean configFailed = false;
        for (int i = 0; !configFailed && i < testRunConfig.getNumConsumers(); i++) {
            BenchmarkConsumer client = null;
            try {
                client = (BenchmarkConsumer) clientFactory.getObject();
                client.initialise(testRunConfig, i, metricCollector);
                consumers.add(client);
            } catch (Exception e) {
                log().warn("Exception during client initialisation", e);
                configFailed = true;
                closedown();
            }
        }
        return !configFailed;
    }
    
    synchronized public boolean start(TestRunConfig testRunConfig) {
        if (!isRunning()) {
            try {
            	log().info("Starting BenchmarkConsumerWrapper [" + testRunConfig + "]");
            	initialise(testRunConfig);
            	MetricsFlusher flusher = getMetricsFlusher();
            	if (flusher != null) {
            		flusher.setRunId(testRunConfig.getRunId());
                    flusher.setCollector(getMetricCollector());
                    if (flusher.initialiseFlusher()) {
                        scheduler = new ScheduledThreadPoolExecutor(2);
                        scheduler.scheduleWithFixedDelay(flusher, 30, 30, TimeUnit.SECONDS);
                        if (initialise(testRunConfig)) {
                            log().info("Benchmark consumers initialised and ready to benchmark [" + testRunConfig + "]");
                            running = true;
                        } else {
                            log().warn("Failed to initialise benchmark consumers [" + testRunConfig + "]");
                        }
                    }
            	}
            	running = true;
            } catch (Exception e) {
                log().error("Unable to get BenchmarkClient from factory", e);
            }
        } else {
            log.error("Benchmark execution is in progress, request will be ignored [jmsParameterMix: " + testRunConfig + "]");
        }
        return isRunning();
    }

    public void setRunning(boolean running) {
    	this.running = running;
    }

    public boolean isRunning() {
    	return running;
    }
    
    public boolean closedown() {
        if (!consumers.isEmpty()) {
            log().info("Closing down " + consumers.size() + " benchmark clients");
            for (BenchmarkConsumer consumer : consumers) {
                consumer.release();
            }
            log().info(consumers.size() + " benchmark clients closed down");
        }
        if (scheduler != null) {
        	scheduler.shutdown();
        }
        metricCollector.stop();
        return false;
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
