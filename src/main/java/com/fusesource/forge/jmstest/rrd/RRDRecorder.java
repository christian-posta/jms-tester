package com.fusesource.forge.jmstest.rrd;

import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.probe.ProbeDataConsumer;

public interface RRDRecorder extends ProbeDataConsumer {
	
	public RRDController getController();
	public void setController(RRDController controller);

	public DsType getDsType();
	public void setDsType(DsType dsType);
	
	public void record(Number value);	
	public void record(long timestamp, Number value);
}
