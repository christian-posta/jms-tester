package com.fusesource.forge.jmstest.rrd;

public interface RRDController {

	public void record(RRDRecorderImpl recorder, long timestamp, Number value);
}
