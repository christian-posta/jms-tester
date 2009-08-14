package com.fusesource.forge.jmstest.probe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.executor.LimitedTimeScheduledExecutor;

public class ProbeRunner extends LimitedTimeScheduledExecutor {

	private List<Probe> probes;
	
	public void setProbes(List<Probe> probes) {
		this.probes = probes;
	}
	
	public List<Probe> getProbes() {
		if (probes == null) {
			probes = new ArrayList<Probe>();
		}
		return probes;
	}

	public void addProbe(Probe probe) {
		getProbes().add(probe);
	}
	
	public void run() {
		setTask(new Runnable() {
			public void run() {
				log().debug("Gathering probes ...");
				for(Probe probe: probes) {
					probe.probe();
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
