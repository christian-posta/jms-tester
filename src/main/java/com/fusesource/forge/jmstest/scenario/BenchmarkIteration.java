package com.fusesource.forge.jmstest.scenario;

public interface BenchmarkIteration {

	public String getName();
	public void startIteration();
	public boolean needsMoreRuns();
	public long nextEffectiveRate();
	public long getDuration();
    public boolean isMeasured();
    public void setMeasured(boolean measured);
}
