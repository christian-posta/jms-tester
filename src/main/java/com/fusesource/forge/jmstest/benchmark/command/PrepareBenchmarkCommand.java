package com.fusesource.forge.jmstest.benchmark.command;

public class PrepareBenchmarkCommand extends BaseBenchmarkCommand {

	private static final long serialVersionUID = -3493057142415195777L;

	private BenchmarkConfig benchmarkConfig;
	
	public byte getCommandType() {
		return CommandTypes.PREPARE_BENCHMARK;
	}

	public BenchmarkConfig getBenchmarkConfig() {
		return benchmarkConfig;
	}

	public void setBenchmarkConfig(BenchmarkConfig benchmarkConfig) {
		this.benchmarkConfig = benchmarkConfig;
	}
}
