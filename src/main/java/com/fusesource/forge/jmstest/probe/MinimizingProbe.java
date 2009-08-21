package com.fusesource.forge.jmstest.probe;

public class MinimizingProbe extends AbstractProbe {

	private double current = Double.MAX_VALUE;

	synchronized public void reset() {
		current = Double.MAX_VALUE;
	}
	
	synchronized public void addValue(double value) {
		if (value < current) {
			current = value;
		}
	}
	
	@Override
	public Number getValue() {
		return current;
	}
}
