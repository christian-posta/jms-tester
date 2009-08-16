package com.fusesource.forge.jmstest.executor;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfigurationException;
import com.fusesource.forge.jmstest.benchmark.BenchmarkContext;
import com.fusesource.forge.jmstest.config.TestRunConfig;

public class BenchmarkProducerWrapper {

	private transient Log log;
	
	private ObjectFactory profileRunnerFactory;
	private BenchmarkClientNotifier clientNotifier;
	private ConsumerToProducerListener consumerListener;
	private long maxWaitTime = 5L;
	
	public void setProfileRunnerFactory(ObjectFactory profileRunnerFactory) {
		this.profileRunnerFactory = profileRunnerFactory;
	}
	
    public BenchmarkClientNotifier getClientNotifier() {
		return clientNotifier;
	}

	public void setClientNotifier(BenchmarkClientNotifier clientNotifier) {
		this.clientNotifier = clientNotifier;
	}

	public ConsumerToProducerListener getConsumerListener() {
		return consumerListener;
	}

	public void setConsumerListener(ConsumerToProducerListener consumerListener) {
		this.consumerListener = consumerListener;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public void benchmark() {
		log().info("Benchmark");
		
		clientNotifier.initialise();
		consumerListener.initialise();
		
        BenchmarkRunStatus runStatus = null;

		if (runStatus != null) {
            runStatus.reinitialise();
        } else {
            runStatus = new BenchmarkRunStatus();
        }
        IterationRunner runner;
        try {
            runner = (IterationRunner)profileRunnerFactory.getObject();
        } catch (Exception e) {
            throw new BenchmarkConfigurationException("Unable to obtain a ProfileRunner", e);
        }
        
        log().debug("Notifying consumers ...");
        notifyClientAndWaitForResponse(true, BenchmarkContext.getInstance().getTestrunConfig());
        log().info("Benchmark starting [" + BenchmarkContext.getInstance().getTestrunConfig().toString() + "]");
        runner.setIteration(BenchmarkContext.getInstance().getProfile());
        runner.setTestRunConfig(BenchmarkContext.getInstance().getTestrunConfig());
        runner.observeStatus(runStatus);
        CountDownLatch barrier = new CountDownLatch(1);
        runner.setBenchmarkIterationLatch(barrier);
        new Thread(runner, "Benchmark").start();

        try {
            barrier.await();
            log().info("Benchmark completed [" + BenchmarkContext.getInstance().getTestrunConfig().toString() + "]");
        } catch (InterruptedException e) {
            runStatus.setState(BenchmarkRunStatus.State.FAILED);
            log().warn("Benchmark broken", e);
        } finally {
        	log.debug("Notifying consumer of benchmark end");
        	notifyClientAndWaitForResponse(false, BenchmarkContext.getInstance().getTestrunConfig());
        }
    }
    
    private BenchmarkNotificationPayload notifyClientAndWaitForResponse(boolean isStartMessage, TestRunConfig testRunConfig) {
        BenchmarkNotificationPayload payload;
        if (isStartMessage) {
            payload = clientNotifier.startBenchmark(testRunConfig);
        } else {
            payload = clientNotifier.endBenchmark(testRunConfig);
        }
        try {
            synchronized (payload) {
                consumerListener.registerWaiter(payload);
                while (consumerListener.isWaiting()) {
                    payload.wait((getMaxWaitTime() * 1000));
                }
            }
        } catch (InterruptedException e) {
            // ignore
            log().warn("Wait interrupted", e);
        }
        return consumerListener.takeClientReply();
    }
    
    private Log log() {
    	if (log == null) {
    		log = LogFactory.getLog(this.getClass());
    	}
    	return log;
    }
}
