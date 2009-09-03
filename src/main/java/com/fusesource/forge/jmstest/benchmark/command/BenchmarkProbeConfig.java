package com.fusesource.forge.jmstest.benchmark.command;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fusesource.forge.jmstest.probe.jmx.JMXConnectionFactory;

public class BenchmarkProbeConfig implements Serializable {

	private static final long serialVersionUID = -7087505154273084776L;

	private Map<String, String> jmxConnectionFactoryNames = null;
	
	private String clientNames = "All";
	private String probeNames = "All";
	
	public String getProbeNames() {
		return probeNames;
	}
	
	public void setProbeNames(String probeNames) {
		this.probeNames = probeNames;
	}
	
	public String getClientNames() {
		return clientNames;
	}
	
	public void setClientNames(String clientNames) {
		this.clientNames = clientNames;
	}

	public Map<String, String> getJmxConnectionFactoryNames() {
		if (jmxConnectionFactoryNames == null) {
			jmxConnectionFactoryNames = new HashMap<String, String>();
		}
		return jmxConnectionFactoryNames;
	}

	public void setJmxConnectionFactoryNames(
		Map<String, String> jmxConnectionFactoryNames) {
		this.jmxConnectionFactoryNames = jmxConnectionFactoryNames;
	}
	
	public String getPreferredJmxConnectionFactoryName(String clientName) {
		
		for(String key: getJmxConnectionFactoryNames().keySet()) {
			if (key.matches(clientName)) {
				return getJmxConnectionFactoryNames().get(key);
			}
		}
		
		return JMXConnectionFactory.DEFAULT_JMX_CONNECTION_FACTORY_NAME;
	}
}
