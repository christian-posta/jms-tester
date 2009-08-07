/**
 *  Copyright (C) 2008 Progress Software, Inc. All rights reserved.
 *  http://fusesource.com
 *
 *  The software in this package is published under the terms of the AGPL license
 *  a copy of which has been included with this distribution in the license.txt file.
 */
package com.fusesource.forge.jmstest.config;

import java.util.Map;

import org.apache.activemq.broker.BrokerService;

/**
 * The BrokerServiceFactory can be used by test cases to instantiate a number of embedded 
 * within the JVM for testing purposes.
 * 
 * @author   andreasgies
 */
public interface BrokerServicesFactory {

	/**
	 * Retrieve the list of brokers that can be created by this factory instance. The result 
	 * will be a Map with the brokerName as a key and the BrokerService as value.
	 * 
	 * @return Map<String, BrokerService>
	 * @throws Exception When the Brokers could not be instantiated by the Factory
	 * 
	 * @see BrokerService#getBrokerName()
	 */
	public Map<String, BrokerService> getBrokerServices() throws Exception;
	
	/**
	 * Retrieve adedicated broker by it's name from the brokers cerated by this 
	 * factory. 
	 * @param brokerName The name of the desired broker.
	 * @return The desired broker, null if it doesn't exist
	 */
	public BrokerService getBroker(String brokerName) throws Exception;
	
	/**
	 * Start the broker with the given name.
	 */
	public void start(String brokerName) throws Exception;
	/**
	 * Stop the broker with the given name.
	 */
	public void stop(String brokerName) throws Exception;
	/**
	 * Start all brokers created by this factory.
	 */
	public void startAll() throws Exception;
	/**
	 * Sop all brokers created by this factory.
	 */
	public void stopAll() throws Exception;
}
