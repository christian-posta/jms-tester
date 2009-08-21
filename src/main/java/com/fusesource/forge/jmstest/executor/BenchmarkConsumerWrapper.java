package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.rrd.RRDController;

public class BenchmarkConsumerWrapper extends BenchmarkClientWrapper {

    private Log log;

    private boolean running = false;

    private List<BenchmarkConsumer> consumers;
    private RRDController controller;

    public BenchmarkConsumerWrapper(BenchmarkClient container, BenchmarkPartConfig partConfig) {
    	super(container, partConfig);
    }
    
    @Override
    public ClientType getClientType() {
    	return ClientType.CONSUMER;
    }
    
    public RRDController getRRDController() {
    	if (controller == null) {
    		// TODO: fix me
    	}
		return controller;
	}

    @Override
    public boolean prepare() {
    	super.prepare();

    	consumers = new ArrayList<BenchmarkConsumer>(getConfig().getNumConsumers());

    	boolean configFailed = false;
        
    	for (int i = 0; !configFailed && i < getConfig().getNumConsumers(); i++) {
            BenchmarkConsumer client = null;
            try {
            	client = new BenchmarkConsumer(this, i, getRRDController(), getProbeRunner());
                client.prepare();
                consumers.add(client);
            } catch (Exception e) {
                log().warn("Exception during client initialisation", e);
                configFailed = true;
                closedown();
            }
        }
        return !configFailed;
    }
    
    synchronized public void start() {
        if (!isRunning()) {
        	if (getProbeRunner() != null) {
        		getProbeRunner().start();
        	}
        	if (getRRDController() != null) {
        		getRRDController().start();
        	}

			for (BenchmarkConsumer consumer: consumers) {
        		consumer.start();
        	}
        	
			running = true;
        }
    }

    public boolean isRunning() {
    	return running;
    }
    
    public boolean closedown() {
        if (consumers != null && !consumers.isEmpty()) {
            log().info("Closing down " + consumers.size() + " benchmark clients");
            for (BenchmarkConsumer consumer : consumers) {
                consumer.release();
            }
            log().info(consumers.size() + " benchmark clients closed down");
        }
        getRRDController().stop();
        return false;
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
