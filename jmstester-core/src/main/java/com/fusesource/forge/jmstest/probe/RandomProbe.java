package com.fusesource.forge.jmstest.probe;

import java.util.Random;

public class RandomProbe extends AbstractProbe {

	private double multiplier = 1000.0;
	private Random rnd = new Random();

	public RandomProbe() {
		super();
	}
	
	public RandomProbe(String name) {
		super(name);
	}

	public RandomProbe(double multiplier) {
		super();
		this.multiplier = multiplier;
	}
	
	public RandomProbe(String name, double multiplier) {
		super(name);
		this.multiplier = multiplier;
	}

	public Number getValue() {
		double result = rnd.nextDouble() * multiplier;
		return new Double(result);
	}
}
