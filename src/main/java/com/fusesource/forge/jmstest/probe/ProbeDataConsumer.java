package com.fusesource.forge.jmstest.probe;

public interface ProbeDataConsumer {
	public String getName();

	public void setProbe(Probe name);
	public Probe getProbe();
	
	public void record(Number value);
	public void record(long timestamp, Number value);
}
