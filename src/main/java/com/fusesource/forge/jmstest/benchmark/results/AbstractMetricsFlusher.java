package com.fusesource.forge.jmstest.benchmark.results;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author  andreasgies
 */
public abstract class AbstractMetricsFlusher implements MetricsFlusher {

    private String runId;
    /**
	 * @uml.property  name="collector"
	 * @uml.associationEnd  
	 */
    private RawMetricCollector collector;
    /**
	 * @uml.property  name="summariser"
	 * @uml.associationEnd  
	 */
    private MetricSummariser summariser;

    public AbstractMetricsFlusher() {
    }

    public abstract boolean initialiseFlusher();
    
    public abstract void flush();
    
    /**
	 * @param runId
	 * @uml.property  name="runId"
	 */
    public void setRunId(String runId) {
        this.runId = runId;
    }
    
    public String getRunId() {
    	return runId;
    }

    /**
	 * @param collector
	 * @uml.property  name="collector"
	 */
    public void setCollector(RawMetricCollector collector) {
        this.collector = collector;
    }

    /**
	 * @param summariser
	 * @uml.property  name="summariser"
	 */
    public void setSummariser(MetricSummariser summariser) {
        this.summariser = summariser;
    }

    /**
     * Intention is to run this on a schedule, e.g. flush every 30s
     */
    public void run() {
        flush();
    }

    protected List<SummarisedMetric> summarize() {
    	List<RawMetric> metrics = collector.popAll();
        List<SummarisedMetric> summarised = summariser.summarise(metrics);
        
        return summarised;
    }
}
