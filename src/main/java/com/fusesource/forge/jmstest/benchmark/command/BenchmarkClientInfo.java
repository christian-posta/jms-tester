package com.fusesource.forge.jmstest.benchmark.command;


public class BenchmarkClientInfo extends BaseBenchmarkCommand {
	
	private static final long serialVersionUID = 1095196139580697595L;

	private String clientName = "BenchmarkClient";

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	public byte getCommandType() {
		return CommandTypes.CLIENT_INFO;
	}
	
}
