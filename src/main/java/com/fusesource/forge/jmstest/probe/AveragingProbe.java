package com.fusesource.forge.jmstest.probe;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicLong;

public class AveragingProbe extends AbstractProbe {

	private AtomicLong counter = new AtomicLong(0l);
	private double currentAverage = 0.0;

	synchronized public void reset() {
		counter = new AtomicLong(0l);
		currentAverage = 0.0;
	}
	
	synchronized public void addValue(double value) {
		currentAverage = (currentAverage * counter.get() + value);
		counter.incrementAndGet();
		currentAverage /= counter.get();
	}
	
	@Override
	protected Number getValue() {
		return currentAverage;
	}
	
}
