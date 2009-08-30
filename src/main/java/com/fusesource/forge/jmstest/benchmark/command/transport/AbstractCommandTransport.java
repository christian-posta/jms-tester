package com.fusesource.forge.jmstest.benchmark.command.transport;

import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandHandler;

abstract public class AbstractCommandTransport implements CommandTransport {

	private BenchmarkCommandHandler handler = null;
	
	public void setHandler(BenchmarkCommandHandler handler) {
		this.handler = handler;
	}
	
	public BenchmarkCommandHandler getHandler() {
		return handler;
	}
}
