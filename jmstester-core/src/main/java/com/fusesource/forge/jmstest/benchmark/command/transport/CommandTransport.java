package com.fusesource.forge.jmstest.benchmark.command.transport;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.handler.BenchmarkCommandHandler;

public interface CommandTransport {
	
	public void setHandler(BenchmarkCommandHandler handler);
	public BenchmarkCommandHandler getHandler();
	public void sendCommand(BenchmarkCommand command);
	public void start();
	public void stop();

}
