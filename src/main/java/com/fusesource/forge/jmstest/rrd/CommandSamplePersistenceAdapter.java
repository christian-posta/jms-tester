package com.fusesource.forge.jmstest.rrd;

import java.util.ArrayList;
import java.util.List;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.CommandTransport;
import com.fusesource.forge.jmstest.benchmark.command.ReportStatsCommand;

public class CommandSamplePersistenceAdapter extends CachingSamplePersistenceAdapter {
	
	private CommandTransport transport;
	private List<BenchmarkSample> samples;
	
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
		samples = new ArrayList<BenchmarkSample>();
	}
	
	@Override
	protected void finishFlush() {
		super.finishFlush();
		BenchmarkCommand cmd = new ReportStatsCommand(getClientId(), samples);
		getTransport().sendCommand(cmd);
	}

	@Override
	protected void flushSample(BenchmarkSample sample) {
		samples.add(sample);
	}
}
