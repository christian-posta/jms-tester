package com.fusesource.forge.jmstest.probe;

public class MinimizingProbe extends AbstractProbe {

	private double current = Double.MAX_VALUE;

	public MinimizingProbe() {
		super();
	}

	public MinimizingProbe(String name) {
		super(name);
	}

	synchronized public void addValue(double value) {
		if (value < current) {
			current = value;
		}
	}
	
	synchronized public void reset() {
		current = Double.MAX_VALUE;
	}
	
	@Override
	public Number getValue() {
		return current;
	}
}
