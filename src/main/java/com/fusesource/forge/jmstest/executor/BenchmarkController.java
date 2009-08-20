package com.fusesource.forge.jmstest.executor;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.command.BenchmarkClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCommand;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkCoordinator;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkGetClientInfo;
import com.fusesource.forge.jmstest.benchmark.command.CommandTypes;
import com.fusesource.forge.jmstest.benchmark.command.DefaultCommandHandler;

import edu.emory.mathcs.backport.java.util.concurrent.CountDownLatch;

public class BenchmarkController extends AbstractBenchmarkExecutionContainer {

	private Log log = null;
	
	private Map<String, BenchmarkClientInfo> clients = new TreeMap<String, BenchmarkClientInfo>();
	private BenchmarkCoordinator coordinator = null;
	private BrokerService broker = null;
	
	@Override
	protected void createHandlerChain() {
		super.createHandlerChain();
		
		getConnector().addHandler(new DefaultCommandHandler() {
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
		getConnector().addHandler(coordinator);
	}
	
	public void refreshClientInfos() {
		synchronized (clients) {
			clients.clear();
		}
		sendCommand(new BenchmarkGetClientInfo());
	}

	@Override
	synchronized public void start(String[] args) {
		log().info("Starting embedded broker for Benchmark framework: ");
		try {
			getBroker().start();
			broker.waitUntilStarted();
		} catch (Exception e) {
			log().error("Embedded broker could not be started.", e);
			stop();
		}
		super.start(args);
	}

	@Override
	synchronized public void stop() {

		log().info("BenchmarkController going down in 5 Seconds");
		
		final CountDownLatch latch = new CountDownLatch(1);
		final ScheduledThreadPoolExecutor waiter = new ScheduledThreadPoolExecutor(1);
		waiter.schedule(new Runnable() {
			public void run() {
				latch.countDown();
				waiter.shutdown();
			}
		}, 5, TimeUnit.SECONDS);
		
		try {
			latch.await();
		} catch (InterruptedException e1) {
		}

		super.stop();
		
		if (broker != null) {
			log().info("Stopping embedded broker for Benchmark framework: ");
			try {
				broker.stop();
			} catch (Exception e) {
				//log().error("Embedded broker could not be stopped.", e);
			}
		}
	}
	
	private BrokerService getBroker() throws Exception {
		if (broker == null) {
			broker = new BrokerService();
			broker.addConnector("tcp://0.0.0.0:" + getConnector().getPort());
			broker.setPersistent(false);
			broker.start();
			broker.waitUntilStarted();
		}
		return broker;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
	
	public static void main(String[] args) {
		final BenchmarkController controller = new BenchmarkController();
		controller.setAutoTerminate(false);
		controller.start(args);
	}
}