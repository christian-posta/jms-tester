package com.fusesource.forge.jmstest.benchmark;

import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.config.TestRunConfig;
import com.fusesource.forge.jmstest.rrd.RRDController;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public class BenchmarkContext {

	private static BenchmarkContext instance = null;

	private ReleaseManager releaseManager = null;
	private BenchmarkIteration profile = null;
	private TestRunConfig testrunConfig = null;
	private RRDController rrdController = null;
	private ApplicationContext appContext = null;
	
	private BenchmarkContext() {
		getReleaseManager();
	}
	
	synchronized public static BenchmarkContext getInstance() {
		if (instance == null) {
			instance = new BenchmarkContext();
		}
		return instance;
	}
	
	public ReleaseManager getReleaseManager() {
		if (releaseManager == null) {
			releaseManager = new ReleaseManager();
			Runtime.getRuntime().addShutdownHook(releaseManager);
		}
		return releaseManager;
	}
	
	public RRDController getRRDController() {
		if (rrdController == null) {
			if (appContext != null) {
				String[] rrdNames = appContext.getBeanNamesForType(RRDController.class);
				if (rrdNames.length > 0) {
					rrdController = (RRDController)appContext.getBean(rrdNames[0]);
				} else {
					rrdController = new RRDController();
					rrdController.setFileName("PerformanceTest.rrd");
				}
			}
		}
		return rrdController;
	}
	
	public BenchmarkIteration getProfile() {
		return profile;
	}
	
	public void setTestrunConfig(TestRunConfig testrunConfig) {
		this.testrunConfig = testrunConfig;
	}
	
	public TestRunConfig getTestrunConfig() {
		return testrunConfig;
	}
	
	public void setProfile(BenchmarkIteration profile) {
		this.profile = profile;
	}
}
