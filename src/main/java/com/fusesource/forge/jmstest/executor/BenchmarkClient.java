package com.fusesource.forge.jmstest.executor;

import java.util.HashMap;
import java.util.Map;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkLifeCycleHandler;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.DefaultCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.EndBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.StartBenchmarkCommand;

public class BenchmarkClient extends AbstractBenchmarkExecutor {

	private BenchmarkClientInfo clientInfo = null;
	private Map<String, BenchmarkClientWrapper> activeClients;
	
	@Override
	protected void init() {
		super.init();
		activeClients = new HashMap<String, BenchmarkClientWrapper>();
	}
	
	public BenchmarkClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(BenchmarkClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	public void addConsumers(BenchmarkPartConfig partConfig) {
		//TODO: implement me
	}
	
	public void addProducers(BenchmarkPartConfig partConfig) {
		//TODO: implement me
	}
	
	public void startBenchmark(StartBenchmarkCommand startCommand) {
		//TODO: implement me
	}
	
	public void endBenchmark(EndBenchmarkCommand endCommand) {
		//TODO: implement me
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
		
		getHandler().addHandler(new BenchmarkLifeCycleHandler(
			getCmdTransport(), this
		));
	}
	
	@Override
	protected void execute() {
	}
	
	public static void main(String[] args) {
		BenchmarkClient client = new BenchmarkClient();

		BenchmarkClientInfo info = new BenchmarkClientInfo();
		info.setClientName("TestClient");
		client.setClientInfo(info);
		client.setAutoTerminate(false);
		
		client.start(args);
	}
}
