package com.fusesource.forge.jmstest.benchmark.results;

import java.util.List;

public interface MetricSummariser {
    public List<SummarisedMetric> summarise(List<RawMetric> rawData);
}
