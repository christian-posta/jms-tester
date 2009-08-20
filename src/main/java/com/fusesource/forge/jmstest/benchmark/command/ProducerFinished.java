package com.fusesource.forge.jmstest.benchmark.command;

import com.fusesource.forge.jmstest.executor.BenchmarkProducerWrapper;

public class ProducerFinished extends PrepareBenchmarkResponse {

	private static final long serialVersionUID = -5192978795630743148L;

	public ProducerFinished(BenchmarkProducerWrapper client) {
		super(client);
	}
	
	@Override
	public byte getCommandType() {
		return CommandTypes.PRODUCER_FINISHED;
	}

}
