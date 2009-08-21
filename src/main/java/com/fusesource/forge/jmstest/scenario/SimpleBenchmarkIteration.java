package com.fusesource.forge.jmstest.scenario;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleBenchmarkIteration extends AbstractBenchmarkIteration {

	private int position;
	private long duration;
    private long initialRate;
    private long currentRate;
    private long maxRate;
    private long increment = 0;
    private boolean iterated = false;
    
    private Log log = null;

    public void startIteration() {
    	position = 0;
    	iterated = false;
    }
    
    public boolean needsMoreRuns() {
    	return (!iterated) || (currentRate < getMaxRate());
    }
    
    public long nextEffectiveRate() {
    	currentRate = getInitialRate() + getIncrement() * position;
    	
        if (currentRate <= 0) {
            log().warn("EffectiveRate <=0, defaulting to 1");
            currentRate = 1;
        } else if (currentRate > maxRate) {
            currentRate = maxRate;
        }
        
        if (currentRate < maxRate) {
        	position++;
        }
    	
        iterated = true;
        return currentRate;
    }
    
    public long getCurrentDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getInitialRate() {
        return initialRate;
    }

    public void setInitialRate(long initialRate) {
        this.initialRate = initialRate;
        validate();
    }

    public long getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(long maxRate) {
        this.maxRate = maxRate;
        validate();
    }

    private long getIncrement() {
    	if (increment == 0) {
    		increment = getMaxRate() - getInitialRate();
    		if (increment == 0) {
    			increment = 1;
    		}
    		return maxRate - initialRate;
    	} 
    	return increment;
    }

    public long getRunsNeeded() {
    	long result = (getMaxRate() - getInitialRate()) / getIncrement();
    	if ((getMaxRate() - getInitialRate()) % getIncrement() == 0) {
    		result++;
    	}
    	return result;
    }
    
    public long getTotalDuration() {
    	return getRunsNeeded() * getCurrentDuration();
    }
    
    public void setIncrement(long incrementalRate) {
        this.increment = incrementalRate;
    }

    private void validate() {
        if (maxRate < initialRate) {
            maxRate = initialRate;
        }
    }

    public String toString() {
        return "BenchmarkIterationImpl{" + "duration=" + duration + ", initialRate=" + initialRate +
                ", maxRate=" + maxRate + ", increment=" + increment + "}";
    }
    
    private Log log() {
    	if (log == null) {
    		log = LogFactory.getLog(this.getClass());
    	}
    	return log;
    }
}
