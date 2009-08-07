package com.fusesource.forge.jmstest.benchmark.results;

public interface MetricsFlusher extends Runnable {

	public void setCollector(RawMetricCollector collector);
	public void setSummariser(MetricSummariser summariser);
	public void setRunId(String runId);
	public String getRunId();
    public boolean initialiseFlusher();
    public void flush();
}
