package com.fusesource.forge.jmstest.probe;

public class MaximizingProbe extends AbstractProbe {

	private double current = Double.MIN_VALUE;

	public MaximizingProbe() {
		super();
	}

	public MaximizingProbe(String name) {
		super(name);
	}

	synchronized public void reset() {
		current = Double.MIN_VALUE;
	}
	
	synchronized public void addValue(double value) {
		if (value > current) {
			current = value;
		}
	}
	
	@Override
	public Number getValue() {
		return current;
	}
	
}
