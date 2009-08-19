package com.fusesource.forge.jmstest.executor;

import java.util.Map;
import java.util.TreeMap;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCoordinator;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkGetClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.DefaultCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.ShutdownCommand;
import com.fusesource.forge.jmstest.benchmark.command.SubmitBenchmarkCommand;

public class BenchmarkController extends AbstractBenchmarkExecutor {

	private Map<String, BenchmarkClientInfo> clients = new TreeMap<String, BenchmarkClientInfo>();
	BenchmarkCoordinator coordinator = null;
	
	@Override
	protected void createHandlerChain() {
		super.createHandlerChain();
		
		getHandler().addHandler(new DefaultCommandHandler() {
			public boolean handleCommand(BenchmarkCommand command) {
				if (command.getCommandType() == CommandTypes.CLIENT_INFO) {
					BenchmarkClientInfo info = (BenchmarkClientInfo)command;
					synchronized (clients) {
						clients.put(info.getClientName(), info);
					}
					return true;
				}
				return false;
			}
		});
		
		coordinator = new BenchmarkCoordinator();
		coordinator.setCommandTransport(getCmdTransport());
		coordinator.start();
		getHandler().addHandler(coordinator);
	}
	
	public void refreshClientInfos() {
		synchronized (clients) {
			clients.clear();
		}
		sendCommand(new BenchmarkGetClientInfo());
	}

	@Override
	protected void execute() {
		// Read in all BenchmarkConfigs
		for(String beanName: getApplicationContext().getBeanNamesForType(BenchmarkConfig.class)) {
			BenchmarkConfig cfg = (BenchmarkConfig)getApplicationContext().getBean(beanName);
			sendCommand(new SubmitBenchmarkCommand(cfg));
		}
		coordinator.waitUntilFinished();
		sendCommand(new ShutdownCommand());
	}
	
	public static void main(String[] args) {
		final BenchmarkController controller = new BenchmarkController();
		controller.start(args);
	}
}