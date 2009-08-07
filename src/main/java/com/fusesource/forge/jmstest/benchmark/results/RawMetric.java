package com.fusesource.forge.jmstest.benchmark.results;

import java.util.StringTokenizer;

/**
 * @author  andreasgies
 */
public class RawMetric {

	private String clientId;
    private long timeOfReceipt;
    private long latency;
    private long messageSize;

    public RawMetric(String clientId, long timeOfReceipt, long latency, long messageSize) {
        this.clientId = clientId;
        this.timeOfReceipt = timeOfReceipt;
        this.latency = latency;
        this.messageSize = messageSize;
    }

    public RawMetric() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getTimeOfReceipt() {
        return timeOfReceipt;
    }

    public void setTimeOfReceipt(long timeOfReceipt) {
        this.timeOfReceipt = timeOfReceipt;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public long getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(long messageSize) {
        this.messageSize = messageSize;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RawMetric that = (RawMetric) o;

        if (latency != that.latency) {
            return false;
        }
        if (messageSize != that.messageSize) {
            return false;
        }
        if (timeOfReceipt != that.timeOfReceipt) {
            return false;
        }
        if (!clientId.equals(that.clientId)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = clientId.hashCode();
        result = 29 * result + (int) (timeOfReceipt ^ (timeOfReceipt >>> 32));
        result = 29 * result + (int) (latency ^ (latency >>> 32));
        result = 29 * result + (int) (messageSize ^ (messageSize >>> 32));
        return result;
    }


    public String toString() {
        return "BenchmarkMetric{" + "clientId='" + clientId + '\'' + ", timeOfReceipt=" + timeOfReceipt + ", latency="
                + latency + ", messageSize=" + messageSize + '}';
    }

    public String toCSV() {
        return "" + clientId + "," + timeOfReceipt + "," + latency + "," + messageSize;  
    }

    public static String getHeader() {
        return "clientId,timeOfReceipt,latency,messageSize";
    }

    public static RawMetric fromCSV(String line) {
        StringTokenizer tokeniser = new StringTokenizer(line, ",");
        RawMetric metric = null;
        if (tokeniser.countTokens() == 4) {
            String clientId = tokeniser.nextToken();
            long timeOfReceipt = Long.valueOf(tokeniser.nextToken());
            long latency = Long.valueOf(tokeniser.nextToken());
            long messageSize = Long.valueOf(tokeniser.nextToken());
            metric = new RawMetric(clientId, timeOfReceipt, latency, messageSize);
        } else {
            //borkage!
        }
        return metric;
    }
}
