package com.fusesource.forge.jmstest.probe;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractProbe implements Probe {
	
	private String name;
	private ProbeDataConsumer dataConsumer;
	
	protected abstract Number getValue();

	public AbstractProbe() {
	}

	public ProbeDataConsumer getDataConsumer() {
		return dataConsumer;
	}

	public void setDataConsumer(ProbeDataConsumer dataConsumer) {
		this.dataConsumer = dataConsumer;
		dataConsumer.setProbe(this);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		if (name == null) {
			name = "Probe-" + UUID.randomUUID();
		}
		return name;
	}
	
	public void probe() {
		if (getDataConsumer() != null) {
			Number n = getValue();
			log().debug("Probe " + getName() + " : " + n.toString());
			dataConsumer.record(n);
		}
	}
	
	private Log log() {
		return LogFactory.getLog(this.getClass());
	}
}
