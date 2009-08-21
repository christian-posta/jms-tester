package com.fusesource.forge.jmstest.executor;

import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.fusesource.forge.jmstest.benchmark.ReleaseManager;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkPartConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.config.JMSConnectionProvider;
import com.fusesource.forge.jmstest.config.JMSDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultDestinationProvider;
import com.fusesource.forge.jmstest.config.impl.DefaultJMSConnectionProvider;
import com.fusesource.forge.jmstest.probe.ProbeRunner;
import com.fusesource.forge.jmstest.rrd.BenchmarkSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.rrd.CommandSamplePersistenceAdapter;
import com.fusesource.forge.jmstest.scenario.BenchmarkIteration;

public abstract class BenchmarkClientWrapper implements Releaseable {
	
	private BenchmarkPartConfig partConfig;
	private ApplicationContext appContext;
	
	private JMSConnectionProvider jmsConnectionProvider = null;
	private JMSDestinationProvider jmsDestinationProvider = null;
	
	private BenchmarkClient container;
	private ClientId clientId = null;
	
	private ProbeRunner probeRunner = null;
	private BenchmarkSamplePersistenceAdapter adapter = null;
	
	private Log log = null;
	
	public BenchmarkClientWrapper(BenchmarkClient container, BenchmarkPartConfig config) {
		this.partConfig = config;
		this.container = container;
	}

	public BenchmarkClient getContainer() {
		return container;
	}

	public BenchmarkPartConfig getConfig() {
		return partConfig;
	}
	
	public ApplicationContext getApplicationContext() {
		if (appContext == null) {
			appContext = getConfig().getParent().getApplicationContext();
		}
		return appContext;
	}

    public BenchmarkSamplePersistenceAdapter getSamplePersistenceAdapter() {
    	if (adapter == null) {
    		adapter = new CommandSamplePersistenceAdapter(getClientId(), getContainer().getCmdTransport());
    	}
		return adapter;
	}

	ProbeRunner getProbeRunner() {
		if (probeRunner == null) {
			probeRunner = new ProbeRunner();
			probeRunner.setName(getClientId().toString());
			probeRunner.setInterval(1);
			probeRunner.setDuration(getIteration().getTotalDuration());
		}
		return probeRunner;
	}
	
	public abstract ClientType getClientType();
	
	public ClientId getClientId() {
		
		if (clientId == null) {
			clientId = new ClientId(
				getClientType(), getContainer().getClientInfo().getClientName(), getConfig().getParent().getBenchmarkId(), getConfig().getPartID()
			);
		}
		return clientId;
	}
	
	public boolean prepare() {
		ReleaseManager.getInstance().register(this);
		return true;
	}
	
	public abstract void start();

	public void release() {
		ReleaseManager.getInstance().deregister(this);
		if (adapter != null) {
			adapter.stop();
		}
	}
	
	protected JMSConnectionProvider getJmsConnectionProvider() {
		if (jmsConnectionProvider == null) {
			jmsConnectionProvider = (JMSConnectionProvider)getBean(
				new String[] { 
					partConfig.getJmsConnectionProviderName(),
					JMSConnectionProvider.DEFAULT_BEAN_NAME
				}, JMSConnectionProvider.class
			);
		}
		if (jmsConnectionProvider == null) {
			log().warn("Creating default JMS Connection Provider.");
			ConnectionFactory cf = (ConnectionFactory)getBean(
				new String[] { "connectionFactory" }, ConnectionFactory.class
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
	
	@SuppressWarnings("unchecked")
	public Object getBean(String[] names, Class type) {
		Object result = null;
		
		for(String name: names) {
			try {
				log().debug("Retrieving bean from Benchmark Application Context: " + name);
				result = getApplicationContext().getBean(name);
				return result;
			} catch (Exception e) {
				//ignore
			}
		}
		
		log().debug("Bean not found, trying to resolve by type: " + type.getName());
		String beanNames[] = getApplicationContext().getBeanNamesForType(type);
		if (beanNames.length == 0) {
			log().error("Could not resolve bean by type: " + type.getName());
		} else {
			if (beanNames.length > 1) {
				log().warn("Multiple beans found for type: " + type.getName());
			}
			result = getApplicationContext().getBean(beanNames[0]);
		}
		
		return result;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

	synchronized public BenchmarkIteration getIteration() {
		
		BenchmarkIteration iteration = null;
		
		try {
			iteration = (BenchmarkIteration)getApplicationContext().getBean(getConfig().getProfileName());
		} catch (Exception e) {
			log().error("Error creating Iteration." ,e);
		}
		return iteration;
	}
}
