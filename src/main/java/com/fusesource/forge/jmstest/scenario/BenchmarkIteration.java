package com.fusesource.forge.jmstest.scenario;

public interface BenchmarkIteration {

	public String getName();
	public void startIteration();
	public boolean needsMoreRuns();
	public long nextEffectiveRate();
	public long getCurrentDuration();
	public long getRunsNeeded();
	public long getTotalDuration();
    public boolean isMeasured();
    public void setMeasured(boolean measured);
}
