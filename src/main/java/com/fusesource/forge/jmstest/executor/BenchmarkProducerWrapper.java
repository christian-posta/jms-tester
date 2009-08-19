package com.fusesource.forge.jmstest.executor;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;

public class BenchmarkProducerWrapper {

	private transient Log log;
	
	private ObjectFactory profileRunnerFactory;
	private long maxWaitTime = 5L;
	
	public void setProfileRunnerFactory(ObjectFactory profileRunnerFactory) {
		this.profileRunnerFactory = profileRunnerFactory;
	}
	
	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public void benchmark() {
		log().info("Benchmark");
		
        IterationRunner runner;
        try {
            runner = (IterationRunner)profileRunnerFactory.getObject();
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to obtain a ProfileRunner", e);
        }
        
        log().debug("Notifying consumers ...");
        //notifyClientAndWaitForResponse(true, BenchmarkContext.getInstance().getTestrunConfig());
        log().info("Benchmark starting [" + BenchmarkContext.getInstance().getTestrunConfig().toString() + "]");
        runner.setIteration(BenchmarkContext.getInstance().getProfile());
        runner.setTestRunConfig(BenchmarkContext.getInstance().getTestrunConfig());
        CountDownLatch barrier = new CountDownLatch(1);
        runner.setBenchmarkIterationLatch(barrier);
        new Thread(runner, "Benchmark").start();

        try {
            barrier.await();
            log().info("Benchmark completed [" + BenchmarkContext.getInstance().getTestrunConfig().toString() + "]");
        } catch (InterruptedException e) {
            log().warn("Benchmark broken", e);
        } finally {
        	log.debug("Notifying consumer of benchmark end");
        	//notifyClientAndWaitForResponse(false, BenchmarkContext.getInstance().getTestrunConfig());
        }
    }
    
    private Log log() {
    	if (log == null) {
    		log = LogFactory.getLog(this.getClass());
    	}
    	return log;
    }
}
