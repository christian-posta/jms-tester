package com.fusesource.forge.jmstest.benchmark.command;

public class StartBenchmarkCommand extends BaseBenchmarkCommand {

	private static final long serialVersionUID = -8346378307563600726L;

	private String benchmarkId;
	
	public StartBenchmarkCommand(String benchmarkId) {
		super();
		this.benchmarkId = benchmarkId;
	}
	
	public byte getCommandType() {
		return CommandTypes.START_BENCHMARK;
	}
	
	public String getBenchmarkId() {
		return benchmarkId;
	}

	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
}
