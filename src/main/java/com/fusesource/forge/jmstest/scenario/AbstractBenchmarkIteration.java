package com.fusesource.forge.jmstest.scenario;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractBenchmarkIteration implements BenchmarkIteration {

	private String name;
    private transient Log log;
    private boolean measured;

    public String getName() {
    	return (name != null?name:"Unknown");
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public boolean isMeasured() {
        return measured;
    }

    public void setMeasured(boolean measured) {
        this.measured = measured;
    }
    
    protected Log log() {
        if (log == null) {
            log = LogFactory.getLog(getClass());
        }
        return log;
    }
}
