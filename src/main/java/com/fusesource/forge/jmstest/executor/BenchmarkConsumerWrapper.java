package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.rrd.FileSystemRRDController;

public class BenchmarkConsumerWrapper extends BenchmarkClientWrapper {

    private Log log;

    private ScheduledThreadPoolExecutor scheduler;
    private boolean running = false;

    private ObjectFactory clientFactory;
    private List<BenchmarkConsumer> consumers;
    
    private FileSystemRRDController controller;

    public BenchmarkConsumerWrapper(BenchmarkClient container, BenchmarkPartConfig partConfig) {
    	super(container, partConfig);
    }
    
    @Override
    public ClientType getClientType() {
    	return ClientType.CONSUMER;
    }
    
    public FileSystemRRDController getRRDController() {
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
                client = (BenchmarkConsumer) clientFactory.getObject();
                client.initialize(i, getRRDController());
                consumers.add(client);
            } catch (Exception e) {
                log().warn("Exception during client initialisation", e);
                configFailed = true;
                closedown();
            }
        }
        try {
			getRRDController().start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return !configFailed;
    }
    
    synchronized public void start() {
        if (!isRunning()) {
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
        if (scheduler != null) {
        	scheduler.shutdown();
        }
        getRRDController().release();
        return false;
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
