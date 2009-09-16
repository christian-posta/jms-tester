package com.fusesource.forge.jmstest.benchmark.command;

import com.fusesource.forge.jmstest.executor.AbstractBenchmarkClient;

public class ProducerFinishedCommand extends StartBenchmarkCommand {

	private static final long serialVersionUID = -5192978795630743148L;

	private ClientId clientId;

	public ProducerFinishedCommand(AbstractBenchmarkClient client) {
		super(client.getConfig().getBenchmarkId());
		this.clientId = client.getClientId();
	}
	
	@Override
	public byte getCommandType() {
		return CommandTypes.PRODUCER_FINISHED;
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}
}
