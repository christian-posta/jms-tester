package com.fusesource.forge.jmstest.benchmark.results;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.MathContext;

//TODO: Refactor this to make it more general
public class PerUnitMetricSummariser implements MetricSummariser {

    private transient Log log;
    private TimeUnit resolution = TimeUnit.SECONDS;
    private long windowLength = 1;
    private long startTime = 0l;

    public PerUnitMetricSummariser() {
    }

/*
        //TODO implement windows of other than 1s
    public PerUnitMetricSummariser(TimeUnit resolution, long windowLength) {
        this.resolution = resolution;
        this.windowLength = windowLength;
    }
*/

    public List<SummarisedMetric> summarise(List<RawMetric> rawData) {
        List<SummarisedMetric> summarisedResults = new ArrayList<SummarisedMetric>();
        Map<Long, Map<String, List<RawMetric>>> byTimeUnit = getMapByTimeUnit(rawData);
        for (Long period : byTimeUnit.keySet()) {
            Map<String, List<RawMetric>> byClient = byTimeUnit.get(period);
            for (String clientId : byClient.keySet()) {
                List<RawMetric> rawForClient = byClient.get(clientId);
                SummarisedMetric summary = new SummarisedMetric(resolution);
                summary.setClientId(clientId);
                summary.setCount(rawForClient.size());
                summary.setStart(period);
                summary.setEnd(period);
                long min = 0l;
                long max = 0l;
                long[] latencies = new long[rawForClient.size()];
                int counter = 0;
                long msgSize = 0l;
                for (RawMetric rawMetric : rawForClient) {
                    latencies[counter++] = rawMetric.getLatency();
                    if (min == 0l || rawMetric.getLatency() < min) {
                        min = rawMetric.getLatency();
                    }
                    if (max == 0l || rawMetric.getLatency() > max) {
                        max = rawMetric.getLatency();
                    }
                    if (msgSize == 0l) {
                        msgSize = rawMetric.getMessageSize();
                    }
                }
                summary.setMaxLatency(max);
                summary.setMinLatency(min);
                BigDecimal[] meanAndStdDev = getMeanAndStandardDeviation(latencies);
                summary.setMeanLatency(meanAndStdDev[0]);
                summary.setStandardDeviation(meanAndStdDev[1]);
                summary.setMessageSize(msgSize);
                summarisedResults.add(summary);
            }
        }
        log().info(rawData.size() + " summarised into " + summarisedResults.size() + " units");
        return summarisedResults;
    }

    /**
     * Breaks the raw data down into Map<Long, Map<String, List<RawMetric>>) which assigns each
     * raw data point to a time period and clientId using the resolution we want. If window is >1
     * then this break down happens later.
     *
     * @param rawData
     * @return the map
     */
    private Map<Long, Map<String, List<RawMetric>>> getMapByTimeUnit(List<RawMetric> rawData) {
        Map<Long, Map<String, List<RawMetric>>> finalMap = new HashMap<Long, Map<String, List<RawMetric>>>(rawData.size() / 1000);
        for (RawMetric rawMetric : rawData) {
            if (startTime == 0l) {
                startTime = rawMetric.getTimeOfReceipt(); // assume that the 1st metric that comes in is the start time
            }
            long convertedDeliveryTime = resolution.convert(rawMetric.getTimeOfReceipt() - startTime, TimeUnit.MILLISECONDS);
            Map<String, List<RawMetric>> timeMap = finalMap.get(convertedDeliveryTime);
            if (timeMap == null) {
                timeMap = new HashMap<String, List<RawMetric>>(1000);
                finalMap.put(convertedDeliveryTime, timeMap);
            }
            List<RawMetric> rawDataByClient = timeMap.get(rawMetric.getClientId());
            if (rawDataByClient == null) {
                rawDataByClient = new ArrayList<RawMetric>(1000);
                timeMap.put(rawMetric.getClientId(), rawDataByClient);
            }
            rawDataByClient.add(rawMetric);
        }
        log().info("Raw data mapped by time and client [incoming: " + rawData.size() +
                ", distinctTimeUnits: " + finalMap.size() + "]");
        return finalMap;
    }

    private Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }

    public BigDecimal[] getMeanAndStandardDeviation(long[] latencies) {
        double[] varianceAndMean = getVarianceAndMean(latencies);
        BigDecimal mean = new BigDecimal(varianceAndMean[1], new MathContext(8));
        BigDecimal stdDev = new BigDecimal(Math.sqrt(varianceAndMean[0]), new MathContext(8));
        return new BigDecimal[] {mean, stdDev};
    }

    public double[] getVarianceAndMean(long[] latencies) {
        long n = 0;
        double mean = 0.0d;
        double s = 0.0d;
        for (long latency : latencies) {
            n++;
            double delta = latency - mean;
            mean += delta / n;
            s += delta * (latency - mean);
        }
        return new double[] {(s / n), mean};
    }
}
