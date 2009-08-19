package com.fusesource.forge.jmstest.benchmark.command;

public class PrepareBenchmarkCommand extends SubmitBenchmarkCommand {

	private static final long serialVersionUID = -3493057142415195777L;

	public PrepareBenchmarkCommand(BenchmarkConfig benchmarkConfig) {
		super(benchmarkConfig);
	}
	
	public byte getCommandType() {
		return CommandTypes.PREPARE_BENCHMARK;
	}
}
