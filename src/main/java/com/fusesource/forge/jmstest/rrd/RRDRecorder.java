package com.fusesource.forge.jmstest.rrd;

import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.probe.ProbeDataConsumer;

public class RRDRecorder implements ProbeDataConsumer {
	
	private String name;
	private RRDController controller;
	private DsType dsType = DsType.COUNTER;
	
	public RRDRecorder() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RRDController getController() {
		return controller;
	}

	public void setController(RRDController controller) {
		this.controller = controller;
	}

	public DsType getDsType() {
		return dsType;
	}

	public void setDsType(DsType dsType) {
		this.dsType = dsType;
	}
	
	public void record(Number value) {
		record(System.currentTimeMillis() / 1000, value);
	}
	
	public void record(long timestamp, Number value) {
		controller.record(this, timestamp, value);
	}
}
