package com.fusesource.forge.jmstest.benchmark.command;

public class EndBenchmarkCommand extends BaseBenchmarkCommand {

	private static final long serialVersionUID = -3493057142415195777L;

	private String benchmarkId;
	
	public byte getCommandType() {
		return CommandTypes.END_BENCHMARK;
	}

	public String getBenchmarkId() {
		return benchmarkId;
	}

	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
}
