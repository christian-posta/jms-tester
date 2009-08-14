package com.fusesource.forge.jmstest.benchmark;

public class BenchmarkContext {

	private static BenchmarkContext instance = null;

	private ReleaseManager releaseManager = null;
	
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
}
