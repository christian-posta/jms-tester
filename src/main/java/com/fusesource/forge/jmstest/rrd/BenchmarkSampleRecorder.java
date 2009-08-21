package com.fusesource.forge.jmstest.rrd;

import org.rrd4j.DsType;

import com.fusesource.forge.jmstest.probe.ProbeDataConsumer;

public interface BenchmarkSampleRecorder extends ProbeDataConsumer {
	
	public BenchmarkSamplePersistenceAdapter getAdapter();
	public void setAdapter(BenchmarkSamplePersistenceAdapter controller);

	public DsType getDsType();
	public void setDsType(DsType dsType);
	
	public void record(Number value);	
	public void record(long timestamp, Number value);
}
