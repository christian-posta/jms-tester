package com.fusesource.forge.jmstest.peristence;

import java.util.Observer;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;

public interface BenchmarkSamplePersistenceAdapter extends Observer {
	
	public void start();
	public void stop();
	
	public void record(BenchmarkProbeValue value);
}
