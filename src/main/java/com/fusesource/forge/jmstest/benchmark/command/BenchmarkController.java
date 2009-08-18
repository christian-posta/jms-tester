package com.fusesource.forge.jmstest.benchmark.command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fusesource.forge.jmstest.executor.TerminatingThreadPoolExecutor;

public class BenchmarkController extends AbstractBenchmarkExecutor {

	public static void main(String[] args) {
		ExecutorService service = new TerminatingThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		
		final BenchmarkController controller = new BenchmarkController();
		controller.setSpringConfigLocations(args);
		service.submit(controller);
		
		
		// for now terminate the controller at some point 
		
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		
		executor.schedule(
		    new Runnable() {
				public void run() {
					controller.sendCommand(new ShutdownCommand());
					executor.shutdown();
				}
			}, 10, TimeUnit.SECONDS
		);
	}
}
 