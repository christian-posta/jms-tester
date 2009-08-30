package com.fusesource.forge.jmstest.probe;

import java.util.concurrent.atomic.AtomicLong;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue.ValueType;

public class CountingProbe extends AbstractProbe {

	private AtomicLong counter = new AtomicLong(0l);
	
	public CountingProbe() {
		super();
	}

	public CountingProbe(String name) {
		super(name);
	}

	public long increment() {
		return counter.incrementAndGet();
	}
	
	public long increment(long delta) {
		counter.getAndAdd(delta);
		return counter.get();
	}
	
	@Override
	public ValueType getValueType() {
		return ValueType.COUNTER;
	}

	@Override
	public Number getValue() {
		return counter.longValue();
	}
	
	@Override
	public void reset() {
		counter.set(0l);
	}
}
