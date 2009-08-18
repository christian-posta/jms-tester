package com.fusesource.forge.jmstest.benchmark.command;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BenchmarkController extends AbstractBenchmarkExecutor {

	private Map<String, BenchmarkClientInfo> clients = new TreeMap<String, BenchmarkClientInfo>();
	
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
	}
	
	public void refreshClientInfos() {
		synchronized (clients) {
			clients.clear();
		}
		sendCommand(new BenchmarkGetClientInfo());
	}
	
	public static void main(String[] args) {
		
		final BenchmarkController controller = new BenchmarkController();
		start(controller, args);

		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		
		executor.schedule(
		    new Runnable() {
				public void run() {
					controller.sendCommand(new ShutdownCommand());
					executor.shutdown();
				}
			}, 5, TimeUnit.SECONDS
		);
	}
}