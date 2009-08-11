package com.fusesource.forge.jmstest.probe;

import java.util.Random;

public class RandomProbe extends AbstractProbe {
	
	private Random rnd;
	
	public RandomProbe() {
		rnd = new Random(System.currentTimeMillis());
	}
	
	public Number getValue() {
		double result = rnd.nextDouble() * 1000;
		return new Double(result);
	}
}
