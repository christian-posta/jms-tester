package com.fusesource.forge.jmstest.probe;

import java.util.Observer;

public interface Probe {

	public void setName(String name);
	public String getName();
	public void probe();
	
	public boolean isResetOnRead();
	public void reset();
	
	public BenchmarkProbeValue getProbeValue();
	public BenchmarkProbeValue.ValueType getValueType();
	public ProbeDescriptor getDescriptor();
	
	public void addObserver(Observer observer);
}
