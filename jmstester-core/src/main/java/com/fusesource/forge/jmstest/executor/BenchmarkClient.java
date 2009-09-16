package com.fusesource.forge.jmstest.executor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfoCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.EndBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.PrepareBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.PrepareBenchmarkResponse;
import com.fusesource.forge.jmstest.benchmark.command.StartBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.handler.DefaultCommandHandler;

public class BenchmarkClient extends AbstractBenchmarkExecutionContainer {

	private BenchmarkClientInfoCommand clientInfo = null;
	private Map<String, AbstractBenchmarkClient> activeClients = new HashMap<String, AbstractBenchmarkClient>();
	private Log log = null;
	
	public BenchmarkClientInfoCommand getClientInfo() {
		if (clientInfo == null) {
			clientInfo = new BenchmarkClientInfoCommand();
			try {
				clientInfo.setClientName("bmClient-" + InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				clientInfo.setClientName("bmClient-UnknownHost");
			}
		}
		return clientInfo;
	}

	public void setClientInfo(BenchmarkClientInfoCommand clientInfo) {
		this.clientInfo = clientInfo;
	}

	public AbstractBenchmarkClient addProbeRunner(BenchmarkConfig config) {
		AbstractBenchmarkClient bc = new BenchmarkProbeWrapper(this, config);
		if (bc.prepare()) {
			synchronized (activeClients) {
				activeClients.put(bc.getClientId().toString(), bc);
			}
			return bc;
		} else {
			log().warn("Error while setting up clients for clientId: " + bc.getClientId());
			return null;
		}
	}
	
	public AbstractBenchmarkClient addClients(ClientType type, BenchmarkPartConfig partConfig) {

		AbstractBenchmarkClient bc = null;
		
		switch (type) {
			case CONSUMER:
				bc = new BenchmarkJMSConsumerWrapper(this, partConfig);
				break;
			case PRODUCER:
				bc = new BenchmarkJMSProducerWrapper(this, partConfig);
				break;
			case PROBER:
				bc = new BenchmarkProbeWrapper(this, partConfig.getParent());
				break;
			default:
				// can't happen
				break;
		}
		
		boolean prepared = bc.prepare();
		
		if (prepared) {
			synchronized (activeClients) {
				activeClients.put(bc.getClientId().toString(), bc);
			}
			return bc;
		} else {
			log().warn("Error while setting up clients for clientId: " + bc.getClientId());
			return null;
		}
	}
	
	public void setName(String name) {
		getClientInfo().setClientName(name);
	}
	
	private List<AbstractBenchmarkClient> getClientsByBenchmarkId(String benchmarkId) {
		
		ArrayList<AbstractBenchmarkClient> result = new ArrayList<AbstractBenchmarkClient>();
		synchronized (activeClients) {
			for(AbstractBenchmarkClient client: activeClients.values()) {
				if (client.getClientId().getBenchmarkId().equals(benchmarkId)) {
					result.add(client);
				}
			}
		}
		return result;
	}
	
	public void startBenchmark(StartBenchmarkCommand startCommand) {
		for (AbstractBenchmarkClient client: getClientsByBenchmarkId(startCommand.getBenchmarkId())) {
			log().debug("Starting client: " + client.getClientId());
			client.start();
		}
	}
	
	public void endBenchmark(EndBenchmarkCommand endCommand) {
		for (AbstractBenchmarkClient client: getClientsByBenchmarkId(endCommand.getBenchmarkId())) {
			client.release();
			synchronized (activeClients) {
				activeClients.remove(client.getClientId().toString());
			}
		}
	}
	
	@Override
	public void release() {
		for(AbstractBenchmarkClient bc: activeClients.values()) {
			bc.release();
		}
		super.release();
	}

	@Override
	protected void createHandlerChain() {
		super.createHandlerChain();
		
		getConnector().addHandler(new DefaultCommandHandler() {
			@Override
			public boolean handleCommand(BenchmarkCommand command) {
				switch (command.getCommandType()) {
					case CommandTypes.GET_CLIENT_INFO: {
						sendCommand(getClientInfo());
						return true;
					}
					case CommandTypes.PREPARE_BENCHMARK:
						PrepareBenchmarkCommand prepCommand = (PrepareBenchmarkCommand)command;
						log().debug("Handling Benchmark prepare command ..." + prepCommand.getBenchmarkConfig().getBenchmarkId());
						
						AbstractBenchmarkClient bc = null;
						
						for(BenchmarkPartConfig partConfig: prepCommand.getBenchmarkConfig().getBenchmarkParts()) {
							if (partConfig.isAcceptAllConsumers() || matchesClient(partConfig.getConsumerClients())) {
								bc = addClients(ClientType.CONSUMER, partConfig);
								if (bc != null) {
									PrepareBenchmarkResponse response = new PrepareBenchmarkResponse(bc);
									getCmdTransport().sendCommand(response);
								}
							}
							if (partConfig.isAcceptAllProducers() || matchesClient(partConfig.getProducerClients())) {
								bc = addClients(ClientType.PRODUCER, partConfig);
								if (bc != null) {
									PrepareBenchmarkResponse response = new PrepareBenchmarkResponse(bc);
									getCmdTransport().sendCommand(response);
								}
							}
						}
						bc = addProbeRunner(prepCommand.getBenchmarkConfig());
						if (bc != null) {
							PrepareBenchmarkResponse response = new PrepareBenchmarkResponse(bc);
							getCmdTransport().sendCommand(response);
						}
						return true;
					case CommandTypes.START_BENCHMARK:
						StartBenchmarkCommand startCommand = (StartBenchmarkCommand)command;
						log().debug("Handling Benchmark start command ..." + startCommand.getBenchmarkId());
						startBenchmark(startCommand);
						return true;
					case CommandTypes.END_BENCHMARK:
						EndBenchmarkCommand endCommand = (EndBenchmarkCommand)command;
						log().debug("Handling Benchmark end command ..." + endCommand.getBenchmarkId());
						endBenchmark(endCommand);
						return true;
					default:
						return false;
				
				}
			}
		});
	}
	
	public boolean matchesClient(String clientIds) {
		
		String clientName = getClientInfo().getClientName();
		
		log().debug("Matching Client name (" +  clientName + ") against (" + clientIds + ")");
		StringTokenizer sTok = new StringTokenizer(clientIds, ",");
		while(sTok.hasMoreTokens()) {
			String token = sTok.nextToken();
			if (token.equalsIgnoreCase("All") || getClientInfo().getClientName().matches(token)) {
				return true;
			}
		}
		return false;
	}

	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
