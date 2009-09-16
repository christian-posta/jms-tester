package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;

public class BenchmarkJMSConsumerWrapper extends AbstractBenchmarkJMSClient {

    private Log log;

    private boolean running = false;

    private List<BenchmarkConsumer> consumers;

    public BenchmarkJMSConsumerWrapper(BenchmarkClient container, BenchmarkPartConfig partConfig) {
    	super(container, partConfig);
    }
    
    @Override
    public ClientType getClientType() {
    	return ClientType.CONSUMER;
    }
    
    @Override
    public boolean prepare() {
    	super.prepare();

    	consumers = new ArrayList<BenchmarkConsumer>(getPartConfig().getNumConsumers());

    	boolean configFailed = false;
        
    	for (int i = 0; !configFailed && i < getPartConfig().getNumConsumers(); i++) {
            BenchmarkConsumer client = null;
            try {
            	client = new BenchmarkConsumer(this, i, getSamplePersistenceAdapter(), getProbeRunner());
                client.prepare();
                consumers.add(client);
            } catch (Exception e) {
                log().warn("Exception during client initialisation", e);
                configFailed = true;
                release();
            }
        }
        return !configFailed;
    }
    
    synchronized public void start() {
        if (!isRunning()) {
        	super.start();
        	log().info("ConsumerWrapper [" + getClientId() + "] starting " + consumers.size() + " consumers.");
			for (BenchmarkConsumer consumer: consumers) {
        		consumer.start();
        	}
			running = true;
        }
    }

    public boolean isRunning() {
    	return running;
    }
    
    public void release() {
    	if (isRunning()) {
    		super.release();
	        if (consumers != null && !consumers.isEmpty()) {
	            log().info("ConsumerWrapper [" + getClientId() + "] closing down " + consumers.size() + " benchmark clients");
	            for (BenchmarkConsumer consumer : consumers) {
	                consumer.release();
	            }
	        }
    	}
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
