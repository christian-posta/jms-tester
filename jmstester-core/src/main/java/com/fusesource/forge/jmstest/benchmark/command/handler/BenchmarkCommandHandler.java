package com.fusesource.forge.jmstest.benchmark.command.handler;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;

public interface BenchmarkCommandHandler {

	public boolean handleCommand(BenchmarkCommand command);
	
	public BenchmarkCommandHandler next();
	public void setNext(BenchmarkCommandHandler next);
}
