package com.fusesource.forge.jmstest.benchmark.results;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RawMetricCollector {

    private transient Log log;

    private Vector<RawMetric> metrics;

    public RawMetricCollector() {
        metrics = new Vector<RawMetric>(10000);
    }

    public void collect(RawMetric metric) {
        metrics.add(metric);
    }

    public List<RawMetric> popAll() {
        List<RawMetric> copy;
        synchronized (metrics) {
            copy = new ArrayList<RawMetric>(metrics);
            metrics = new Vector<RawMetric>(copy.size());
        }
        log().debug("Metrics popped [" + copy.size() + "]");
        return copy;
    }

    public void pushAll(List<RawMetric> metrics) {
        log().debug("Metrics pushed [" + metrics.size() + "]");
        this.metrics.addAll(metrics);
    }

    public void stop() {
        log().debug("Stats remaining on closedown [" + metrics.size() + "]");
    }

    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
