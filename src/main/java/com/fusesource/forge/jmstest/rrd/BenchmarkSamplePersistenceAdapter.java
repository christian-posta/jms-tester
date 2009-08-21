package com.fusesource.forge.jmstest.rrd;

public interface BenchmarkSamplePersistenceAdapter {
	
	public void addRecorder(BenchmarkSampleRecorder recorder);
	public void record(BenchmarkSampleRecorder recorder, long timestamp, Number value);
	public void start();
	public void stop();
}
