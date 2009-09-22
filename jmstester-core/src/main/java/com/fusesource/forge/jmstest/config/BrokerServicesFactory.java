/*
 * Copyright (C) 2009, Progress Software Corporation and/or its
 * subsidiaries or affiliates.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fusesource.forge.jmstest.config;

import java.util.Map;

import org.apache.activemq.broker.BrokerService;

/**
 * The BrokerServiceFactory can be used by test cases to instantiate a number of
 * embedded within the JVM for testing purposes.
 *
 * @author andreasgies
 */
public interface BrokerServicesFactory {

  /**
   * Retrieve the list of brokers that can be created by this factory instance.
   * The result will be a Map with the brokerName as a key and the BrokerService
   * as value.
   *
   * @return Map<String, BrokerService>
   * @throws Exception
   *           When the Brokers could not be instantiated by the Factory
   *
   * @see BrokerService#getBrokerName()
   */
  Map<String, BrokerService> getBrokerServices() throws Exception;

  /**
   * Retrieve adedicated broker by it's name from the brokers cerated by this
   * factory.
   *
   * @param brokerName
   *          The name of the desired broker.
   * @return The desired broker, null if it doesn't exist
   */
  BrokerService getBroker(String brokerName) throws Exception;

  /**
   * Start the broker with the given name.
   */
  void start(String brokerName) throws Exception;

  /**
   * Stop the broker with the given name.
   */
  void stop(String brokerName) throws Exception;

  /**
   * Start all brokers created by this factory.
   */
  void startAll() throws Exception;

  /**
   * Stop all brokers created by this factory.
   */
  void stopAll() throws Exception;
}
