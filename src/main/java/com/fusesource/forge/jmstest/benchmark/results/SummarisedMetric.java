package com.fusesource.forge.jmstest.benchmark.results;

import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

public class SummarisedMetric {
    private TimeUnit resolution = TimeUnit.SECONDS; // default
    private String clientId;
    private long start;
    private long end;
    private long count;
    private BigDecimal meanLatency;
    private BigDecimal standardDeviation;
    private long maxLatency;
    private long minLatency;
    private long messageSize;

    public SummarisedMetric() {
    }

    public SummarisedMetric(TimeUnit resolution) {
        this.resolution = resolution;
    }

    public TimeUnit getResolution() {
        return resolution;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public BigDecimal getMeanLatency() {
        return meanLatency;
    }

    public void setMeanLatency(BigDecimal meanLatency) {
        this.meanLatency = meanLatency;
    }

    public BigDecimal getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(BigDecimal standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public long getMaxLatency() {
        return maxLatency;
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }

    public long getMinLatency() {
        return minLatency;
    }

    public void setMinLatency(long minLatency) {
        this.minLatency = minLatency;
    }

    public long getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(long messageSize) {
        this.messageSize = messageSize;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final SummarisedMetric that = (SummarisedMetric) o;

        if (count != that.count) return false;
        if (end != that.end) return false;
        if (maxLatency != that.maxLatency) return false;
        if (messageSize != that.messageSize) return false;
        if (minLatency != that.minLatency) return false;
        if (start != that.start) return false;
        if (!clientId.equals(that.clientId)) return false;
        if (meanLatency != null ? !meanLatency.equals(that.meanLatency) : that.meanLatency != null)
            return false;
        if (resolution != that.resolution) return false;
        if (standardDeviation != null ? !standardDeviation.equals(that.standardDeviation) : that.standardDeviation != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = resolution.hashCode();
        result = 29 * result + clientId.hashCode();
        result = 29 * result + (int) (start ^ (start >>> 32));
        result = 29 * result + (int) (end ^ (end >>> 32));
        result = 29 * result + (int) (count ^ (count >>> 32));
        result = 29 * result + (meanLatency != null ? meanLatency.hashCode() : 0);
        result = 29 * result + (standardDeviation != null ? standardDeviation.hashCode() : 0);
        result = 29 * result + (int) (maxLatency ^ (maxLatency >>> 32));
        result = 29 * result + (int) (minLatency ^ (minLatency >>> 32));
        result = 29 * result + (int) (messageSize ^ (messageSize >>> 32));
        return result;
    }

    public static String getHeader() {
        return "clientId,startTime,endTime,count,mean,stdDev,min,max,messageSize";
    }

    public String toCSV() {
        return "" + clientId + "," + start + "," + end + "," + count + ","
                + meanLatency.toPlainString() + "," + standardDeviation.toPlainString()
                + "," + minLatency + "," + maxLatency + "," + messageSize;
    }
}
