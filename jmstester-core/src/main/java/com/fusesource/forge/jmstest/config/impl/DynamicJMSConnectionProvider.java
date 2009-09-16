package com.fusesource.forge.jmstest.config.impl;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;

public class DynamicJMSConnectionProvider extends DefaultJMSConnectionProvider {

	private BrokerServicesFactory brokerServicesFactory;
	private String selectedBroker;
	
    protected ConnectionFactory createConnectionFactory(String brokerName) throws Exception {
        
    	BrokerService broker = getBrokerServicesFactory().getBroker(brokerName);
    	if (broker == null) {
    		throw new Exception("Broker (" + brokerName + ") does not exist.");
    	}
        String url = ((TransportConnector) broker.getTransportConnectors().get(0)).getServer().getConnectURI().toString();
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        ActiveMQPrefetchPolicy qPrefetchPolicy= new ActiveMQPrefetchPolicy();
        qPrefetchPolicy.setQueuePrefetch(1);
        connectionFactory.setPrefetchPolicy(qPrefetchPolicy);
        return connectionFactory;
    }

	public BrokerServicesFactory getBrokerServicesFactory() {
		return brokerServicesFactory;
	}

	public void setBrokerServicesFactory(
			BrokerServicesFactory brokerServicesFactory) {
		this.brokerServicesFactory = brokerServicesFactory;
	}

	public String getSelectedBroker() {
		return selectedBroker;
	}

	public void setSelectedBroker(String selectedBroker) {
		try {
			setConnectionFactory(createConnectionFactory(selectedBroker));
			this.selectedBroker = selectedBroker;
		} catch (Exception e) {
			// TODO: Handle this properly 
		}
	}
}
