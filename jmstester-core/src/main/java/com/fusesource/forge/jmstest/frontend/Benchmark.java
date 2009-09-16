package com.fusesource.forge.jmstest.frontend;

import java.util.StringTokenizer;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkExecutor;
import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.executor.BenchmarkValueRecorder;

public class Benchmark extends AbstractBenchmarkExecutor {
	
	private boolean controller = false;
	private String  clientNames = null;
	private boolean recorder   = false;
	
	public boolean isClient() {
		return (getClientNames() != null);
	}

	public String getClientNames() {
		return clientNames;
	}
	
	public void setClientNames(String clientNames) {
		this.clientNames = clientNames;
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
			BenchmarkController controller = new BenchmarkController();
			controller.setAutoTerminate(false);
			controller.start(args);
		}
		if (isRecorder()) {
			BenchmarkValueRecorder recorder = new BenchmarkValueRecorder();
			recorder.setAutoTerminate(false);
			recorder.start(args);
		}
		if (isClient()) {
			StringTokenizer sTok = new StringTokenizer(getClientNames(), ",");
			while(sTok.hasMoreTokens()) {
				String clientName = sTok.nextToken();
				BenchmarkClient client = new BenchmarkClient();
				client.setName(clientName);
				client.setAutoTerminate(false);
				client.start(args);
			}
		}
	}
	
	public static void main(String[] args) {
		final Benchmark bm = new Benchmark();
		bm.start(args);
	}
}
