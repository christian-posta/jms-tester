package com.fusesource.forge.jmstest.executor;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultJMSConnectionProvider;
import com.fusesource.forge.jmstest.message.DefaultMessageFactory;
import com.fusesource.forge.jmstest.message.MessageFactory;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public abstract class AbstractBenchmarkJMSClient extends AbstractBenchmarkClient {
	
	private BenchmarkPartConfig partConfig;
	
	private JMSConnectionProvider jmsConnectionProvider = null;
	private JMSDestinationProvider jmsDestinationProvider = null;
	
	private ClientId clientId = null;
	
	private MessageFactory messageFactory = null;
	
	private Log log = null;
	
	public AbstractBenchmarkJMSClient(BenchmarkClient container, BenchmarkPartConfig partConfig) {
		super(container, partConfig.getParent());
		this.partConfig = partConfig;
	}

	public BenchmarkPartConfig getPartConfig() {
		return partConfig;
	}

	@Override
	protected ProbeRunner getProbeRunner() {
		ProbeRunner runner = super.getProbeRunner();
		
		BenchmarkIteration iteration = getIteration(getPartConfig().getProfileName());
		runner.setDuration(iteration.getTotalDuration());

		return runner;
	}
	
	public MessageFactory getMessageFactory() {
		if (messageFactory == null) {
			messageFactory = (MessageFactory)getBean(
				new String[] { 
					getPartConfig().getMessageFactoryName(),
					MessageFactory.DEFAULT_BEAN_NAME
				}, MessageFactory.class
			);
		}
		if (messageFactory == null) {
			log().warn("Could not resolve message factory object. Creating Default ...");
			messageFactory = new DefaultMessageFactory();
			((DefaultMessageFactory)messageFactory).setPrefix(getClientId().toString());
		}
		return messageFactory;
	}
	
	public abstract ClientType getClientType();
	
	public ClientId getClientId() {
		
		if (clientId == null) {
			clientId = new ClientId(
				getClientType(), getContainer().getClientInfo().getClientName(), getConfig().getBenchmarkId(), getPartConfig().getPartID()
			);
		}
		return clientId;
	}
	
	private String getPreferredConnectionFactoryName() {
		
		for(String key: getPartConfig().getConnectionFactoryNames().keySet()) {
			if (key.matches(getContainer().getClientInfo().getClientName())) {
				return getPartConfig().getConnectionFactoryNames().get(key);
			}
		}
		return JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME + "-" + getContainer().getClientInfo().getClientName();
	}
	
	protected JMSConnectionProvider getJmsConnectionProvider() {
		if (jmsConnectionProvider == null) {
			log().warn("Creating default JMS Connection Provider.");
			ConnectionFactory cf = (ConnectionFactory)getBean(
				new String[] { 
				  getPreferredConnectionFactoryName(),
				  JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME + "-" + getContainer().getClientInfo().getClientName(),
				  JMSConnectionProvider.DEFAULT_CONNECTION_FACTORY_NAME
				}, ConnectionFactory.class
			);
			if (cf != null) {
				jmsConnectionProvider = new DefaultJMSConnectionProvider();
				((DefaultJMSConnectionProvider)jmsConnectionProvider).setConnectionFactory(cf);
			}
		}
		return jmsConnectionProvider;
	}
	
	protected JMSDestinationProvider getJmsDestinationProvider() {
		if (jmsDestinationProvider == null) {
			jmsDestinationProvider = (JMSDestinationProvider)getBean(
					new String[] { 
						partConfig.getJmsDestinationProviderName(),
						JMSDestinationProvider.DEFAULT_BEAN_NAME
					}, JMSDestinationProvider.class
				);
		}
		if (jmsDestinationProvider == null) {
			log().warn("Creating Default Destination Provider.");
			jmsDestinationProvider = new DefaultDestinationProvider();
		}
		return jmsDestinationProvider;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

}
