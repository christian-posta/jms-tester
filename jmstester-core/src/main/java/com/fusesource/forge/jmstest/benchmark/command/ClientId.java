package com.fusesource.forge.jmstest.benchmark.command;

import java.io.Serializable;

public class ClientId implements Serializable {
	
	private static final long serialVersionUID = 36768397997494584L;

	private ClientType clientType;
	private String clientName;
	private String benchmarkId;
	private String partId;

	public ClientId(
		ClientType clientType, String clientName,
		String benchmarkId, String partId) {
		
		this.clientType = clientType;
		this.clientName = clientName;
		this.benchmarkId = benchmarkId;
		this.partId = partId;
	}

	public ClientType getClientType() {
		return clientType;
	}
	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getBenchmarkId() {
		return benchmarkId;
	}
	public void setBenchmarkId(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
	public String getPartId() {
		return partId;
	}
	public void setPartId(String partId) {
		this.partId = partId;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClientType());
		buf.append("-");
		buf.append(getClientName());
		buf.append("-");
		buf.append(getBenchmarkId());
		buf.append("-");
		buf.append(getPartId());
		return buf.toString();
	}

}
