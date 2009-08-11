package com.fusesource.forge.jmstest.probe;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProbeRunner implements Runnable {

	private List<ProbeDataConsumer> probes;
	private long duration = 300;
	private long interval = 5;
	
	public List<ProbeDataConsumer> getProbes() {
		return probes;
	}

	public void setProbes(List<ProbeDataConsumer> probes) {
		this.probes = probes;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void run() {
	  	getLog().info("Prober starting");
	  	getLog().info("Probe finished");
	}
	
	private Log getLog() {
		return LogFactory.getLog(this.getClass());
	}
}
