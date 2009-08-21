package com.fusesource.forge.jmstest.scenario;

public abstract class AbstractBenchmarkIteration implements BenchmarkIteration {

	private String name;
 
    public String getName() {
    	return (name != null?name:"Unknown");
    }
    
    public void setName(String name) {
    	this.name = name;
    }
}
