package com.fusesource.forge.jmstest.benchmark.command;

public class SubmitBenchmarkCommand extends BaseBenchmarkCommand {

	private static final long serialVersionUID = -1061721082313649016L;
	private BenchmarkConfig benchmarkConfig;
	
	public SubmitBenchmarkCommand(BenchmarkConfig benchmarkConfig) {
		setBenchmarkConfig(benchmarkConfig);
	}
	
	public byte getCommandType() {
		return CommandTypes.SUBMIT_BENCHMARK;
	}

	public BenchmarkConfig getBenchmarkConfig() {
		return benchmarkConfig;
	}

	public void setBenchmarkConfig(BenchmarkConfig benchmarkConfig) {
		this.benchmarkConfig = benchmarkConfig;
	}
}
