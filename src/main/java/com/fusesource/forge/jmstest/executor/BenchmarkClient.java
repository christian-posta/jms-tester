package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkLifeCycleHandler;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.DefaultCommandHandler;
import com.fusesource.forge.jmstest.benchmark.command.EndBenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.StartBenchmarkCommand;

public class BenchmarkClient extends AbstractBenchmarkExecutor {

	private BenchmarkClientInfo clientInfo = null;
	private Map<String, BenchmarkClientWrapper> activeClients;

	private Log log = null;
	
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

	public BenchmarkClientWrapper addClients(ClientType type, BenchmarkPartConfig partConfig) {

		BenchmarkClientWrapper bcw = null;
		
		switch (type) {
			case CONSUMER:
				bcw = new BenchmarkConsumerWrapper(this, partConfig);
				break;
			case PRODUCER:
				bcw = new BenchmarkProducerWrapper(this, partConfig);
				break;
			default:
				// can't happen
				break;
		}
		
		boolean prepared = bcw.prepare();
		
		if (prepared) {
			synchronized (activeClients) {
				activeClients.put(bcw.getClientId().toString(), bcw);
			}
			return bcw;
		} else {
			log().warn("Error while setting up clients for clientId: " + bcw.getClientId());
			return null;
		}
	}
	
	private List<BenchmarkClientWrapper> getClientsByBenchmarkId(String benchmarkId) {
		
		ArrayList<BenchmarkClientWrapper> result = new ArrayList<BenchmarkClientWrapper>();
		return result;
	}
	
	public void startBenchmark(StartBenchmarkCommand startCommand) {
		for (BenchmarkClientWrapper client: getClientsByBenchmarkId(startCommand.getBenchmarkId())) {
			log().debug("Starting client: " + client.getClientId());
			client.start();
		}
	}
	
	public void endBenchmark(EndBenchmarkCommand endCommand) {
		for (BenchmarkClientWrapper client: getClientsByBenchmarkId(endCommand.getBenchmarkId())) {
			client.start();
		}
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
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
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
