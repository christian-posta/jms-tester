package com.fusesource.forge.jmstest.benchmark.command;

import java.util.List;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkClient;
import com.fusesource.forge.jmstest.probe.ProbeDescriptor;

public class PrepareBenchmarkResponse extends ProducerFinishedCommand {

	private static final long serialVersionUID = -5586700527088404915L;
	private List<ProbeDescriptor> clientProbes;

	public PrepareBenchmarkResponse(AbstractBenchmarkClient client) {
		super(client);
		this.clientProbes = client.getProbeDescriptors();
	}
	
	public byte getCommandType() {
		return CommandTypes.PREPARE_RESPONSE;
	}

	public List<ProbeDescriptor> getClientProbes() {
		return clientProbes;
	}

	public void setClientProbes(List<ProbeDescriptor> clientProbes) {
		this.clientProbes = clientProbes;
	}
}

