package com.fusesource.forge.jmstest.benchmark.command;

import com.fusesource.forge.jmstest.executor.BenchmarkClientWrapper;

public class PrepareBenchmarkResponse extends StartBenchmarkCommand {

	private static final long serialVersionUID = -5586700527088404915L;

	private ClientId clientId;
	
	public PrepareBenchmarkResponse(BenchmarkClientWrapper client) {
		super(client.getConfig().getParent().getBenchmarkId());
		this.clientId = client.getClientId();
	}
	
	public byte getCommandType() {
		return CommandTypes.PREPARE_RESPONSE;
	}

	public ClientId getClientId() {
		return clientId;
	}

	public void setClientId(ClientId clientId) {
		this.clientId = clientId;
	}
}

