package com.fusesource.forge.jmstest.probe;

public interface Probe {

	public String getName();
	public void probe();
	
	public boolean isResetOnRead();
	public void reset();
	
	public BenchmarkProbeValue getProbeValue();
	public BenchmarkProbeValue.ValueType getValueType();
	public ProbeDescriptor getDescriptor();
}
