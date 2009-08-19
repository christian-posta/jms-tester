package com.fusesource.forge.jmstest.benchmark.command;

public class ProducerFinished extends PrepareBenchmarkResponse {

	private static final long serialVersionUID = -5192978795630743148L;

	public ProducerFinished(
			ClientType clientType, String clientId,
			String benchmarkId, String partId) {
		super(clientType, clientId, benchmarkId, partId);
	}
	
	@Override
	public byte getCommandType() {
		return CommandTypes.PRODUCER_FINISHED;
	}

}
