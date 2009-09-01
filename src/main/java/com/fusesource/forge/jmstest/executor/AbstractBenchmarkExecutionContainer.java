package com.fusesource.forge.jmstest.executor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;

public class AbstractBenchmarkExecutionContainer extends AbstractBenchmarkExecutor implements Releaseable, Runnable {
	
	private boolean autoTerminate = true;
	
	private ExecutorService executor = null;
	private Boolean started = false;
	private CountDownLatch latch;
	private Log log = null;
	
	protected  void createHandlerChain() {
		getConnector().addHandler(new DefaultCommandHandler() {
			public boolean handleCommand(BenchmarkCommand command) {
				if (command.getCommandType() == CommandTypes.SHUTDOWN) {
					stop();
					return true;
				}
				return false;
			}
		});
	}

	synchronized public void start(String[] args) {
		handleArguments(args);
		if (!started) {
			if (isAutoTerminate()) {
				executor = new TerminatingThreadPoolExecutor("BenchmarkExecutor", 1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
			} else  {
				executor = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		  	}
			executor.submit(this);
			started = true;
		}
	}

	synchronized public void stop() {
		getCmdTransport().stop();
		if (started) {
			if (!isAutoTerminate()) {
				if (executor != null) {
					executor.shutdown();
				}
			}
		}
		started = false;
		latch.countDown();
	}
	
	protected void init() {
		log().info("Initializing Benchmarking framework ...");
		ReleaseManager.getInstance().register(this); 
		getCmdTransport();
		createHandlerChain();
	}
	
	protected void execute() {
	}
	
	public void run() {
		init();
		log().info("Running Benchmarking framework ...");
		latch = new CountDownLatch(1);
		execute();

		try {
			latch.await();
		} catch (InterruptedException e) {
			// ignore
		}
		
		release();
		ReleaseManager.getInstance().deregister(this);
		log().info("Done Benchmarking framework");
	}

	public void release() {
	}
	
	public boolean isAutoTerminate() {
		return autoTerminate;
	}

	public void setAutoTerminate(boolean autoTerminate) {
		this.autoTerminate = autoTerminate;
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}