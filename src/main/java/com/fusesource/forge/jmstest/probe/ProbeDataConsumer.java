package com.fusesource.forge.jmstest.probe;

public interface ProbeDataConsumer {

	public void record(Number value);
	public void record(long timestamp, Number value);
}
