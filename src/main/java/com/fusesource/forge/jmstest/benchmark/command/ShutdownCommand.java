package com.fusesource.forge.jmstest.benchmark.command;

public class ShutdownCommand extends BaseBenchmarkCommand {

	private static final long serialVersionUID = -7720387306161471197L;

	public byte getCommandType() {
		return CommandTypes.SHUTDOWN;
	}
}
