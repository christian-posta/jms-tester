package com.fusesource.forge.jmstest.rrd;

public interface RRDController {
	
	public void addRRDRecorder(RRDRecorder recorder);
	public void record(RRDRecorder recorder, long timestamp, Number value);
	public void start();
	public void stop();
}
