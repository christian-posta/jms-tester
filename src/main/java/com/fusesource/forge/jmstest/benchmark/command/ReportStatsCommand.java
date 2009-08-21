package com.fusesource.forge.jmstest.benchmark.command;

import java.util.List;

import com.fusesource.forge.jmstest.rrd.BenchmarkSample;

public class ReportStatsCommand extends BaseBenchmarkCommand { 
	
	private static final long serialVersionUID = -5141521547392349990L;
	
	private ClientId clientId = null;
	private List<BenchmarkSample> samples;
	
	public ReportStatsCommand(ClientId clientId, List<BenchmarkSample> samples) {
		this.clientId = clientId;
		this.samples = samples;
	}

	public ClientId getClientId() {
		return clientId;
	}
	
	public List<BenchmarkSample> getSamples() {
		return samples;
	}

	public byte getCommandType() {
		return CommandTypes.REPORT_STATS;
	}

}
