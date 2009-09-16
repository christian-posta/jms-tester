package com.fusesource.forge.jmstest.benchmark.command;

import java.util.List;

import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;

public class ReportStatsCommand extends BaseBenchmarkCommand { 
	
	private static final long serialVersionUID = -5141521547392349990L;
	
	private ClientId clientId = null;
	private List<BenchmarkProbeValue> values;
	
	public ReportStatsCommand(ClientId clientId, List<BenchmarkProbeValue> values) {
		this.clientId = clientId;
		this.values = values;
	}

	public ClientId getClientId() {
		return clientId;
	}
	
	public List<BenchmarkProbeValue> getValues() {
		return values;
	}

	public byte getCommandType() {
		return CommandTypes.REPORT_STATS;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("ReportStatsCommand[");
		buf.append(getClientId().toString());
		buf.append(",");
		buf.append(values.size());
		buf.append("]");
		return buf.toString();
	}
}
