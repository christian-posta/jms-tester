package com.fusesource.forge.jmstest.benchmark.command;

public interface BenchmarkCommandHandler {

	public boolean handleCommand(BenchmarkCommand command);
	
	public BenchmarkCommandHandler next();
	public void setNext(BenchmarkCommandHandler next);
}
