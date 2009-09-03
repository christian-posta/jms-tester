package com.fusesource.forge.jmstest.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.benchmark.BenchmarkConfig;
import com.fusesource.forge.jmstest.benchmark.command.BenchmarkProbeConfig;
import com.fusesource.forge.jmstest.benchmark.command.ClientId;
import com.fusesource.forge.jmstest.benchmark.command.ClientType;
import com.fusesource.forge.jmstest.probe.Probe;
import com.fusesource.forge.jmstest.probe.jmx.JMXConnectionFactory;
import com.fusesource.forge.jmstest.probe.jmx.JMXProbe;

public class BenchmarkProbeWrapper extends AbstractBenchmarkClient {
	
	private ClientId clientId = null;
	private Log log = null;
	
	public BenchmarkProbeWrapper(BenchmarkClient container, BenchmarkConfig config) {
		super(container, config);
	}


	public ClientType getClientType() {
		return ClientType.PROBER;
	}
	
	public ClientId getClientId() {
		
		if (clientId == null) {
			clientId = new ClientId(
				getClientType(), getContainer().getClientInfo().getClientName(), getConfig().getBenchmarkId(), ""
			);
		}
		return clientId;
	}
	
	private List<JMXConnectionFactory> getJmxConnectionFactories(BenchmarkProbeConfig probeConfig) {
		
		ArrayList<JMXConnectionFactory> result = new ArrayList<JMXConnectionFactory>();
		String jmxNamePattern = probeConfig.getPreferredJmxConnectionFactoryName(getContainer().getClientInfo().getClientName());
		
		for(String name: getApplicationContext().getBeanNamesForType(JMXConnectionFactory.class)) {
			if (name.matches(jmxNamePattern)) {
				result.add((JMXConnectionFactory)getApplicationContext().getBean(name));
			}
		}
		
		if (result.isEmpty()) {
			result.add((JMXConnectionFactory)getBean(
				new String[] {
					JMXConnectionFactory.DEFAULT_JMX_CONNECTION_FACTORY_NAME + "-" + getContainer().getClientInfo().getClientName(),
					JMXConnectionFactory.DEFAULT_JMX_CONNECTION_FACTORY_NAME
				}, JMXConnectionFactory.class
			));
		}
		
		if (result.isEmpty()) {
			log().warn("Could not resolve jmxConnectionFactory. Creating default.");
			JMXConnectionFactory cf = new JMXConnectionFactory();
			cf.setUsername("smx");
			cf.setPassword("smx");
			result.add(cf);
		}
		
		return result;
	}
	
	@Override
	public boolean prepare() {
		for(BenchmarkProbeConfig probeConfig : getConfig().getProbeConfigurations()) {
			if (getContainer().matchesClient(probeConfig.getClientNames())) {
				
				String[] probeNames = null;
				
				if (probeConfig.getProbeNames().equalsIgnoreCase("all")) {
					probeNames = getApplicationContext().getBeanNamesForType(Probe.class);
				} else {
					List<String> names = new ArrayList<String>();
					
					StringTokenizer sTok = new StringTokenizer(probeConfig.getProbeNames(), ",");
					while(sTok.hasMoreTokens()) {
						names.add(sTok.nextToken());
					}
					
					probeNames = names.toArray(new String[0]);
				}
				
				for(String probeName : probeNames) {
					try {
						Probe p = (Probe)getBean(new String[] { probeName }, null );
						
						if (p instanceof JMXProbe) {
  						    for(JMXConnectionFactory cf: getJmxConnectionFactories(probeConfig)) {
  								Probe jmxProbe = (Probe)getBean(new String[] { probeName }, null );
  								jmxProbe.setName(cf.getUrl().toString() + p.getName());
  								jmxProbe.addObserver(getSamplePersistenceAdapter());
  							    ((JMXProbe)jmxProbe).setJmxConnectionFactory(cf);
  								getProbeRunner().addProbe(jmxProbe);
						    }
						} else {
							p.setName(getClientId().toString() + p.getName());
							p.addObserver(getSamplePersistenceAdapter());
							getProbeRunner().addProbe(p);
						}
					} catch (Exception e) {
						log().error("Could not create probe: " + probeName, e);
					}
				}
			}
		}
		
		if (getProbeRunner().getProbes().size() == 0) {
			return false;
		}
		
		return super.prepare();
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}

}
