package com.fusesource.forge.jmstest.config.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBrokerServicesFactory extends AbstractBrokerServicesFactory implements ApplicationContextAware {

	private transient Log log = null;

	private Map<String, BrokerService> brokerServices = null;
	private ApplicationContext ac = null;

	public Map<String, BrokerService> getBrokerServices() {
		if (brokerServices == null) {
			brokerServices = new HashMap<String, BrokerService>();
			
			for(String name: ac.getBeanNamesForType(BrokerService.class)) {
				BrokerService broker = (BrokerService) ac.getBean(name);
				log().info("Found broker definition for broker : " + broker.getBrokerName());
				brokerServices.put(broker.getBrokerName(), broker);
			}
		}
		return brokerServices;
	}

	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		this.ac = ac;
	}
	
	private Log log() {
		if (log == null) {
			log = LogFactory.getLog(this.getClass());
		}
		return log;
	}
}
