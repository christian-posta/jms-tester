package com.fusesource.forge.jmstest.executor;

import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;

public abstract class BenchmarkClientWrapper implements Releaseable {
	
	private BenchmarkPartConfig partConfig;
	private ApplicationContext appContext;
	
	public BenchmarkClientWrapper(BenchmarkPartConfig config) {
		this.partConfig = config;
	}

	public ApplicationContext getApplicationContext() {
		return appContext;
	}

	public void setApplicationContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public void start() {
		ReleaseManager.getInstance().register(this);
	}

	public void release() {
		ReleaseManager.getInstance().deregister(this);
	}
}
