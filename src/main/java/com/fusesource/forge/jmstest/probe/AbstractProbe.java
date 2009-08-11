package com.fusesource.forge.jmstest.probe;

public abstract class AbstractProbe {
	
	private ProbeDataConsumer dataConsumer;
	
	protected abstract Number getValue();
	
	
}
