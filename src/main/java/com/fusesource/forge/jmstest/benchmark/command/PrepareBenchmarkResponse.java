package com.fusesource.forge.jmstest.benchmark.command;

public class PrepareBenchmarkResponse extends StartBenchmarkCommand {

	private static final long serialVersionUID = -5586700527088404915L;
	private String clientId;
	private String partId;
	private ClientType clientType;
	
	public PrepareBenchmarkResponse(ClientType clientType, String clientId, String benchmarkId, String partId) {
		super(benchmarkId);
		this.clientType = clientType;
		this.clientId = clientId;
		this.partId = partId;
	}
	
	public byte getCommandType() {
		return CommandTypes.PREPARE_RESPONSE;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public String getCombinedId() {
		StringBuffer buf = new StringBuffer();
		buf.append(getClientType());
		buf.append("-");
		buf.append(getClientId());
		buf.append("-");
		buf.append(getBenchmarkId());
		buf.append("-");
		buf.append(getPartId());
		return buf.toString();
	}
}

