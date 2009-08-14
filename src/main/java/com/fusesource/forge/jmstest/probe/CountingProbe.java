package com.fusesource.forge.jmstest.probe;

import java.util.concurrent.atomic.AtomicLong;

public class CountingProbe extends AbstractProbe {

	private AtomicLong counter;
	
	public CountingProbe() {
		super();
		counter = new AtomicLong(0);
	}
	
	public long increment() {
		return counter.incrementAndGet();
	}
	
	public long increment(long delta) {
		counter.getAndAdd(delta);
		return counter.get();
	}
	
	@Override
	protected Number getValue() {
		return counter.longValue();
	}
}
