package com.fusesource.forge.jmstest.benchmark;

import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class BenchmarkContext {

	private static BenchmarkContext instance = null;

	private BenchmarkIteration profile = null;
	private BenchmarkPartConfig testrunConfig = null;
	private ApplicationContext appContext = null;
	
	synchronized public static BenchmarkContext getInstance() {
		if (instance == null) {
			instance = new BenchmarkContext();
		}
		return instance;
	}
	
	public BenchmarkIteration getProfile() {
		return profile;
	}
	
	public void setTestrunConfig(BenchmarkPartConfig testrunConfig) {
		this.testrunConfig = testrunConfig;
	}
	
	public BenchmarkPartConfig getTestrunConfig() {
		return testrunConfig;
	}
	
	public void setProfile(BenchmarkIteration profile) {
		this.profile = profile;
	}

	public ApplicationContext getApplicationContext() {
		return appContext;
	}

	public void setApplicationContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}
	
}
