package com.fusesource.forge.jmstest.probe;

import java.util.Observable;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public abstract class AbstractProbe extends Observable implements Probe {

	private ProbeDescriptor descriptor = null;
	private String name;
	private boolean resetOnRead = false;
	
	private Log log = null;

	public AbstractProbe() {
		this("Probe-" + UUID.randomUUID().toString());
	}
	
	public AbstractProbe(String name) {
		this.name = name;
	}

	public final BenchmarkProbeValue getProbeValue() {
		BenchmarkProbeValue result = new BenchmarkProbeValue(
			getDescriptor(), System.currentTimeMillis() / 1000, getValue()
		);
		if (isResetOnRead()) {
			reset();
		}
		return result;
	}

	protected abstract Number getValue() ;
	
	public void setName(String name) {
		this.name = name;
		descriptor = null;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isResetOnRead() {
		return resetOnRead;
	}

	public void setResetOnRead(boolean resetOnRead) {
		this.resetOnRead = resetOnRead;
	}

	public void reset() {
	}
	
	public BenchmarkProbeValue.ValueType getValueType() {
		return ValueType.GAUGE;
	}

	public ProbeDescriptor getDescriptor() {
		if (descriptor == null) {
			descriptor = new ProbeDescriptor(this);
		}
		return descriptor;
	}
	
	public void probe() {
		BenchmarkProbeValue value = getProbeValue();
		log().debug("Probe: " + getName() + "=" + value);
		setChanged();
		notifyObservers(value);
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
