package com.fusesource.forge.jmstest.benchmark.command;

public interface CommandTransport {
	
	public void setHandler(BenchmarkCommandHandler handler);
	public BenchmarkCommandHandler getHandler();
	public void sendCommand(BenchmarkCommand command);
	public void start();
	public void stop();

}
