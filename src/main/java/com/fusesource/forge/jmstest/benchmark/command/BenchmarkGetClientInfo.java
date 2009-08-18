package com.fusesource.forge.jmstest.benchmark.command;

public class BenchmarkGetClientInfo extends BaseBenchmarkCommand {

	private static final long serialVersionUID = 5040011314355539214L;

	public byte getCommandType() {
		return CommandTypes.GET_CLIENT_INFO;
	}
}
