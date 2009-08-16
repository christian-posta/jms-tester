package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.config.TestRunConfig;

public class BenchmarkConsumerWrapper {

    private transient Log log;

    private ScheduledThreadPoolExecutor scheduler;
    private boolean running = false;
    
    private ObjectFactory clientFactory;
    private List<BenchmarkConsumer> consumers;

    public void setConsumerFactory(ObjectFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public boolean initialise(TestRunConfig testRunConfig)  {
        consumers = new ArrayList<BenchmarkConsumer>(testRunConfig.getNumConsumers());
        boolean configFailed = false;
        for (int i = 0; !configFailed && i < testRunConfig.getNumConsumers(); i++) {
            BenchmarkConsumer client = null;
            try {
                client = (BenchmarkConsumer) clientFactory.getObject();
                client.initialise(testRunConfig, i);
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
        return false;
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
