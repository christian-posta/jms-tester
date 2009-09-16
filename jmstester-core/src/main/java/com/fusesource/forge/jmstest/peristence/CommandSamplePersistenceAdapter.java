package com.fusesource.forge.jmstest.peristence;

import java.util.ArrayList;
import java.util.List;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;
import com.fusesource.forge.jmstest.benchmark.command.transport.CommandTransport;
import com.fusesource.forge.jmstest.probe.BenchmarkProbeValue;

public class CommandSamplePersistenceAdapter extends CachingSamplePersistenceAdapter {
	
	private CommandTransport transport;
	private List<BenchmarkProbeValue> values;
	
	public CommandSamplePersistenceAdapter(ClientId clientId, CommandTransport transport) {
		super(clientId);
		this.transport = transport;
	}

	public CommandTransport getTransport() {
		return transport;
	}

	@Override
	protected void startFlush() {
		super.startFlush();
		values = new ArrayList<BenchmarkProbeValue>();
	}
	
	@Override
	protected void finishFlush() {
		super.finishFlush();
		BenchmarkCommand cmd = new ReportStatsCommand(getClientId(), values);
		getTransport().sendCommand(cmd);
	}

	@Override
	protected void flushValues(List<BenchmarkProbeValue> reportValues) {
		values.addAll(reportValues);
	}
}
