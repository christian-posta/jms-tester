package com.fusesource.forge.jmstest.benchmark.command;

import java.io.Serializable;

public class BenchmarkProbeConfig implements Serializable {

	private static final long serialVersionUID = -7087505154273084776L;

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
}
