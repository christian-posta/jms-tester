package com.fusesource.forge.jmstest.benchmark.command;

public class BenchmarkClient extends AbstractBenchmarkExecutor {

	private BenchmarkClientInfo clientInfo = null;

	public BenchmarkClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(BenchmarkClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	@Override
	protected void createHandlerChain() {
		super.createHandlerChain();
		
		getHandler().addHandler(new DefaultCommandHandler() {
			@Override
			public boolean handleCommand(BenchmarkCommand command) {
				if (command.getCommandType() == CommandTypes.GET_CLIENT_INFO) {
					sendCommand(getClientInfo());
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	public static void main(String[] args) {
		BenchmarkClient client = new BenchmarkClient();
		BenchmarkClientInfo info = new BenchmarkClientInfo();
		info.setClientName("TestClient");
		client.setClientInfo(info);
		
		start(new BenchmarkClient(), args);
	}
	
}
