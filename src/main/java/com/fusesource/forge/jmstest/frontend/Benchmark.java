package com.fusesource.forge.jmstest.frontend;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutor;
import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.executor.BenchmarkValueRecorder;

public class Benchmark extends AbstractBenchmarkExecutor {
	
	private boolean controller = false;
	private boolean client     = false;
	private boolean recorder   = false;
	
	public boolean isClient() {
		return client;
	}

	public void setClient(String client) {
		try {
			this.client = new Boolean(client).booleanValue();
		} catch (Exception e) {}
	}

	public boolean isRecorder() {
		return recorder;
	}

	public void setRecorder(String recorder) {
		try {
			this.recorder = new Boolean(recorder).booleanValue();
		} catch (Exception e) {}
	}

	public boolean isController() {
		return controller;
	}

	public void setController(String controller) {
		try {
			this.controller = new Boolean(controller).booleanValue();
		} catch (Exception e) {}
	}

	public void start(String[] args) {
		handleArguments(args);
		
		if (isController()) {
			final BenchmarkController controller = new BenchmarkController();
			controller.setAutoTerminate(false);
			controller.start(args);
		}
		if (isRecorder()) {
			final BenchmarkValueRecorder recorder = new BenchmarkValueRecorder();
			recorder.setAutoTerminate(false);
			recorder.start(args);
		}
		if (isClient()) {
			final BenchmarkClient client = new BenchmarkClient();
			client.setAutoTerminate(false);
			client.start(args);
		}
	}
	
	public static void main(String[] args) {
		final Benchmark bm = new Benchmark();
		bm.start(args);
	}
}
