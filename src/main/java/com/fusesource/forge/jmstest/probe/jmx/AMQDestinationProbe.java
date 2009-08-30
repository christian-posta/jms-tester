package com.fusesource.forge.jmstest.probe.jmx;

import org.springframework.beans.factory.InitializingBean;

public class AMQDestinationProbe extends JMXProbe implements InitializingBean {

	private String destinationName = null;
	private String destinationType = "Queue";
	private String brokerName = "localhost";
	private String objectNamePrefix = null;
	
	public AMQDestinationProbe() {
		super();
	}
	
	public AMQDestinationProbe(String destinationName) {
		super();
		this.destinationName = destinationName;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	public void afterPropertiesSet() throws Exception {
		setObjectNameString(getObjectNamePrefix() + getDestinationName());
		setName(getDestinationType() + ":" + getDestinationName() + "-" + getAttributeName());
	}
	
	private String getObjectNamePrefix() {
		if (objectNamePrefix == null) {
			objectNamePrefix = "org.apache.activemq:BrokerName=" + getBrokerName() + ",Type=" + getDestinationType() + ",Destination=";
		}
		return objectNamePrefix;
	}
}
