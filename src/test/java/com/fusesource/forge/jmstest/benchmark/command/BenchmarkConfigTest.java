package com.fusesource.forge.jmstest.benchmark.command;

import java.util.ArrayList;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.executor.BenchmarkClient;
import com.fusesource.forge.jmstest.executor.BenchmarkController;
import com.fusesource.forge.jmstest.executor.BenchmarkProbeWrapper;
import com.fusesource.forge.jmstest.probe.jmx.JMXConnectionFactory;

@ContextConfiguration(locations={
		"classpath:complete/benchmarks.xml",
		"classpath:complete/beans.xml"})
public class BenchmarkConfigTest extends AbstractTestNGSpringContextTests {

	private String[] clientNames = {
		"API-1", "API-2", "WebMail-1", "WebMail-2", "SMS-1", "SMS-2", 
		"RPG-1", "RPG-2", "Monitor-1", "Monitor-2"
	};
	
	private BenchmarkConfig config = null;
	private BenchmarkController controller = null;
	private int jmsPort = 62626;
	private Log log = null;

	@BeforeTest
	public void startTest() throws Exception {
		controller = new BenchmarkController();
		controller.setHostname("0.0.0.0");
		controller.setJmsPort(0);
		controller.setAutoTerminate(false);
		controller.start(new String[] {});
		jmsPort = controller.getJmsPort();
	}
	
	@AfterTest
	public void stopTest() {
		controller.stop();
	}
	
	private BenchmarkConfig getConfig() {
		if (config == null) {
			config = (BenchmarkConfig)applicationContext.getBean("complete");
			List<String> configLocations = new ArrayList<String>();
			configLocations.add("src/test/resources/complete");
			config.setConfigLocations(configLocations);

			Assert.assertEquals(config.getBenchmarkId(), "complete");
		}
		return config;
	}
	
	private void checkPartNames() {
		String[] queuePartIDs = new String[] {
			"eventEngineCommands", "webmailRelay", "sendUserSms", "sourceActivities",
			"sendAppSms", "webmailRelayUserChange", "groupChanges", "webmailRelayContactChanges",
			"relayToZyb"
		};
		
		String[] topicPartIDs = new String[] {
			"activities", "contactChanges", "identityChanges", "invitations", 
			"profileChanges", "userChanges"
		};

		List<BenchmarkPartConfig> parts = getConfig().getBenchmarkParts();
		List<String> partNames = new ArrayList<String>();
		
		for(BenchmarkPartConfig part: parts) {
			partNames.add(part.getPartID());
		}
		
		for(String partId: queuePartIDs) {
			log().info("Checking partID: " + partId);
			Assert.assertTrue(partNames.contains(partId));
		}

		for(String partId: topicPartIDs) {
			log().info("Checking partID: " + partId);
			Assert.assertTrue(partNames.contains(partId));
		}
	}
	
	private List<String> matchingClients(int mode, BenchmarkPartConfig part) {
		List<String> result = new ArrayList<String>();
		
		for(String clientName: clientNames) {
			BenchmarkClient client = new BenchmarkClient();
			BenchmarkClientInfoCommand clientInfo = new BenchmarkClientInfoCommand();
			clientInfo.setClientName(clientName);
			client.setClientInfo(clientInfo);
			client.setJmsPort(jmsPort);
			switch (mode) {
				case 0: {
					if (client.matchesClient(part.getConsumerClients())) {
						result.add(clientName);
					}
					break;
				}
				case 1: {
					if (client.matchesClient(part.getProducerClients())) {
						result.add(clientName);
					}
					break;
				}
				case 2: {
					BenchmarkProbeWrapper bpw = new BenchmarkProbeWrapper(client, getConfig());
					bpw.prepare();
					if (bpw.getProbeDescriptors().size() > 0) {
						result.add(clientName);
					}
					
					break;
				}
				default: 
					// ignore
			}
				
		}
		return result;
	}
	
	private void checkBenchmarkParts() {
		
		
		for(BenchmarkPartConfig part: getConfig().getBenchmarkParts()) {
			log().info("Analyzing Benchmark Part: " + part.getPartID());
			Assert.assertTrue(part.getTestDestinationName().endsWith(part.getPartID()));
		
			int consumerCount = matchingClients(0, part).size();
			int producerCount = matchingClients(1, part).size();
			
			if (part.getTestDestinationName().startsWith("topic:")) {
				Assert.assertEquals(consumerCount, 4);
				Assert.assertEquals(producerCount, 2);
			} else {
				Assert.assertEquals(consumerCount, 2);
				Assert.assertEquals(producerCount, 1);
			}
		}
	}
	
	@Test
	public void testBenchmarkConfig() {
		String[] beanNames = getConfig().getApplicationContext().getBeanNamesForType(JMXConnectionFactory.class);
		Assert.assertEquals(beanNames.length, 4);
		
		beanNames = getConfig().getApplicationContext().getBeanNamesForType(ConnectionFactory.class);
		Assert.assertEquals(beanNames.length, 2);
		
		checkPartNames();
		checkBenchmarkParts();
		
		Assert.assertEquals(matchingClients(2, null).size(), 2);

	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
