package com.fusesource.forge.jmstest.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.executor.LimitedTimeScheduledExecutor;

public class ProbeRunner extends LimitedTimeScheduledExecutor {

	private List<Probe> probes;
	
	public ProbeRunner() {
		probes = new ArrayList<Probe>();
	}
	
	synchronized public void setProbes(List<Probe> probes) {
		if (probes != null) {
			this.probes = probes;
		}
	}
	
    public List<Probe> getProbes() {
		return probes;
	}

	public void addProbe(Probe probe) {
	    synchronized (probes) {
			getProbes().add(probe);
		}	
	}
	
	public void run() {
		setTask(new Runnable() {
			public void run() {
				log().debug("Gathering probes ...");
				synchronized (probes) {
					for(Probe probe: getProbes()) {
						probe.probe();
					}
				}
				log().debug("Gathering complete ...");
			}
		});
		super.run();
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
