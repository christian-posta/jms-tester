package com.fusesource.forge.jmstest.benchmark.command;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkClientWrapper;

public class BenchmarkLifeCycleHandler extends DefaultCommandHandler {
	
	private CommandTransport cmdTransport = null;
	private BenchmarkClient client = null;
	private Log log = null;
	
	public BenchmarkLifeCycleHandler(CommandTransport cmdTransport, BenchmarkClient client) {
		this.cmdTransport = cmdTransport;
		this.client = client;
	}
	
	private boolean matchesConsumer(String clientIds) {
		
		String clientName = client.getClientInfo().getClientName();
		log().debug("Matching Client name (" +  clientName + ") against (" + clientIds + ")");
		StringTokenizer sTok = new StringTokenizer(clientIds, ",");
		while(sTok.hasMoreTokens()) {
			if (client.getClientInfo().getClientName().matches(sTok.nextToken())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean handleCommand(BenchmarkCommand command) {
		switch (command.getCommandType()) {
			case CommandTypes.PREPARE_BENCHMARK:
				PrepareBenchmarkCommand prepCommand = (PrepareBenchmarkCommand)command;
				log().debug("Handling Benchmark prepare command ..." + prepCommand.getBenchmarkConfig().getBenchmarkId());
				BenchmarkConfig config = prepCommand.getBenchmarkConfig();
				for(BenchmarkPartConfig partConfig: prepCommand.getBenchmarkConfig().getBenchmarkParts()) {
					BenchmarkClientWrapper bcw = null;
					if (partConfig.isAcceptAllConsumers() || matchesConsumer(partConfig.getConsumerClients())) {
						bcw = client.addClients(ClientType.CONSUMER, partConfig);
					}
					if (partConfig.isAcceptAllProducers() || matchesConsumer(partConfig.getProducerClients())) {
						bcw = client.addClients(ClientType.PRODUCER, partConfig);
					}
					if (bcw != null) {
						PrepareBenchmarkResponse response = new PrepareBenchmarkResponse(bcw);
						cmdTransport.sendCommand(response);
					}
				}
				return true;
			case CommandTypes.START_BENCHMARK:
				StartBenchmarkCommand startCommand = (StartBenchmarkCommand)command;
				log().debug("Handling Benchmark start command ..." + startCommand.getBenchmarkId());
				client.startBenchmark(startCommand);
				return true;
			case CommandTypes.END_BENCHMARK:
				EndBenchmarkCommand endCommand = (EndBenchmarkCommand)command;
				log().debug("Handling Benchmark end command ..." + endCommand.getBenchmarkId());
				client.endBenchmark(endCommand);
				return true;
			default:
				return false;
		}
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

}
