package com.fusesource.forge.jmstest.benchmark.command;

public class DefaultCommandHandler implements BenchmarkCommandHandler {

	private BenchmarkCommandHandler next = null;
	
	public boolean handleCommand(BenchmarkCommand command) {
		return false;
	}

	public BenchmarkCommandHandler next() {
		return next;
	}

	public void setNext(BenchmarkCommandHandler next) {
		this.next = next;
	}
}
