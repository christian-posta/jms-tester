package com.fusesource.forge.jmstest.probe;

import java.util.UUID;

public abstract class AbstractProbeDataConsumer implements ProbeDataConsumer {

	private String name;
	private Probe probe = null;
	
	public String getName() {
		if (probe != null) {
			return probe.getName();
		}
		if (name == null) {
			name = this.getClass().getName() + "-" + UUID.randomUUID();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProbe(Probe probe) {
		this.probe = probe;
	}

	public Probe getProbe() {
		return probe;
	}
	
	public void record(Number value) {
		record(System.currentTimeMillis() / 1000, value);
	}
	
	abstract public void record(long timestamp, Number value);
	
}
