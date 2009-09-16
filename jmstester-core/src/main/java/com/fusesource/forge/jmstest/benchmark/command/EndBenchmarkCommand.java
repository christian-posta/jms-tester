package com.fusesource.forge.jmstest.benchmark.command;

public class EndBenchmarkCommand extends StartBenchmarkCommand {

	private static final long serialVersionUID = -3493057142415195777L;

	public EndBenchmarkCommand(String benchmarkId) {
		super(benchmarkId);
	}
	
	public byte getCommandType() {
		return CommandTypes.END_BENCHMARK;
	}
}
