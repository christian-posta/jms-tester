/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config.impl;

import java.util.Iterator;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fusesource.forge.jmstest.config.BrokerServicesFactory;


/**
 * Provide a base class implementing the BrokerServiceFactory. All methods except 
 * getBrokerServices can be implemented by this class.
 */
public abstract class AbstractBrokerServicesFactory implements BrokerServicesFactory {
	
	private static final Log LOG = LogFactory.getLog(AbstractBrokerServicesFactory.class);
	
	public BrokerService getBroker(String brokerName) throws Exception {
		return getBrokerServices().get(brokerName);
	}
	
	public void start(String brokerName) throws Exception {
		LOG.trace("Trying to start Broker : " + brokerName);
		BrokerService broker = getBrokerServices().get(brokerName);
		if (broker == null) {
			throw new Exception("No such broker : " + brokerName);
		}
		broker.start();
	}

	public void stop(String brokerName) throws Exception {
		LOG.trace("Trying to stop Broker : " + brokerName);
		BrokerService broker = getBrokerServices().get(brokerName);
		if (broker == null) {
			throw new Exception("No such broker : " + brokerName);
		}
		broker.stop();
		broker.waitUntilStopped();
	}
	
	@SuppressWarnings("unchecked")
	public void startAll() throws Exception {
		LOG.trace("Trying to start Message brokers");
		if (getBrokerServices() != null) {
			for(Iterator it = getBrokerServices().keySet().iterator(); it.hasNext(); ) {
				String brokerName = (String)it.next();
				start(brokerName);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void stopAll() throws Exception {
		LOG.trace("Trying to stop Message brokers");
		if (getBrokerServices() != null) {
			for(Iterator it = getBrokerServices().keySet().iterator(); it.hasNext(); ) {
				String brokerName = (String)it.next();
				stop(brokerName);
			}
		}
	}
}
